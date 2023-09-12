package com.bot.cqs.query.handler;

import com.bot.cqs.gateway.persistence.InquiryLog;
import com.bot.cqs.monitor.proxy.MonitorThreadLocal;
import com.bot.cqs.query.annotation.NetchLogWritingAction;
import com.bot.cqs.query.command.NetchLogContent;
import com.bot.cqs.query.exception.NetchLogMessageException;
import com.bot.cqs.query.persistence.QueryBank;
import com.bot.cqs.query.persistence.SessionLog;
import com.bot.cqs.query.persistence.TransactionRate;
import com.bot.cqs.query.service.*;
import com.bot.cqs.query.util.DateUtil;
import com.bot.cqs.query.util.factory.QueryBankFactory;
import com.bot.cqs.report.service.QueryDetailCsvRptService;
import com.bot.cqs.report.service.QueryDetailPdfRptService;
import com.bot.cqs.security.model.EtchUserDetails;
import com.iisigroup.cap.annotation.HandlerType;
import com.iisigroup.cap.annotation.HandlerType.HandlerTypeEnum;
import com.iisigroup.cap.component.Request;
import com.iisigroup.cap.component.Result;
import com.iisigroup.cap.component.impl.AjaxFormResult;
import com.iisigroup.cap.component.impl.BeanGridResult;
import com.iisigroup.cap.component.impl.ByteArrayDownloadResult;
import com.iisigroup.cap.db.constants.SearchMode;
import com.iisigroup.cap.db.dao.SearchSetting;
import com.iisigroup.cap.db.dao.impl.SearchSettingImpl;
import com.iisigroup.cap.db.model.Page;
import com.iisigroup.cap.db.service.CommonService;
import com.iisigroup.cap.exception.CapFormatException;
import com.iisigroup.cap.formatter.BeanFormatter;
import com.iisigroup.cap.formatter.Formatter;
import com.iisigroup.cap.report.constants.ContextTypeEnum;
import com.iisigroup.cap.security.CapSecurityContext;
import com.iisigroup.cap.utils.CapAppContext;
import com.iisigroup.cap.utils.CapDate;
import com.iisigroup.cap.utils.CapString;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <pre>
 * 分行查詢明細
 * </pre>
 * 
 * @since 2016年12月29日
 * @author bob peng
 * @version
 *          <ul>
 *          <li>2016年12月29日,bob peng,new
 *          </ul>
 */
@Controller("branchquerydetailhandler")
public class BranchQueryDetailHandler extends NetchMFormHandler {

    private static final String FUNCTION_ID = "L040";
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    private static final int cyale = 60;

    @Resource
    private QueryRoleService queryRoleService;
    @Resource
    private InquiryLogManager inquiryLogManager;
    @Resource
    private QueryBankFactory queryBankFactory;
    @Resource
    private QueryDetailCsvRptService queryDetailCsvRptService;
    @Resource
    private QueryDetailPdfRptService queryDetailPdfService;
    @Resource
    private TransactionRateManager transactionRateManager;
    @Resource
    private CommonService commonService;
    @Resource
    private SessionLogService sessionLogService;
    @Resource
    private EtchAuditLogService etchAuditLogService;

    @Override
    NetchLogContent generateDataForLogWriting(Request request) {
        String startDate = request.get("startDate") != null ? request.get("startDate").replace("/", "") : "";
        String endDate = request.get("endDate") != null ? request.get("endDate").replace("/", "") : "";
        String inquiryChargeBankId = CapSecurityContext.<EtchUserDetails>getUser().getChargeBankId();
        String inquiryAccount = request.get("inquiryAccount");
        String inquiryTxCode = request.get("inquiryTxCode");

        String inquiryTxCodeName = CapString.isEmpty(inquiryTxCode) ? "ALL" : inquiryTxCode + " " + TransactionRate.getTransactionName(inquiryTxCode);
        String inquiryChargeBankIdName = "";
        List<QueryBank> formQueryBankList = queryBankFactory.getQueryBankList();
        for (QueryBank queryBank : formQueryBankList) {
            if (queryBank.getChargeBankId().equalsIgnoreCase(inquiryChargeBankId)) {
                inquiryChargeBankIdName = inquiryChargeBankId + " " + queryBank.getChargeBankName();
                break;
            }
        }
        if (CapString.isEmpty(inquiryChargeBankIdName)) {
            inquiryChargeBankIdName = inquiryChargeBankId;
        }

        List<Object> argsList = new ArrayList<Object>();
        argsList.add("chargeBankId=" + inquiryChargeBankIdName);
        argsList.add("txCode=" + inquiryTxCodeName);
        argsList.add("account=" + inquiryAccount);
        argsList.add("from=" + startDate);
        argsList.add("to=" + endDate);
        return new NetchLogContent(argsList);
    }

