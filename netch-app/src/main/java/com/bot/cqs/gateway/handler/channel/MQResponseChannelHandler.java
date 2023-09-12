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
public abstract class MQResponseChannelHandler<T, U, S, V> {

    private MQInstance instance = null;
    private String instanceId = null;
    private MQQueueManager mqQueueManager;

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
    
    /**
     * 接收MQ回傳的回覆訊息，將回覆訊息由Queue中取出。
     * 
     * @param account
     *            查詢者帳號
     * @return 回傳的回應訊息
     * @throws GatewayException
     *             若處理過程中有異常發生時拋出
     */
    public abstract V receive(MQQueueManager mqQueueManager, S account) throws GatewayException;

    /**
     * 接收MQ回傳的回覆訊息，將回覆訊息由Queue中取出。
     *
     * @param mqQueueManager
     *            MQQueueManager
     * @param corrId
     *            MQ相關連訊息代碼
     * @param account
     *            查詢者帳號
     * @return 回傳的回應訊息
     * @throws GatewayException
     *             若處理過程中有異常發生時拋出
     */
    public abstract T receive(MQQueueManager mqQueueManager, U corrId, S account) throws GatewayException;

    /**
     * 接收MQ回傳的回覆訊息，將回覆訊息由Queue中取出。
     *
     * @param mqQueueManager
     *            MQQueueManager
     * @param msgId
     *            MQ訊息代碼
     * @param corrId
     *            MQ相關連訊息代碼
     * @param account
     *            查詢者帳號
     * @return 回傳的回應訊息
     * @throws GatewayException
     *             若處理過程中有異常發生時拋出
     */
    public abstract T receive(MQQueueManager mqQueueManager, U msgId, U corrId, S account) throws GatewayException;

    /**
     * 接收MQ回傳的回覆訊息，將回覆訊息由Queue先瀏覽，若符合條件則取出。
     *
     * @param mqQueueManager
     *            MQQueueManager
     * @param corrId
     *            MQ相關連訊息代碼
     * @param account
     *            查詢者帳號
     * @param filter
     *            訊息瀏覽檢核程式名稱
     * @return 回傳的回應訊息
     * @throws GatewayException
     *             若處理過程中有異常發生時拋出
     */
    public abstract T browse(MQQueueManager mqQueueManager, U corrId, S account, S filter) throws GatewayException;

    /**
     * 接收MQ回傳的回覆訊息，將回覆訊息由Queue先瀏覽，若符合條件則取出。
     *
     * @param mqQueueManager
     *            MQQueueManager
     * @param account
     *            查詢者帳號
     * @param filter
     *            訊息瀏覽檢核程式名稱
     * @return 回傳的回應訊息
     * @throws GatewayException
     *             若處理過程中有異常發生時拋出
     */
    public abstract V browse(MQQueueManager mqQueueManager, S account, S filter) throws GatewayException;
}
