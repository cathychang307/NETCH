
package com.bot.cqs.query.util;

import java.lang.reflect.Method;

import com.bot.cqs.query.util.factory.ApplicationParameterFactory;

public class PageInfo {

    public static final int FIRST_PAGE = 1;
    public static final int UNKNOWN = -1;

    private int page;
    private int lastPage;
    private int recordCount;
    private ApplicationParameterFactory applicationParameterFactory;

    public PageInfo() {

        setPage(FIRST_PAGE);
        setLastPage(UNKNOWN);
        applicationParameterFactory = ApplicationParameterFactory.newInstance();
    }

    public int getLastPage() {

        return lastPage;
    }

    public void setLastPage(int lastPage) {

        this.lastPage = lastPage;
    }

    public int getPage() {

        return page;
    }

    public void setPage(int page) {

        this.page = page;
    }

    public int getPageSize() {

        return applicationParameterFactory.getQueryPageRows();
    }

    public int getRecordCount() {

        return recordCount;
    }

    public void setRecordCount(int recordCount) {

        this.recordCount = recordCount;

        if (recordCount < 0)
            setLastPage(UNKNOWN);
        if (recordCount == 0)
            setLastPage(0);

        int pageSize = applicationParameterFactory.getQueryPageRows();
        int maxPage = recordCount / pageSize;
        if (recordCount % pageSize > 0)
            maxPage++;

        setLastPage(maxPage);
    }

    public int getMaxRecordCount() {

        return applicationParameterFactory.getQueryMaxRows();
    }

    public boolean isRecordCountMaximum() {

        return getRecordCount() >= getMaxRecordCount();
    }

    public static int getPage(Object obj) {

        return getIntAttribute(obj, "getPage", FIRST_PAGE);
    }

    public static void setPage(Object obj, int value) {

        setIntAttribute(obj, "setPage", value);
    }

    public static int getLastPage(Object obj, int defaultValue) {

        return getIntAttribute(obj, "getLastPage", UNKNOWN);
    }

    public static void setLastPage(Object obj, int value) {

        setIntAttribute(obj, "setLastPage", value);
    }

    public static int getIntAttribute(Object obj, String name, int defaultValue) {

        if (obj == null)
            return defaultValue;

        try {
            Method method = obj.getClass().getMethod(name, new Class[] {});
            int page = (Integer) method.invoke(obj, new Object[] {});
            return page;
        } catch (Exception e) {
            String msg = obj.getClass() + "," + name;
            // WebAction.getDefaultLogger().warn(
            // "getIntAttribute error : " + msg,
            // e );
            return defaultValue;
        }
    }

    public static void setIntAttribute(Object obj, String name, int value) {

        if (obj == null)
            return;

        try {
            Method method = obj.getClass().getMethod(name, new Class[] { int.class });
            method.invoke(obj, new Object[] { value });

        } catch (Exception e) {
            String msg = obj.getClass() + "," + name;
            // WebAction.getDefaultLogger().warn(
            // "setIntAttribute error : " + msg,
            // e );
        }
    }
}
