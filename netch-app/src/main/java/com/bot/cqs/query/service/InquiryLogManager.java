package com.bot.cqs.query.service;

import com.bot.cqs.gateway.persistence.InquiryLog;
import com.bot.cqs.query.command.QueryInquiryLogCommand;
import com.bot.cqs.query.dto.InquiryLogDto;

import java.util.Date;
import java.util.List;

/**
 * 此介面，相關於{@link tw.com.bot.cqs.gateway.persistence.InquiryLog} 查詢明細資料表的計費、明細的交易處理程式。
 * 
 * @author Sunkist Wang
 * @since 0.1 2007/05/31
 */
public interface InquiryLogManager {
    /**
     * 依輸入條件，檢索{@link tw.com.bot.cqs.gateway.persistence.InquiryLog} 資料表。
     * 
     * @param command
     *            輸入條件
     * @return
     */
    public List<InquiryLog> findQueryDetail(QueryInquiryLogCommand command);

    /**
     * 計算指定的付費分行，及指定的日期的月收費彙整。 (付費分行可以是ALL 所有付費分行)
     * 
     * @param inquiryChargeBankId
     *            付費分行
     * @param date
     *            日期(格式：年/月/日)
     * @return
     */
    public List<InquiryLogDto> findChargeQuery(String inquiryChargeBankId, Date[] date);

    public List<InquiryLogDto> findForInquiryLogDtoList(QueryInquiryLogCommand command);

    /**
     * 取得付費分行列表。
     * 
     * @return
     */
    public List<InquiryLog> getChargeBankList();

    /**
     * 儲存查詢記錄
     * 
     * @param inquiryLog
     */
    public void saveInquiryLog(InquiryLog inquiryLog);
}
