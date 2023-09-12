package com.bot.cqs.gateway.context;

import java.util.Map;

/**
 * 此類別主要用於存放應用程式使用之MQ的設定值。
 *
 * @author Jeff Tseng
 * @since 1.0 2007/08/31
 */
public class IBMWebSphereMQ {

    /**
     * Thread Pool的最大限定值。
     */
    private int poolMaxSize;
    /**
     * Thread Pool未使用中大小的最大限定值。
     */
    private int unusedMaxSize;
    /**
     * 各MQ連結設定資訊群組。
     */
    private Map<String, MQInstance> instances;

    public int getPoolMaxSize() {
        return poolMaxSize;
    }

    public void setPoolMaxSize(int poolMaxSize) {
        this.poolMaxSize = poolMaxSize;
    }

    public int getUnusedMaxSize() {
        return unusedMaxSize;
    }

    public void setUnusedMaxSize(int unusedMaxSize) {
        this.unusedMaxSize = unusedMaxSize;
    }

    public Map getInstances() {
        return instances;
    }

    public void setInstances(Map<String, MQInstance> instances) {
        this.instances = instances;
    }
}
