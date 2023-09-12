package com.bot.cqs.gateway.service;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.bot.cqs.gateway.context.ContextLoader;
import com.bot.cqs.gateway.context.GatewayContext;
import com.bot.cqs.gateway.context.MQInstance;
import com.bot.cqs.gateway.handler.channel.MQRequestChannelHandler;
import com.bot.cqs.gateway.handler.channel.MQResponseChannelHandler;
import com.bot.cqs.gateway.handler.channel.impl.BOTMQRequestChannelHandler;
import com.bot.cqs.gateway.handler.channel.impl.IBMWebSphereMQUtils;
import com.bot.cqs.gateway.handler.message.RequestMessageHandler;
import com.bot.cqs.gateway.handler.message.ResponseMessageHandler;
import com.bot.cqs.gateway.handler.message.impl.BOTConnServiceRequestMessageHandler;
import com.bot.cqs.gateway.handler.message.impl.BOTConnServiceResponseMessageHandler;
import com.bot.cqs.gateway.service.message.ServiceSessionMsg;
import com.ibm.mq.MQException;
import com.ibm.mq.MQGetMessageOptions;
import com.ibm.mq.MQMessage;
import com.ibm.mq.MQQueue;
import com.ibm.mq.MQQueueManager;
import com.ibm.mq.constants.CMQC;

import tw.com.iisi.common.message.format.util.MessageUtils;

/**
 * 此類別主要用於接收票交所系統傳送過來的通訊類電文（啟動服務、暫停服務、TPing）。 若電文為啟動服務（S001），則組成登入電文傳送給票交所，等待登入結果。 若電文為暫停服務（S005），則組成登入電文傳送給票交所，等待登出結果。 若電文為TPing服務（T001），則組成TPing回覆電文傳送給票交所。若處理過程中有錯誤發生，系統會重試6次。
 *
 * @author Jeff Tseng
 * @see MQInstance
 * @see MQRequestChannelHandler
 * @see MQResponseChannelHandler
 * @see RequestMessageHandler
 * @see ResponseMessageHandler
 * @see "IBM MQ 開發指南"
 * @since 1.0 2007/08/31
 * 
 *        2020/3/22 因MQC以及MQException部分常數定義已於MQ7宣告deprecated, 所以將MQC及MQException部分常數定義換成CMQC
 */
public class BOTMessageResolverForConnService {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    // 初始化每個處理程式，將實際的實作程式指定給處理程式介面上。此部分之後可以用Spring的介面替代。
    static {
        ContextLoader.init();
    }

    /**
     * 系統每隔設定的時間啟動接收票交所TPing請求，並回覆票交所TPing。若系統有發生錯誤，則等候兩秒後會重試。總共重試次數為6次。
     */
    public void ping() {

        int count = 0;

        MQInstance instance = IBMWebSphereMQUtils.TCHInstance;

        MQQueueManager mqQueueManager = null;
        try {
            mqQueueManager = new MQQueueManager(instance.getQueueManager(), IBMWebSphereMQUtils.getMQProperies(IBMWebSphereMQUtils.TCH), IBMWebSphereMQUtils.BOTMQTCHConnMgr);
        } catch (MQException e) {
        }
        
        do {
            try {
                doPing(mqQueueManager);
                count = 6;
            } catch (GatewayException ex) {
                count++;
                if (count >= 5) {
                    if (logger.isDebugEnabled()) {
                        logger.debug(ex.getMessage());
                    }
                    break;
                } else {
                    try {
                        Thread.sleep(2000);
                    } catch (Exception e) {
                        if (logger.isDebugEnabled()) {
                            logger.debug(ex.getMessage());
                        }
                        break;
                    }
                }
            } catch (Exception ex) {
                if (logger.isDebugEnabled()) {
                    logger.debug(ex.getMessage());
                }
                break;
            }
        } while (count < 5);
        
        if (mqQueueManager != null) {
            try {
                mqQueueManager.disconnect();
            } catch (MQException e) {
            }
            mqQueueManager = null;
        }
    }

