package com.bot.cqs.security.handler;

import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;

import com.bot.cqs.query.persistence.SessionLog;
import com.bot.cqs.query.service.SessionLogService;
import com.bot.cqs.security.model.EtchUserDetails;
import com.iisigroup.cap.security.CapSecurityContext;

/**
 * <pre>
 * Mega e-Loan 子系統登出.
 * </pre>
 * 
 * @since 2012/3/5
 * @author iristu
 * @version
 *          <ul>
 *          <li>2012/3/5,iristu,new
 *          <li>2015/7/7,sk,move
 *          </ul>
 */
public class EtchLogoutHandler extends SimpleUrlLogoutSuccessHandler {

    private static final Logger logger = LoggerFactory.getLogger(EtchLogoutHandler.class);
    protected final static Logger defaultLogger = LoggerFactory.getLogger("queryDefaultLog");

    @Resource(name = "sessionRegistry")
    SessionRegistry sessionRg;
    @Resource
    SessionLogService sessionLogService;

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.security.web.authentication.logout.LogoutSuccessHandler #onLogoutSuccess(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse,
     * org.springframework.security.core.Authentication)
     */
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // 移除文件開啟者,開發環境因多人共用同一個帳號，因些不處理移除動作。
        // EtchUserDetails u = CapSecurityContext.<EtchUserDetails> getUser();

        EtchUserDetails user = null;
        String userId = null;
        try {
            logger.debug(" DO THE LOGGOUT LOG_STEP1, NOW!!");
            if (authentication != null) { // !megaSSO.isDevelopmentMode()
                logger.debug(" DO THE LOGGOUT LOG_STEP2, NOW!!");
                user = (EtchUserDetails) authentication.getPrincipal();
                userId = user.getUserId();
                if (user != null) {
                    logger.debug(userId + " DO THE LOGGOUT LOG_STEP3, NOW!!");
                    sessionLogService.createAndWriteSessionLog(request, user, SessionLog.PRIMARY_STATUS_LOGOUT, SessionLog.SECONDARY_STATUS_NO_MORE_DESCRIPTION, null, null);// 登出 寫紀錄
                }
            }
            logger.debug(" DO THE LOGGOUT LOG_STEP4, NOW!!");

            // 檢核使用者是否已在其它地方登入
            List<SessionInformation> sessions = sessionRg.getAllSessions(authentication.getPrincipal(), false);
            for (SessionInformation sessionInfos : sessions) {
                sessionRg.removeSessionInformation(sessionInfos.getSessionId());
            }

            // megaSSO.eloanApLogout(megaSSO.getSysId(), userId, "N");

        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        if (authentication != null) {
            defaultLogger.warn("使用者 [" + userId + "] 登出");
        }

        super.onLogoutSuccess(request, response, authentication);

    }

}
