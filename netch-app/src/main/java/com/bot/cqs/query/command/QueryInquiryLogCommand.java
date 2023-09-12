package com.bot.cqs.query.command;

import java.util.Date;

import org.springframework.util.StringUtils;

import com.bot.cqs.gateway.persistence.InquiryLog;
import com.bot.cqs.query.util.DateUtil;
import com.bot.cqs.query.util.QueryBankUtil;

/**
 * 帳務報表檢視(網頁) 對應的 command object.
 * 
 * @author Sunkist Wang
 * @since 2007/07/13 v1.0
 */
public class QueryInquiryLogCommand extends InquiryLog {
    /**
     * 查詢所有交易類別的代號
     */
    public static final String ALL_TYPE = "ALL";
    /**
     * 查詢所有付費分行的代號
     */
    public static final String ALL_BANK = "ALL";

    /**
     * privige 0:branch only, 1:all
     */
    public String queryMode;

    public String action;

    private String inputYear;

    private String inputMonth;

    private Date inputDate;

    private String inputAccessDateFrom;

    private String inputAccessDateTo;

    private Date accessDateFrom;

    private Date accessDateTo;

    private String startDate;

    private String endDate;

    private String outputMethod;

    private String inTxCode;

    private String inAccount;

    private String inChargeBankId;

    public String getAction() {

        return action;
    }

    public void setAction(String action) {

        this.action = action;
    }

    public String getQueryMode() {
        return queryMode;
    }

    public void setQueryMode(String queryMode) {
        this.queryMode = queryMode;
    }

    /**
     * 轉換西元年的時間(起)，格式：<B>西元年 月 日</B>的字串。
     * 
     * @return
     */
    public String getStartDate() {

        return startDate;
    }

    public void setStartDate(String startDate) {

        this.startDate = startDate;
    }

    /**
     * 轉換為西元年的時間(迄)，格式：<B>西元年 月 日</B>的字串。
     * 
     * @return
     */
    public String getEndDate() {

        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    /**
     * 網頁上輸入的時間(起)，格式為：<B>民國年/月/日</B>的字串。
     * 
     * @return
     */
    public String getInputAccessDateFrom() {

        return inputAccessDateFrom;
    }

    public void setInputAccessDateFrom(String inputAccessDateFrom) {

        this.inputAccessDateFrom = inputAccessDateFrom;
    }

    /**
     * 網頁上輸入的時間(迄)，格式為：<B>民國年/月/日</B>的字串。
     * 
     * @return
     */
    public String getInputAccessDateTo() {

        return inputAccessDateTo;
    }

    public void setInputAccessDateTo(String inputAccessDateTo) {

        this.inputAccessDateTo = inputAccessDateTo;
    }

    /**
     * 網頁上輸入的查詢類型，格式為交易代碼+描述。
     * 
     * @return
     */
    public String getInTxCode() {

        return inTxCode;
    }

    public void setInTxCode(String inTxCode) {

        this.inTxCode = inTxCode;
        parseInputTxCode();
    }

    /**
     * 將網頁輸入的查詢類型轉換成只剩下交易代碼。
     *
     */
    public void parseInputTxCode() {

        if (StringUtils.hasText(getInTxCode())) {
            if (ALL_TYPE.equals(getInTxCode()))
                setInquiryTxCode(null);
            else {
                int pos = getInTxCode().indexOf(' ');
                if (pos == -1)
                    setInquiryTxCode(null);
                else
                    setInquiryTxCode(getInTxCode().substring(0, pos));
            }
        }
    }

    public String getInChargeBankId() {

        return inChargeBankId;
    }

    /**
     * 將網頁輸入的付費分行，轉換成只剩下InquiryChargeBankId。
     *
     */
    public void setInChargeBankId(String inChargeBankId) {

        this.inChargeBankId = inChargeBankId;
        // 借QueryBankUtil裡的這個方法來用，有一樣的功用。(以空白分界，取bankId)
        setInquiryChargeBankId(QueryBankUtil.getDepartmentIdFromShortDesc(inChargeBankId));
    }

    public String getInAccount() {

        return inAccount;
    }

    public void setInAccount(String inAccount) {

        this.inAccount = inAccount;
        setInquiryAccount(inAccount);
    }

    /**
     * 網頁上輸入的時間(起)，格式為：<code>java.util.Date</code>物件。
     * 
     * @return
     */
    public Date getAccessDateFrom() {

        return accessDateFrom;
    }

    public void setAccessDateFrom(Date accessDateFrom) {

        this.accessDateFrom = accessDateFrom;
    }

    /**
     * 網頁上輸入的時間(迄)，格式為：<code>java.util.Date</code>物件。
     * 
     * @return
     */
    public Date getAccessDateTo() {

        return accessDateTo;
    }

    public void setAccessDateTo(Date accessDateTo) {

        this.accessDateTo = accessDateTo;
    }

    /**
     * 將輸入的起始日期、結束日期做格式轉換。
     *
     */
    public void caculateInputAccessDate() {

        setAccessDateFrom(DateUtil.getDate(getInputAccessDateFrom(), DateUtil.DATE_STYLE_FROM));
        setStartDate(DateUtil.toADDate(getAccessDateFrom()));
        setAccessDateTo(DateUtil.getDate(getInputAccessDateTo(), DateUtil.DATE_STYLE_TO));
        setEndDate(DateUtil.toADDate(getAccessDateTo()));
        if (!StringUtils.hasText(getStartDate()) && !StringUtils.hasText(getEndDate())) {
            setStartDate(DateUtil.toADDate(new Date()));
            setAccessDateFrom(new Date());
        }
    }

    /**
     * 指示要輸出何種格式。
     * 
     * @return
     */
    public String getOutputMethod() {

        return outputMethod;
    }

    public void setOutputMethod(String outputMethod) {

        this.outputMethod = outputMethod;
    }

    /**
     * 月收費彙整與查詢統計(網頁)上輸入的月份
     * 
     * @return
     */
    public String getInputMonth() {

        return inputMonth;
    }

    public void setInputMonth(String inputMonth) {

        this.inputMonth = inputMonth;
    }

    /**
     * 月收費彙整與查詢統計(網頁)上輸入的年份
     * 
     * @return
     */
    public String getInputYear() {

        return inputYear;
    }

    public void setInputYear(String inputYear) {

        this.inputYear = inputYear;
    }

    /**
     * 將月收費彙整與查詢統計(網頁)上輸入的年份與月份，轉換成每個月的一號的日期物件。
     *
     */
    public void caculateInputDate() {

        if (StringUtils.hasText(getInputYear()) || StringUtils.hasText(getInputMonth())) {
            setInputDate(DateUtil.getDate(getInputYear(), getInputMonth(), "1", DateUtil.DATE_STYLE_FROM));
        }
    }

    /**
     * 月收費彙整與查詢統計查詢時用的日期，為每個月1號。
     * 
     * @return
     */
    public Date getInputDate() {

        return inputDate;
    }

    public void setInputDate(Date inputDate) {

        this.inputDate = inputDate;
    }
}
