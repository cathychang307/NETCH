package com.bot.cqs.gateway.handler.channel.impl;

import com.bot.cqs.gateway.context.ContextLoader;
import com.bot.cqs.gateway.context.GatewayContext;
import com.bot.cqs.gateway.context.MQInstance;
import com.bot.cqs.gateway.handler.channel.MQRequestChannelHandler;
import com.bot.cqs.gateway.handler.channel.MQResponseChannelHandler;
import com.bot.cqs.gateway.service.GatewayException;
import com.bot.cqs.gateway.util.BOTGatewayServiceUtil;
import com.ibm.mq.*;
import com.ibm.mq.constants.CMQC;
import com.iisigroup.cap.utils.CapString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tw.com.iisi.common.message.format.util.MessageUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 此類別主要用於建立與MQ之間的通訊通道，由instance屬性內指向的Queue中取出或瀏覽回應訊息。
 *
 * @author Jeff Tseng
 * @see MQRequestChannelHandler
 * @see "IBM MQ 開發指南"
 * @since 1.0 2007/08/31
 * 
 *        2020/3/22 因MQC以及MQException部分常數定義已於MQ7宣告deprecated, 所以將MQC及MQException部分常數定義換成CMQC
 */
public class BOTMQResponseChannelHandler extends MQResponseChannelHandler<String, byte[], String, Map> {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    private MQInstance instance = null;
    private String instanceId = null;

    /**
     * 透過IBM MQ API，建立MQ連結，並開啟instance屬性指向的Queue，接收於Queue中的訊息，並以corrId比對是否為正確接收的訊息。
     *
     * @param mqQueueManager
     *            MQQueueManager
     * @param corrId
     *            MQ相關連訊息代碼
     * @param account
     *            查詢者帳號
     * @return 回傳之回應訊息
     * @throws GatewayException
     *             若處理過程中有異常發生時拋出
     */
    public String receive(MQQueueManager mqQueueManager, byte[] corrId, String account) throws GatewayException {
        return receive(mqQueueManager, null, corrId, account);
    }

