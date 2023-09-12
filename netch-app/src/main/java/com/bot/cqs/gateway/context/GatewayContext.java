package com.bot.cqs.gateway.context;

import java.util.Map;

import org.slf4j.Logger;

/**
 * 此類別主要用於存放Gateway模組的所有設定值。
 *
 * @author Jeff Tseng
 * @since 1.0 2007/08/31
 */
public class GatewayContext {

    /**
     * Gateway程式的共通Logger。
     */
    public static Logger logger;

    /**
     * Thread Pool設定群。
     */
    private Map<String, ThreadPool> threadPools;
    /**
     * IBMWebSphereMQ物件。
     */
    private IBMWebSphereMQ ibmWebSphereMQ;
    /**
     * IBM MQ 異常訊息日誌路徑。若未設定，則以IBM MQ library內定設定為主。
     */
    private String logLocation;
    /**
     * 是否啟用追蹤日誌。
     */
    private boolean logFlag;

    public String getLogLocation() {
        return logLocation;
    }

    public void setLogLocation(String logLocation) {
        this.logLocation = logLocation;
    }

    public IBMWebSphereMQ getIbmWebSphereMQ() {
        return ibmWebSphereMQ;
    }

    public void setIbmWebSphereMQ(IBMWebSphereMQ ibmWebSphereMQ) {
        this.ibmWebSphereMQ = ibmWebSphereMQ;
    }

    public Map getThreadPools() {
        return threadPools;
    }

    public void setThreadPools(Map<String, ThreadPool> threadPools) {
        this.threadPools = threadPools;
    }

    public boolean isLogFlag() {
        return logFlag;
    }

    public void setLogFlag(boolean logFlag) {
        this.logFlag = logFlag;
    }
}