    /**
     * 系統每隔設定的時間啟動接收票交所StartService請求，透過IBM MQ API，建立MQ連結， 並開啟GatewayContext中instance中各Queue Name屬性指向的Queue，接收於Queue中的訊息，並向票交所請求登入系統。
     */
    public void start() {

        MQInstance instance = IBMWebSphereMQUtils.TCHInstance;
        
        MQQueueManager mqQueueManager = null;
        try {
            mqQueueManager = new MQQueueManager(instance.getQueueManager(), IBMWebSphereMQUtils.getMQProperies(IBMWebSphereMQUtils.TCH), IBMWebSphereMQUtils.BOTMQTCHConnMgr);
        } catch (MQException e) {
        }

        MQQueue queue = null;
        MQMessage retrievedMessage = null;
        MQGetMessageOptions gmo = new MQGetMessageOptions();

        try {

            int openOptions = CMQC.MQOO_BROWSE | CMQC.MQOO_INQUIRE | CMQC.MQOO_INPUT_SHARED;
            queue = mqQueueManager.accessQueue(instance.getInboundQueue(), openOptions);

            byte[] content = null;
            while (true) {
                try {
                    retrievedMessage = new MQMessage();

                    gmo.options = CMQC.MQGMO_NO_WAIT | CMQC.MQGMO_BROWSE_NEXT; // | CMQC.MQGMO_LOCK
                    queue.get(retrievedMessage, gmo);

                    content = new byte[retrievedMessage.getDataLength()];
                    retrievedMessage.readFully(content);

                    if (IBMWebSphereMQUtils.getFilter("BOTStartServiceMQMessageFilter").hit(new String(content, instance.getEncoding()))) {
                        gmo.options = CMQC.MQGMO_MSG_UNDER_CURSOR;
                        queue.get(retrievedMessage, gmo);
                        BOTGatewayService.logon(mqQueueManager);
                        if (ContextLoader.getGatewayContext().isLogFlag()) {
                            GatewayContext.logger.info("系統服務啟動成功");
                        }
                    } else {
                        if (queue.getCurrentDepth() == 1) {
                            break;
                        }
                    }
                } catch (MQException e) {
                    boolean isMQError2009 = e.completionCode == 2 && e.reasonCode == CMQC.MQRC_CONNECTION_BROKEN;
                    boolean isMQError2033 = e.completionCode == 2 && e.reasonCode == CMQC.MQRC_NO_MSG_AVAILABLE;
                    if (!isMQError2009 && !isMQError2033) {
                        if (logger.isErrorEnabled()) {
                            logger.error("系統服務發生錯誤：" + e.reasonCode + " " + e.getMessage());
                        }
                    } else {
                        break;
                    }
                } catch (Exception e) {
                    if (logger.isDebugEnabled()) {
                        logger.debug(e.getMessage());
                    }
                    break;
                } finally {
                    content = null;
                }
            }
        } catch (Exception e1) {
            if (logger.isDebugEnabled()) {
                logger.debug(e1.getMessage());
            }
        } finally {
            if (queue != null) {
                try {
                    queue.close();
                } catch (MQException e) {
                }
                queue = null;
            }
            if (mqQueueManager != null) {
                try {
                    mqQueueManager.disconnect();
                } catch (MQException e) {
                }
                mqQueueManager = null;
            }
        }
    }

    /**
     * 系統每隔設定的時間啟動接收票交所StopService請求，透過IBM MQ API，建立MQ連結， 並開啟GatewayContext中instance中各Queue Name屬性指向的Queue，接收於Queue中的訊息，並向票交所請求登出系統。
     */
    public void stop() {

        MQInstance instance = IBMWebSphereMQUtils.TCHInstance;

        MQQueueManager mqQueueManager = null;
        try {
            mqQueueManager = new MQQueueManager(instance.getQueueManager(), IBMWebSphereMQUtils.getMQProperies(IBMWebSphereMQUtils.TCH), IBMWebSphereMQUtils.BOTMQTCHConnMgr);
        } catch (MQException e) {
        }
        
        MQQueue queue = null;
        MQMessage retrievedMessage = null;
        MQGetMessageOptions gmo = new MQGetMessageOptions();

        try {

            int openOptions = CMQC.MQOO_BROWSE | CMQC.MQOO_INQUIRE | CMQC.MQOO_INPUT_SHARED;
            queue = mqQueueManager.accessQueue(instance.getInboundQueue(), openOptions);

            byte[] content = null;
            while (true) {
                try {
                    retrievedMessage = new MQMessage();

                    gmo.options = CMQC.MQGMO_NO_WAIT | CMQC.MQGMO_BROWSE_NEXT; // | CMQC.MQGMO_LOCK
                    queue.get(retrievedMessage, gmo);

                    content = new byte[retrievedMessage.getDataLength()];
                    retrievedMessage.readFully(content);

                    if (IBMWebSphereMQUtils.getFilter("BOTStopServiceMQMessageFilter").hit(new String(content, instance.getEncoding()))) {
                        gmo.options = CMQC.MQGMO_MSG_UNDER_CURSOR;
                        queue.get(retrievedMessage, gmo);
                        BOTGatewayService.logoff(mqQueueManager);
                        if (ContextLoader.getGatewayContext().isLogFlag()) {
                            GatewayContext.logger.info("系統服務暫停成功");
                        }
                    } else {
                        if (queue.getCurrentDepth() == 1) {
                            break;
                        }
                    }
                } catch (MQException e) {
                    boolean isMQError2009 = e.completionCode == 2 && e.reasonCode == CMQC.MQRC_CONNECTION_BROKEN;
                    boolean isMQError2033 = e.completionCode == 2 && e.reasonCode == CMQC.MQRC_NO_MSG_AVAILABLE;
                    if (!isMQError2009 && !isMQError2033) {
                        if (logger.isErrorEnabled()) {
                            logger.error("系統服務發生錯誤：" + e.reasonCode + " " + e.getMessage());
                        }
                    } else {
                        break;
                    }
                } catch (Exception e) {
                    if (logger.isDebugEnabled()) {
                        logger.debug(e.getMessage());
                    }
                    break;
                } finally {
                    content = null;
                }
            }
        } catch (Exception e1) {
            if (logger.isDebugEnabled()) {
                logger.debug(e1.getMessage());
            }
        } finally {
            if (queue != null) {
                try {
                    queue.close();
                } catch (MQException e) {
                }
                queue = null;
            }
            if (mqQueueManager != null) {
                try {
                    mqQueueManager.disconnect();
                } catch (MQException e) {
                }
                mqQueueManager = null;
            }
        }
    }

