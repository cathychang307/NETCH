package com.bot.cqs.gateway.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bot.cqs.gateway.persistence.Cache;
import com.bot.cqs.gateway.util.BOTGatewayServiceUtil;
import com.bot.cqs.query.util.DateUtil;
import com.bot.cqs.query.util.factory.ApplicationParameterFactory;

/**
 * 此類別主要用以清除資料庫中快取資料表的內容。系統會先由設定值中取得快取的天數設定，並對照目前資料表中所有資料，若超過設定的天數，則該筆資料便由資料表中刪除。
 * 
 * @author jefftseng
 * @see Cache
 * @since 1.0 2007/08/31
 */
public class BOTCacheCleaner {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 每天的毫秒數
     */
    private final static long MILLISECOND_IN_DAY = 24 * 60 * 60 * 1000;

    /**
     * 此方法為主要處理邏輯，由系統中取得設定的快取時間設定，並由資料表中取得每一筆資料，若超過設定天數則直接清除資料列，反之不處理。
     */
    public void clean() {
        if (ApplicationParameterFactory.newInstance().getQueryCacheInterval() != 0) {
            List<Cache> caches = BOTGatewayServiceUtil.cacheManager.findAll();
            for (Cache cache : caches) {
                if (!inCacheDuration(cache.getInquiryDate(), ApplicationParameterFactory.newInstance().getQueryCacheInterval()))
                    BOTGatewayServiceUtil.cacheManager.deleteCache(cache);
            }
        }
    }

    /**
     * 此方法透過查詢日期及設定天數檢查是否有超過設定範圍
     * 
     * @param dateString
     *            資料列原始產生時間
     * @param duration
     *            系統設定快取天數
     * @return 是否有超過設定範圍
     */
    private boolean inCacheDuration(String dateString, int duration) {
        boolean flag = false;

        Calendar changedDate = Calendar.getInstance();
        try {
            changedDate.setTime(new SimpleDateFormat("yyyyMMdd").parse(dateString));
            if (DateUtil.getToday() - changedDate.getTimeInMillis() <= duration * MILLISECOND_IN_DAY) {
                flag = true;
            }
        } catch (ParseException e) {
            if (logger.isDebugEnabled()) {
                logger.debug(e.getMessage());
            }
        }

        return flag;
    }

}
