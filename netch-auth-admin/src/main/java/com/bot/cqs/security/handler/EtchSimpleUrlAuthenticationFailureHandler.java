package com.bot.cqs.security.handler;

import com.bot.cqs.query.persistence.SessionLog;
import com.bot.cqs.query.service.SessionLogService;
import com.bot.cqs.signon.SignOnException;
import com.iisigroup.cap.utils.CapAppContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.Serializable;

public class EtchSimpleUrlAuthenticationFailureHandler implements AuthenticationFailureHandler {
    protected final Log logger = LogFactory.getLog(getClass());
    protected final static Logger defaultLogger = LoggerFactory.getLogger("queryDefaultLog");
    @Resource
    private SessionLogService sessionLogService;

    private String defaultFailureUrl;
    private boolean forwardToDestination = false;
    private boolean allowSessionCreation = true;
    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    public EtchSimpleUrlAuthenticationFailureHandler() {
    }

    public EtchSimpleUrlAuthenticationFailureHandler(String defaultFailureUrl) {
        setDefaultFailureUrl(defaultFailureUrl);
    }

    /**
     * Performs the redirect or forward to the {@code defaultFailureUrl} if set, otherwise returns a 401 error code.
     * <p>
     * If redirecting or forwarding, {@code saveException} will be called to cache the exception for use in the target view.
     */
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        customWriteLog(request, response, exception);

        if (defaultFailureUrl == null) {
            logger.debug("No failure URL set, sending 401 Unauthorized error");

            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication Failed: " + exception.getMessage());
        } else {
            saveException(request, exception);

            if (forwardToDestination) {
                logger.debug("Forwarding to " + defaultFailureUrl);

                request.getRequestDispatcher(defaultFailureUrl).forward(request, response);
            } else {
                logger.debug("Redirecting to " + defaultFailureUrl);
                redirectStrategy.sendRedirect(request, response, defaultFailureUrl);
            }
        }
    }

    private void customWriteLog(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) {
        String userId = request.getParameter("employee_id");
        String jumperId = request.getParameter("jumper_id");
        defaultLogger.warn(CapAppContext.getMessage("ssoCheckFailed", new Object[] { userId, exception.toString() }));
        String errmsg = "";
        String secondaryStatus = "";
        if (exception != null) {
            errmsg = exception.getMessage();
            if (exception instanceof SignOnException) {
                SignOnException signOnException = (SignOnException) exception;
                secondaryStatus = signOnException.getSecondaryStatus();
            }
        }
        sessionLogService.createAndWriteSessionLogById(request, userId, "", "", SessionLog.PRIMARY_STATUS_LOGIN, secondaryStatus, errmsg, jumperId);
    }

    /**
     * Caches the {@code AuthenticationException} for use in view rendering.
     * <p>
     * If {@code forwardToDestination} is set to true, request scope will be used, otherwise it will attempt to store the exception in the session. If there is no session and
     * {@code allowSessionCreation} is {@code true} a session will be created. Otherwise the exception will not be stored.
     */
    protected final void saveException(HttpServletRequest request, Serializable ser) {
        //Serializable ser = AuthenticationException exception
        if (forwardToDestination) {
            request.setAttribute(WebAttributes.AUTHENTICATION_EXCEPTION, ser);
        } else {
            HttpSession session = request.getSession(false);

            if (session != null || allowSessionCreation) {
                session = request.getSession(true);
                session.setAttribute(WebAttributes.AUTHENTICATION_EXCEPTION, ser);
            }
        }
    }

    /**
     * The URL which will be used as the failure destination.
     *
     * @param defaultFailureUrl
     *            the failure URL, for example "/loginFailed.jsp".
     */
    public void setDefaultFailureUrl(String defaultFailureUrl) {
        Assert.isTrue(UrlUtils.isValidRedirectUrl(defaultFailureUrl), "'" + defaultFailureUrl + "' is not a valid redirect URL");
        this.defaultFailureUrl = defaultFailureUrl;
    }

    protected boolean isUseForward() {
        return forwardToDestination;
    }

    /**
     * If set to <tt>true</tt>, performs a forward to the failure destination URL instead of a redirect. Defaults to <tt>false</tt>.
     */
    public void setUseForward(boolean forwardToDestination) {
        this.forwardToDestination = forwardToDestination;
    }

    /**
     * Allows overriding of the behaviour when redirecting to a target URL.
     */
    public void setRedirectStrategy(RedirectStrategy redirectStrategy) {
        this.redirectStrategy = redirectStrategy;
    }

    protected RedirectStrategy getRedirectStrategy() {
        return redirectStrategy;
    }

    protected boolean isAllowSessionCreation() {
        return allowSessionCreation;
    }

    public void setAllowSessionCreation(boolean allowSessionCreation) {
        this.allowSessionCreation = allowSessionCreation;
    }

}
