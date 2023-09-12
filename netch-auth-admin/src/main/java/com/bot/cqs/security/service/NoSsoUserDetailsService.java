package com.bot.cqs.security.service;

import com.bot.cqs.query.dao.QueryFunctionDao;
import com.bot.cqs.query.dao.QueryRoleDao;
import com.bot.cqs.query.persistence.QueryBank;
import com.bot.cqs.query.persistence.QueryRole;
import com.bot.cqs.query.persistence.QueryUser;
import com.bot.cqs.query.util.factory.QueryBankFactory;
import com.bot.cqs.security.model.EtchUserDetails;
import com.iisigroup.cap.security.model.CapUserDetails;
import com.iisigroup.cap.security.model.User;
import com.iisigroup.cap.utils.CapString;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.XPP3Reader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.xmlpull.v1.XmlPullParserException;

import javax.annotation.Resource;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <pre>
 * BotNoSSOUserDetailsService
 * </pre>
 * 
 * @since 2017年2月21日
 * @author bob peng
 * @version
 *          <ul>
 *          <li>2017年2月21日,bob peng,new
 *          </ul>
 */
public class NoSsoUserDetailsService implements UserDetailsService {

    @Resource
    private QueryFunctionDao queryFunctionDao;
    @Resource
    private QueryRoleDao queryRoleDao;
    @Resource
    private QueryBankFactory queryBankFactory;
    private final Logger logger = LoggerFactory.getLogger(NoSsoUserDetailsService.class);

    public Document parse(URL url) throws DocumentException, IOException, XmlPullParserException {
        XPP3Reader reader = new XPP3Reader();
        Document document = reader.read(url);
        return document;
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.springframework.security.userdetails.UserDetailsService# loadUserByUsername(java.lang.String)
     */
    public UserDetails loadUserByUsername(String username) {
        if (CapString.isEmpty(username)) {
            throw new UsernameNotFoundException("Empty login");
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Security verification for user '" + username + "'");
        }
        CapUserDetails userDetails = obtainNoSsoUserDetails(username);
        if (userDetails != null) {
            return userDetails;
        } else {
            throw new UsernameNotFoundException("帳號[" + username + "]輸入錯誤");
        }

    }

    public CapUserDetails obtainNoSsoUserDetails(String usernameFromUserInput) {

        String userName = "";
        String userPa88wd = "";
        String roleId = "";
        String roleName = "";
        String departmentId = "";
        String departmentName = "";

        String chargeBankId = "";
        String chargeBankName = "";

        ClassPathResource resource1 = new ClassPathResource("skyline_user.xml");
        URL url = null;
        Document document = null;
        Element element = null;
        try {
            url = resource1.getURL();
            document = parse(url);
        } catch (IOException e) {
            logger.debug(e.getMessage());
        } catch (DocumentException e) {
            logger.debug(e.getMessage());
        } catch (XmlPullParserException e) {
            logger.debug(e.getMessage());
        }
        if (document != null) {
            element = (Element) document.selectSingleNode("//user/name");
        }
        if (element != null) {
            userName = element.getStringValue();
        }
        if (document != null) {
            element = (Element) document.selectSingleNode("//user/pwd");
        }
        if (element != null) {
            userPa88wd = element.getStringValue();
        }
        if (document != null) {
            element = (Element) document.selectSingleNode("//user/role");
        }
        if (element != null) {
            roleName = element.getStringValue();
            roleId = element.getStringValue();
        }
        if (document != null) {
            element = (Element) document.selectSingleNode("//user/department");
        }
        if (element != null) {
            departmentId = element.getStringValue();
            if (departmentId.length() > 7) {
                // throw new SignOnException("部門代號長度錯誤["+ details.getDepartment().length()+ "]", "invalid Deaprtment"); TODO
            }
            QueryBank queryBank = queryBankFactory.getQueryBank(departmentId);
            departmentName = queryBank.getDepartmentName();
            chargeBankId = queryBank.getChargeBankId();
            chargeBankName = queryBank.getChargeBankName();
        }

        Map<String, String> roles = new HashMap<String, String>();
        List<QueryRole> queryRoleList = queryRoleDao.findAll();
        for (QueryRole queryRole : queryRoleList) {
            // test
            // if("59040000".equals(queryRole.getRoleId())){
            roles.put(queryRole.getRoleId(), queryRole.getRoleName());
            // }
        }

        if (userName.equals(usernameFromUserInput)) {
            QueryRole queryRole = new QueryRole();
            queryRole.setRoleId(roleId);
            queryRole.setRoleName(roleName);

            QueryUser user = new QueryUser();
            user.setEmployeeId(userName);
            user.setEmployeeName(userName);
            user.setDepartmentId(departmentId);
            user.setDepartmentName(departmentName);
            user.setQueryRole(queryRole);
            EtchUserDetails etchUserDetails = new EtchUserDetails(user, chargeBankId, chargeBankName, userPa88wd, roles);
            return etchUserDetails;
        } else {
            return null;
        }

    }

    public UserDetails obtainUserDetails(User user, String pwd, Map<String, String> roles) {
        return new CapUserDetails(user, pwd, roles);
    }

}
