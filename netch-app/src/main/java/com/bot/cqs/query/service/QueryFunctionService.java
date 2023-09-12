package com.bot.cqs.query.service;

import com.bot.cqs.query.persistence.QueryRole;

/**
 * <pre>
 * QueryFunctionService
 * </pre>
 * @since  2017年1月6日
 * @author bob peng
 * @version <ul>
 *           <li>2017年1月6日,bob peng,new
 *          </ul>
 */
public interface QueryFunctionService {

    String renderFunctionOptions(String[] functionIds);

    String getDisplayFunctionStrByFunctionIds(String functionIds);


}
