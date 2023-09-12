package com.bot.cqs.security.entryPoint;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

/**
 * <pre>
 * 當session過期時的動作， 若Ajax Request時需記錄為AjaxRequest，導致loginFormUrl以便判別 若為一般頁面之Request時，需導到loginFormUrl
 * </pre>
 * @since  2017年2月7日
 * @author bob peng
 * @version <ul>
 *           <li>2017年2月7日,bob peng,new
 *          </ul>
 */
@SuppressWarnings("deprecation")
public class EtchAuthenticationEntryPoint extends LoginUrlAuthenticationEntryPoint {

    private static final Log logger = LogFactory.getLog(EtchAuthenticationEntryPoint.class);
    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    public EtchAuthenticationEntryPoint(String loginFormUrl) {
        super(loginFormUrl);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint#commence(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse,
     * org.springframework.security.core.AuthenticationException)
     */
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        String redirectUrl = null;
        if (super.isUseForward()) {
            if (super.isForceHttps() && "http".equals(request.getScheme())) {
                // First redirect the current request to HTTPS.
                // When that request is received, the forward to the login page will be used.
                redirectUrl = buildHttpsRedirectUrlForRequest(request);
            }
            if (redirectUrl == null) {
                String loginForm = determineUrlToUseForThisRequest(request, response, authException);
                if (logger.isDebugEnabled()) {
                    logger.debug("Server side forward to: " + loginForm);
                }
                RequestDispatcher dispatcher = request.getRequestDispatcher(loginForm);
                dispatcher.forward(request, response);
                return;
            }
        } else {
            // redirect to login page. Use https if forceHttps true
            redirectUrl = buildRedirectUrlToLoginPage(request, response, authException);
        }
        // ajax redirect 無作用，set customize response code
        if (redirectUrl.indexOf("ajax=1") > 0) {
            RequestDispatcher dispatcher = request.getRequestDispatcher(determineUrlToUseForThisRequest(request, response, authException));
            response.setStatus(999);
            dispatcher.forward(request, response);
        } else {
            redirectStrategy.sendRedirect(request, response, redirectUrl);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.security.ui.AuthenticationEntryPoint#commence(javax .servlet.ServletRequest, javax.servlet.ServletResponse, org.springframework.security.AuthenticationException)
     */
    protected String determineUrlToUseForThisRequest(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String xReq = httpRequest.getHeader("x-requested-with");
        if ("XMLHttpRequest".equalsIgnoreCase(xReq)) {
            return new StringBuffer(getLoginFormUrl()).append("?ajax=1").toString();
        } else {
            return getLoginFormUrl();
        }
    }

}
