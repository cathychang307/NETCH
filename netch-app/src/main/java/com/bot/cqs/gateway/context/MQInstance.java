package com.bot.cqs.gateway.context;

/**
 * 此類別主要用於存放Gateway模組中所使用到的MQ連結之設定值。
 *
 * @author Jeff Tseng
 * @since 1.0 2007/08/31
 */
public class MQInstance {

    /**
     * 等待訊息回覆時間設定，以秒為單位。
     */
    private int timeout;
    /**
     * MQ連結設定代碼。
     */
    private String id;
    /**
     * MQ Server host name or IP。
     */
    private String host;
    /**
     * MQ Server Listener port number。
     */
    private String port;
    /**
     * 佇列預設資料編碼代碼。
     */
    private String ccsid;
    /**
     * 訊息通道名稱（接收及傳送同一個時）。
     */
    private String channel;
    /**
     * MQ QM UserId。
     */
    private String userId;
    /**
     * MQ QM User P-ass W-ord。
     */
    private String psswwdd;
    /**
     * 訊息接收通道名稱。
     */
    private String inboundChannel;
    /**
     * 訊息傳送通道名稱。
     */
    private String outboundChannel;
    /**
     * 訊息接收佇列名稱。
     */
    private String inboundQueue;
    /**
     * 訊息傳送佇列名稱。
     */
    private String outboundQueue;
    /**
     * 佇列管理程式名稱。
     */
    private String queueManager;
    /**
     * 應用程式實際使用之資料編碼。
     */
    private String encoding;
    /**
     * 應用程式與MQ連結模式，有：client（應用程式利用MQ Client連結Server）及binding（應用程式與MQ Server同一主機上）兩種。
     */
    private String connMode;
    /**
     * 訊息過期時間
     */
    private int expiryTime;

    public String getConnMode() {
        return connMode;
    }

    public void setConnMode(String connMode) {
        this.connMode = connMode;
    }

    public String getCcsid() {
        return ccsid;
    }

    public void setCcsid(String ccsid) {
        this.ccsid = ccsid;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInboundChannel() {
        return inboundChannel;
    }

    public void setInboundChannel(String inboundChannel) {
        this.inboundChannel = inboundChannel;
    }

    public String getInboundQueue() {
        return inboundQueue;
    }

    public void setInboundQueue(String inboundQueue) {
        this.inboundQueue = inboundQueue;
    }

    public String getOutboundChannel() {
        return outboundChannel;
    }

    public void setOutboundChannel(String outboundChannel) {
        this.outboundChannel = outboundChannel;
    }

    public String getOutboundQueue() {
        return outboundQueue;
    }

    public void setOutboundQueue(String outboundQueue) {
        this.outboundQueue = outboundQueue;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getQueueManager() {
        return queueManager;
    }

    public void setQueueManager(String queueManager) {
        this.queueManager = queueManager;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getExpiryTime() {
        return expiryTime;
    }

    public void setExpiryTime(int expiryTime) {
        this.expiryTime = expiryTime;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPsswwdd() {
        return psswwdd;
    }

    public void setPsswwdd(String psswwdd) {
        this.psswwdd = psswwdd;
    }
}
