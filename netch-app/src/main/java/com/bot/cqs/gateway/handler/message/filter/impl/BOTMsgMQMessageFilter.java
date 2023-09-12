package com.bot.cqs.gateway.handler.message.filter.impl;

import com.bot.cqs.gateway.handler.message.filter.MQMessageFilter;

/**
 * 此類別主要用於MQ訊息過濾，由交易類別代碼判斷是否為票交所傳送過來的有效電文。
 *
 * @author Jeff Tseng
 * @since 1.0 2007/08/31
 */
public class BOTMsgMQMessageFilter implements MQMessageFilter {

    /**
     * 接受MQ訊息並判斷是否為應接收的訊息。
     * 2020/03/22 增加OBU四類訊息: 4132, 4133, 4135, 4136
     * 
     * @param data
     *            MQ訊息
     * @return 是否為應接收的訊息
     */
    public boolean hit(String data) {
        String trxCode = null;
        try {
            if (data == null)
                return false;

            trxCode = data.substring(21, 25);
            if ("L001".equals(trxCode) || "L003".equals(trxCode) || "S001".equals(trxCode) || "S005".equals(trxCode) || "T001".equals(trxCode) || "4111".equals(trxCode) || "4112".equals(trxCode)
                    || "4113".equals(trxCode) || "4114".equals(trxCode) || "4115".equals(trxCode) || "4116".equals(trxCode) || "4121".equals(trxCode) || "4122".equals(trxCode)
                    || "4123".equals(trxCode) || "4124".equals(trxCode) || "4132".equals(trxCode) || "4133".equals(trxCode) || "4135".equals(trxCode) || "4136".equals(trxCode)) {
                return true;
            } else {
                return false;
            }
        } catch (Exception ex) {
            return false;
        }
    }
}
