package com.bot.cqs.gateway.handler.message.filter.impl;

import com.bot.cqs.gateway.handler.message.filter.MQMessageFilter;

/**
 * 此類別主要用於MQ訊息過濾，由交易類別代碼判斷是否為票交所傳送過來的TPing電文。
 *
 * @author Jeff Tseng
 * @since 1.0 2007/08/31
 */
public class BOTTpingMQMessageFilter implements MQMessageFilter {

    /**
     * 接受MQ訊息並判斷是否為應接收的訊息。
     * 
     * @param data
     *            MQ訊息
     * @return 是否為應接收的訊息
     */
    public boolean hit(String data) {
        try {
            if (data == null)
                return false;

            if ("T001".equals(data.substring(21, 25))) {
                return true;
            } else {
                return false;
            }
        } catch (Exception ex) {
            return false;
        }
    }
}
