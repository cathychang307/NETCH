package com.bot.cqs.query.dao;

import java.util.List;

import com.bot.cqs.query.persistence.QueryBank;
import com.iisigroup.cap.db.dao.GenericDao;

/**
 * <code>QueryBankDao</code> 是用來對 {@link QueryBank (單位/分行)} 存取的物件
 * 
 * @author Damon Lu
 */
public interface QueryBankDao extends GenericDao<QueryBank> {

    /**
     * 依部門 ID 讀取 {@link QueryBank (單位/分行)} 資料
     * 
     * @param departmentId
     * @return
     */
    public QueryBank find(String departmentId);

    /**
     * 依收費行 ID 取得 {@link QueryBank (單位/分行)} 資料
     * 
     * @param chargeBankId
     * @return
     */
    public List<QueryBank> findByChargeBankId(String chargeBankId);

    /**
     * 列出所有 {@link QueryBank (單位/分行)} 資料
     * 
     * @return
     */
    public List<QueryBank> findAll();

    /**
     * 更改一筆 {@link QueryBank (單位/分行)} 資料
     * 
     * @param queryBank
     * @return
     */
    public QueryBank update(QueryBank queryBank);

}
