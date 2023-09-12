package com.bot.cqs.query.handler;

import com.bot.cqs.monitor.proxy.MonitorThreadLocal;
import com.bot.cqs.query.annotation.NetchLogWritingAction;
import com.bot.cqs.query.command.NetchLogContent;
import com.bot.cqs.query.exception.NetchLogMessageException;
import com.bot.cqs.query.persistence.QueryBank;
import com.bot.cqs.query.persistence.SessionLog;
import com.bot.cqs.query.service.InquiryLogManager;
import com.bot.cqs.query.service.QueryRoleService;
import com.bot.cqs.query.service.SessionLogService;
import com.bot.cqs.query.service.TransactionRateManager;
import com.bot.cqs.query.util.DateUtil;
import com.bot.cqs.query.util.factory.QueryBankFactory;
import com.bot.cqs.report.service.SessionLogCsvRptService;
import com.bot.cqs.report.service.SessionLogPdfRptService;
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
import com.iisigroup.cap.utils.CapAppContext;
import com.iisigroup.cap.utils.CapDate;
import com.iisigroup.cap.utils.CapString;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <pre>
 * 登入登出記錄查詢
 * </pre>
 * 
 * @since 2016年12月29日
 * @author bob peng
 * @version
 *          <ul>
 *          <li>2016年12月29日,bob peng,new
 *          </ul>
 */
@Controller("loginqueryhandler")
public class LoginQueryHandler extends NetchMFormHandler {

    private static final String FUNCTION_ID = "J010";

    @Resource
    private QueryRoleService queryRoleService;
    @Resource
    private InquiryLogManager inquiryLogManager;
    @Resource
    private QueryBankFactory queryBankFactory;
    @Resource
    private SessionLogCsvRptService sessionLogCsvRptService;
    @Resource
    private SessionLogPdfRptService sessionLogPdfRptService;
    @Resource
    private TransactionRateManager transactionRateManager;
    @Resource
    private CommonService commonService;
    @Resource
    private SessionLogService sessionLogService;

    private static final List<String> LOGIN_LOGOUT_STATUS = new ArrayList<String>();
    static{
        LOGIN_LOGOUT_STATUS.add( SessionLog.PRIMARY_STATUS_LOGIN );
        LOGIN_LOGOUT_STATUS.add( SessionLog.PRIMARY_STATUS_LOGOUT );
    }

    @Override
    NetchLogContent generateDataForLogWriting(Request request) {
        String departmentId = request.get("departmentId");
        String userId = request.get("userId");
        String startDate = request.get("startDate") != null ? request.get("startDate").replace("/", "") : "";
        String endDate = request.get("endDate") != null ? request.get("endDate").replace("/", "") : "";

        List<Object> argsList = new ArrayList<Object>();
        argsList.add("departmentId=" + (CapString.isEmpty(departmentId) ? "ALL" : departmentId));
        if(!CapString.isEmpty(userId)){
            argsList.add("user=" + userId);
        }
        if(!CapString.isEmpty(startDate)){
            argsList.add("from=" + startDate);
        }
        if(!CapString.isEmpty(endDate)){
            argsList.add("to=" + endDate);
        }
        return new NetchLogContent(argsList);
    }

    @HandlerType(HandlerTypeEnum.GRID)
    @NetchLogWritingAction(functionId = FUNCTION_ID, writeSuccessLogAfterAction=true)
    public BeanGridResult query(SearchSetting search, Request request) {
        Page<SessionLog> page = queryForPage(search, request);
        String getPrepareStatement = MonitorThreadLocal.threadLocal.get();
        Map<String, Formatter> fmt = new HashMap<String, Formatter>();
        fmt.put("accessDatetime", new AccessDatetimeFormatter());
        fmt.put("primaryStatusDesc", new PrimaryStatusFormatter());
        fmt.put("secondaryStatusDesc", new SecondaryStatusFormatter());
        sessionLogService.createAndWriteSessionLog(request, SessionLog.PRIMARY_STATUS_QUERY_SESSIONLOG_LOGIN, SessionLog.SECONDARY_STATUS_NO_MORE_DESCRIPTION, request.get("action", ""), null);
        return new BeanGridResult(page.getContent(), page.getTotalRow(), fmt);
    }
    
