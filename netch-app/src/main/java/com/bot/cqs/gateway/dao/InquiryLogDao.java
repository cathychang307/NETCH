package com.bot.cqs.gateway.dao;

import java.util.Date;
import java.util.List;

import com.bot.cqs.gateway.persistence.InquiryLog;
import com.bot.cqs.query.command.QueryInquiryLogCommand;
import com.bot.cqs.query.dto.InquiryLogDto;
import com.iisigroup.cap.db.dao.GenericDao;

;

public interface InquiryLogDao extends GenericDao<InquiryLog> {
    /**
     * 列出所有資料查詢記錄。
     * 
     * @return
     */
    public List<InquiryLog> findAll();

    /**
     * 依資料查詢主鍵值搜尋單筆資料查詢記錄。
     * 
     * @param inquiryLogKey
     * @return
     */
    public InquiryLog findByInquiryLogKey(String inquiryLogKey);

    /**
     * 帳務報表檢視-查詢明細
     * 
     * @param command
     * @return
     */
    public List<InquiryLog> findQueryDetail(QueryInquiryLogCommand command);

    /**
     * 新增 資料查詢記錄。
     * 
     * @param inquiryLog
     */
    public void insertInquiryLog(InquiryLog inquiryLog);

    /**
     * 修改 資料查詢記錄。
     * 
     * @param inquiryLog
     */
    public void updateInquiryLog(InquiryLog inquiryLog);

    /**
     * 查詢月收費檔
     * 
     * @param inquiryChargeBankId
     *            付費分行
     * @param date[]
     *            時間 一個月的第一天與最後一天
     * @return
     */
    public List<InquiryLogDto> findChargeQuery(String inquiryChargeBankId, Date[] date);

    /**
     * 選出所有的charge_bank_id。
     * 
     * @return
     */
    public List<InquiryLog> findInquiryChargeBankList();
}
