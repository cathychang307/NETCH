
package com.bot.cqs.query.util;

import com.bot.cqs.gateway.persistence.InquiryLog;

import tw.com.iisi.common.message.format.util.MessageUtils;

public class QueryCacheUtil {

    public static String formatCacheKey(InquiryLog inquiryLog) {
        String key = null;
        StringBuffer buf = new StringBuffer();

        if (MessageUtils.nil(inquiryLog.getInquiryTxCode())) {
            buf.append(" ");
        } else {
            buf.append(inquiryLog.getInquiryTxCode());
        }

        if (MessageUtils.nil(inquiryLog.getInquiryDate())) {
            buf.append(" ");
        } else {
            buf.append(inquiryLog.getInquiryDate());
        }

        if (MessageUtils.nil(inquiryLog.getInquiryId())) {
            buf.append(" ");
        } else {
            buf.append(inquiryLog.getInquiryId());
        }

        if (MessageUtils.nil(inquiryLog.getInquiryBizId())) {
            buf.append(" ");
        } else {
            buf.append(inquiryLog.getInquiryBizId());
        }

        if (MessageUtils.nil(inquiryLog.getInquiryBankCode())) {
            buf.append(" ");
        } else {
            buf.append(inquiryLog.getInquiryBankCode());
        }

        if (MessageUtils.nil(inquiryLog.getInquiryBankAccount())) {
            buf.append(" ");
        } else {
            buf.append(inquiryLog.getInquiryBankAccount());
        }

        if (MessageUtils.nil(inquiryLog.getInquiryResponseFormat())) {
            buf.append(" ");
        } else {
            buf.append(inquiryLog.getInquiryResponseFormat());
        }

        if (buf != null && buf.toString().trim().length() > 0) {
            key = buf.toString().trim();
        }

        return key;
    }

    public static void changeFieldValue(InquiryLog inquiryLog, boolean recovery) {
        String id = null;

        if ("4111".equals(inquiryLog.getInquiryTxCode()) || "4114".equals(inquiryLog.getInquiryTxCode()) || "4121".equals(inquiryLog.getInquiryTxCode())
                || "4123".equals(inquiryLog.getInquiryTxCode())) {

            if (recovery) {
                id = inquiryLog.getInquiryBizId();
                inquiryLog.setInquiryBizId(null);
                inquiryLog.setInquiryId(id);
            } else {
                id = inquiryLog.getInquiryId();
                inquiryLog.setInquiryId(null);
                inquiryLog.setInquiryBizId(id);
            }
        }
    }
}
