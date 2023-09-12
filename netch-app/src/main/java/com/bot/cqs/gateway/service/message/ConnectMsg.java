package com.bot.cqs.gateway.service.message;

/**
 * 此類別主要作為票交所系統簽入、簽出時用的訊息物件。
 * 
 * @author jefftseng
 * @see "台灣票據票交所MQ票信查詢系統會員機構端規格說明手冊"，L001及L003訊息規格。
 * @since 1.0 2007/06/07
 */
public class ConnectMsg {

    /**
     * 銀行代號，三或七碼
     */
    private String bankId;
    /**
     * 交易日期 YYYYMMDD
     */
    private String trxDate;
    /**
     * 交易時間 HHMMSS
     */
    private String trxTime;
    /**
     * 交易代碼，L001或L003
     */
    private String trxCode;
    /**
     * 會員專用欄位
     */
    private String appropriative;
    /**
     * 交易序號
     */
    private String sequenceNo;
    /**
     * 票交所錯誤代碼
     */
    private String errCode;
    /**
     * 保留欄位
     */
    private String reserved;
    /**
     * Continue Flag
     */
    private String continueFlag;

    public String getAppropriative() {
        return appropriative;
    }

    public void setAppropriative(String appropriative) {
        this.appropriative = appropriative;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public String getContinueFlag() {
        return continueFlag;
    }

    public void setContinueFlag(String continueFlag) {
        this.continueFlag = continueFlag;
    }

    public String getErrCode() {
        return errCode;
    }

    public void setErrCode(String errCode) {
        this.errCode = errCode;
    }

    public String getReserved() {
        return reserved;
    }

    public void setReserved(String reserved) {
        this.reserved = reserved;
    }

    public String getSequenceNo() {
        return sequenceNo;
    }

    public void setSequenceNo(String sequenceNo) {
        this.sequenceNo = sequenceNo;
    }

    public String getTrxCode() {
        return trxCode;
    }

    public void setTrxCode(String trxCode) {
        this.trxCode = trxCode;
    }

    public String getTrxDate() {
        return trxDate;
    }

    public void setTrxDate(String trxDate) {
        this.trxDate = trxDate;
    }

    public String getTrxTime() {
        return trxTime;
    }

    public void setTrxTime(String trxTime) {
        this.trxTime = trxTime;
    }
}
