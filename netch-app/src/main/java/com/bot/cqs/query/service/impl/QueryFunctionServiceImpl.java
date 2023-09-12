package com.bot.cqs.query.service.impl;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.bot.cqs.query.dao.QueryFunctionDao;
import com.bot.cqs.query.persistence.QueryFunction;
import com.bot.cqs.query.service.QueryFunctionService;
import com.iisigroup.cap.utils.CapString;

/**
 * <pre>
 * QueryFunctionServiceImpl
 * </pre>
 * 
 * @since 2017年1月6日
 * @author bob peng
 * @version
 *          <ul>
 *          <li>2017年1月6日,bob peng,new
 *          </ul>
 */
@Service
public class QueryFunctionServiceImpl implements QueryFunctionService {

    @Resource
    private QueryFunctionDao queryFunctionDao;

//    private static final String TABLE = "<table class=\"table table-bordered tb_style_1\">{0}</table>";
//    private static final String TR_TD_ACTIVE = "<tr><td class=\"hd_style_1\">{0}</td></tr>";
//    private static final String TR_TD = "<tr><td class=\"td_style_1\">{0}</td></tr>";
//    private static final String DIV_FUNCTION = "<div class=\"col-md-3 col-sm-6 col-xs-12\"><input type=\"checkbox\" id=\"{0}\" name=\"functionIds\" value=\"{0}\"/><label for=\"{0}\">{1}</label></div>";
//    private static final String STRIKE = "<strike>{0}</strike>";
    private static final String TABLE = "{0}";
    private static final String TR_TD_ACTIVE = "<div class=\"row row-flex row-flex-wrap\"><div class=\"col-sm-12 col-xs-12 hd_style_1\">{0}</div></div>";
    private static final String TR_TD = "<div class=\"row row-flex row-flex-wrap\"><div class=\"col-sm-12 col-xs-12 td_style_1\"><div class=\"row\">{0}</div></div></div>";
    private static final String DIV_FUNCTION = "<div class=\"col-md-3 col-sm-6 col-xs-12\"><input type=\"checkbox\" id=\"{0}\" name=\"functionIds\" value=\"{0}\"/><label for=\"{0}\">{1}</label></div>";
    private static final String STRIKE = "<strike>{0}</strike>";

    @Override
    public String renderFunctionOptions(String[] functionIds) {
        Map<String, MenuItem> menuMap = new HashMap<String, MenuItem>();
        List<MenuItem> menuList = new LinkedList<MenuItem>();
        List<QueryFunction> queryFunctionList = queryFunctionDao.findAll();
        for (QueryFunction queryFunction : queryFunctionList) {
            MenuItem item = new MenuItem();
            item.setFunctionEnabled(queryFunction.isFunctionEnabled());
            item.setFunctionId(queryFunction.getFunctionId());
            item.setFunctionName(queryFunction.getFunctionName());
            menuMap.put(queryFunction.getFunctionId(), item);
            if (queryFunction.isMenu()) {
                menuList.add(item);
            } else {
                MenuItem pItem = menuMap.get(queryFunction.getParentFunctionId());
                pItem.getChild().add(item);
            }
        }
        return renderTableForFuctionOptions(menuList);
    }

    private String renderTableForFuctionOptions(List<MenuItem> menuList) {
        StringBuffer result = new StringBuffer();
        for (MenuItem menuItem : menuList) {
            result.append(renderTrForFunctionOptions(menuItem));
        }
        return MessageFormat.format(TABLE, new Object[] { result.toString() });
    }

    private String renderTrForFunctionOptions(MenuItem menuItem) {
        List<MenuItem> childList = menuItem.getChild();
        StringBuffer childs = new StringBuffer();
        for (MenuItem child : childList) {
            childs.append(MessageFormat.format(DIV_FUNCTION, new Object[] { child.getFunctionId(), child.getCustomedFunctionName() }));
        }
        return MessageFormat.format(TR_TD_ACTIVE, new Object[] { menuItem.getCustomedFunctionName() }) + MessageFormat.format(TR_TD, new Object[] { childs.toString() });
    }

    public static class MenuItem implements Serializable {
        private static final long serialVersionUID = 1L;
        private String functionId;
        private String functionName;
        private boolean functionEnabled;
        List<MenuItem> child = new LinkedList<MenuItem>();

        public String getFunctionId() {
            return functionId;
        }

        public void setFunctionId(String functionId) {
            this.functionId = functionId;
        }

        public String getFunctionName() {
            return functionName;
        }

        public String getCustomedFunctionName() {
            return functionEnabled ? functionName : MessageFormat.format(STRIKE, new Object[] { functionName });
        }

        public void setFunctionName(String functionName) {
            this.functionName = functionName;
        }

        public boolean isFunctionEnabled() {
            return functionEnabled;
        }

        public void setFunctionEnabled(boolean functionEnabled) {
            this.functionEnabled = functionEnabled;
        }

        public List<MenuItem> getChild() {
            return child;
        }

        public void setChild(List<MenuItem> child) {
            this.child = child;
        }

    }

    @Override
    public String getDisplayFunctionStrByFunctionIds(String functionIds) {
        String[] functionIdAry = functionIds.split(",");
        StringBuffer sb = new StringBuffer();
        List<QueryFunction> queryFunctionList = queryFunctionDao.findByfunctionIdAry(functionIdAry);
        for(QueryFunction queryFunction : queryFunctionList){
            sb.append("(");
            sb.append(queryFunction.getFunctionId());
            sb.append(")");
            sb.append(queryFunction.getFunctionName());
            sb.append("<br>");
        }
        return sb.toString();
    }

}
