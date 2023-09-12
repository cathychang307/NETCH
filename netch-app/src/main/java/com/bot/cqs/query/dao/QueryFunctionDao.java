package com.bot.cqs.query.dao;

import java.util.List;
import java.util.Set;

import com.bot.cqs.query.persistence.QueryFunction;
import com.iisigroup.cap.db.dao.GenericDao;

/**
 * 存取 {@link QueryFunction 系統功能} 的物件.
 *
 * @since 2016年12月30日
 * @author bob peng
 * @version
 *          <ul>
 *          <li>2016年12月30日,bob peng,new
 *          </ul>
 */
public interface QueryFunctionDao extends GenericDao<QueryFunction> {

    /**
     * 讀取所有的功能
     * 
     * @return
     */
    public List<QueryFunction> findAll();

    public List<QueryFunction> findByfunctionIdAry(String[] functionIdAry);
    
    List<QueryFunction> findMenuDataByRoles(Set<String> roles);

    public List<QueryFunction> findByUrl(String url);
}
