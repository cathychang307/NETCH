package com.bot.cqs.security.service;

import com.bot.cqs.query.dao.QueryFunctionDao;
import com.bot.cqs.query.dao.QueryRoleDao;
import com.bot.cqs.query.persistence.QueryBank;
import com.bot.cqs.query.persistence.QueryRole;
import com.bot.cqs.query.persistence.QueryUser;
import com.bot.cqs.query.persistence.SessionLog;
import com.bot.cqs.query.util.factory.QueryBankFactory;
import com.bot.cqs.security.model.EtchUserDetails;
import com.bot.cqs.signon.SignOnException;
import com.bot.cqs.signon.service.SsoCheckManager;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * <pre>
 * BotSSOUserDetailsService
 * </pre>
 * 
 * @since 2017年2月21日
 * @author bob peng
 * @version
 *          <ul>
 *          <li>2017年2月21日,bob peng,new
 *          </ul>
 */
public class SsoUserDetailsService implements AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> {

    private static final Logger logger = LoggerFactory.getLogger(SsoUserDetailsService.class);
    @Resource
    private QueryFunctionDao queryFunctionDao;
    @Resource
    private QueryRoleDao queryRoleDao;
    @Resource
    private QueryBankFactory queryBankFactory;
    @Resource(name = "ssoCheckManager")
    private SsoCheckManager ssoCheckManager;
    // @Resource(name = "dummySsoCheckManager")
    // private SsoCheckManager ssoCheckManager;
    private static String SSO_MSG = "<script type=\"text/javascript\">alert(\"您尚未設定使用本項作業之權限\\r\\n請至行內全球資訊網「使用權限維護」設定權限\");top.location.href = \"https://web.bot.com.tw\";</script>";


    /**
     * Get a UserDetails object based on the user name contained in the given token, and the GrantedAuthorities as returned by the GrantedAuthoritiesContainer implementation as returned by the
     * token.getDetails() method.
     */
    @Override
    public final UserDetails loadUserDetails(PreAuthenticatedAuthenticationToken token) throws AuthenticationException {

        String employeeId = (String) token.getPrincipal();
        String jumperId = (String) token.getCredentials();
        // sign on
        EtchUserDetails etchUserDetails = null;
        QueryUser queryUser = null;
        String secondaryStatus = null;
        String memo = null;
        String errorMessage = null;
        try {
            queryUser = ssoCheckManager.checkJumperId(employeeId, jumperId);
            Map<String, QueryRole> roleMap = queryRoleDao.findAllRoleMap();
            Map<String, String> checkRoles = ssoCheckManager.checkInfoId(queryUser, roleMap);
            if (queryUser.getQueryRole() == null) {
                throw new SignOnException(SSO_MSG, null, SessionLog.SECONDARY_STATUS_LOGIN_NO_WORKABLE_ROLE);
            }
            QueryRole queryRole = queryUser.getQueryRole();
            Map<String, String> roles = new HashMap<String, String>();
            if (MapUtils.isEmpty(checkRoles)) {
                roles.put(queryRole.getRoleId(), queryRole.getRoleName());
            } else {
                roles.putAll(checkRoles);
            }
            QueryBank queryBank = queryBankFactory.getQueryBank(queryUser.getDepartmentId());
            logger.debug("QueryBank:" + queryBank + ", QueryUser DepartmentId:" + queryUser.getDepartmentId());
            String chargeBankId = queryBank.getChargeBankId();
            String chargeBankName = queryBank.getChargeBankName();
            etchUserDetails = new EtchUserDetails(queryUser, chargeBankId, chargeBankName, "", roles);
        } catch (SignOnException e) {
            secondaryStatus = e.getSecondaryStatus();
            errorMessage = e.getMessage();
            memo = e.getMessage();
            if (errorMessage != null && errorMessage.length() > 30)
                memo = errorMessage.substring(0, 27) + "...";
            else
                memo = errorMessage;
            throw e;
        }catch (Exception e){
            logger.error("UserDetails Exception:" + e);
            throw e;
        }

        return etchUserDetails;
    }

}
