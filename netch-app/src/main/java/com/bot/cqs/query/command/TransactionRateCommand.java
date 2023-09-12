
package com.bot.cqs.query.command;

import java.util.Date;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.bot.cqs.query.persistence.TransactionRate;
import com.bot.cqs.query.persistence.TransactionRateKey;
import com.bot.cqs.query.util.DateUtil;

/**
 * 費率查詢 / 設定 (網頁) 對應的 command object
 * 
 * @author Damon Lu
 */
@Component
public class TransactionRateCommand extends TransactionRate {

    public static final String ALL_TYPE = "ALL";

    public static final String QUERY_CURRENT = "0";
    public static final String QUERY_HISTORY = "1";

    public static final int ACTION_UNKNOWN = -1;
    public static final int ACTION_QUERY = 0;
    public static final int ACTION_CREATE = 1;
    public static final int ACTION_MODIFY = 2;
    public static final int ACTION_DELETE = 3;
    public static final int ACTION_SAVE = 4;
    public static final int ACTION_UPDATE = 5;

    private String inputYear;
    private String inputMonth;
    private Date inputDate;
    // private String inputDateString;

    private String inputRateTypeDesc;
    private String inputRateType;

    private String inputQueryType;

    private String action;
    private int actionType;

    private String inputRate;
    private String inputPoundage;
    private String inputRecordsAtDiscount;
    private String inputDiscountRate;

    public TransactionRateCommand() {

    }

    /**
     * 以一個已存在的費率 <code>TransactionRate</code> 建構 command object.
     * <p>
     * 這會將 <code>TransactionRate</code> 物件的內容含 <code>TransactionRateKey</code> 抄寫一份至 command object
     * 
     * @param rate
     * @see tw.com.bot.cqs.query.persistence.TransactionRate
     * @see tw.com.bot.cqs.query.persistence.TransactionRateKey
     */
    public TransactionRateCommand(TransactionRate rate) {

        super();

        TransactionRateKey key = new TransactionRateKey();
        key.setTransactionId(rate.getKey().getTransactionId());
        key.setTransactionRateEffectDate(rate.getKey().getTransactionRateEffectDate());
        setKey(key);

        setInputRateType(rate.getKey().getTransactionId());
        setInputRateTypeDesc(rate.getTransactionShortDesc());
        setInputDate(rate.getKey().getTransactionRateEffectDate());
        setInputYear(rate.getKey().getEffectDateRocYear());
        setInputMonth(rate.getKey().getEffectDateMonth());

        setTransactionDiscountRate(rate.getTransactionDiscountRate());
        setInputDiscountRate(String.valueOf(rate.getTransactionDiscountRate()));

        setTransactionPoundage(rate.getTransactionPoundage());
        setInputPoundage(String.valueOf(rate.getTransactionPoundage()));

        setTransactionRate(rate.getTransactionRate());
        setInputRate(String.valueOf(rate.getTransactionRate()));

        setTransactionRecordsAtDiscount(rate.getTransactionRecordsAtDiscount());
        setInputRecordsAtDiscount(String.valueOf(rate.getTransactionRecordsAtDiscount()));

        setVersion(rate.getVersion());
    }

    /**
     * 對應於網頁的 "action" 欄位, 以指定本次作業的行為
     * 
     * @return
     */
    public String getAction() {

        return action;
    }

    public void setAction(String action) {

        this.action = action;
    }

    /**
     * 由 <code>getAction()</code> 欄位轉換而來, 提供一個 <code>int</code> 的型態以便使用。這個值在 {@link #parseAction()} 後可以取得
     * 
     * @return
     * @see #parseAction()
     * @see #getAction()
     */
    public int getActionType() {

        return actionType;
    }

    public void setActionType(int actionType) {

        this.actionType = actionType;
    }

    /**
     * 對應於網頁的 "inputMonth" 欄位, 代表選取的月份
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
     * 對應於網頁的 "inputYear" 欄位, 代表選取的年份
     * 
     * @return
     */
    public String getInputYear() {

        return inputYear;
    }

    public void setInputYear(String inputYear) {

        this.inputYear = inputYear;
    }

    // public String getInputDateString() {
    //
    // return inputDateString;
    // }
    //
    // public void setInputDateString( String inputDateString ) {
    //
    // this.inputDateString = inputDateString;
    // }

    /**
     * 取得使用者於網頁的輸入日期, 這個值在 {@link #caculateInputDate()} 後可以取得
     * 
     * @return
     * @see #caculateInputDate()
     * @see #getInputYear()
     * @see #getInputMonth()
     */
    public Date getInputDate() {

        return inputDate;
    }

    public void setInputDate(Date inputDate) {

        this.inputDate = inputDate;
    }

    /**
     * 這個值等同於 transaction ID, 在 {@link #parseInputRateType()} 後會經由 {@link #getInputRateTypeDesc()} 值來取得
     * 
     * @return
     * @see #parseInputRateType()
     * @see #getInputRateTypeDesc()
     */
    public String getInputRateType() {

        return inputRateType;
    }

    public void setInputRateType(String inputRateType) {

        this.inputRateType = inputRateType;
    }

    /**
     * 對應於網頁的 "inputRateTypeDesc" 欄位, 內容應為 transaction ID + " " + transaction 描述.
     * 
     * @return
     * 
     */
    public String getInputRateTypeDesc() {

        return inputRateTypeDesc;
    }

    public void setInputRateTypeDesc(String inputRateTypeDesc) {

        this.inputRateTypeDesc = inputRateTypeDesc;
    }

