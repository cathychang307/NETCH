package com.bot.cqs.gateway.handler.message.filter;

/**
 * 此界面主要用於MQ訊息過濾程式的主介面。
 *
 * @author Jeff Tseng
 * @since 1.0 2007/08/31
 */
public interface MQMessageFilter {

    /**
     * 接受MQ訊息並判斷是否為應接收的訊息。
     * 
     * @param data
     *            MQ訊息
     * @return 是否為應接收的訊息
     */
    public boolean hit(String data);
}
