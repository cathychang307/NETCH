package com.bot.cqs.gateway.service;

import com.bot.cqs.gateway.context.ContextLoader;
import com.bot.cqs.gateway.context.GatewayContext;
import com.bot.cqs.gateway.context.MQInstance;
import com.bot.cqs.gateway.handler.channel.impl.IBMWebSphereMQUtils;
import com.bot.cqs.gateway.util.BOTGatewayServiceUtil;
import com.ibm.mq.*;
import com.ibm.mq.constants.CMQC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.GregorianCalendar;

/**
 * 此類別為MQ佇列清除程式。因目前系統中對於每一個MQ訊息並無設定Expiration以及Dead Letter Queue，所以以應用程式方式處理超過系統逾時設定的訊息。
 * 
 * @author Jeff Tseng
 * @since 1.0 2007/08/31
 * 
 *        2020/3/22 因MQC以及MQException部分常數定義已於MQ7宣告deprecated, 所以將MQC及MQException部分常數定義換成CMQC
 */
public class BOTMQMessageCleaner {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 此方法首先對特定的佇列瀏覽每一個訊息，並檢查訊息的產生時間是否超過系統設定的逾時設定。若是，則以MQGET的方式取出訊息；反之不處理。
     * 
     * @param queueName
     *            特定的佇列名稱
     * @param instanceId
     *            特定的MQ設定代碼
     * @param timeout
     *            逾時設定秒數
     */
    public void clean(String queueName, String instanceId, int timeout) {
        MQInstance instance = null;
        if("EAI".equalsIgnoreCase(instanceId)) {
            instance = IBMWebSphereMQUtils.EAIInstance;
        }else if("Dispatch".equalsIgnoreCase(instanceId)) {
            instance = IBMWebSphereMQUtils.DispatchInstance;
        }else {
            instance = IBMWebSphereMQUtils.TCHInstance;
        }

        MQQueueManager mqQueueManager = null;
        MQQueue queue = null;
        MQMessage retrievedMessage = null;
        MQGetMessageOptions gmo = new MQGetMessageOptions();

        try {
            if("EAI".equalsIgnoreCase(instanceId)) {
                mqQueueManager = new MQQueueManager(instance.getQueueManager(), IBMWebSphereMQUtils.getMQProperies(IBMWebSphereMQUtils.EAI), IBMWebSphereMQUtils.BOTMQEAIConnMgr);

            }else if("Dispatch".equalsIgnoreCase(instanceId)) {
                mqQueueManager = new MQQueueManager(instance.getQueueManager(), IBMWebSphereMQUtils.getMQProperies(IBMWebSphereMQUtils.Dispatch), IBMWebSphereMQUtils.BOTMQTCHConnMgr);

            }else {
                mqQueueManager = new MQQueueManager(instance.getQueueManager(), IBMWebSphereMQUtils.getMQProperies(IBMWebSphereMQUtils.TCH), IBMWebSphereMQUtils.BOTMQTCHConnMgr);
            }
            int openOptions = CMQC.MQOO_BROWSE | CMQC.MQOO_INQUIRE | CMQC.MQOO_INPUT_SHARED;
            queue = mqQueueManager.accessQueue(queueName, openOptions);
            if (ContextLoader.getGatewayContext().isLogFlag())
                GatewayContext.logger.info("[佇列：" + queueName + "] 清除開始");
            while (true) {
                try {
                    retrievedMessage = new MQMessage();

                    gmo.options = CMQC.MQGMO_NO_WAIT | CMQC.MQGMO_BROWSE_NEXT; // | CMQC.MQGMO_LOCK
                    queue.get(retrievedMessage, gmo);

                    if (timeover(retrievedMessage.putDateTime, timeout)) {
                        gmo.options = CMQC.MQGMO_MSG_UNDER_CURSOR;
                        queue.get(retrievedMessage, gmo);
                        if (ContextLoader.getGatewayContext().isLogFlag())
                            GatewayContext.logger.info("[佇列：" + queueName + "] 清除訊息：" + BOTGatewayServiceUtil.msgId2Hex(retrievedMessage.messageId) + " 產生時間：" + (retrievedMessage.putDateTime).toString()+ " 已逾時" + timeout + "分鐘");
                    } else {
                        if (ContextLoader.getGatewayContext().isLogFlag())
                            GatewayContext.logger.info("[佇列：" + queueName + "] 訊息：" + BOTGatewayServiceUtil.msgId2Hex(retrievedMessage.messageId) + " 產生時間：" + (retrievedMessage.putDateTime).toString()+ " 未逾時" + timeout + "分鐘不清除");
                        if (queue.getCurrentDepth() == 1) {
                            break;
                        }
                    }
                } catch (MQException e) {
                    boolean isMQError2009 = e.completionCode == 2 && e.reasonCode == CMQC.MQRC_CONNECTION_BROKEN;
                    boolean isMQError2033 = e.completionCode == 2 && e.reasonCode == CMQC.MQRC_NO_MSG_AVAILABLE;
                    if (!isMQError2009 && !isMQError2033) {
                        if (ContextLoader.getGatewayContext().isLogFlag())
                            GatewayContext.logger.info("[佇列：" + queueName + "] 清除發生錯誤：" + e.reasonCode + " " + e.getMessage());
                    } else {
                        break;
                    }
                } catch (Exception e) {
                    if (logger.isDebugEnabled()) {
                        logger.debug(e.getMessage());
                    }
                    break;
                }
            }
            if (ContextLoader.getGatewayContext().isLogFlag())
                GatewayContext.logger.info("[佇列：" + queueName + "] 清除結束");
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
     * 檢查訊息產生時間是否超過逾時設定
     *
     * @param putDateTime
     *            訊息產生時間
     * @param timeout
     *            系統逾時設定
     * @return boolean
     */
    private boolean timeover(GregorianCalendar putDateTime, int timeout) {
        boolean flag = false;
        if (System.currentTimeMillis() - putDateTime.getTimeInMillis() > 0
                && (System.currentTimeMillis() - putDateTime.getTimeInMillis()) / (1000 * 60 * timeout) > 0){
            flag = true;
        }
        return flag;
    }
}