    @HandlerType(HandlerTypeEnum.GRID)
    @NetchLogWritingAction(functionId = FUNCTION_ID, writeSuccessLogAfterAction=true)
    public BeanGridResult query(SearchSetting search, Request request) {
        Page<InquiryLog> page = queryForPage(search, request);
        String getPrepareStatement = MonitorThreadLocal.threadLocal.get();
        Map<String, Formatter> fmt = new HashMap<String, Formatter>();
        fmt.put("inquiryCacheFlag", new InquiryCacheFlagFormatter());
        sessionLogService.createAndWriteSessionLog(request, SessionLog.PRIMARY_STATUS_QUERY_INQUIRYLOG_BY_BRANCH, SessionLog.SECONDARY_STATUS_NO_MORE_DESCRIPTION, request.get("action", ""), null);
        // 寫 EtchAuditLog
        String functionName = this.getClass().getSimpleName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName();
        try {
            etchAuditLogService.saveEtchAuditLog(request, functionName, FUNCTION_ID, page.getTotalRow());
        } catch (UnknownHostException e) {
            logger.debug(e.getMessage());
        }
        return new BeanGridResult(page.getContent(), page.getTotalRow(), fmt);
    }

    class InquiryCacheFlagFormatter implements BeanFormatter {
        private static final long serialVersionUID = 1L;

        @SuppressWarnings("unchecked")
        public String reformat(Object in) throws CapFormatException {
            String result = "";
            if (in instanceof InquiryLog) {
                InquiryLog inquiryLog = (InquiryLog) in;
                if (inquiryLog.getInquiryCacheFlag()) {
                    result = "Y";
                } else {
                    result = "N";
                }
            }
            return result;
        }
    }

    private Page<InquiryLog> queryForPage(SearchSetting search, Request request) {
        String startDate = CapString.isEmpty(request.get("startDate")) ? "" : sdf.format(DateUtil.getDate(request.get("startDate"), DateUtil.DATE_STYLE_DEFAULT));
        String endDate =  CapString.isEmpty(request.get("endDate")) ? "" : sdf.format(DateUtil.getDate(request.get("endDate"), DateUtil.DATE_STYLE_DEFAULT));
        String inquiryChargeBankId = CapSecurityContext.<EtchUserDetails>getUser().getChargeBankId();
        String inquiryAccount = request.get("inquiryAccount");
        String inquiryTxCode = request.get("inquiryTxCode");
        search.addSearchModeParameters(SearchMode.IS_NULL, "inquiryErrorCode", null);
        if (!CapString.isEmpty(inquiryChargeBankId)) {
            search.addSearchModeParameters(SearchMode.EQUALS, "inquiryChargeBankId", inquiryChargeBankId);
        }
        if (!CapString.isEmpty(inquiryAccount)) {
            search.addSearchModeParameters(SearchMode.EQUALS, "inquiryAccount", inquiryAccount);
        }
        if (!CapString.isEmpty(inquiryTxCode)) {
            search.addSearchModeParameters(SearchMode.EQUALS, "inquiryTxCode", inquiryTxCode);
        }
        if (!CapString.isEmpty(startDate) && !CapString.isEmpty(endDate)) {
            search.addSearchModeParameters(SearchMode.GREATER_EQUALS, "inquiryDate", startDate);
            search.addSearchModeParameters(SearchMode.LESS_EQUALS, "inquiryDate", endDate);
        } else if (!CapString.isEmpty(startDate)) {
            search.addSearchModeParameters(SearchMode.EQUALS, "inquiryDate", startDate);
        } else if (!CapString.isEmpty(endDate)) {
            search.addSearchModeParameters(SearchMode.EQUALS, "inquiryDate", endDate);
        } else {
            search.addSearchModeParameters(SearchMode.EQUALS, "inquiryDate", CapDate.getCurrentDate("yyyyMMdd"));
        }
        search.addOrderBy("inquiryDate");
        search.addOrderBy("inquiryChargeBankId");
        search.addOrderBy("inquiryAccount");
        search.addOrderBy("inquiryTxCode");
        return commonService.findPage(InquiryLog.class, search);
    }

    public Result loadOptions(Request request) {
        AjaxFormResult result = new AjaxFormResult();
        List<InquiryLog> chargeBankIdList = inquiryLogManager.getChargeBankList();
        List<QueryBank> formQueryBankList = queryBankFactory.getQueryBankList();
        Map<String, String> chargeBankMap = new HashMap<String, String>();
        for (InquiryLog currentInquiry : chargeBankIdList) {
            for (QueryBank queryBank : formQueryBankList) {
                if (queryBank.getChargeBankId().equalsIgnoreCase(currentInquiry.getInquiryChargeBankId())) {
                    chargeBankMap.put(queryBank.getChargeBankId(), queryBank.getChargeBankName());
                    break;
                }
            }
            if (!chargeBankMap.containsKey(currentInquiry.getInquiryChargeBankId())) {
                chargeBankMap.put(currentInquiry.getInquiryChargeBankId(), "");
            }
        }
        List<TransactionRate> transactionRateList = TransactionRate.AVAILABLE_TRANSACTION_RATE;
        // List<TransactionRate> transactionRateList = transactionRateManager.findAll();
        Map<String, String> transactionRateMap = new HashMap<String, String>();
        for (TransactionRate transactionRate : transactionRateList) {
            transactionRateMap.put(transactionRate.getKey().getTransactionId(), transactionRate.getTransactionName());
        }
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put("chargeBankMap", chargeBankMap);
        resultMap.put("transactionRateMap", transactionRateMap);
        result.putAll(resultMap);
        return result;
    }

