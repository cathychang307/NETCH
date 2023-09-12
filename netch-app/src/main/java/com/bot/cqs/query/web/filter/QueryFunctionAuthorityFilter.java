package com.bot.cqs.query.web.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bot.cqs.query.persistence.QueryFunction;
import com.bot.cqs.query.web.WebAction;
import com.iisigroup.cap.component.Request;
import com.iisigroup.cap.component.impl.CapSpringMVCRequest;

public class QueryFunctionAuthorityFilter extends AbstractFilter {

    @Override
    protected boolean doInternalFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        return true;
        // 這不檢查是否 Login, 那該是其它 filter 的事
        // QueryUser user = WebAction.getQueryUser(request.getSession());
        // Map<String, QueryFunction> funcUriMap = WebAction.getQueryFunctionUriMap(request.getSession().getServletContext());
        //
        // String requestURI = request.getRequestURI();
        // String contextPath = request.getContextPath();
        // String[] applicationURI = requestURI.split(contextPath);
        //
        // // 理論上不會發生, 丟出不處理
        // if (applicationURI.length != 2)
        // throw new ServletException("Error when parsing URI [" + requestURI + "]");
        //
        // QueryFunction requestFunction = funcUriMap.get(applicationURI[1]);
        // boolean allowFunction = false;
        // if (requestFunction == null) {
        // WebAction.getDefaultLogger().warn(CapAppContext.getMessage("accessFunctionNotFound.2", new Object[] { user.getEmployeeId(), applicationURI[1] }));
        // throw new NoSuchFunctionException();
        // } else {
        // if (!requestFunction.isFunctionEnabled()) {
        // String message = CapAppContext.getMessage("functionDisabled", new Object[] { requestFunction.getFunctionName(), requestFunction.getFunctionId() });
        // throw new FunctionDisabledException(message);
        // }
        //
        // allowFunction = user.hasFunctionAuthority(requestFunction);
        // }
        //
        // if (!allowFunction)
        // WebAction.getDefaultLogger().warn(CapAppContext.getMessage("accessNoAuthorizationFunction.4",
        // new Object[] { user.getEmployeeId(), requestFunction.getFunctionId(), requestFunction.getFunctionName(), requestFunction.getFunctionUri() }));
        // else
        // // 產生本功能的選單路徑, 給網頁用
        // generateFunctionPath(request, requestFunction);
        //
        // return allowFunction;
    }

    /**
     * 做一個 function path 提供頁面使用. 產生方法: 由 function 尋找 parent 並放入 Stack 中
     * 
     * @param request
     * @param requestFunction
     */
    private void generateFunctionPath(HttpServletRequest request, QueryFunction requestFunction) {

        Stack<QueryFunction> functionPath1 = new Stack<QueryFunction>();
        functionPath1.push(requestFunction);
        Map<String, QueryFunction> functionMap = WebAction.getQueryFunctionMap(request.getSession().getServletContext());

        QueryFunction currentFunctionLevel = requestFunction;
        while (currentFunctionLevel.getParentFunctionId() != null) {
            String parentId = currentFunctionLevel.getParentFunctionId();
            currentFunctionLevel = functionMap.get(parentId);
            if (currentFunctionLevel == null)
                break;
            else
                functionPath1.push(currentFunctionLevel);
        }

        List<QueryFunction> functionPath2 = new ArrayList<QueryFunction>();
        while (functionPath1.size() > 0)
            functionPath2.add(functionPath1.pop());
        Request capRequest = new CapSpringMVCRequest();
        capRequest.setRequestObject(request);
        WebAction.setCurrentFunction(capRequest, requestFunction);
        WebAction.setCurrentFunctionPath(capRequest, functionPath2);
    }
}
