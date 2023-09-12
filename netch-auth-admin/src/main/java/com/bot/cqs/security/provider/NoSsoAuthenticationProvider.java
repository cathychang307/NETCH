package com.bot.cqs.security.provider;

import com.bot.cqs.query.persistence.SessionLog;
import com.bot.cqs.query.service.SessionLogService;
import com.bot.cqs.security.model.EtchUserDetails;
import com.bot.cqs.security.support.NoSsoVerifyUtil;
import com.iisigroup.cap.auth.exception.CapAuthenticationException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import javax.annotation.Resource;

public class NoSsoAuthenticationProvider implements AuthenticationProvider {

    private final Logger logger = LoggerFactory.getLogger(NoSsoAuthenticationProvider.class);
    protected final static Logger defaultLogger = LoggerFactory.getLogger("queryDefaultLog");
    private UserDetailsService userService;
    @Resource
    private SessionLogService sessionLogService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = String.valueOf(authentication.getPrincipal());
        String password = String.valueOf(authentication.getCredentials());
        WebAuthenticationDetails webDetail = (WebAuthenticationDetails) authentication.getDetails();
        String ipAddress = webDetail.getRemoteAddress();
        logger.debug("Checking authentication for user {}", username);
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            throw new CapAuthenticationException("請輸入帳號、密碼");
        } else {
            logger.debug("wrongCount-{}: {}", username);
            EtchUserDetails user;
            try {
                user = (EtchUserDetails) userService.loadUserByUsername(username);
                user.setIpAddress(ipAddress);
            } catch (Exception e) {
                throw new CapAuthenticationException(e.getMessage());
            }
            boolean currentPwdVerified = NoSsoVerifyUtil.verifyPwd(username, authentication.getCredentials().toString(), user.getPassword());
            if (currentPwdVerified) {
                defaultLogger.warn("使用者 [" + username + "] 獨立帳密登入");
                sessionLogService.createAndWriteSessionLog(ipAddress, user, SessionLog.PRIMARY_STATUS_LOGIN, SessionLog.SECONDARY_STATUS_NO_MORE_DESCRIPTION, "獨立帳密登入", null);// 登出 寫紀錄
                return new UsernamePasswordAuthenticationToken(user, password, user.getAuthorities());
            } else {
                throw new CapAuthenticationException("密碼輸入錯誤。");
            }
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
    }

    public UserDetailsService getUserService() {
        return userService;
    }

    public void setUserService(UserDetailsService userService) {
        this.userService = userService;
    }

}
