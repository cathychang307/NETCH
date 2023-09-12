
package com.bot.cqs.query.dao;

import java.util.List;

import com.bot.cqs.query.persistence.ApplicationParameter;
import com.iisigroup.cap.db.dao.GenericDao;

/**
 * <code>ApplicationParameterDao</code> 是用來存取 {@link ApplicationParameter 應用程式參數} 的物件
 * 
 * @since 2016年12月30日
 * @author bob peng
 * @version
 *          <ul>
 *          <li>2016年12月30日,bob peng,new
 *          </ul>
 */
public interface ApplicationParameterDao extends GenericDao<ApplicationParameter> {

    /**
     * 取得所有應用程式參數
     * 
     * @return
     */
    public List<ApplicationParameter> findAll();

    /**
     * 取得指定的應用程式參數
     * 
     * @param parameterName
     * @return
     */
    public ApplicationParameter findParameter(String parameterName);

    /**
     * 更新一個應用程式參數
     * 
     * @param applicationParameter
     */
    public void updateParameter(ApplicationParameter applicationParameter);
}
