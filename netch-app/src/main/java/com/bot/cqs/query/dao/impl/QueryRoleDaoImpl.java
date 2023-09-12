
package com.bot.cqs.query.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.bot.cqs.query.dao.QueryRoleDao;
import com.bot.cqs.query.persistence.QueryRole;
import com.iisigroup.cap.db.constants.SearchMode;
import com.iisigroup.cap.db.dao.SearchSetting;
import com.iisigroup.cap.db.dao.impl.GenericDaoImpl;

/**
 * <pre>
 * QueryRoleDaoImpl
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
public class QueryRoleDaoImpl extends GenericDaoImpl<QueryRole> implements QueryRoleDao {

    public List<QueryRole> findAll() {
        SearchSetting search = createSearchTemplete();
        search.addOrderBy("roleOrder");
        search.setMaxResults(Integer.MAX_VALUE);
        // 這些資料重要, 不要限筆數, 不然會影響營運
        return find(search);
    }

    public QueryRole findQueryRole(String roleId) {
        SearchSetting search = createSearchTemplete();
        search.addSearchModeParameters(SearchMode.EQUALS, "roleId", roleId);
        return findUniqueOrNone(search);
    }

    public void saveQueryRole(QueryRole role) {
        save(role);
    }

    public void updateQueryRole(QueryRole role) {
        save(role);
    }

    public void deleteQueryRole(QueryRole role) {
        delete(role);
    }

    @Override
    public QueryRole findByRoleId(String roleId) {
        SearchSetting search = createSearchTemplete();
        search.addSearchModeParameters(SearchMode.EQUALS, "roleId", roleId);
        return findUniqueOrNone(search);
    }

    @Override
    public List<Map<String, Object>> findByPath(String url) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("url", url);
        return getNamedJdbcTemplate().query("QueryRole_findByPath", param);
    }

    @Override
    public Map<String, QueryRole> findAllRoleMap() {
        List<QueryRole> queryRoleList = findAll();
        Map<String, QueryRole> map = new HashMap<String, QueryRole>();
        for(QueryRole queryRole : queryRoleList){
            map.put(queryRole.getRoleId(), queryRole);
        }
        return map;
    }

}
