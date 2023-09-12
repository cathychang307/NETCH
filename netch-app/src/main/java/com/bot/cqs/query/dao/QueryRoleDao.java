
package com.bot.cqs.query.dao;

import java.util.List;
import java.util.Map;

import com.bot.cqs.query.persistence.QueryRole;
import com.iisigroup.cap.db.dao.GenericDao;

/**
 * 存取 {@link QueryRole 角色} 的物件
 *
 * @since 2016年12月30日
 * @author bob peng
 * @version
 *          <ul>
 *          <li>2016年12月30日,bob peng,new
 *          </ul>
 */
public interface QueryRoleDao extends GenericDao<QueryRole> {

    /**
     * 讀取所有 {@link QueryRole 角色}
     * 
     * @return
     */
    public List<QueryRole> findAll();

    /**
     * 依指定 ID 讀取{@link QueryRole 角色}
     * 
     * @param roleId
     * @return
     */
    public QueryRole findQueryRole(String roleId);

    /**
     * 新增一筆{@link QueryRole 角色}資訊
     * 
     * @param role
     */
    public void saveQueryRole(QueryRole role);

    /**
     * 更新一筆已存在的{@link QueryRole 角色}
     * 
     * @param role
     */
    public void updateQueryRole(QueryRole role);

    /**
     * 刪除指定的{@link QueryRole 角色}
     * 
     * @param role
     */
    public void deleteQueryRole(QueryRole role);

    public QueryRole findByRoleId(String roleId);

    public List<Map<String, Object>> findByPath(String url);

    public Map<String, QueryRole> findAllRoleMap();
}
