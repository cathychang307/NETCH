package com.bot.cqs.security.filter;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedCredentialsNotFoundException;

/**
 * <pre>
 * sso filter
 * </pre>
 * 
 * @since 2017年2月21日
 * @author bob peng
 * @version
 *          <ul>
 *          <li>2017年2月21日,bob peng,new
 *          </ul>
 */
public class SsoPreAuthenticationProcessingFilter extends AbstractPreAuthenticatedProcessingFilter {

    private String principalRequestKey = "employee_id";
    private String credentialsRequestKey = "jumper_id";
    private boolean exceptionIfRequestMissing = false;

    protected SsoPreAuthenticationProcessingFilter() {
        super();
    }

    @Override
    protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
        String principal = request.getParameter(principalRequestKey);

        if (principal == null && exceptionIfRequestMissing) {
            throw new PreAuthenticatedCredentialsNotFoundException(principalRequestKey + " header not found in request.");
        }

        return principal;
    }

    @Override
    protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
        if (credentialsRequestKey != null) {
            String credentials = request.getParameter(credentialsRequestKey);

            return credentials;
        }

        return "N/A";
    }

    public void setPrincipalRequestKey(String principalRequestKey) {
        this.principalRequestKey = principalRequestKey;
    }

    public void setCredentialsRequestKey(String credentialsRequestKey) {
        this.credentialsRequestKey = credentialsRequestKey;
    }

    public void setExceptionIfRequestMissing(boolean exceptionIfRequestMissing) {
        this.exceptionIfRequestMissing = exceptionIfRequestMissing;
    }
    
    @Override
    protected boolean principalChanged(HttpServletRequest request, Authentication currentAuthentication) {

        Object principal = getPreAuthenticatedPrincipal(request);
        
        if(principal == null){
            return false; 
        }

        if ((principal instanceof String) && currentAuthentication.getName().equals(principal)) {
            return false;
        }

        if (principal != null && principal.equals(currentAuthentication.getPrincipal())) {
            return false;
        }

        if(logger.isDebugEnabled()) {
            logger.debug("Pre-authenticated principal has changed to " + principal + " and will be reauthenticated");
        }
        return true;
    }
    
    
}
