package com.bot.cqs.report.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.bot.cqs.query.command.QueryInquiryLogCommand;
import com.bot.cqs.query.dto.InquiryLogDto;
import com.bot.cqs.query.persistence.QueryBank;
import com.bot.cqs.query.persistence.QueryUser;
import com.bot.cqs.query.service.InquiryLogManager;
import com.bot.cqs.query.util.factory.QueryBankFactory;
import com.bot.cqs.report.service.AbstractJasperPdfService;
import com.bot.cqs.report.service.ChargeQueryPdfRptService;
import com.bot.cqs.security.model.EtchUserDetails;
import com.iisigroup.cap.component.Request;
import com.iisigroup.cap.security.CapSecurityContext;
import com.iisigroup.cap.utils.CapAppContext;
import com.iisigroup.cap.utils.CapString;

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
public class ChargeQueryPdfRptServiceImpl extends AbstractJasperPdfService implements ChargeQueryPdfRptService {

    @Resource
    private InquiryLogManager inquiryLogManager;
    @Resource
    private QueryBankFactory queryBankFactory;

    public Map<String, Object> excute(Request request) {

        String inChargeBankId = request.get("inChargeBankId");
        String inputYear = request.get("inputYear");
        String inputMonth = request.get("inputMonth");
        String queryMode = request.get("queryMode");
        QueryInquiryLogCommand command = new QueryInquiryLogCommand();
        command.setInputYear(inputYear);
        command.setInputMonth(inputMonth);
        command.setInChargeBankId(CapString.isEmpty(inChargeBankId) ? "ALL" : inChargeBankId);
        command.setQueryMode(queryMode);
        List<InquiryLogDto> dataList = inquiryLogManager.findForInquiryLogDtoList(command);

        Map<String, Object> reportParameters = new HashMap<>();
        reportParameters.put("systemName", CapAppContext.getMessage("js.report.systemName"));// 台灣銀行票據信用查詢系統
        String reportName = "1".equals(queryMode) ? CapAppContext.getMessage("js.report.reportName.chargeQuery") : CapAppContext.getMessage("js.report.reportName.branchChargeQuery");
        reportParameters.put("reportName", reportName);
        QueryUser user = CapSecurityContext.<EtchUserDetails>getUser().getQueryUser();
        reportParameters.put("SESSION_QUERY_USER", user);
        List<QueryBank> formQueryBankList = queryBankFactory.getQueryBankList();
        for (QueryBank queryBank : formQueryBankList) {
            if (queryBank.getChargeBankId().equalsIgnoreCase(command.getInChargeBankId())) {
                command.setInChargeBankId(command.getInChargeBankId() + " " + queryBank.getChargeBankName());
                break;
            }
        }
        reportParameters.put("dataCommand", command);
        reportParameters.put("dataList", dataList);

        return reportParameters;
    }

    public String getReportDefinition() {
        return "report/chargeQuery";
    }
}
