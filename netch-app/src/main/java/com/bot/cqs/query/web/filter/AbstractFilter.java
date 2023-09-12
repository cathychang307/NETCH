package com.bot.cqs.query.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class AbstractFilter implements Filter {

    private String errorPage;
    private boolean redirectWhenError;

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        if (!(request instanceof HttpServletRequest) || !(response instanceof HttpServletResponse))
            throw new ServletException("QuerySessionFilter just supports HTTP requests");

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // test
        // System.out.println( httpRequest.getRequestURI());
        // Enumeration<String> enu = httpRequest.getParameterNames();
        // while( enu.hasMoreElements()) {
        // String name = enu.nextElement();
        // System.out.println( name + "=\t" + httpRequest.getParameter( name ));
        // }

        // 主要工作
        boolean isPass = doInternalFilter(httpRequest, httpResponse, chain);

        if (isPass) {
            chain.doFilter(request, response);
            return;
        } else {
            if (isRedirectWhenError())
                (httpResponse).sendRedirect(getErrorPage());
            else
                request.getRequestDispatcher(getErrorPage()).forward(request, response);
            return;
        }
    }

    public void destroy() {

    }

    public void init(FilterConfig filterConfig) throws ServletException {

        String redirectWhenError = filterConfig.getInitParameter("redirectWhenError");
        if (redirectWhenError == null)
            setRedirectWhenError(false);
        else
            setRedirectWhenError(redirectWhenError.trim().equalsIgnoreCase("true"));

        String errorPage = filterConfig.getInitParameter("errorPage");
        setErrorPage(errorPage == null ? null : errorPage.trim());
    }

    /**
     * @param request
     * @param response
     * @param chain
     * @return true is pass filter, otherwise false
     * @throws IOException
     * @throws ServletException
     */
    protected abstract boolean doInternalFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException;

    public String getErrorPage() {

        return errorPage;
    }

    public void setErrorPage(String errorPage) {

        this.errorPage = errorPage;
    }

    public boolean isRedirectWhenError() {

        return redirectWhenError;
    }

    public void setRedirectWhenError(boolean redirectWhenError) {

        this.redirectWhenError = redirectWhenError;
    }

}
