
package com.bot.cqs.query.service;

import java.util.List;

import com.bot.cqs.query.persistence.QueryBank;

public interface QueryBankManager extends QueryManager {

    /**
     * 重新讀取一筆分行資料至記 cache 中, 可以由 {@link tw.com.bot.cqs.query.util.QueryBankFactory} 中取得
     * 
     * @param departmentId
     */
    // public void reload( String departmentId );

    /**
     * 重新讀取所有分行資料至記 cache 中, 可以由 {@link com.bot.cqs.query.util.factory.QueryBankFactory} 中取得
     * 
     */
    public void reload();

    /**
     * 依 bank ID 讀取 Bank 資料
     * 
     * @param departmentId
     * @return
     */
    public QueryBank find(String departmentId);

    /**
     * 依收費行 ID 取得 Bank 資料
     * 
     * @param chargeBankId
     * @return
     */
    public List<QueryBank> findByChargeBankId(String chargeBankId);

    /**
     * 列出所有 Bank 資料
     * 
     * @return
     */
    public List<QueryBank> findAll();

    /**
     * 更改一筆 Bank 資料
     * 
     * @param queryBank
     * @return
     */
    public QueryBank update(QueryBank queryBank);

    /**
     * 刪除一筆 Bank 資料
     * 
     * @param queryBank
     * @return
     */
    public QueryBank delete(QueryBank queryBank);

    /**
     * 新增一筆 Bank 資料
     * 
     * @param queryBank
     * @return
     */
    public Object save(QueryBank queryBank);
}
