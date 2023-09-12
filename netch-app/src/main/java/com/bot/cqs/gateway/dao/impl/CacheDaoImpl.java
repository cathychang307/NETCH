package com.bot.cqs.gateway.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.bot.cqs.gateway.dao.CacheDao;
import com.bot.cqs.gateway.persistence.Cache;
import com.iisigroup.cap.db.constants.SearchMode;
import com.iisigroup.cap.db.dao.SearchSetting;
import com.iisigroup.cap.db.dao.impl.GenericDaoImpl;

@Repository
public class CacheDaoImpl extends GenericDaoImpl<Cache> implements CacheDao {
    /**
     * 刪除 Cache
     * 
     * @param cache
     */
    public void deleteCache(Cache cache) {
        delete(cache);
    }

    /**
     * 列出所有 Cache
     * 
     * @return
     */
    public List<Cache> findAll() {
        SearchSetting search = createSearchTemplete();
        search.addOrderBy("dummyKey");
        return find(search);
    }

    /**
     * 新增 Cache
     * 
     * @param cache
     */
    public void insertCache(Cache cache) {
        save(cache);
    }

    /**
     * 依 dummyKey 找單筆 Cache
     * 
     * @param dummyKey
     * @return
     */
    public Cache findByDummyKey(String dummyKey) {
        SearchSetting search = createSearchTemplete();
        search.addSearchModeParameters(SearchMode.EQUALS, "dummyKey", dummyKey);
        return findUniqueOrNone(search);
    }

    /**
     * 依criterion內的值查詢特定 Cache 資料
     * 
     * @param criterion
     *            為一個 Map<Stirng, Object> 物件(以<code>map.put("欄位名稱", 值)</code>方式輸入)。若對應屬性的值為 null 或是空字串，則不做為查詢條件。
     * @return List<Cache>
     */
    public List<Cache> findByCriterion(Map<String, Object> criterion) {
        // TODO
        return null;
        // String inquiryDate = criterion.get("inquiryDate").toString();
        // String inquiryId = criterion.get("inquiryId").toString();
        // String inquiryBizId = criterion.get("inquiryBizId").toString();
        // String inquiryBankCode = criterion.get("inquiryBankCode").toString();
        // String inquiryBankAccount = criterion.get("inquiryBankAccount")
        // .toString();
        // String inquireResponseFormat = criterion.get("inquireResponseFormat").toString();
        //
        // DetachedCriteria criteria = DetachedCriteria.forClass(Cache.class);
        // if (inquiryId != null && !("".equalsIgnoreCase(inquiryId)))
        // criteria.add(Restrictions.eq("inquiryId", inquiryId));
        // if (inquiryBizId != null && !("".equalsIgnoreCase(inquiryBizId)))
        // criteria.add(Restrictions.eq("inquiryBizId", inquiryBizId));
        // if (inquiryBankCode != null && !("".equalsIgnoreCase(inquiryBankCode)))
        // criteria.add(Restrictions.eq("inquiryBankCode", inquiryBankCode));
        // if (inquiryBankAccount != null
        // && !("".equalsIgnoreCase(inquiryBankAccount)))
        // criteria.add(Restrictions.eq("inquiryBankAccount",
        // inquiryBankAccount));
        // if (inquiryDate != null && !("".equalsIgnoreCase(inquiryDate)))
        // criteria.add(Restrictions.eq("inquiryDate", inquiryDate));
        // if (inquireResponseFormat != null && !("".equalsIgnoreCase(inquireResponseFormat)))
        // criteria.add(Restrictions.eq("inquireResponseFormat", inquireResponseFormat));
        // criteria.addOrder(Order.asc("dummyKey"));
        // return getHibernateTemplate().findByCriteria(criteria);
    }
}