    class AccessDatetimeFormatter implements Formatter {
        private static final long serialVersionUID = 1L;
        @SuppressWarnings("unchecked")
        public String reformat(Object in) throws CapFormatException {
            String result = "";
            if (in instanceof Date) {
                Date date = (Date) in;
                result = DateUtil.toRocDatetime(date);
            }
            return result;
        }
    }

    class PrimaryStatusFormatter implements BeanFormatter {
        private static final long serialVersionUID = 1L;
        @SuppressWarnings("unchecked")
        public String reformat(Object in) throws CapFormatException {
            String result = "";
            if (in instanceof SessionLog) {
                SessionLog sessionLog = (SessionLog) in;
                result = sessionLog.getPrimaryStatusDesc();
            }
            return result;
        }
    }
    
    class SecondaryStatusFormatter implements BeanFormatter {
        private static final long serialVersionUID = 1L;
        @SuppressWarnings("unchecked")
        public String reformat(Object in) throws CapFormatException {
            String result = "";
            if (in instanceof SessionLog) {
                SessionLog sessionLog = (SessionLog) in;
                if(CapString.isEmpty(sessionLog.getSecondaryStatusDesc())){
                    result = "成功";
                }else{
                    result = "<span style='color: red;'>失敗</span>";
                }
            }
            return result;
        }
    }

    private Page<SessionLog> queryForPage(SearchSetting search, Request request) {
        String userId = request.get("userId");
        String departmentId = request.get("departmentId");
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String startDate = CapString.isEmpty(request.get("startDate")) ? "" : sdf.format(DateUtil.getDate(request.get("startDate"), DateUtil.DATE_STYLE_DEFAULT));
        String endDate =  CapString.isEmpty(request.get("endDate")) ? "" : sdf.format(DateUtil.getDate(request.get("endDate"), DateUtil.DATE_STYLE_DEFAULT));
        
        search.addSearchModeParameters(SearchMode.IN, "primaryStatus", LOGIN_LOGOUT_STATUS.toArray());
        if (!CapString.isEmpty(userId)) {
            search.addSearchModeParameters(SearchMode.EQUALS, "userId", userId);
        }
        if (!CapString.isEmpty(departmentId)) {
            search.addSearchModeParameters(SearchMode.EQUALS, "departmentId", departmentId);
        }
        if (!CapString.isEmpty(startDate) && !CapString.isEmpty(endDate)) {
            search.addSearchModeParameters(SearchMode.GREATER_EQUALS, "accessDatetime", CapDate.getFirstMinuteDate(startDate, "yyyyMMdd"));
            search.addSearchModeParameters(SearchMode.LESS_EQUALS, "accessDatetime", CapDate.getLastMinuteDate(endDate, "yyyyMMdd"));
        } else if (!CapString.isEmpty(startDate)) {
            search.addSearchModeParameters(SearchMode.GREATER_EQUALS, "accessDatetime", CapDate.getFirstMinuteDate(startDate, "yyyyMMdd"));
            search.addSearchModeParameters(SearchMode.LESS_EQUALS, "accessDatetime", CapDate.getLastMinuteDate(CapDate.getCurrentDate("yyyyMMdd"), "yyyyMMdd"));
        } else if (!CapString.isEmpty(endDate)) {
            search.addSearchModeParameters(SearchMode.GREATER_EQUALS, "accessDatetime", CapDate.getFirstMinuteDate(endDate, "yyyyMMdd"));
            search.addSearchModeParameters(SearchMode.LESS_EQUALS, "accessDatetime", CapDate.getLastMinuteDate(CapDate.getCurrentDate("yyyyMMdd"), "yyyyMMdd"));
        } else {
            search.addSearchModeParameters(SearchMode.GREATER_EQUALS, "accessDatetime", CapDate.getFirstMinuteDate(CapDate.getCurrentDate("yyyyMMdd"), "yyyyMMdd"));
            search.addSearchModeParameters(SearchMode.LESS_EQUALS, "accessDatetime", CapDate.getLastMinuteDate(CapDate.getCurrentDate("yyyyMMdd"), "yyyyMMdd"));
        }
        search.addOrderBy("accessDatetime");
        return commonService.findPage(SessionLog.class, search);
    }

