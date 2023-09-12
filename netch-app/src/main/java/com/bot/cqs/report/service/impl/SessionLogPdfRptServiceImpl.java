package com.bot.cqs.report.service.impl;

import com.bot.cqs.query.command.SessionLogCommand;
import com.bot.cqs.query.persistence.QueryBank;
import com.bot.cqs.query.persistence.QueryUser;
import com.bot.cqs.query.persistence.SessionLog;
import com.bot.cqs.query.service.InquiryLogManager;
import com.bot.cqs.query.util.factory.QueryBankFactory;
import com.bot.cqs.report.service.AbstractJasperPdfService;
import com.bot.cqs.report.service.SessionLogPdfRptService;
import com.bot.cqs.security.model.EtchUserDetails;
import com.iisigroup.cap.component.Request;
import com.iisigroup.cap.security.CapSecurityContext;
import com.iisigroup.cap.utils.CapAppContext;
import com.iisigroup.cap.utils.CapDate;
import com.iisigroup.cap.utils.CapString;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <pre>
 * ChargeQueryPdfServiceImpl
 * </pre>
 * 
 * @since 2017年1月5日
 * @author bob peng
 * @version
 *          <ul>
 *          <li>2017年1月5日,bob peng,new
 *          </ul>
 */
@Service
public class SessionLogPdfRptServiceImpl extends AbstractJasperPdfService implements SessionLogPdfRptService {

    @Resource
    private InquiryLogManager inquiryLogManager;
    @Resource
    private QueryBankFactory queryBankFactory;

    public Map<String, Object> excute(Request request) {

        String functionId = request.get("functionId");
        List<SessionLog> sessionLogList = (List<SessionLog>) request.getObject("sessionLogList");

        Map<String, Object> reportParameters = new HashMap<>();
        reportParameters.put("systemName", CapAppContext.getMessage("js.report.systemName"));// 台灣銀行票據信用查詢系統
        String reportName = "J010".equals(functionId) ? CapAppContext.getMessage("js.report.reportName.loginQuery") : CapAppContext.getMessage("js.report.reportName.operationSummary");
        reportParameters.put("reportName", reportName);
        QueryUser user = CapSecurityContext.<EtchUserDetails> getUser().getQueryUser();
        reportParameters.put("SESSION_QUERY_USER", user);

        SessionLogCommand command = new SessionLogCommand();
        String userId = request.get("userId");
        String departmentId = request.get("departmentId");
        String startDate = request.get("startDate") != null ? request.get("startDate") : "";
        if (!CapString.isEmpty(startDate) && startDate.contains("/")) {
            int startYyyyDate = startDate.substring(0, startDate.indexOf("/")) != null ? Integer.parseInt(startDate.substring(0, startDate.indexOf("/"))) + 1911 : 0;
            startDate = startYyyyDate != 0 ? startYyyyDate + startDate.substring(startDate.indexOf("/")).replace("/", "") : "";
        }
        String endDate = request.get("endDate") != null ? request.get("endDate") : "";
        if (!CapString.isEmpty(endDate) && endDate.contains("/")) {
            int endYyyyDate = endDate.substring(0, endDate.indexOf("/")) != null ? Integer.parseInt(endDate.substring(0, endDate.indexOf("/"))) + 1911 : 0;
            endDate = endYyyyDate != 0 ? endYyyyDate + endDate.substring(endDate.indexOf("/")).replace("/", "") : "";
        }
        command.setUserId(userId);
        command.setDepartmentId(departmentId);
        // setAccessDateTo, setAccessDateFrom
        if (!CapString.isEmpty(startDate) && !CapString.isEmpty(endDate)) {
            command.setAccessDateFrom(CapDate.convertStringToTimestamp(startDate, "yyyyMMdd"));
            command.setAccessDateTo(CapDate.convertStringToTimestamp(endDate, "yyyyMMdd"));
        } else if (!CapString.isEmpty(startDate)) {
            command.setAccessDateFrom(CapDate.convertStringToTimestamp(startDate, "yyyyMMdd"));
            command.setAccessDateTo(CapDate.getCurrentTimestamp());
        } else if (!CapString.isEmpty(endDate)) {
            command.setAccessDateFrom(CapDate.convertStringToTimestamp(endDate, "yyyyMMdd"));
            command.setAccessDateTo(CapDate.getCurrentTimestamp());
        } else {
            command.setAccessDateFrom(CapDate.getCurrentTimestamp());
            command.setAccessDateTo(CapDate.getCurrentTimestamp());
        }
        // setSelectedDepartmentId
        if (!CapString.isEmpty(departmentId)) {
            List<QueryBank> formQueryBankList = queryBankFactory.getQueryBankList();
            for (QueryBank queryBank : formQueryBankList) {
                if (queryBank.getDepartmentId().equals(departmentId)) {
                    command.setDepartmentName(queryBank.getDepartmentName());
                    break;
                }
            }
            command.setSelectedDepartmentId(command.getDepartmentId() + " " + command.getDepartmentName());
        } else {
            command.setSelectedDepartmentId("ALL");
        }
        reportParameters.put("dataCommand", command);
        reportParameters.put("dataList", sessionLogList);
        return reportParameters;
    }

    public String getReportDefinition() {
        return "report/sessionLog";
    }
}