    /**
     * 系統每隔設定的時間啟動接收票交所TPing請求，透過IBM MQ API，建立MQ連結， 並開啟GatewayContext中instance中各Queue Name屬性指向的Queue，接收於Queue中的訊息，並向票交所回覆TPing訊息，讓票交所確認連線狀態。
     * @param mqQueueManager       
     * @throws GatewayException
     *             若處理過程中有異常發生時拋出
     */
    private void doPing(MQQueueManager mqQueueManager) throws GatewayException {

        MQRequestChannelHandler<String, byte[], String> mqReqChannel = new BOTMQRequestChannelHandler();
        RequestMessageHandler<?, ServiceSessionMsg> reqCSMsgHandler = new BOTConnServiceRequestMessageHandler();
        ResponseMessageHandler<?, String> respCSMsgHandler = new BOTConnServiceResponseMessageHandler();

        MQInstance instance = IBMWebSphereMQUtils.TCHInstance;
        mqReqChannel.setInstanceId("TCH");

        MQQueue queue = null;
        MQMessage retrievedMessage = null;
        MQGetMessageOptions gmo = new MQGetMessageOptions();

        try {

            int openOptions = CMQC.MQOO_BROWSE | CMQC.MQOO_INQUIRE | CMQC.MQOO_INPUT_SHARED;
            queue = mqQueueManager.accessQueue(instance.getInboundQueue(), openOptions);

            ServiceSessionMsg data = null;
            byte[] content = null;
            while (true) {
                try {
                    retrievedMessage = new MQMessage();

                    gmo.options = CMQC.MQGMO_NO_WAIT | CMQC.MQGMO_BROWSE_NEXT; // | CMQC.MQGMO_LOCK
                    queue.get(retrievedMessage, gmo);

                    content = new byte[retrievedMessage.getDataLength()];
                    retrievedMessage.readFully(content);

                    if (IBMWebSphereMQUtils.getFilter("BOTTpingMQMessageFilter").hit(new String(content, instance.getEncoding()))) {

                        data = (ServiceSessionMsg) respCSMsgHandler.parse(new String(content, instance.getEncoding()), null, "T001", "System");

                        gmo.options = CMQC.MQGMO_MSG_UNDER_CURSOR;
                        queue.get(retrievedMessage, gmo);

                        data.setTrxDate(MessageUtils.getDay());
                        data.setTrxTime(MessageUtils.getTime());

                        if (data != null) {
                            mqReqChannel.send(mqQueueManager, (String) reqCSMsgHandler.format(data, true), MessageUtils.getMessageId(20).getBytes(), "System");

                            if (ContextLoader.getGatewayContext().isLogFlag()) {
                                GatewayContext.logger.info("系統TPing回應成功");
                            }
                        }
                    } else {
                        if (queue.getCurrentDepth() == 1) {
                            break;
                        }
                    }
                } catch (MQException e) {
                    boolean isMQError2009 = e.completionCode == 2 && e.reasonCode == CMQC.MQRC_CONNECTION_BROKEN;
                    boolean isMQError2033 = e.completionCode == 2 && e.reasonCode == CMQC.MQRC_NO_MSG_AVAILABLE;
                    if (!isMQError2009 && !isMQError2033) {
                        if (logger.isErrorEnabled()) {
                            logger.error("系統服務發生錯誤：" + e.reasonCode + " " + e.getMessage());
                        }
                    } else {
                        break;
                    }
                } catch (Exception e) {
                    if (logger.isDebugEnabled()) {
                        logger.debug(e.getMessage());
                    }
                    break;
                } finally {
                    content = null;
                }
            }
        } catch (Exception e1) {
            if (logger.isDebugEnabled()) {
                logger.debug(e1.getMessage());
            }
        } finally {
            if (queue != null) {
                try {
                    queue.close();
                } catch (MQException e) {
                }
                queue = null;
            }

            mqReqChannel = null;
            reqCSMsgHandler = null;
            respCSMsgHandler = null;
        }
    }
}
