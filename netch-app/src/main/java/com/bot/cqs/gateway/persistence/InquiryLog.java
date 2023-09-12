
package com.bot.cqs.gateway.persistence;

import com.iisigroup.cap.model.GenericBean;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "InquiryLog")
@org.hibernate.annotations.GenericGenerator(name = "hibernate-uuid", strategy = "uuid")
public class InquiryLog extends GenericBean {

    /**
     * Continue Flag，非DB欄位值
     */
    private transient String continueFlag;
    /**
     * 查詢序號，非DB欄位值
     */
    private transient String seqNo;
    /**
     * 暫時放付費行名稱的。列表使用的。
     */
    private transient String chargeBankName;
    /**
     * 給查詢明細報表用的，依不同的查詢類型放查詢條件的字串。非DB欄位
     */
    private transient String conditions;
    /**
     * 查詢資料格式代碼，非DB欄位值
     */
    private transient String msgFid;
    /**
     * 查詢資料MQ Message ID，非DB欄位值
     */
    private transient String msgId;
    /**
     * 錯誤訊息，非DB欄位值
     */
    private transient String inquiryErrorMsg;
    /**
     * 資料者姓名 對應欄位：inquiry_user_name
     */
    private String inquiryUserName;
    /**
     * cache 裡的查詢時間 對應欄位:inquiry_cache_time
     */
    private String inquiryCacheTime;
    /**
     * 查詢資料是否由Cache取得旗標，true表示由Cache取得 對應欄位：inquiry_cache_flag
     */
    private boolean inquiryCacheFlag;
    /**
     * 資料查詢該筆費用 對應欄位：inquiry_fee
     */
    private float inquiryFee;

    /**
     * 資料回覆格式 對應欄位：inquiry_response_format
     */
    private String inquiryResponseFormat;

    /**
     * 資料查詢交易別代碼：4111 ~ 4136 對應欄位：inquiry_tx_code
     */
    private String inquiryTxCode;
    /**
     * 資料查詢交易日期，格式：yyyyMMdd 對應欄位：inquiry_date
     */
    private String inquiryDate;
    /**
     * 資料查詢交易時間，格式：HHmmss 對應欄位：inquiry_time
     */
    private String inquiryTime;
    /**
     * 資料查詢條件：身份證字號，或負責人身份證字號 對應欄位：inquiry_id
     */
    private String inquiryId;
    /**
     * 資料查詢條件：公司統一編號 對應欄位：inquiry_biz_id
     */
    private String inquiryBizId;
    /**
     * 資料查詢條件：個人姓名，或是公司名稱 對應欄位：inquiry_name
     */
    private String inquiryName;
    /**
     * 資料查詢條件：開戶行代碼 對應欄位：inquiry_bank_code
     */
    private String inquiryBankCode;
    /**
     * 資料查詢條件：開戶行帳號 對應欄位：inquiry_bank_account
     */
    private String inquiryBankAccount;
    /**
     * 資料查詢回覆資料格式 對應欄位：inquiry_response_format
     */
    private String inquiryResponse;
    /**
     * 資料查詢，付費行代碼 對應欄位：inquiry_charge_bank_id
     */
    private String inquiryChargeBankId;
    /**
     * 資料查詢，查詢行代碼 對應欄位：inquiry_qry_bank_id
     */
    private String inquiryQryBankId;
    /**
     * 資料查詢回覆，主機錯誤回覆碼，該值為票交回覆中的主機回覆錯誤碼 對應欄位：inquiry_error_code
     */
    private String inquiryErrorCode;
    /**
     * 資料查詢主鍵值，為一個UUID 對應欄位：inquiry_log_key
     */
    private String inquiryLogKey;
    /**
     * 資料查詢，交換所代碼 對應欄位：inquiry_tch_id
     */
    private String inquiryTchId;
    /**
     * 資料查詢，查詢者ID 對應欄位：inquiry_account
     */
    private String inquiryAccount;

    /**
     * application 內部運用的
     */
    private Date inquiryStartDatetime, inquiryEndDatetime;

    /**
     * AP 使用, 記錄本輸入資料在於網頁上的位置
     */
    private int requestFieldPosition;

    public InquiryLog() {

        super();
        // 如果不想 import QueryFieldException, 可以改用 -1
        // setRequestFieldPosition( QueryFieldException.UNKNOWN_REQUEST_FIELD_POSITION );
        setRequestFieldPosition(-1);
    }

    @Column(name = "inquiry_tch_id", length = 2, columnDefinition = "char")
    public String getInquiryTchId() {

        return inquiryTchId;
    }

    public void setInquiryTchId(String inquiryTchId) {

        this.inquiryTchId = inquiryTchId;
    }

    @Id
    @GeneratedValue(generator = "hibernate-uuid")
    @Column(name = "inquiry_log_key", length = 36)
    public String getInquiryLogKey() {

        return inquiryLogKey;
    }

