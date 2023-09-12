package com.iisigroup.cap.auth.vote;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.FilterInvocation;

import com.iisigroup.cap.auth.service.AccessCtrlService;

/**
 * <pre>
 * CapPermissionVoter
 * </pre>
 * 
 * @since 2017年3月30日
 * @author bob peng
 * @version
 *          <ul>
 *          <li>2017年3月30日,bob peng,new
 *          </ul>
 */
public class CapPermissionVoter extends RoleVoter {

    protected AccessCtrlService securityService;

    @SuppressWarnings("rawtypes")
    @Override
    public int vote(Authentication authentication, Object object, Collection<ConfigAttribute> attributes) {
        int result = ACCESS_ABSTAIN;
        Iterator iter = attributes.iterator();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        // [123, 59040000, 123123, 59010000, 59020000, 789, 1231, 59030000]

        while (iter.hasNext()) {
            ConfigAttribute attribute = (ConfigAttribute) iter.next();

            if (this.supports(attribute)) {
                result = ACCESS_DENIED;

                FilterInvocation filterInvocation = (FilterInvocation) object;

                String url = getRequestURL(filterInvocation);

                // 僅檢查該功能第一個頁面，其餘不檢查
                if (!securityService.checkThisUrl(url)) {
                    return ACCESS_GRANTED;
                }

                List<Map<String, Object>> roleIds = securityService.getAuthRolesByUrl(url);

                if (roleIds != null && !roleIds.isEmpty()) {
                    // Attempt to find a matching granted authority
                    for (Map<String, Object> map : roleIds) {
                        for (GrantedAuthority auth : authorities) {
                            if (auth.getAuthority().equals(MapUtils.getString(map, "role_id"))) {
                                return ACCESS_GRANTED;
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    public void setSecurityService(AccessCtrlService securityService) {
        this.securityService = securityService;
    }

    private boolean stripQueryStringFromUrls;

    public boolean isStripQueryStringFromUrls() {
        return stripQueryStringFromUrls;
    }

    public void setStripQueryStringFromUrls(boolean stripQueryStringFromUrls) {
        this.stripQueryStringFromUrls = stripQueryStringFromUrls;
    }

    /**
     * Gets the request url.
     * 
     * @param filter
     *            the filter
     * 
     * @return the request url
     */
    protected String getRequestURL(FilterInvocation filter) {
        String url = filter.getRequestUrl();

        if (stripQueryStringFromUrls) {
            // Strip anything after a question mark symbol, as per SEC-161. See
            // also SEC-321
            int firstQuestionMarkIndex = url.indexOf("?");
            if (firstQuestionMarkIndex != -1) {
                url = url.substring(0, firstQuestionMarkIndex);
            }
        } else {
            int firstQuestionMarkIndex = url.indexOf("?");
            if (firstQuestionMarkIndex != -1) {
                String queryString = url.substring(firstQuestionMarkIndex + 1);
                StringBuffer newQueryString = new StringBuffer();
                String[] query = queryString.split("&");
                for (String q : query) {
                    if (q.startsWith("x=") || q.startsWith("jsessionid=")) {
                        continue;
                    } else {
                        newQueryString.append(q).append('&');
                    }
                }
                if (newQueryString.length() > 0) {
                    newQueryString.deleteCharAt(newQueryString.length() - 1);
                }
                return new StringBuffer(url.substring(0, firstQuestionMarkIndex)).append("?").append(newQueryString.toString()).toString();
            }
        }
        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }
        return url;
    }

}
