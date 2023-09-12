
package com.bot.cqs.query.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.bot.cqs.query.dao.QueryBankDao;
import com.bot.cqs.query.persistence.QueryBank;
import com.iisigroup.cap.db.constants.SearchMode;
import com.iisigroup.cap.db.dao.SearchSetting;
import com.iisigroup.cap.db.dao.impl.GenericDaoImpl;

/**
 * <pre>
 * QueryBankDaoImpl
 * </pre>
 * 
 * @since 2016年12月30日
 * @author bob peng
 * @version
 *          <ul>
 *          <li>2016年12月30日,bob peng,new
 *          </ul>
 */
@Repository
public class QueryBankDaoImpl extends GenericDaoImpl<QueryBank> implements QueryBankDao {

    public QueryBank find(String departmentId) {
        SearchSetting search = createSearchTemplete();
        search.addSearchModeParameters(SearchMode.EQUALS, "departmentId", departmentId);
        return findUniqueOrNone(search);
    }

    public List<QueryBank> findAll() {
        SearchSetting search = createSearchTemplete();
        search.addOrderBy("departmentId");
        search.setMaxResults(Integer.MAX_VALUE);
        // 這些資料重要, 不要限筆數, 不然會影響營運
        return find(search);
    }

    public List<QueryBank> findByChargeBankId(String chargeBankId) {
        SearchSetting search = createSearchTemplete();
        search.addSearchModeParameters(SearchMode.EQUALS, "chargeBankId", chargeBankId);
        return find(search);
    }

    public QueryBank update(QueryBank queryBank) {
        save(queryBank);
        return queryBank;
    }

}