    public Result loadOptions(Request request) {
        AjaxFormResult result = new AjaxFormResult();
        List<QueryBank> queryBankList = queryBankFactory.getQueryBankList();
        Map<String, String> departmentMap = new HashMap<String, String>();
        for (QueryBank queryBank : queryBankList) {
            departmentMap.put(queryBank.getDepartmentId(), queryBank.getDepartmentName());
        }
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put("departmentMap", departmentMap);
        result.putAll(resultMap);
        return result;
    }

    @NetchLogWritingAction(functionId = FUNCTION_ID, writeLogBeforeAction=true)
    public Result checkIfDataExist(Request request) {
        AjaxFormResult result = new AjaxFormResult();
        SearchSetting search = new SearchSettingImpl();
        List<SessionLog> list = queryForPage(search, request).getContent();
        if (CollectionUtils.isEmpty(list)) {
            sessionLogService.createAndWriteSessionLog(request, SessionLog.PRIMARY_STATUS_QUERY_SESSIONLOG_LOGIN, SessionLog.SECONDARY_STATUS_DATA_NOT_FOUND, request.get("action", ""), null);
            throw new NetchLogMessageException(CapAppContext.getMessage("js.chargeQuery.msg.02"), this.getClass(), "無資料");// 選取的範圍無資料
        }
        return result;
    }

    @HandlerType(HandlerTypeEnum.FileDownload)
    @NetchLogWritingAction(functionId = FUNCTION_ID, writeSuccessLogAfterAction=true)
    public Result downloadCsv(Request request) {
        AjaxFormResult result = new AjaxFormResult();
        SearchSetting search = new SearchSettingImpl();
        search.setFirstResult(0).setMaxResults(Integer.MAX_VALUE);
        List<SessionLog> list = queryForPage(search, request).getContent();
        String getPrepareStatement = MonitorThreadLocal.threadLocal.get();
        String csvString = sessionLogCsvRptService.generateCsvStringForQueryDetail(list);
        try {
            byte[] input = csvString.getBytes("Big5");
            if (csvString != null) {
                sessionLogService.createAndWriteSessionLog(request, SessionLog.PRIMARY_STATUS_QUERY_SESSIONLOG_LOGIN, SessionLog.SECONDARY_STATUS_NO_MORE_DESCRIPTION, request.get("action", ""), null);
                return new ByteArrayDownloadResult(request, input, "text/plain", "loginQuery.csv");
            }
        } catch (UnsupportedEncodingException e) {
            logger.debug(e.getMessage());
        }
        return result;
    }

    @HandlerType(HandlerTypeEnum.FileDownload)
    @NetchLogWritingAction(functionId = FUNCTION_ID, writeSuccessLogAfterAction=true)
    public Result downloadPdf(Request request) throws FileNotFoundException {
        request.put("functionId", FUNCTION_ID);
        SearchSetting search = new SearchSettingImpl();
        search.setFirstResult(0).setMaxResults(Integer.MAX_VALUE);
        List<SessionLog> list = queryForPage(search, request).getContent();
        String getPrepareStatement = MonitorThreadLocal.threadLocal.get();
        request.put("sessionLogList", list);
        ByteArrayOutputStream outputstream = sessionLogPdfRptService.generateReport(request);
        sessionLogService.createAndWriteSessionLog(request, SessionLog.PRIMARY_STATUS_QUERY_SESSIONLOG_LOGIN, SessionLog.SECONDARY_STATUS_NO_MORE_DESCRIPTION, request.get("action", ""), null);
        return new ByteArrayDownloadResult(request, outputstream.toByteArray(), ContextTypeEnum.pdf.toString(), "loginQuery.pdf");
    }
}