    /**
     * 透過IBM MQ API，建立MQ連結，並開啟instance屬性指向的Queue，接收於Queue中的訊息，並以msgId、corrId比對是否為正確接收的訊息。
     *
     * @param mqQueueManager
     *            MQQueueManager
     * @param msgId
     *            MQ訊息代碼
     * @param corrId
     *            MQ相關連訊息代碼
     * @param account
     *            查詢者帳號
     * @return 回傳之回應訊息
     * @throws GatewayException
     *             若處理過程中有異常發生時拋出
     */
    public String receive(MQQueueManager mqQueueManager, byte[] msgId, byte[] corrId, String account) throws GatewayException {
        if("EAI".equalsIgnoreCase(instanceId)) {
            instance = IBMWebSphereMQUtils.EAIInstance;
        }else if("Dispatch".equalsIgnoreCase(instanceId)) {
            instance = IBMWebSphereMQUtils.DispatchInstance;
        }else {
            instance = IBMWebSphereMQUtils.TCHInstance;
        }

        byte[] content = null;
        String result = null;
        MQQueue queue = null;
        int waitLoop = 0;
        int maxLoop = instance.getTimeout() / 6000; // 每6秒一個迴圈, 例如180秒timeout 則會loop 30次

        while (waitLoop <= maxLoop) {
        try {
            int openOptions = CMQC.MQOO_INPUT_AS_Q_DEF | CMQC.MQOO_FAIL_IF_QUIESCING | CMQC.MQOO_INQUIRE;
            queue = mqQueueManager.accessQueue(instance.getInboundQueue(), openOptions);
            MQMessage retrievedMessage = new MQMessage();// 取得MQ資訊
            MQGetMessageOptions gmo = new MQGetMessageOptions();
            //gmo.options = gmo.options + CMQC.MQGMO_SYNCPOINT; // 在同步點控制下獲取訊息
            gmo.options = gmo.options + CMQC.MQGMO_FAIL_IF_QUIESCING; // 如果佇列管理器停頓則失敗
            if (corrId != null || msgId != null) {
                if (msgId != null)
                    retrievedMessage.messageId = corrId;
                if (corrId != null)
                    retrievedMessage.correlationId = corrId;
                gmo.options = gmo.options + CMQC.MQGMO_WAIT; // 如果在佇列上沒有訊息則等待
                //gmo.options = CMQC.MQGMO_WAIT | CMQC.MQGMO_FAIL_IF_QUIESCING;
                gmo.waitInterval = 5 * 1000; // 5秒
            } else {
                gmo.options = gmo.options + CMQC.MQGMO_NO_WAIT; // 如果在佇列上沒有訊息則不等待
                //gmo.options = CMQC.MQGMO_NO_WAIT | CMQC.MQGMO_FAIL_IF_QUIESCING;
                maxLoop=1; // 不等待只需run 一個loop. 
            }
            int depth = queue.getCurrentDepth();
            queue.get(retrievedMessage, gmo);
            if (ContextLoader.getGatewayContext().isLogFlag()) {
                GatewayContext.logger
                        .debug(String.format("[%s][MsgId_%s] %s Queue Depth: %s.", instanceId, BOTGatewayServiceUtil.msgId2Hex(retrievedMessage.messageId), instance.getInboundQueue(), depth));
            }
            //mqQueueManager.commit();// 提交事務處理
            if (corrId == null) {
                corrId = retrievedMessage.messageId;
            }
            content = new byte[retrievedMessage.getDataLength()];
            retrievedMessage.readFully(content);
            result = new String(content, instance.getEncoding());
            if (ContextLoader.getGatewayContext().isLogFlag()) {
                GatewayContext.logger.debug(String.format("[%s][MsgId_%s] Receive InboundQueue: %s, RetrievedMessage result: %s, putDateTime: %s.", instanceId,
                        BOTGatewayServiceUtil.msgId2Hex(retrievedMessage.messageId), instance.getInboundQueue(), result, retrievedMessage.putDateTime));
            }
        } catch (MQException e) {
            boolean isMQError2009 = e.completionCode == 2 && e.reasonCode == CMQC.MQRC_CONNECTION_BROKEN;
            boolean isMQError2033 = e.completionCode == 2 && e.reasonCode == CMQC.MQRC_NO_MSG_AVAILABLE;
            waitLoop ++;
            if (e.reasonCode == CMQC.MQRC_NO_MSG_AVAILABLE) { // 5秒逾時回應代碼 2033
            	if (waitLoop >= maxLoop) {
            		throw new GatewayException("MG933", new String[] { "Receive:"+ e.getMessage() });
            	}
            } else if (!isMQError2009 && !isMQError2033) {
                if (ContextLoader.getGatewayContext().isLogFlag())
                    GatewayContext.logger.error("[Receive_1] MQException:" + e);
                throw new GatewayException("MG999", new String[] { "Receive MQException:" + e.getMessage() });
            }
        } catch (Exception e) {
            throw new GatewayException("MG999", new String[] { "Receive Exception:"+ e.getMessage() });
        } finally {
            if (queue != null) {
                try {
                    queue.close();
                } catch (MQException e) {
                }
                queue = null;
            }
        }
        if (result != null) {
        	break;
        }
        if (waitLoop < maxLoop) {
        	try {
        		Thread.sleep(1000);
        	} catch (InterruptedException e1) {
        		//e1.printStackTrace();
				break;
        	}
        } 
        result = null;
        content = null;
        }
        return result;
    }