    public void setInquiryLogKey(String inquiryLogKey) {

        this.inquiryLogKey = inquiryLogKey;
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

    @Column(name = "inquiry_charge_bank_id", length = 7)
    public String getInquiryChargeBankId() {

        return inquiryChargeBankId;
    }

    public void setInquiryChargeBankId(String inquiryChargeBankId) {

        this.inquiryChargeBankId = inquiryChargeBankId;
    }

    @Column(name = "inquiry_error_code", length = 5)
    public String getInquiryErrorCode() {

        return inquiryErrorCode;
    }

    public void setInquiryErrorCode(String inquiryErrorCode) {

        this.inquiryErrorCode = inquiryErrorCode;
    }

    @Column(name = "inquiry_name", length = 20)
    public String getInquiryName() {

        return inquiryName;
    }

    public void setInquiryName(String inquiryName) {

        this.inquiryName = inquiryName;
    }

    @Column(name = "inquiry_qry_bank_id", length = 7)
    public String getInquiryQryBankId() {

        return inquiryQryBankId;
    }

    public void setInquiryQryBankId(String inquiryQryBankId) {

        this.inquiryQryBankId = inquiryQryBankId;
    }

    @Column(name = "inquiry_time", length = 6)
    public String getInquiryTime() {

        return inquiryTime;
    }

    public void setInquiryTime(String inquiryTime) {

        this.inquiryTime = inquiryTime;
    }

    @Column(name = "inquiry_cache_flag", columnDefinition = "int")
    public boolean getInquiryCacheFlag() {

        return inquiryCacheFlag;
    }

    public void setInquiryCacheFlag(boolean inquiryCacheFlag) {

        this.inquiryCacheFlag = inquiryCacheFlag;
    }

    @Column(name = "inquiry_fee")
    public float getInquiryFee() {

        return inquiryFee;
    }

    public void setInquiryFee(float inquiryFee) {

        this.inquiryFee = inquiryFee;
    }

    @Column(name = "inquiry_tx_code", length = 4)
    public String getInquiryTxCode() {

        return inquiryTxCode;
    }

    public void setInquiryTxCode(String inquiryTxCode) {

        this.inquiryTxCode = inquiryTxCode;
    }

    @Column(name = "inquiry_account", length = 20)
    public String getInquiryAccount() {

        return inquiryAccount;
    }

    public void setInquiryAccount(String inquiryAccount) {

        this.inquiryAccount = inquiryAccount;
    }

    @Transient
    public String getMsgFid() {

        return msgFid;
    }

    public void setMsgFid(String msgFid) {

        this.msgFid = msgFid;
    }

    @Transient
    public String getMsgId() {

        return msgId;
    }

    public void setMsgId(String msgId) {

        this.msgId = msgId;
    }

    @Transient
    public String getInquiryErrorMsg() {

        return inquiryErrorMsg;
    }

    public void setInquiryErrorMsg(String inquiryErrorMsg) {

        this.inquiryErrorMsg = inquiryErrorMsg;
    }

    @Column(name = "inquiry_user_name", length = 20)
    public String getInquiryUserName() {

        return inquiryUserName;
    }

    public void setInquiryUserName(String inquiryUserName) {

        this.inquiryUserName = inquiryUserName;
    }

    @Transient
    public String getConditions() {

        /*
         * 身分證字號=${log.inquiryId }&nbsp;,公司統編=${log.inquiryBizId }&nbsp;,開戶行代碼=${log.inquiryBankCode }&nbsp;,開戶行帳號=${log.inquiryBankAccount }
         */
        StringBuffer conditions = new StringBuffer();
        int t3 = 0;
        int t4 = 0;
        try {
            t3 = Integer.parseInt(Character.toString(inquiryTxCode.charAt(2)));
            t4 = Integer.parseInt(Character.toString(inquiryTxCode.charAt(3)));
            if (!"4".equalsIgnoreCase(Character.toString(inquiryTxCode.charAt(0))) || !"1".equalsIgnoreCase(Character.toString(inquiryTxCode.charAt(1)))) {
                t3 = 0;
            }
        } catch (NumberFormatException nfe) {
            conditions.append("資料查詢交易別代碼型態錯誤");
        } catch (NullPointerException npe) {
            conditions.append("無資料查詢交易別代碼");
        }
        switch (t3) {
        case 1:
            switch (t4) {
            case 1:
            case 4:
                conditions.append("[身份證統一編號=");
                conditions.append(inquiryId != null ? inquiryId : "");
                conditions.append("]，[姓名=");
                conditions.append(inquiryName != null ? inquiryName : "");
                conditions.append("]");
                break;
            case 2:
            case 5:
                conditions.append("[營利事業統一編號=");
                conditions.append(inquiryBizId != null ? inquiryBizId : "");
                conditions.append("]，[負責人身份證統一編號=");
                conditions.append(inquiryId != null ? inquiryId : "");
                conditions.append("]，[公司名稱=");
                conditions.append(inquiryName != null ? inquiryName : "");
                conditions.append("]");
                break;
            case 3:
            case 6:
                conditions.append("[銀行代號=");
                conditions.append(inquiryBankCode != null ? inquiryBankCode : "");
                conditions.append("]，[帳號=");
                conditions.append(inquiryBankAccount != null ? inquiryBankAccount : "");
                conditions.append("]");
                break;
            default:
                break;
            }
            break;
        case 2:
            switch (t4) {
            case 1:
            case 3:
                conditions.append("[身份證統一編號=");
                conditions.append(inquiryId != null ? inquiryId : "");
                conditions.append("]，[姓名=");
                conditions.append(inquiryName != null ? inquiryName : "");
                conditions.append("]");
                break;
            case 2:
            case 4:
                conditions.append("[營利事業統一編號=");
                conditions.append(inquiryBizId != null ? inquiryBizId : "");
                conditions.append("]，[負責人身份證統一編號=");
                conditions.append(inquiryId != null ? inquiryId : "");
                conditions.append("]，[公司名稱=");
                conditions.append(inquiryName != null ? inquiryName : "");
                conditions.append("]");
                break;
            default:
                break;
            }
            break;
        case 3:
            switch (t4) {
            case 2:
            case 5:
                conditions.append("[營利事業統一編號=");
                conditions.append(inquiryBizId != null ? inquiryBizId : "");
                conditions.append("]，[負責人身份證統一編號=");
                conditions.append(inquiryId != null ? inquiryId : "");
                conditions.append("]，[公司名稱=");
                conditions.append(inquiryName != null ? inquiryName : "");
                conditions.append("]");
                break;
            case 3:
            case 6:
                conditions.append("[銀行代號=");
                conditions.append(inquiryBankCode != null ? inquiryBankCode : "");
                conditions.append("]，[帳號=");
                conditions.append(inquiryBankAccount != null ? inquiryBankAccount : "");
                conditions.append("]");
                break;
            default:
                break;
            }
            break;
        default:
            break;
        }
        if (conditions.length() == 0) {
            conditions.append("未知的資料查詢交易別代碼");
        }
        setConditions(conditions.toString());
        return this.conditions;
    }

    public void setConditions(String conditions) {

        if (conditions != null) {
            this.conditions = conditions;
        } else {
            this.conditions = "";
        }
    }

    @Transient
    public Date getInquiryEndDatetime() {

        return inquiryEndDatetime;
    }

    public void setInquiryEndDatetime(Date inquiryEndDatetime) {

        this.inquiryEndDatetime = inquiryEndDatetime;
    }

    @Transient
    public Date getInquiryStartDatetime() {

        return inquiryStartDatetime;
    }

    public void setInquiryStartDatetime(Date inquiryStartDatetime) {

        this.inquiryStartDatetime = inquiryStartDatetime;
    }

    @Transient
    public String getChargeBankName() {

        return chargeBankName;
    }

    public void setChargeBankName(String chargeBankName) {

        this.chargeBankName = chargeBankName;
    }

    @Column(name = "inquiry_response_format", length = 1, columnDefinition = "char")
    public String getInquiryResponseFormat() {

        return inquiryResponseFormat;
    }

    public void setInquiryResponseFormat(String inquireResponseFormat) {

        this.inquiryResponseFormat = inquireResponseFormat;
    }

    @Transient
    public String getContinueFlag() {

        return continueFlag;
    }

    public void setContinueFlag(String continueFlag) {

        this.continueFlag = continueFlag;
    }

    @Transient
    public String getSeqNo() {

        return seqNo;
    }

    public void setSeqNo(String seqNo) {

        this.seqNo = seqNo;
    }

    /**
     * 取得一個 付費行 ID + 付費行名稱 的資訊, 以便列表顯示
     * 
     * @return
     */
    @Transient
    public String getChargeBankShortDesc() {

        return getInquiryChargeBankId() + " " + (getChargeBankName() != null ? getChargeBankName() : "");
    }

    /**
     * AP 使用, 記錄本輸入資料在於網頁上的位置
     * 
     * @return
     */
    @Transient
    public int getRequestFieldPosition() {

        return requestFieldPosition;
    }

    public void setRequestFieldPosition(int requestFieldPosition) {

        this.requestFieldPosition = requestFieldPosition;
    }

    /**
     * 查詢回覆結果，使用，辯別查詢Cache裡的查詢時間。
     * 
     * @return
     */
    @Column(name = "inquiry_cache_time", length = 6)
    public String getInquiryCacheTime() {
        return inquiryCacheTime;
    }

    public void setInquiryCacheTime(String inquiryCacheTime) {
        this.inquiryCacheTime = inquiryCacheTime;
    }

}
