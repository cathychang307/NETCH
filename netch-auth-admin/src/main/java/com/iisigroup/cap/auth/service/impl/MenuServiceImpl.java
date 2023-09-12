package com.iisigroup.cap.auth.service.impl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.springframework.stereotype.Service;

import com.bot.cqs.query.dao.QueryFunctionDao;
import com.bot.cqs.query.persistence.QueryFunction;
import com.iisigroup.cap.auth.service.MenuService;
import com.iisigroup.cap.utils.CapAppContext;
import com.iisigroup.cap.utils.CapSystemConfig;

@Service
public class MenuServiceImpl implements MenuService {

    @Resource
    QueryFunctionDao queryFunctionDao;
    @Resource
    private CapSystemConfig config;

    public MenuItem getMenuByRoles(Set<String> roles) {
        Map<String, MenuItem> menuMap = new HashMap<String, MenuItem>();

        // cap根目錄
        MenuItem root = new MenuItem();
        root.setCode("0");

        // cap第一層目錄，會藏起來，看不到
        MenuItem firstLevel = new MenuItem();
        firstLevel.setCode("000");
        firstLevel.setName("firstLevel");
        firstLevel.setUrl("function");

        root.getChild().add(firstLevel);

        List<QueryFunction> list = queryFunctionDao.findMenuDataByRoles(roles);
        for (QueryFunction f : list) {
            MenuItem item = new MenuItem();
            item.setCode(f.getFunctionId());
            item.setName(CapAppContext.getMessage("menu." + f.getFunctionId())); // item.setName(f.getFunctionName());
            item.setUrl(f.getFunctionUri().replace("/query/", "").replace(".htm", ""));// 修正成cap要的url
            menuMap.put(item.getCode(), item);

            MenuItem pItem = menuMap.get(f.getParentFunctionId() == null ? "000" : f.getParentFunctionId());
            if (pItem == null) {
                pItem = firstLevel;
            }
            pItem.getChild().add(item);
        }
        return root;
    }

    public static class MenuItem implements Serializable {

        private static final long serialVersionUID = 7329433370534984288L;
        String code;
        String name;
        String url;
        List<MenuItem> child = new LinkedList<MenuItem>();

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public List<MenuItem> getChild() {
            return child;
        }

        public void setChild(List<MenuItem> child) {
            this.child = child;
        }

        public String toString() {
            return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE, false, false);
        }
    }
}