    /**
     * 透過IBM MQ API，建立MQ連結，並開啟instance屬性指向的Queue，接收於Queue中的訊息。
     *
     * @param mqQueueManager
     *            MQQueueManager
     * @param account
     *            查詢者帳號
     * @return 回傳之回應訊息（data）及該訊息之訊息產生時間（putDateTime）及訊息關連代碼（corrId）
     * @throws GatewayException
     *             若處理過程中有異常發生時拋出
     */
    @Override
    public Map receive(MQQueueManager mqQueueManager, String account) throws GatewayException {
        if("EAI".equalsIgnoreCase(instanceId)) {
            instance = IBMWebSphereMQUtils.EAIInstance;
        }else if("Dispatch".equalsIgnoreCase(instanceId)) {
            instance = IBMWebSphereMQUtils.DispatchInstance;
        }else {
            instance = IBMWebSphereMQUtils.TCHInstance;
        }

        byte[] content = null;
        Map<String, Object> result = null;
        String data = null;
        MQQueue queue = null;

        try {
            int openOptions = CMQC.MQOO_INPUT_AS_Q_DEF | CMQC.MQOO_FAIL_IF_QUIESCING | CMQC.MQOO_INQUIRE;
            queue = mqQueueManager.accessQueue(instance.getInboundQueue(), openOptions);
            MQMessage retrievedMessage = new MQMessage();// 取得MQ資訊
            MQGetMessageOptions gmo = new MQGetMessageOptions();
            //gmo.options = gmo.options + CMQC.MQGMO_SYNCPOINT; // 在同步點控制下獲取訊息
            gmo.options = gmo.options + CMQC.MQGMO_FAIL_IF_QUIESCING; // 如果佇列管理器停頓則失敗
//            if ("EAI".equalsIgnoreCase(account)) {
                gmo.options = gmo.options + CMQC.MQGMO_NO_WAIT; // 如果在佇列上沒有訊息則不等待
//            } else {
//                gmo.options = gmo.options + CMQC.MQGMO_WAIT; // 如果在佇列上沒有訊息則等待
//                gmo.waitInterval = instance.getTimeout();
//            }
            //gmo.options = CMQC.MQGMO_NO_WAIT | CMQC.MQGMO_FAIL_IF_QUIESCING;
            int depth = queue.getCurrentDepth();
            if (depth > 0) {
                queue.get(retrievedMessage, gmo);
                if (ContextLoader.getGatewayContext().isLogFlag()) {
                    GatewayContext.logger.debug(String
                            .format("[%s][MsgId_%s] Receive %s Queue Depth: %s.", instanceId, BOTGatewayServiceUtil.msgId2Hex(retrievedMessage.messageId), instance.getInboundQueue(), depth));
                }
                //mqQueueManager.commit();// 提交事務處理
                //送回給EAI Queue
                String replyToQueueName = retrievedMessage.replyToQueueName != null ? retrievedMessage.replyToQueueName : "";
                if (!CapString.isEmpty(replyToQueueName) && "EAI".equals(instanceId)) {
                    result = result == null ? new HashMap<String, Object>() : result;
                    result.put("replyToQueueName", replyToQueueName);
                    if (ContextLoader.getGatewayContext().isLogFlag())
                        GatewayContext.logger
                                .debug(String.format("[%s][MsgId_%s] Receive EAI MQ replyToQueueName: %s.", instanceId, BOTGatewayServiceUtil.msgId2Hex(retrievedMessage.messageId), replyToQueueName));
                }
                content = new byte[retrievedMessage.getDataLength()];
                retrievedMessage.readFully(content);
                data = new String(content, instance.getEncoding());
                if (!MessageUtils.nil(data) && !MessageUtils.empty(data)) {
                    result = result == null ? new HashMap<String, Object>() : result;
                    result.put("data", data);
                    result.put("msgId", retrievedMessage.messageId);
                    result.put("corrId", retrievedMessage.messageId);
                    result.put("putDateTime", retrievedMessage.putDateTime);
                }
                if (ContextLoader.getGatewayContext().isLogFlag() && queue.getCurrentDepth() > 0) {
                    GatewayContext.logger.debug(String
                            .format("[%s][MsgId_%s] Receive InboundQueue: %s, RetrievedMessage data: %s, putDateTime: %s.", instanceId, BOTGatewayServiceUtil.msgId2Hex(retrievedMessage.messageId),
                                    instance.getInboundQueue(), data, retrievedMessage.putDateTime));
                }
            }
        } catch (MQException e) {
            boolean isMQError2009 = e.completionCode == 2 && e.reasonCode == CMQC.MQRC_CONNECTION_BROKEN;
            boolean isMQError2033 = e.completionCode == 2 && e.reasonCode == CMQC.MQRC_NO_MSG_AVAILABLE;
            if (e.reasonCode == CMQC.MQRC_NO_MSG_AVAILABLE) {
                throw new GatewayException("MG933", new String[] { "Receive:" + e.getMessage() });
            } else if (!isMQError2009 && !isMQError2033) {
                if (ContextLoader.getGatewayContext().isLogFlag())
                    GatewayContext.logger.error("[Receive_2] MQException:" + e);
                throw new GatewayException("MG999", new String[] { "Receive MQException:" + e.getMessage() });
            }
        } catch (Exception e) {
            throw new GatewayException("MG999", new String[] { "Receive Exception:" + e.getMessage() });
        } finally {
            if (queue != null) {
                try {
                    queue.close();
                } catch (MQException e) {
                }
                queue = null;
            }
            content = null;
        }
        return result;
    }

