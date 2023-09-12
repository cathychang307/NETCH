package com.iisigroup.cap.auth.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;

import com.bot.cqs.query.dao.QueryFunctionDao;
import com.bot.cqs.query.dao.QueryRoleDao;
import com.bot.cqs.query.persistence.QueryFunction;
import com.iisigroup.cap.auth.service.AccessCtrlService;

//@Service
public class AccessCtrlServiceImpl implements AccessCtrlService {

    @Resource
    private QueryRoleDao queryRoleDao;
    @Resource
    private QueryFunctionDao queryFunctionDao;

    public AccessCtrlServiceImpl() {
        super();
    }

    @Override
    public List<Map<String, Object>> getAuthRolesByUrl(String url) {
        url = url.replaceAll("/page/", "");
        if (url.indexOf("_") > 0) {
            url = url.substring(0, url.lastIndexOf("_"));
        }
        List<Map<String, Object>> mapList = queryRoleDao.findByPath(url);
        return mapList;
    }

    @Override
    public boolean checkThisUrl(String url) {
        url = url.replaceAll("/page/", "");
        if (url.indexOf("_") > 0) {
            url = url.substring(0, url.lastIndexOf("_"));
        }
        List<QueryFunction> list = queryFunctionDao.findByUrl(url);
        if (!CollectionUtils.isEmpty(list)) {
            return true;
        } else {
            return false;
        }

    }
}
