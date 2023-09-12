package com.bot.cqs.gateway.dao;

import java.util.List;
import java.util.Map;

import com.bot.cqs.gateway.persistence.Cache;
import com.iisigroup.cap.db.dao.GenericDao;

/**
 * Cache dao
 * 
 * @author sunkistwang
 * @since 0.1 2007/06/01
 */
public interface CacheDao extends GenericDao<Cache> {
    /**
     * 列出所有 Cache
     * 
     * @return
     */
    public List<Cache> findAll();

    /**
     * 依 dummyKey 找單筆 Cache
     * 
     * @param dummyKey
     * @return
     */
    public Cache findByDummyKey(String dummyKey);

    /**
     * 新增 Cache
     * 
     * @param cache
     */
    public void insertCache(Cache cache);

    /**
     * 刪除 Cache
     * 
     * @param cache
     */
    public void deleteCache(Cache cache);

    /**
     * 依criterion內的值查詢特定 Cache 資料
     * 
     * @param criterion
     *            為一個 Map<Stirng, Object> 物件(以<code>map.put("欄位名稱", 值)</code>方式輸入)。若對應屬性的值為 null 或是空字串，則不做為查詢條件。
     * @return List<Cache>
     */
    public List<Cache> findByCriterion(Map<String, Object> criterion);
}
