package com.bot.cqs.security.support;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.bot.cqs.query.persistence.QueryFunction;

/**
 * <pre>
 * CodeItem RowMapper
 * </pre>
 * 
 * @since 2014/4/30
 * @author Lancelot
 * @version
 *          <ul>
 *          <li>2014/4/30,Lancelot,new
 *          </ul>
 */
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
