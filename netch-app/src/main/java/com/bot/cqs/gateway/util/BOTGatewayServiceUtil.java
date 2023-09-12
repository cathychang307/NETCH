package com.bot.cqs.gateway.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bot.cqs.gateway.persistence.Cache;
import com.bot.cqs.gateway.persistence.InquiryLog;
import com.bot.cqs.query.service.CacheManager;
import com.bot.cqs.query.service.impl.InquiryLogManagerImpl;
import com.bot.cqs.query.util.QueryCacheUtil;
import com.iisigroup.cap.utils.CapAppContext;

/**
 * 此類別為Gateway程式的工具程式。
 * 
 * @author Jeff Tseng
 * @since 1.0 2007/08/31
 */
public class BOTGatewayServiceUtil {

    protected static final Logger logger = LoggerFactory.getLogger(BOTGatewayServiceUtil.class);

    /**
     * CacheDao物件
     */
    public static CacheManager cacheManager = null;
    /**
     * InquiryLogManagerImpl物件
     */
    public static InquiryLogManagerImpl inquiryLogManager = null;

    // 將Spring 中的設定值載入，以提供 cacheDao and inquiryLogDao 連結 (ioc)
    static {
        cacheManager = CapAppContext.getBean("cacheManagerImpl");
        inquiryLogManager = CapAppContext.getBean("inquiryLogManagerImpl");
    }

    /**
     * 工具程式，透過CacheDao向系統查詢是否有當天同樣調件的查詢結果，若有，則回傳Cache結果；若無，則回傳Null物件。
     * 
     * @param inquiryLog
     *            查詢條件物件
     * @return Cache物件串列集
     */
    public static Cache checkAndGetFromCache(InquiryLog inquiryLog) {
        Cache cache = null;

        if (inquiryLog != null) {
            try {
                cache = cacheManager.findByDummyKey(QueryCacheUtil.formatCacheKey(inquiryLog));
            } catch (Exception ex) {
                if (logger.isDebugEnabled()) {
                    logger.debug(ex.getMessage());
                }
            }
        }

        return cache;
    }

    public static String msgId2Hex(byte[] bytes) {
        if (null == bytes)
            return "";
        String buff = new String("");
        int i = 0;
        for (byte b : bytes) {
            buff = buff + String.format("%02X", b);
            i++;
            if (i >= 24)
                break;
        }
        return buff;
    }

}
