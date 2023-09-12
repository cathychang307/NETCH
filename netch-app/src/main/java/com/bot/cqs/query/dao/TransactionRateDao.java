
package com.bot.cqs.query.dao;

import java.util.List;

import com.bot.cqs.query.persistence.TransactionRate;
import com.bot.cqs.query.persistence.TransactionRateKey;
import com.iisigroup.cap.db.dao.GenericDao;

/**
 * 存取 {@link TransactionRate 查詢費率} 的物件
 *
 * @since 2016年12月30日
 * @author bob peng
 * @version
 *          <ul>
 *          <li>2016年12月30日,bob peng,new
 *          </ul>
 */
public interface TransactionRateDao extends GenericDao<TransactionRate> {

    /**
     * 依指定的 ID 取得 {@link TransactionRate 查詢費率} 列表
     * 
     * @param transactionId
     * @return
     */
    public List<TransactionRate> find(String transactionId);

    /**
     * 依 {@link TransactionRateKey 查詢費率的索引} <i>( primary key )</i>查詢指定的費率
     * 
     * @param key
     * @return
     */
    public TransactionRate find(TransactionRateKey key);

    /**
     * 取得全部的 {@link TransactionRate 查詢費率} 列表
     * 
     * @return
     */
    public List<TransactionRate> findAll();

    /**
     * 更新一筆 {@link TransactionRate 查詢費率}
     * 
     * @param transactionRate
     */
    public void update(TransactionRate transactionRate);

}
