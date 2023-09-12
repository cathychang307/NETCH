
package com.bot.cqs.query.util.factory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.stereotype.Service;

import com.bot.cqs.query.persistence.ApplicationParameter;
import com.bot.cqs.query.util.ApplicationParameterType;
import com.bot.cqs.query.util.IntApplicationParameterType;

/**
 * <pre>
 * ApplicationParameterFactory
 * </pre>
 * 
 * @since 2017年1月3日
 * @author bob peng
 * @version
 *          <ul>
 *          <li>2017年1月3日,bob peng,new
 *          </ul>
 */
@Service
public class ApplicationParameterFactory {

    public static final String PARAM_ACCOUNT_CACULATE_TYPE = "ACCOUNT_CACULATE_TYPE";
    public static final String PARAM_QUERY_CACHE_INTERVAL = "QUERY_CACHE_INTERVAL";
    public static final String PARAM_QUERY_MAX_ROWS = "QUERY_MAX_ROWS";
    private static final String PARAM_QUERY_PAGE_ROWS = "QUERY_PAGE_ROWS";

    private final Map<String, ApplicationParameterType> parameterTypeMap;
    private final Map<String, ApplicationParameter> parameterMap;
    private final List<ApplicationParameter> parameterList;

    protected ApplicationParameterFactory() {
        DEFAULT_APPLICATION_PARAMETER_FACTORY = this;
        parameterMap = new TreeMap<String, ApplicationParameter>();
        parameterList = new ArrayList<ApplicationParameter>();

        parameterTypeMap = new HashMap<String, ApplicationParameterType>();
        parameterTypeMap.put(PARAM_ACCOUNT_CACULATE_TYPE, new IntApplicationParameterType(0, 1));
        parameterTypeMap.put(PARAM_QUERY_CACHE_INTERVAL, new IntApplicationParameterType(0, 35));
        parameterTypeMap.put(PARAM_QUERY_MAX_ROWS, new IntApplicationParameterType(200, 1500));
        parameterTypeMap.put(PARAM_QUERY_PAGE_ROWS, new IntApplicationParameterType(10, 100));
    }

    /**
     * 帳務計算方式
     * 
     * @return 一個整數, 預設為 0 (一般計算)
     */
    public int getAccountCaculateType() {

        try {
            ApplicationParameter parameter = getApplicationParameter(PARAM_ACCOUNT_CACULATE_TYPE);
            return Integer.parseInt(parameter.getParameterValue());
        } catch (RuntimeException e) {
            return 0;
        }
    }

    /**
     * 取得票信查詢快取天數
     * 
     * @return 一個整數, 預設為 0, 即不使用快取
     */
    public int getQueryCacheInterval() {

        try {
            ApplicationParameter parameter = getApplicationParameter(PARAM_QUERY_CACHE_INTERVAL);
            return Integer.parseInt(parameter.getParameterValue());
        } catch (RuntimeException e) {
            return 0;
        }
    }

    /**
     * 取得網頁查詢 "最大" 筆數
     * 
     * @return 一個整數, 預設為 500 筆
     */
    public int getQueryMaxRows() {

        try {
            ApplicationParameter parameter = getApplicationParameter(PARAM_QUERY_MAX_ROWS);
            return Integer.parseInt(parameter.getParameterValue());
        } catch (RuntimeException e) {
            return 500;
        }
    }

    /**
     * 取得網頁查詢 "每頁" 筆數
     * 
     * @return 一個整數, 預設為 20 筆
     */
    public int getQueryPageRows() {

        try {
            ApplicationParameter parameter = getApplicationParameter(PARAM_QUERY_PAGE_ROWS);
            return Integer.parseInt(parameter.getParameterValue());
        } catch (RuntimeException e) {
            return 20;
        }
    }

    /**
     * 檢核參數
     * 
     * @param parameterName
     * @return 0 : 合法的參數 <br>
     *         -1 : 參數名稱不合法<br>
     *         -2 : 參數值不合法<br>
     */
    public int checkParameterStatus(ApplicationParameter parameter) {

        if (parameter.getParameterName() == null)
            return -1;
        if (parameter.getParameterValue() == null)
            return -2;
        // if ( parameter.getParameterDesc() == null )
        // return -3;

        ApplicationParameterType type = parameterTypeMap.get(parameter.getParameterName());
        if (type != null) {
            if (type.isValueValid(parameter.getParameterValue()))
                return 0;
            else
                return -2;
        } else
            return -1;
    }

    public synchronized ApplicationParameter getApplicationParameter(String parameterName) {

        return parameterMap.get(parameterName);
    }

    public synchronized ApplicationParameterType getApplicationParameterType(String parameterName) {

        return parameterTypeMap.get(parameterName);
    }

    public synchronized void replaceApplicationParameter(ApplicationParameter applicationParameter) {

        if (applicationParameter == null)
            return;

        parameterMap.put(applicationParameter.getParameterName(), applicationParameter);
        resetApplicationParameterList();
    }

    public synchronized void replaceAllApplicationParameter(Map<String, ApplicationParameter> newApplicationParameterMap) {

        parameterMap.clear();
        if (newApplicationParameterMap != null) {
            parameterMap.putAll(newApplicationParameterMap);
            resetApplicationParameterList();
        }
    }

    public synchronized void replaceAllApplicationParameter(List<ApplicationParameter> newParameterList) {

        parameterMap.clear();
        if (newParameterList != null) {
            for (ApplicationParameter parameter : newParameterList)
                parameterMap.put(parameter.getParameterName(), parameter);
            resetApplicationParameterList();
        }

    }

    public synchronized ApplicationParameter[] getApplicationParameterArray() {

        ApplicationParameter[] array = new ApplicationParameter[parameterList.size()];
        return parameterList.toArray(array);
    }

    protected void resetApplicationParameterList() {

        parameterList.clear();
        Iterator<String> ite = parameterMap.keySet().iterator();
        while (ite.hasNext()) {
            parameterList.add(parameterMap.get(ite.next()));
        }
    }

    // ------------- get a new instance of QueryBankFactory
    // in this case, return a default factory only

    private static ApplicationParameterFactory DEFAULT_APPLICATION_PARAMETER_FACTORY = new ApplicationParameterFactory();

    public static ApplicationParameterFactory newInstance() {

        return DEFAULT_APPLICATION_PARAMETER_FACTORY;
    }

}