    /**
     * 透過IBM MQ API，建立MQ連結，並開啟instance屬性指向的Queue，瀏覽於Queue中指定corrId的訊息。
     *
     * @param mqQueueManager
     *            MQQueueManager
     * @param corrId
     *            MQ相關連訊息代碼
     * @param account
     *            查詢者帳號
     * @param filter
     *            訊息過濾程式名稱
     * @return 回傳之回應訊息
     * @throws GatewayException
     *             若處理過程中有異常發生時拋出
     */
    @Override
    public String browse(MQQueueManager mqQueueManager, byte[] corrId, String account, String filter) throws GatewayException {
        byte[] content = null;
        String result = null;
        if("EAI".equalsIgnoreCase(instanceId)) {
            instance = IBMWebSphereMQUtils.EAIInstance;
        }else if("Dispatch".equalsIgnoreCase(instanceId)) {
            instance = IBMWebSphereMQUtils.DispatchInstance;
        }else {
            instance = IBMWebSphereMQUtils.TCHInstance;
        }

        MQQueue queue = null;
        MQMessage retrievedMessage = null;
        MQGetMessageOptions gmo = new MQGetMessageOptions();
        try {

            do {
                int openOptions = CMQC.MQOO_BROWSE | CMQC.MQOO_INQUIRE | CMQC.MQOO_INPUT_SHARED;
                queue = mqQueueManager.accessQueue(instance.getInboundQueue(), openOptions);

                retrievedMessage = new MQMessage();
                gmo.options = CMQC.MQGMO_NO_WAIT | CMQC.MQGMO_BROWSE_NEXT; // CMQC.MQGMO_LOCK |
                queue.get(retrievedMessage, gmo);
                if (ContextLoader.getGatewayContext().isLogFlag())
                    GatewayContext.logger.debug(String.format("[MsgId_%s] Browse InboundQueue: %s, instanceId: %s.", BOTGatewayServiceUtil.msgId2Hex(retrievedMessage.messageId), instance.getInboundQueue(), instanceId));
                content = new byte[retrievedMessage.getDataLength()];
                retrievedMessage.readFully(content);
                result = new String(content, instance.getEncoding());
                corrId = retrievedMessage.messageId;
                if (ContextLoader.getGatewayContext().isLogFlag())
                    GatewayContext.logger.debug(String.format("[MsgId_%s] Browse RetrievedMessage result: %s, instanceId: %s.", BOTGatewayServiceUtil.msgId2Hex(retrievedMessage.messageId), result, instanceId));
            } while ((filter == null) ? false : !IBMWebSphereMQUtils.getFilter(filter).hit(result));
        } catch (MQException e) {
            if (e.reasonCode != CMQC.MQRC_NO_MSG_AVAILABLE && e.reasonCode != CMQC.MQRC_HANDLE_NOT_AVAILABLE) {
                throw new GatewayException("MG996", new String[] { "Browse:" + e.getMessage() });
            } else if (e.reasonCode == CMQC.MQRC_HANDLE_NOT_AVAILABLE) {
                if (logger.isDebugEnabled()) {
                    logger.debug(e.getMessage());
                }
                throw new GatewayException("MG990", new String[] { "Browse:" + e.getMessage() });
            } else {
                throw new GatewayException("MG933", new String[] { "Browse:" + e.getMessage() });
            }
        } catch (Exception e) {
            throw new GatewayException("MG999", new String[] { "Browse:" + e.getMessage() });
        } finally {
            if (queue != null) {
                try {
                    queue.close();
                } catch (MQException e) {
                }
                queue = null;
            }
            content = null;
        }
        return result;
    }

