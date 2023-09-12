
package com.bot.cqs.query.util.queryField;

public interface QueryRequestDefinition {

    /**
     * 查詢作業的名稱, 這會顯示在 view 的表單上
     * 
     * @return
     */
    public String getQueryName();

    /**
     * 查詢作業的代號, 會顯示在 view 的查詢名稱之後
     * 
     * @return
     * @see getQueryName()
     * @see isDisplayTransactionId()
     */
    public String getTransactionId();

    /**
     * 取得欄位定義
     * 
     * @return
     * @see com.bot.cqs.query.util.queryField.QueryInputFieldDefinition
     */
    public QueryInputFieldDefinition[] getQueryInputFieldDefinition();

    /**
     * 以陣列表示的字串列出所有 QueryInputFieldDefinition 的名稱, 格式為 [ "name1", "name2", ... ]
     * 
     * @return
     * @see getQueryInputFieldDefinition();
     */
    public String getQueryInputFieldNameExpression();

    /**
     * 取得查詢的說明, 會顯示在 view 的說明區塊中
     * 
     * @return
     */
    public String[] getMemo();

    /**
     * 是否在查詢名稱後, 附加查詢代號
     * 
     * @return
     * @see getTransactionId()
     */
    public boolean isDisplayTransactionId();

}
