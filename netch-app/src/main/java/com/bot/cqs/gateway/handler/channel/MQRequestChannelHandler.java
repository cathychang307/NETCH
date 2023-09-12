package com.bot.cqs.gateway.handler.channel;

import com.bot.cqs.gateway.context.MQInstance;
import com.bot.cqs.gateway.service.GatewayException;
import com.ibm.mq.MQQueueManager;

/**
 * 此抽象類別主要用於初始取得建立MQ通道時，所有必須用到的參數。
 *
 * @author Jeff Tseng
 * @see "IBM MQ 開發指南"
 * @since 1.0 2007/03/07
 */
public abstract class MQRequestChannelHandler<T, V, S> {

    private MQInstance instance = null;
    private String instanceId = null;
    private MQQueueManager mqQueueManager = null;

    /**
     * 接收傳入的請求訊息，將請求訊息置放於Queue中。
     *
     * @param mqQueueManager
     *            MQQueueManager
     * @param queryObject
     *            使用者輸入之查詢條件
     * @param msgId
     *            MQ訊息代碼
     * @param account
     *            查詢者帳號
     * @throws GatewayException
     *             若處理過程中有異常發生時拋出
     */
    public abstract void send(MQQueueManager mqQueueManager, T queryObject, V msgId, S account) throws GatewayException;

    /**
     * 接收傳入的請求訊息，將請求訊息置放於Queue中。
     *
     * @param mqQueueManager
     *            MQQueueManager
     * @param queryObject
     *            使用者輸入之查詢條件
     * @param msgId
     *            MQ訊息代碼
     * @param corId
     *            MQ訊息相關訊息代碼
     * @param account
     *            查詢者帳號
     * @throws GatewayException
     *             若處理過程中有異常發生時拋出
     */
    public abstract void send(MQQueueManager mqQueueManager, T queryObject, V msgId, V corId, S account) throws GatewayException;

    /**
     * 接收傳入的請求訊息，將請求訊息置放於Queue中。
     *
     * @param mqQueueManager
     *            MQQueueManager
     * @param queryObject
     *            使用者輸入之查詢條件
     * @param msgId
     *            MQ訊息代碼
     * @param corId
     *            MQ訊息相關訊息代碼
     * @param replyToQueueName
     *            對應來源訊息的replyToQueueName參數值
     * @param account
     *            查詢者帳號
     * @throws GatewayException
     *             若處理過程中有異常發生時拋出
     */
    public abstract void send(MQQueueManager mqQueueManager, T queryObject, V msgId, V corId, S replyToQueueName, S account) throws GatewayException;

    public MQInstance getInstance() {
        return instance;
    }

    public void setInstance(MQInstance instance) {
        this.instance = instance;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }
    
}
