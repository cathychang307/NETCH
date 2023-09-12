
package com.bot.cqs.query.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.bot.cqs.query.dao.QueryFunctionDao;
import com.bot.cqs.query.persistence.QueryFunction;
import com.iisigroup.cap.db.constants.SearchMode;
import com.iisigroup.cap.db.dao.SearchSetting;
import com.iisigroup.cap.db.dao.impl.GenericDaoImpl;

/**
 * <pre>
 * QueryFunctionDaoImpl
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
public class QueryFunctionDaoImpl extends GenericDaoImpl<QueryFunction> implements QueryFunctionDao {

    public List<QueryFunction> findAll() {
        SearchSetting search = createSearchTemplete();
        search.addOrderBy("functionId");
        search.setMaxResults(Integer.MAX_VALUE);
        // 這些資料重要, 不要限筆數, 不然會影響營運
        return find(search);
    }

    @Override
    public List<QueryFunction> findByfunctionIdAry(String[] functionIdAry) {
        SearchSetting search = createSearchTemplete();
        search.addSearchModeParameters(SearchMode.IN, "functionId", functionIdAry);
        return find(search);
    }
    
    @Override
    public List<QueryFunction> findMenuDataByRoles(Set<String> roles) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("role_id", roles);
        return getNamedJdbcTemplate().query("function_findMenu", "", param, new FunctionRowMapper());
    }
    
    
    public class FunctionRowMapper implements RowMapper<QueryFunction> {

        @Override
        public QueryFunction mapRow(ResultSet rs, int rowNum) throws SQLException {
            QueryFunction item = new QueryFunction();
            item.setFunctionId(rs.getString("function_id"));
            item.setFunctionName(rs.getString("function_name"));
            item.setFunctionUri(rs.getString("function_uri"));
            item.setFunctionModifyDate(rs.getTimestamp("function_modify_date"));
            item.setFunctionEnabled("1".equals(rs.getString("function_enabled")) ? true : false);
            item.setMenu("1".equals(rs.getString("is_menu")) ? true : false);
            item.setParentFunctionId(rs.getString("parent_function_id"));
            return item;
        }

    }


    @Override
    public List<QueryFunction> findByUrl(String url) {
        SearchSetting search = createSearchTemplete();
        search.addSearchModeParameters(SearchMode.LIKE, "functionUri", "%" + url + "%");
        return find(search);
        
    }


}
