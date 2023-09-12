package com.bot.cqs.gateway.util.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bot.cqs.gateway.persistence.Cache;
import com.bot.cqs.gateway.persistence.InquiryLog;
import com.bot.cqs.gateway.util.BOTGatewayServiceUtil;
import com.bot.cqs.query.util.QueryCacheUtil;

/**
 * 此類別主要連結資料庫中之Cache資料表，將查詢的資料記入快取中，以便於系統能提供快取功能。
 * 
 * @author jefftseng
 * @since 1.0 2007/08/31
 */
public class BOTCacheWorker {

    protected static final Logger logger = LoggerFactory.getLogger(BOTCacheWorker.class);

    /**
     * 將查詢條件及查詢結果記入快取資料表中
     * 
     * @param inquiryLog
     *            查詢條件及查詢結果物件
     */
    public static void cache(InquiryLog inquiryLog) {
        Cache cache = null;
        try {
            if (inquiryLog != null) {
                cache = new Cache();
                cache.setDummyKey(QueryCacheUtil.formatCacheKey(inquiryLog));
                cache.setInquiryResponseFormat(inquiryLog.getInquiryResponseFormat());
                cache.setInquiryBankAccount(inquiryLog.getInquiryBankAccount());
                cache.setInquiryBankCode(inquiryLog.getInquiryBankCode());
                cache.setInquiryBizId(inquiryLog.getInquiryBizId());
                cache.setInquiryDate(inquiryLog.getInquiryDate());
                cache.setInquiryTime(inquiryLog.getInquiryTime());
                cache.setInquiryId(inquiryLog.getInquiryId());
                cache.setInquiryResponse(inquiryLog.getInquiryResponse());

                BOTGatewayServiceUtil.cacheManager.insertCache(cache);
            }
        } catch (Exception ex) {
            if (logger.isDebugEnabled()) {
                logger.debug(ex.getMessage());
            }
        }
    }
}