    @NetchLogWritingAction(functionId = FUNCTION_ID, writeLogBeforeAction=true)
    public Result checkIfDataExist(Request request) {
        AjaxFormResult result = new AjaxFormResult();
        SearchSetting search = new SearchSettingImpl();
        Date startDate = DateUtil.getDate(request.get("startDate"), DateUtil.DATE_STYLE_DEFAULT);
        Date endDate = DateUtil.getDate(request.get("endDate"), DateUtil.DATE_STYLE_DEFAULT);
        if (startDate != null && endDate != null) {
            int days = CapDate.calculateDays(endDate, startDate);
            if (days > cyale) {
                throw new NetchLogMessageException(CapAppContext.getMessage("js.branchQueryDetail.msg.03", new Object[] { cyale }), this.getClass());// 已超過最大週期天數： [{0}] 天
            }
        }
        List<InquiryLog> list = queryForPage(search, request).getContent();
        if (CollectionUtils.isEmpty(list)) {
            sessionLogService.createAndWriteSessionLog(request, SessionLog.PRIMARY_STATUS_QUERY_INQUIRYLOG_BY_BRANCH, SessionLog.SECONDARY_STATUS_DATA_NOT_FOUND, request.get("action", ""), null);
            throw new NetchLogMessageException(CapAppContext.getMessage("js.branchQueryDetail.msg.02"), this.getClass(), "無資料");// 選取的範圍無資料
        }
        return result;
    }

    @HandlerType(HandlerTypeEnum.FileDownload)
    @NetchLogWritingAction(functionId = FUNCTION_ID, writeSuccessLogAfterAction=true)
    public Result downloadCsv(Request request) {
        AjaxFormResult result = new AjaxFormResult();
        SearchSetting search = new SearchSettingImpl();
        search.setFirstResult(0).setMaxResults(Integer.MAX_VALUE);
        List<InquiryLog> list = queryForPage(search, request).getContent();
        String csvString = queryDetailCsvRptService.generateCsvStringForQueryDetail(list);
        try {
            byte[] input = csvString.getBytes("Big5");
            if (csvString != null) {
                sessionLogService.createAndWriteSessionLog(request, SessionLog.PRIMARY_STATUS_QUERY_INQUIRYLOG_BY_BRANCH, SessionLog.SECONDARY_STATUS_NO_MORE_DESCRIPTION, request.get("action", ""), null);
                // 寫 EtchAuditLog
                String functionName = this.getClass().getSimpleName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName();
                try {
                    etchAuditLogService.saveEtchAuditLog(request, functionName, FUNCTION_ID, list.size());
                } catch (UnknownHostException e) {
                    logger.debug(e.getMessage());
                }
                return new ByteArrayDownloadResult(request, input, "text/plain", "branchQueryDetail.csv");
            }
        } catch (UnsupportedEncodingException e) {
            logger.debug(e.getMessage());
        }
        return result;
    }

    @HandlerType(HandlerTypeEnum.FileDownload)
    @NetchLogWritingAction(functionId = FUNCTION_ID, writeSuccessLogAfterAction=true)
    public Result downloadPdf(Request request) throws FileNotFoundException {
        request.put("queryMode", "0");
        SearchSetting search = new SearchSettingImpl();
        search.setFirstResult(0).setMaxResults(Integer.MAX_VALUE);
        List<InquiryLog> list = queryForPage(search, request).getContent();
        request.put("inquiryLogList", list);
        request.put("inquiryChargeBankId", CapSecurityContext.<EtchUserDetails>getUser().getChargeBankId());
        ByteArrayOutputStream outputstream = queryDetailPdfService.generateReport(request);
        sessionLogService.createAndWriteSessionLog(request, SessionLog.PRIMARY_STATUS_QUERY_INQUIRYLOG_BY_BRANCH, SessionLog.SECONDARY_STATUS_NO_MORE_DESCRIPTION, request.get("action", ""), null);
        // 寫 EtchAuditLog
        String functionName = this.getClass().getSimpleName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName();
        try {
            etchAuditLogService.saveEtchAuditLog(request, functionName, FUNCTION_ID, list.size());
        } catch (UnknownHostException e) {
            logger.debug(e.getMessage());
        }
        return new ByteArrayDownloadResult(request, outputstream.toByteArray(), ContextTypeEnum.pdf.toString(), "branchQueryDetail.pdf");
    }
}