    /**
     * 將 inputYear, inputMonth 的值轉為 Date 物件 (西元年), 並執行 {@link #setInputDate(Date)}. 如果 inputYear, inputMonth 均為空值則忽略 不處理
     * <p>
     *
     * 例如: inputYear = 96, inputMonth = 3 <br>
     * ==> date = 2007/3/1 00:00:00
     */
    public void caculateInputDate() {

        if (StringUtils.hasText(getInputYear()) || StringUtils.hasText(getInputMonth())) {
            setInputDate(DateUtil.getDate(getInputYear(), getInputMonth(), "1", DateUtil.DATE_STYLE_FROM));
        }
    }

    /**
     * 解析 {@link #getInputRateTypeDesc()} 的值, 取出 transaction ID 後執行 {@link #setInputRateType(String)}. 這個 method 是方便由網頁輸入轉為程式需要 的參數
     * 
     * @see #getInputRateTypeDesc()
     * @see #getInputRateType()
     */
    public void parseInputRateType() {

        if (StringUtils.hasText(getInputRateTypeDesc())) {
            if (getInputRateTypeDesc().equals(ALL_TYPE))
                setInputRateType(ALL_TYPE);
            else {
                int pos = getInputRateTypeDesc().indexOf(' ');
                if (pos == -1)
                    setInputRateType(getInputRateTypeDesc());
                else
                    setInputRateType(getInputRateTypeDesc().substring(0, pos));
            }
        }
    }

    /**
     * 對應查詢網頁的 "inputQueryType" 欄位, 用以區別查目前費率或是歷史費率
     * 
     * @return
     * 
     * @see #getInputQueryTypeDesc()
     */
    public String getInputQueryType() {

        return inputQueryType;
    }

    public void setInputQueryType(String inputQueryType) {

        this.inputQueryType = inputQueryType;
    }

    /**
     * 
     * @return
     */
    public String getInputQueryTypeDesc() {

        if (getInputQueryType() != null && getInputQueryType().equals(QUERY_CURRENT))
            return "目前費率";
        else
            return "歷史紀錄";
    }

    /**
     * 依 {@link #getAction()} 的內容呼叫 {@link #setActionType(int)} 轉為 int 型能以便使用
     *
     * @see #getActionType()
     * @see #setActionType(int)
     */
    public void parseAction() {

        if (!StringUtils.hasText(getAction()) || getAction().equals("query"))
            setActionType(ACTION_QUERY);
        else if (getAction().equals("create"))
            setActionType(ACTION_CREATE);
        else if (getAction().equals("modify"))
            setActionType(ACTION_MODIFY);
        else if (getAction().equals("delete"))
            setActionType(ACTION_DELETE);
        else if (getAction().equals("save"))
            setActionType(ACTION_SAVE);
        else if (getAction().equals("update"))
            setActionType(ACTION_UPDATE);
        else
            setActionType(ACTION_UNKNOWN);
    }

    /**
     * 檢查 {@link #getAction()} 的內容是否合法
     * 
     * @return
     */
    public boolean isActionTypeValid() {

        return getActionType() != ACTION_UNKNOWN;
    }

    /**
     * 折扣費率, 對應查詢網頁的 "inputDiscountRate" 欄位
     * 
     * @return
     */
    public String getInputDiscountRate() {

        return inputDiscountRate;
    }

    public void setInputDiscountRate(String inputDiscountRate) {

        this.inputDiscountRate = inputDiscountRate;
    }

    public void parseInputDiscountRate() {

        if (!StringUtils.hasText(getInputDiscountRate()))
            throw new NumberFormatException();

        double value = Double.parseDouble(getInputDiscountRate().trim());
        // 要小於 100 (%), 不然怎麼叫折扣
        if (value < 0 || value >= 100)
            throw new IllegalArgumentException();
        setTransactionDiscountRate(value / 100); // 是百分比
    }

    /**
     * 手續費, 對應查詢網頁的 "inputPoundage" 欄位
     * 
     * @return
     */
    public String getInputPoundage() {

        return inputPoundage;
    }

    public void setInputPoundage(String inputPoundage) {

        this.inputPoundage = inputPoundage;
    }

    public void parseInputPoundage() {

        if (!StringUtils.hasText(getInputPoundage()))
            throw new NumberFormatException();

        double value = Double.parseDouble(getInputPoundage().trim());

        if (value < 0)
            throw new IllegalArgumentException();
        setTransactionPoundage(value);
    }

    /**
     * 費率, 對應於網頁的 "inputRate" 欄位
     * 
     * @return
     */
    public String getInputRate() {

        return inputRate;
    }

    public void setInputRate(String inputRate) {

        this.inputRate = inputRate;
    }

    public void parseInputRate() {

        if (!StringUtils.hasText(getInputRate()))
            throw new NumberFormatException();

        double value = Double.parseDouble(getInputRate().trim());

        if (value < 0)
            throw new IllegalArgumentException();
        setTransactionRate(value);
    }

    /**
     * 折扣門檻的筆數, 對于于網頁的 "inputRecordsAtDiscount" 欄位
     * 
     * @return
     */
    public String getInputRecordsAtDiscount() {

        return inputRecordsAtDiscount;
    }

    public void setInputRecordsAtDiscount(String inputRecordsAtDiscount) {

        this.inputRecordsAtDiscount = inputRecordsAtDiscount;
    }

    public void parseInputRecordsAtDiscount() {

        if (!StringUtils.hasText(getInputRecordsAtDiscount()))
            throw new NumberFormatException();

        int value = Integer.parseInt(getInputRecordsAtDiscount().trim());

        if (value < 0)
            throw new IllegalArgumentException();
        setTransactionRecordsAtDiscount(value);
    }

}
