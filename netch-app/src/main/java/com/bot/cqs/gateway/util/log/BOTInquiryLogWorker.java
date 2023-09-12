package com.bot.cqs.gateway.util.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bot.cqs.gateway.persistence.InquiryLog;
import com.bot.cqs.gateway.util.BOTGatewayServiceUtil;

/**
 * 此類別為一Thread，主要連結資料庫中之InquiryLog資料表，將查詢的資料記入查詢日誌中。
 * 
 * @author jefftseng
 * @since 1.0 2007/08/31
 */
public class BOTInquiryLogWorker extends Thread {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 查詢條件及查詢結果物件
     */
    private InquiryLog inquiryLog;

    /**
     * Thread執行點，將inquiryLog內容寫入InquiryLog資料表中。
     */
    public void run() {
        try {
            if (inquiryLog != null) {
                BOTGatewayServiceUtil.inquiryLogManager.saveInquiryLog(inquiryLog);
            }
        } catch (Exception ex) {
            if (logger.isDebugEnabled()) {
                logger.debug(ex.getMessage());
            }
        }
    }

    public InquiryLog getInquiryLog() {
        return inquiryLog;
    }

    public void setInquiryLog(InquiryLog inquiryLog) {
        this.inquiryLog = inquiryLog;
    }

}
