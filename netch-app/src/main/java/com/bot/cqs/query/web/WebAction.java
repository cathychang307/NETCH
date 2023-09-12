package com.bot.cqs.query.web;

import com.bot.cqs.query.persistence.QueryFunction;
import com.bot.cqs.query.persistence.QueryRole;
import com.bot.cqs.query.persistence.QueryUser;
import com.bot.cqs.query.persistence.SessionLog;
import com.iisigroup.cap.component.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.util.*;

public class WebAction {

    public static String getOrganizationUrl(ServletContext servletContext) {
        return servletContext.getInitParameter(WebVar.APPLICATION_ORGANIZATION_URL);
    }

    public static String getHomePage(ServletContext servletContext) {
        return servletContext.getInitParameter(WebVar.APPLICATION_HOME_PAGE);
    }

    public static String getWelcomePage(ServletContext servletContext) {
        return servletContext.getInitParameter(WebVar.APPLICATION_WELCOME_PAGE);
    }

    // ----------- Function Map ( function ID )
    public static Map<String, QueryFunction> getQueryFunctionMap(ServletContext servletContext) {

        return (Map<String, QueryFunction>) servletContext.getAttribute(WebVar.APPLICATION_FUNCTION_MAP);
    }

    public static void setQueryFunctionMap(ServletContext servletContext, Map<String, QueryFunction> queryFunction) {

        servletContext.setAttribute(WebVar.APPLICATION_FUNCTION_MAP, queryFunction);
    }

    public static void clearQueryFunctionMap(ServletContext servletContext) {

        setQueryFunctionMap(servletContext, null);
    }

    public static Map<String, QueryFunction> generateQueryFunctionMap(List<QueryFunction> queryFunction) {

        if (queryFunction == null)
            return null;
        Map<String, QueryFunction> map = new TreeMap<String, QueryFunction>();
        for (QueryFunction function : queryFunction) {
            map.put(function.getFunctionId(), function);
        }

        return map;
    }

    // ----------- Function Map ( function URI )

    public static Map<String, QueryFunction> getQueryFunctionUriMap(ServletContext servletContext) {

        return (Map<String, QueryFunction>) servletContext.getAttribute(WebVar.APPLICATION_FUNCTION_URI_MAP);
    }

    public static void setQueryFunctionUriMap(ServletContext servletContext, Map<String, QueryFunction> queryFunction) {

        servletContext.setAttribute(WebVar.APPLICATION_FUNCTION_URI_MAP, queryFunction);
    }

    // public static void setUserContext( ServletContext servletContext,
    // UserContext userContext ) {
    //
    // servletContext.setAttribute(
    // WebVar.APPLICATION_ESC_USER,
    // userContext );
    // }
    // public static UserContext getUserContext( ServletContext servletContext ){
    // return (UserContext) servletContext.getAttribute( WebVar.APPLICATION_ESC_USER );
    // }

    public static void clearQueryFunctionUriMap(ServletContext servletContext) {

        setQueryFunctionUriMap(servletContext, null);
    }

    public static Map<String, QueryFunction> generateQueryFunctionUriMap(List<QueryFunction> queryFunction) {

        if (queryFunction == null)
            return null;
        Map<String, QueryFunction> map = new HashMap<String, QueryFunction>();
        for (QueryFunction function : queryFunction) {
            if (!function.isMenu())
                map.put(function.getFunctionUri(), function);
        }

        return map;
    }

    // ----------- Role Map ( role ID )
    public static Map<String, QueryRole> getQueryRoleMap(ServletContext servletContext) {

        return (Map<String, QueryRole>) servletContext.getAttribute(WebVar.APPLICATION_ROLE_MAP);
    }

    public static void setQueryRoleMap(ServletContext servletContext, Map<String, QueryRole> queryRolen) {

        servletContext.setAttribute(WebVar.APPLICATION_ROLE_MAP, queryRolen);
    }

    public static void clearQueryRoleMap(ServletContext servletContext) {

        setQueryRoleMap(servletContext, null);
    }

    public static Map<String, QueryRole> generateQueryRoleMap(List<QueryRole> queryRole, Map<String, QueryFunction> functionMap) {

        if (queryRole == null)
            return null;

        Map<String, QueryRole> map = new HashMap<String, QueryRole>();
        for (QueryRole role : queryRole) {

            map.put(role.getRoleId(), role);

            String functionStr = role.getRoleFunction();
            StringTokenizer tokenizer = new StringTokenizer(functionStr, ",");

            while (tokenizer.hasMoreTokens()) {
                String functionId = tokenizer.nextToken();
                QueryFunction function = functionMap.get(functionId);
                if (function != null) {
                    role.addExecutableFunction(function);
                } else {
                    getDefaultLogger().warn("function [" + functionId + "] is invalid");
                }
            }

        }

        return map;
    }

