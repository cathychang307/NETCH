package com.bot.cqs.query.service;

import java.lang.reflect.InvocationTargetException;
import java.net.UnknownHostException;
import java.util.Map;

import com.bot.cqs.gateway.persistence.InquiryLog;
import com.bot.cqs.query.persistence.QueryBank;
import com.bot.cqs.query.persistence.QueryUser;
import com.bot.cqs.query.util.inquiry.InquiryThread;
import com.bot.cqs.query.util.queryField.QueryFieldException;
import com.bot.cqs.query.util.queryField.QueryRequestDefinition;
import com.iisigroup.cap.component.Request;

public interface InquiryManager extends QueryManager {

    /**
     * 經由預先定義好的 {@link QueryRequestDefinition} 物件將 request parameter 轉換為 {@link InquiryLog} 物件
     * 
     * @param paramMap
     *            request 的參數集合
     * @param queryDef
     *            本查詢的輸入資料定義
     * @param user
     *            本次登入的使用者
     * @param bank
     *            使用者所屬的單位 (內含付費行與交換所資料)
     * @param maxCount
     *            使用者輸入最大筆數
     * @return
     * @throws QueryFieldException
     */
    public InquiryLog[] getInquiryLogFromParams(Request request,Map<String, String[]> paramMap, QueryRequestDefinition queryDef, QueryUser user, QueryBank bank, int maxCount)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, QueryFieldException, UnknownHostException;

    /**
     * 交付查詢, 回傳一個執行完畢或執行中的 InquiryThread 陣列
     * 
     * @param inquiryLogs
     * @return
     * @see tw.com.bot.cqs.query.util.inquiry.InquiryThread
     */
    public InquiryThread[] getInquiryResult(InquiryLog[] inquiryLogs);
}
