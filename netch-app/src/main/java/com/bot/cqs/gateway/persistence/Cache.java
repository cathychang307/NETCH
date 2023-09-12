package com.bot.cqs.gateway.persistence;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.iisigroup.cap.model.GenericBean;

@Entity
@Table(name = "Cache")
@org.hibernate.annotations.GenericGenerator(name = "hibernate-uuid", strategy = "uuid")
public class Cache  extends GenericBean {
    /**
     * 資料回覆格式 對應欄位：inquiry_response_format
     */
    private String inquiryResponseFormat;
    private String inquiryDate;
    private String inquiryTime;
    private String inquiryId;
    private String inquiryBizId;
    private String inquiryBankCode;
    private String inquiryBankAccount;
    private String inquiryResponse;
    private String dummyKey;

    @Id
    @Column(name = "dummy_key", length = 50)
    public String getDummyKey() {
        return dummyKey;
    }

    public void setDummyKey(String dummyKey) {
        this.dummyKey = dummyKey;
    }

    @Column(name = "inquiry_bank_account", length = 9)
    public String getInquiryBankAccount() {
        return inquiryBankAccount;
    }

    public void setInquiryBankAccount(String inquiryBankAccount) {
        this.inquiryBankAccount = inquiryBankAccount;
    }

    @Column(name = "inquiry_bank_code", length = 9)
    public String getInquiryBankCode() {
        return inquiryBankCode;
    }

    public void setInquiryBankCode(String inquiryBankCode) {
        this.inquiryBankCode = inquiryBankCode;
    }

    @Column(name = "inquiry_biz_id", length = 10)
    public String getInquiryBizId() {
        return inquiryBizId;
    }

    public void setInquiryBizId(String inquiryBizId) {
        this.inquiryBizId = inquiryBizId;
    }

    @Column(name = "inquiry_id", length = 10)
    public String getInquiryId() {
        return inquiryId;
    }

    public void setInquiryId(String inquiryId) {
        this.inquiryId = inquiryId;
    }

    @Column(name = "inquiry_response", columnDefinition = "varchar(Max)")
    public String getInquiryResponse() {
        return inquiryResponse;
    }

    public void setInquiryResponse(String inquiryResponse) {
        this.inquiryResponse = inquiryResponse;
    }

    @Column(name = "inquiry_date", length = 8)
    public String getInquiryDate() {
        return inquiryDate;
    }

    public void setInquiryDate(String inquiryDate) {
        this.inquiryDate = inquiryDate;
    }

    @Column(name = "inquiry_response_format", length = 1, columnDefinition = "char")
    public String getInquiryResponseFormat() {
        return inquiryResponseFormat;
    }

    public void setInquiryResponseFormat(String inquiryResponseFormat) {
        this.inquiryResponseFormat = inquiryResponseFormat;
    }

    @Column(name = "inquiry_time", length = 6)
    public String getInquiryTime() {
        return inquiryTime;
    }

    public void setInquiryTime(String inquiryTime) {
        this.inquiryTime = inquiryTime;
    }
}
