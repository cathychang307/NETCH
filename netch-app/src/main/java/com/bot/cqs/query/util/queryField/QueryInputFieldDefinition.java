package com.bot.cqs.query.util.queryField;

public interface QueryInputFieldDefinition {

    /**
     * 欄位名稱, controller 類別將以此取得 command object 的屬性
     * 
     * @return
     */
    public String getFieldName();

    /**
     * 欄位說明, 網頁上會以這個值做為欄位說明
     * 
     * @return
     */
    public String getFieldDesc();

    /**
     * 轉換後指定的屬性名稱, 用以對映到 InquiryLog 的資料
     * 
     * @return
     * @see tw.com.bot.cqs.gateway.persistence.InquiryLog
     */
    public String getTargetFieldName();

    /**
     * 最小長度, -1 代表不限制
     * 
     * @return
     */
    public int getMinLength();

    /**
     * 最大長度, -1 代表不限制
     * 
     * @return
     */
    public int getMaxLength();

    /**
     * 是否為必要欄位
     * 
     * @return
     */
    public boolean isRequired();

    /**
     * 是否先去頭尾空白再檢表與轉換
     * 
     * @return
     */
    public boolean isTrimFirst();

    /**
     * 是否把英文字母轉大寫
     * 
     * @return
     */
    public boolean isToUpperCase();

    /**
     * 是否一律轉為全形字
     * 
     * @return
     */
    public boolean isConvertToDoubleBytes();

    /**
     * 是否為空的或未輸入
     * 
     * @param originalText
     * @return
     * @see isTrimFirst()
     */
    public boolean isEmpty(String originalText);

    /**
     * 轉換為 Gateway 所需要的內容
     * 
     * @param originalText
     * @return
     * @throws QueryFieldException
     */
    public String convert(String originalText) throws QueryFieldException;
}
