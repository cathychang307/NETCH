package com.bot.cqs.gateway.handler.channel.impl;

import java.io.IOException;
import java.util.Properties;

import com.bot.cqs.gateway.util.BOTGatewayServiceUtil;
import com.iisigroup.cap.utils.CapString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.bot.cqs.gateway.context.ContextLoader;
import com.bot.cqs.gateway.context.GatewayContext;
import com.bot.cqs.gateway.context.MQInstance;
import com.bot.cqs.gateway.handler.channel.MQRequestChannelHandler;
import com.bot.cqs.gateway.service.GatewayException;
import com.ibm.mq.MQException;
import com.ibm.mq.MQMessage;
import com.ibm.mq.MQPutMessageOptions;
import com.ibm.mq.MQQueue;
import com.ibm.mq.MQQueueManager;
import com.ibm.mq.constants.CMQC;

import tw.com.iisi.common.message.format.util.MessageUtils;

/**
 * 此類別主要用於建立與MQ之間的通訊通道，將請求訊息置放於instance屬性內指向的Queue中。
 *
 * @author Jeff Tseng
 * @see MQRequestChannelHandler
 * @see "IBM MQ 開發指南"
 * @since 1.0 2007/08/31
 *
 *        2020/3/22 因MQC已於MQ7宣告deprecated, 所以將MQC換成CMQC
 */
public class BOTMQRequestChannelHandler extends MQRequestChannelHandler<String, byte[], String> {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    private MQInstance instance = null;
    private String instanceId = null;

    /**
     * 接收傳入的請求訊息，透過IBM MQ API，建立MQ連結，並開啟instance屬性內指向的Queue，將請求訊息置放於Queue中。
     *
     * @param mqQueueManager
     *            MQQueueManager
     * @param queryObject
     *            使用者輸入之查詢條件
     * @param msgId
     *            MQ訊息代碼
     * @param corId
     *            MQ關連訊息代碼
     * @param replyToQueueName
     *            對應來源訊息的replyToQueueName參數值
     * @param account
     *            查詢者帳號
     * @throws GatewayException
     *             若處理過程中有異常發生時拋出
     */
    public void send(MQQueueManager mqQueueManager, String queryObject, byte[] msgId, byte[] corId, String replyToQueueName, String account)
            throws GatewayException {

        if("EAI".equalsIgnoreCase(instanceId)) {
            instance = IBMWebSphereMQUtils.EAIInstance;
        }else if("Dispatch".equalsIgnoreCase(instanceId)) {
            instance = IBMWebSphereMQUtils.DispatchInstance;
        }else {
            instance = IBMWebSphereMQUtils.TCHInstance;
        }

        MQQueue queue = null;

        try {
            MQMessage uploadMessage = new MQMessage();
            uploadMessage.format = CMQC.MQFMT_STRING;
            uploadMessage.characterSet = Integer.parseInt(instance.getCcsid());
            uploadMessage.replyToQueueManagerName = instance.getQueueManager();
            uploadMessage.expiry = instance.getExpiryTime();
            uploadMessage.messageId = msgId;
            uploadMessage.correlationId = corId;

            String outboundQueue = !CapString.isEmpty(instance.getOutboundQueue()) ? instance.getOutboundQueue() : "";
            if (!MessageUtils.nil(replyToQueueName) && !MessageUtils.empty(replyToQueueName)) {
                outboundQueue = replyToQueueName;
                uploadMessage.replyToQueueName = replyToQueueName;
                if (ContextLoader.getGatewayContext().isLogFlag())
                    GatewayContext.logger.debug(String.format("[%s][MsgId_%s] Send replyToQueueName: %s.", instanceId, BOTGatewayServiceUtil.msgId2Hex(msgId), replyToQueueName));
            }

            int openOptions = CMQC.MQOO_OUTPUT | CMQC.MQOO_FAIL_IF_QUIESCING | CMQC.MQOO_BIND_AS_Q_DEF;
            queue = mqQueueManager.accessQueue(outboundQueue, openOptions);

            byte[] content = (queryObject).getBytes(IBMWebSphereMQUtils.getCCSIDEncodingMap(instance.getCcsid()));

            uploadMessage.write(content);

            MQPutMessageOptions pmo = new MQPutMessageOptions();
            queue.put(uploadMessage, pmo);
            //mqQueueManager.commit();// 提交事務處理
            if (ContextLoader.getGatewayContext().isLogFlag()) {
                GatewayContext.logger.debug(String
                        .format("[%s][MsgId_%s] Send 查詢字串: %s, OutboundQueue: %s, putDateTime: %s.", instanceId, BOTGatewayServiceUtil.msgId2Hex(msgId), queryObject, outboundQueue,
                                uploadMessage.putDateTime));
            }
        } catch (MQException e) {
            boolean isMQError2009 = e.completionCode == 2 && e.reasonCode == CMQC.MQRC_CONNECTION_BROKEN;
            boolean isMQError2033 = e.completionCode == 2 && e.reasonCode == CMQC.MQRC_NO_MSG_AVAILABLE;
            if (!isMQError2009 && !isMQError2033) {
                if (ContextLoader.getGatewayContext().isLogFlag())
                    GatewayContext.logger.error("[Send] MQException:" + e);
                if (logger.isDebugEnabled()) {
                    logger.debug(e.getMessage());
                }
                throw new GatewayException("MG999", new String[] { "Send MQException:" + e.getMessage() });
            }
        } catch (Exception e) {
            if (logger.isDebugEnabled()) {
                logger.debug(e.getMessage());
            }
            throw new GatewayException("MG999", new String[] { "Send IOException:" + e.getMessage() });
        } finally {
            if (queue != null) {
                try {
                    queue.close();
                } catch (MQException e) {
                }
                queue = null;
            }
        }
    }

    @Override
    public void send(MQQueueManager mqQueueManager, String queryObject, byte[] msgId, String account) throws GatewayException {
        this.send(mqQueueManager,queryObject, msgId, msgId, account);
    }

    @Override
    public void send(MQQueueManager mqQueueManager, String queryObject, byte[] msgId, byte[] corId, String account) throws GatewayException {
        this.send(mqQueueManager, queryObject, msgId, msgId, null, account);
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }



}