    // ----------------- log ------------------------------------------------
    public static Logger getDefaultLogger() {

        return LoggerFactory.getLogger("queryDefaultLog");
    }

    public static Logger getSsoCheckLogger() {

        return LoggerFactory.getLogger("ssoCheckLog");
    }

    // ------------------ Login User (Query) --------------------------------
    public static QueryUser getQueryUser(HttpSession session) {

        return (QueryUser) session.getAttribute(WebVar.SESSION_QUERY_USER);
    }

    public static void setQueryUser(Request request, QueryUser user) {

        request.getServletRequest().setAttribute(WebVar.SESSION_QUERY_USER, user);
    }

    // ----------------------------------------------------------------------
    public static void setCurrentFunction(Request request, QueryFunction requestFunction) {

        request.getServletRequest().setAttribute(WebVar.REQUEST_CURRENT_FUNCTION, requestFunction);
    }

    public static QueryFunction getCurrentFunction(Request request) {

        return (QueryFunction) request.getServletRequest().getAttribute(WebVar.REQUEST_CURRENT_FUNCTION);
    }

    public static void setCurrentFunctionPath(Request request, List<QueryFunction> requestFunctionPath) {

        request.getServletRequest().setAttribute(WebVar.REQUEST_CURRENT_FUNCTION_PATH, requestFunctionPath);
    }

    public static List<QueryFunction> getCurrentFunctionPath(Request request) {

        return (List<QueryFunction>) request.getServletRequest().getAttribute(WebVar.REQUEST_CURRENT_FUNCTION_PATH);
    }

    // -----------------------------------------------------------------------

    public static byte[] getResponseData(Request request, String message) throws UnsupportedEncodingException {

        if (message == null)
            return new byte[0];
        else
            return message.getBytes(request.getServletRequest().getCharacterEncoding());
    }

    // ------------------------------------------------------------------------

    public static SessionLog createSessionLog(Request request, QueryUser user, String primaryStatus, String secondaryStatus) {

        return createSessionLog(request, user, primaryStatus, primaryStatus, null, null);
    }

    public static SessionLog createSessionLog(Request request, QueryUser user, String primaryStatus, String secondaryStatus, String memo, String correlationId) {

        String ip = request.getServletRequest().getRemoteAddr() + "#" + request.getServletRequest().getRemotePort();
        return createSessionLogInternal(user, primaryStatus, secondaryStatus, memo, correlationId, ip);
    }

    public static SessionLog createSessionLog(QueryUser user, String primaryStatus, String secondaryStatus) {

        return createSessionLog(user, primaryStatus, primaryStatus, null, null);
    }

    public static SessionLog createSessionLog(QueryUser user, String primaryStatus, String secondaryStatus, String memo, String correlationId) {

        return createSessionLogInternal(user, primaryStatus, secondaryStatus, memo, correlationId, null);
    }

    public static SessionLog createSessionLogInternal(QueryUser user, String primaryStatus, String secondaryStatus, String memo, String correlationId, String ip) {

        SessionLog log = new SessionLog();
        log.setAccessDatetime(new Date());
        log.setAccessIp(ip);
        log.setDepartmentId(user.getDepartmentId());
        log.setDepartmentName(user.getDepartmentName());
        log.setRoleId(user.getQueryRole() != null ? user.getQueryRole().getRoleId() : "");
        log.setRoleName(user.getQueryRole() != null ? user.getQueryRole().getRoleName() : null);
        log.setMemo(memo);
        log.setPrimaryStatus(primaryStatus);
        log.setSecondaryStatus(secondaryStatus);
        log.setRank(user.getRank());
        log.setUserId(user.getEmployeeId());
        log.setUserName(user.getEmployeeName());
        log.setCorrelationId(correlationId);
        return log;
    }

    public static SessionLog createSessionLogById(HttpServletRequest request, String userId, String departmentId, String roleId, String primaryStatus, String secondaryStatus, String memo, String correlationId) {

        SessionLog log = new SessionLog();
        log.setAccessDatetime(new Date());
        log.setAccessIp(request.getRemoteAddr() + "#" + request.getRemotePort());
        log.setDepartmentId(departmentId);
        log.setRoleId(roleId);
        log.setPrimaryStatus(primaryStatus);
        log.setSecondaryStatus(secondaryStatus);
        log.setUserId(userId);
        return log;
    }
}
