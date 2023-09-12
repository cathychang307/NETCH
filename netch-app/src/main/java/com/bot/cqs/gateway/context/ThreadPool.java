package com.bot.cqs.gateway.context;

/**
 * 此類別主要用於存放應用程式內部使用之Thread Pool的設定值。
 *
 * @author Jeff Tseng
 * @since 1.0 2007/08/31
 */
public class ThreadPool {

    /**
     * Thread Pool代碼。
     */
    private String id;
    /**
     * Thread Pool大小。
     */
    private int size;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
