package com.bot.cqs.mvc.action;

import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bot.cqs.query.service.EtchAuditLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.bot.cqs.query.handler.MultiQueryHandler;
import com.bot.cqs.query.persistence.TransactionRate;
import com.iisigroup.cap.mvc.action.BaseActionController;
import com.iisigroup.cap.security.CapSecurityContext;
import com.iisigroup.cap.security.model.CapUserDetails;
import com.iisigroup.cap.utils.CapAppContext;
import com.iisigroup.cap.utils.CapString;

/**
 * <pre>
 * Page action of Clear House.
 * </pre>
 * 
 * @since 2017年2月2日
 * @author Sunkist
 * @version
 *          <ul>
 *          <li>2017年2月2日,Sunkist,new
 *          </ul>
 */
@Controller
@RequestMapping("/*")
public class CHPageAction extends BaseActionController {

    @Autowired
    private EtchAuditLogService etchAuditLogService;

    @RequestMapping("/error")
    public ModelAndView error(Locale locale, HttpServletRequest request, HttpServletResponse response) {
        String path = request.getPathInfo();
        ModelAndView model = new ModelAndView(path);
        HttpSession session = request.getSession(false);
        response.setStatus(HttpServletResponse.SC_OK);
        final AuthenticationException ae = (session != null) ? (AuthenticationException) session.getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION) : null;
        String errmsg = "";
        if (ae != null) {
            errmsg = ae.getMessage();
        } else {
            AccessDeniedException accessDenied = (AccessDeniedException) request.getAttribute(WebAttributes.ACCESS_DENIED_403);
            if (accessDenied != null) {
                errmsg = CapAppContext.getMessage("AccessCheck.AccessDenied", locale) + errmsg;
            }
        }
        model.addObject("errorMessage", errmsg);
        return model;
    }

    @RequestMapping("/**")
    public ModelAndView handleRequestInternal(Locale locale, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String path = request.getPathInfo();
        ModelAndView model = new ModelAndView(path);
        CapUserDetails userDetails = CapSecurityContext.getUser();
        if (userDetails != null) {
            model.addObject("userDetails", userDetails);
        }
        model.addObject("hostName",etchAuditLogService.getLocalHostInfo().getProperty("localhost.name"));
        return model;
    }

    @RequestMapping("/**/{mode}Query{detail}")
    public ModelAndView handleSingleQuery(Locale locale, HttpServletRequest request, HttpServletResponse response, @PathVariable String mode, @PathVariable String detail) throws Exception {

        String path = request.getPathInfo();
        ModelAndView model = new ModelAndView(path);
        // Only match to singleQuery/singleQueryDetail/singleQueryResult, multiQuery/multiQueryDetail/multiQueryResult
        if (!CapString.checkRegularMatch(mode, "single|multi")) {
            return model;
        }
        if ("detail".equalsIgnoreCase(detail) || "result".equalsIgnoreCase(detail)) {
            model.addAllObjects((Map<String, Object>) ((HttpServletRequest) request).getSession(true).getAttribute(MultiQueryHandler.EXCHANGE_KEY));
        } else {
            model.addObject(MultiQueryHandler.AVAILABLE_RATELIST, TransactionRate.AVAILABLE_TRANSACTION_RATE);
        }
        return model;
    }

}