    /**
     * 透過IBM MQ API，建立MQ連結，並開啟instance屬性指向的Queue，瀏覽於Queue中的訊息。
     *
     * @param mqQueueManager
     *            MQQueueManager
     * @param account
     *            查詢者帳號
     * @param filter
     *            訊息過濾程式名稱
     * @return 回傳之回應訊息（data）、訊息代碼（msgId）及訊息關連代碼（corrId）
     * @throws GatewayException
     *             若處理過程中有異常發生時拋出
     */
    @Override
    public Map browse(MQQueueManager mqQueueManager, String account, String filter) throws GatewayException {
        byte[] content = null;
        byte[] msgId = null;
        byte[] corrId = null;
        String data = null;
        Map<String, Object> result = null;

        if("EAI".equalsIgnoreCase(instanceId)) {
            instance = IBMWebSphereMQUtils.EAIInstance;
        }else if("Dispatch".equalsIgnoreCase(instanceId)) {
            instance = IBMWebSphereMQUtils.DispatchInstance;
        }else {
            instance = IBMWebSphereMQUtils.TCHInstance;
        }

        MQQueue queue = null;
        MQMessage retrievedMessage = null;
        MQGetMessageOptions gmo = new MQGetMessageOptions();
        try {

            do {
                int openOptions = CMQC.MQOO_BROWSE | CMQC.MQOO_INQUIRE | CMQC.MQOO_INPUT_SHARED;

                queue = mqQueueManager.accessQueue(instance.getInboundQueue(), openOptions);

                retrievedMessage = new MQMessage();
                gmo.options = CMQC.MQGMO_NO_WAIT | CMQC.MQGMO_BROWSE_NEXT; // CMQC.MQGMO_LOCK |
                queue.get(retrievedMessage, gmo);
                if (ContextLoader.getGatewayContext().isLogFlag())
                    GatewayContext.logger.debug(String.format("[MsgId_%s] Browse InboundQueue: %s, instanceId: %s.", BOTGatewayServiceUtil.msgId2Hex(retrievedMessage.messageId), instance.getInboundQueue(), instanceId));

                content = new byte[retrievedMessage.getDataLength()];
                retrievedMessage.readFully(content);

                data = new String(content, instance.getEncoding());
                msgId = retrievedMessage.messageId;
                corrId = retrievedMessage.correlationId;
                if (ContextLoader.getGatewayContext().isLogFlag())
                    GatewayContext.logger.debug(String.format("[MsgId_%s] Browse retrievedMessage correlationId: %s, instanceId: %s.", BOTGatewayServiceUtil.msgId2Hex(retrievedMessage.messageId), retrievedMessage.correlationId != null ? new String(retrievedMessage.correlationId) : "null", instanceId));
            } while ((filter == null) ? false : !IBMWebSphereMQUtils.getFilter(filter).hit(data));

            if (!MessageUtils.nil(data) && !MessageUtils.empty(data)) {
                result = new HashMap<String, Object>();
                result.put("data", data);
                result.put("msgId", msgId);
                result.put("corrId", corrId);
                if (ContextLoader.getGatewayContext().isLogFlag())
                    GatewayContext.logger.debug(String.format("[MsgId_%s] Browse RetrievedMessage result: %s, instanceId: %s.", BOTGatewayServiceUtil.msgId2Hex(retrievedMessage.messageId), result, instanceId));
            }
        } catch (MQException e) {
            if (e.reasonCode != CMQC.MQRC_NO_MSG_AVAILABLE && e.reasonCode != CMQC.MQRC_HANDLE_NOT_AVAILABLE) {
                throw new GatewayException("MG996", new String[] { "Browse:" +e.getMessage() });
            } else if (e.reasonCode == CMQC.MQRC_HANDLE_NOT_AVAILABLE) {
                if (logger.isDebugEnabled()) {
                    logger.debug(e.getMessage());
                }
                throw new GatewayException("MG990", new String[] { "Browse:" + e.getMessage() });
            } else {
                throw new GatewayException("MG933", new String[] { "Browse:" +e.getMessage() });
            }
        } catch (Exception e) {
            throw new GatewayException("MG999", new String[] { "Browse:" +e.getMessage() });
        } finally {
            if (queue != null) {
                try {
                    queue.close();
                } catch (MQException e) {
                }
                queue = null;
            }
            content = null;
        }
        return result;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }
}
