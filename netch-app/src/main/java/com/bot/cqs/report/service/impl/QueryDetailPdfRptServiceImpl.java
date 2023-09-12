package com.bot.cqs.report.service.impl;

import com.bot.cqs.gateway.persistence.InquiryLog;
import com.bot.cqs.query.command.QueryInquiryLogCommand;
import com.bot.cqs.query.persistence.QueryBank;
import com.bot.cqs.query.persistence.QueryUser;
import com.bot.cqs.query.persistence.TransactionRate;
import com.bot.cqs.query.service.InquiryLogManager;
import com.bot.cqs.query.util.DateUtil;
import com.bot.cqs.query.util.factory.QueryBankFactory;
import com.bot.cqs.report.service.AbstractJasperPdfService;
import com.bot.cqs.report.service.QueryDetailPdfRptService;
import com.bot.cqs.security.model.EtchUserDetails;
import com.iisigroup.cap.component.Request;
import com.iisigroup.cap.security.CapSecurityContext;
import com.iisigroup.cap.utils.CapAppContext;
import com.iisigroup.cap.utils.CapDate;
import com.iisigroup.cap.utils.CapString;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
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
public class QueryDetailPdfRptServiceImpl extends AbstractJasperPdfService implements QueryDetailPdfRptService {

    @Resource
    private InquiryLogManager inquiryLogManager;
    @Resource
    private QueryBankFactory queryBankFactory;

    public Map<String, Object> excute(Request request) {
        
        String queryMode = request.get("queryMode");
        List<InquiryLog> inquiryLogList = (List<InquiryLog>) request.getObject("inquiryLogList");
        
        Map<String, Object> reportParameters = new HashMap<>();
        reportParameters.put("systemName", CapAppContext.getMessage("js.report.systemName"));// 台灣銀行票據信用查詢系統
        String reportName = "1".equals(queryMode) ? CapAppContext.getMessage("js.report.reportName.queryDetail") : CapAppContext.getMessage("js.report.reportName.branchQueryDetail");
        reportParameters.put("reportName", reportName);
        QueryUser user = CapSecurityContext.<EtchUserDetails>getUser().getQueryUser();
        reportParameters.put("SESSION_QUERY_USER", user); 

        QueryInquiryLogCommand command = new QueryInquiryLogCommand();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String startDate = CapString.isEmpty(request.get("startDate")) ? "" : sdf.format(DateUtil.getDate(request.get("startDate"), DateUtil.DATE_STYLE_DEFAULT));
        String endDate = CapString.isEmpty(request.get("endDate")) ? "" : sdf.format(DateUtil.getDate(request.get("endDate"), DateUtil.DATE_STYLE_DEFAULT));
        String inquiryChargeBankId = request.get("inquiryChargeBankId");
        String inquiryAccount = request.get("inquiryAccount");
        String inquiryTxCode = request.get("inquiryTxCode");
        command.setInChargeBankId(inquiryChargeBankId);
        command.setInAccount(inquiryAccount);
        if (!CapString.isEmpty(startDate) && !CapString.isEmpty(endDate)) {
            command.setAccessDateFrom(CapDate.convertStringToTimestamp(startDate, "yyyyMMdd"));
            command.setAccessDateTo(CapDate.convertStringToTimestamp(endDate, "yyyyMMdd"));
        } else if (!CapString.isEmpty(startDate)) {
            command.setAccessDateFrom(CapDate.convertStringToTimestamp(startDate, "yyyyMMdd"));
        } else if (!CapString.isEmpty(endDate)) {
            command.setAccessDateTo(CapDate.convertStringToTimestamp(endDate, "yyyyMMdd"));
        } else if (CapString.isEmpty(startDate) && CapString.isEmpty(endDate)) {
            command.setAccessDateFrom(CapDate.convertStringToTimestamp(CapDate.getCurrentDate("yyyyMMdd"), "yyyyMMdd"));
        }
        command.setInTxCode(CapString.isEmpty(inquiryTxCode) ? "ALL" : inquiryTxCode + " " + TransactionRate.getTransactionName(inquiryTxCode));
        List<QueryBank> formQueryBankList = queryBankFactory.getQueryBankList();
        for (QueryBank queryBank : formQueryBankList) {
            if (queryBank.getChargeBankId().equalsIgnoreCase(command.getInChargeBankId())) {
                command.setInChargeBankId(command.getInChargeBankId() + " " + queryBank.getChargeBankName());
                break;
            }
        }
        
        reportParameters.put("dataCommand", command);
        reportParameters.put("dataList", inquiryLogList);
        return reportParameters;
    }

    public String getReportDefinition() {
        return "report/queryDetail";
    }
}
