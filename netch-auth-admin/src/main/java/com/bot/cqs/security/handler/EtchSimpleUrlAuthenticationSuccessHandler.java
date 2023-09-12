package com.bot.cqs.security.handler;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import com.bot.cqs.query.persistence.SessionLog;
import com.bot.cqs.query.service.SessionLogService;
import com.bot.cqs.security.model.EtchUserDetails;
import com.iisigroup.cap.utils.CapAppContext;

public class EtchSimpleUrlAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    protected final static Logger defaultLogger = LoggerFactory.getLogger("queryDefaultLog");
    @Resource
    private SessionLogService sessionLogService;

    public EtchSimpleUrlAuthenticationSuccessHandler() {
    }

    /**
     * Constructor which sets the <tt>defaultTargetUrl</tt> property of the base class.
     * 
     * @param defaultTargetUrl
     *            the URL to which the user should be redirected on successful authentication.
     */
    public EtchSimpleUrlAuthenticationSuccessHandler(String defaultTargetUrl) {
        setDefaultTargetUrl(defaultTargetUrl);
    }

    /**
     * Calls the parent class {@code handle()} method to forward or redirect to the target URL, and then calls {@code clearAuthenticationAttributes()} to remove any leftover session data.
     */
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        customWriteLog(request, response, authentication);
        handle(request, response, authentication);
        clearAuthenticationAttributes(request);
    }

    public void customWriteLog(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        EtchUserDetails user = (EtchUserDetails) authentication.getPrincipal();
        defaultLogger.info(CapAppContext.getMessage("ssoCheckCompleted", new Object[] { user.getUserId(), user.getQueryRole().getRoleName() }));
        sessionLogService.createAndWriteSessionLog(request, user, SessionLog.PRIMARY_STATUS_LOGIN, SessionLog.SECONDARY_STATUS_NO_MORE_DESCRIPTION, null, request.getParameter("jumper_id"));
    }
}
