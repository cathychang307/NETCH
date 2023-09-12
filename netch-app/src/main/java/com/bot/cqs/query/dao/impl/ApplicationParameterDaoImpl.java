
package com.bot.cqs.query.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.bot.cqs.query.dao.ApplicationParameterDao;
import com.bot.cqs.query.persistence.ApplicationParameter;
import com.iisigroup.cap.db.constants.SearchMode;
import com.iisigroup.cap.db.dao.SearchSetting;
import com.iisigroup.cap.db.dao.impl.GenericDaoImpl;

/**
 * <pre>
 * ApplicationParameterDaoImpl
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
public class ApplicationParameterDaoImpl extends GenericDaoImpl<ApplicationParameter> implements ApplicationParameterDao {

    public List<ApplicationParameter> findAll() {
        SearchSetting search = createSearchTemplete();
        search.setMaxResults(Integer.MAX_VALUE);
        // 這些資料重要, 不要限筆數, 不然會影響營運
        return find(search);
    }

    public ApplicationParameter findParameter(String parameterName) {
        SearchSetting search = createSearchTemplete();
        search.addSearchModeParameters(SearchMode.EQUALS, "parameterName", parameterName);
        return findUniqueOrNone(search);
    }

    public void updateParameter(ApplicationParameter applicationParameter) {
        save(applicationParameter);
    }

}
