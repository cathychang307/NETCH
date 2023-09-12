package com.bot.cqs.query.handler;

import com.bot.cqs.gateway.persistence.InquiryLog;
import com.bot.cqs.monitor.proxy.MonitorThreadLocal;
import com.bot.cqs.query.annotation.NetchLogWritingAction;
import com.bot.cqs.query.command.NetchLogContent;
import com.bot.cqs.query.command.QueryInquiryLogCommand;
import com.bot.cqs.query.dto.InquiryLogDto;
import com.bot.cqs.query.exception.NetchLogMessageException;
import com.bot.cqs.query.persistence.QueryBank;
import com.bot.cqs.query.persistence.SessionLog;
import com.bot.cqs.query.service.InquiryLogManager;
import com.bot.cqs.query.service.QueryRoleService;
import com.bot.cqs.query.service.SessionLogService;
import com.bot.cqs.query.util.factory.QueryBankFactory;
import com.bot.cqs.report.service.CountRowCsvRptService;
import com.bot.cqs.report.service.CountRowPdfRptService;
import com.iisigroup.cap.annotation.HandlerType;
import com.iisigroup.cap.annotation.HandlerType.HandlerTypeEnum;
import com.iisigroup.cap.component.Request;
import com.iisigroup.cap.component.Result;
import com.iisigroup.cap.component.impl.AjaxFormResult;
import com.iisigroup.cap.component.impl.ByteArrayDownloadResult;
import com.iisigroup.cap.component.impl.MapGridResult;
import com.iisigroup.cap.db.dao.SearchSetting;
import com.iisigroup.cap.report.constants.ContextTypeEnum;
import com.iisigroup.cap.utils.CapAppContext;
import com.iisigroup.cap.utils.CapString;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * <pre>
 * 各分行查詢統計
 * </pre>
 * 
 * @since 2016年12月29日
 * @author bob peng
 * @version
 *          <ul>
 *          <li>2016年12月29日,bob peng,new
 *          </ul>
 */
@Controller("countrowhandler")
public class CountRowHandler extends NetchMFormHandler {

    private static final String FUNCTION_ID = "L025";

    @Resource
    private QueryRoleService queryRoleService;
    @Resource
    private InquiryLogManager inquiryLogManager;
    @Resource
    private QueryBankFactory queryBankFactory;
    @Resource
    private CountRowCsvRptService countRowCsvRptService;
    @Resource
    private CountRowPdfRptService countRowPdfRptService;
    @Resource
    private SessionLogService sessionLogService;

    @Override
    NetchLogContent generateDataForLogWriting(Request request) {
        QueryInquiryLogCommand command = getCommand(request);
        List<Object> argsList = new ArrayList<Object>();
        
        String inquiryChargeBankId = command.getInChargeBankId();
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
        argsList.add("chargeBankId=" + inquiryChargeBankIdName);
        argsList.add("Year/Month=" + command.getInputYear() + "/" + command.getInputMonth());
        return new NetchLogContent(argsList);
    }

    private QueryInquiryLogCommand getCommand(Request request) {
        String inChargeBankId = request.get("inChargeBankId");
        String inputYear = request.get("inputYear");
        String inputMonth = request.get("inputMonth");
        QueryInquiryLogCommand command = new QueryInquiryLogCommand();
        command.setInputYear(inputYear);
        command.setInputMonth(inputMonth);
        command.setInChargeBankId(CapString.isEmpty(inChargeBankId) ? "ALL" : inChargeBankId);
        command.setQueryMode("1");
        return command;
    }

    private List<InquiryLogDto> queryForList(Request request) {
        QueryInquiryLogCommand command = getCommand(request);
        List<InquiryLogDto> allList = inquiryLogManager.findForInquiryLogDtoList(command);
        return allList;
    }

    @HandlerType(HandlerTypeEnum.GRID)
    @NetchLogWritingAction(functionId = FUNCTION_ID, writeSuccessLogAfterAction=true)
    public MapGridResult query(SearchSetting search, Request request) {
        List<InquiryLogDto> allList = queryForList(request);
        String getPrepareStatement = MonitorThreadLocal.threadLocal.get();
        int totalCnt = allList.size();
        int firstResult = search.getFirstResult();
        int maxResult = search.getMaxResults();
        List<InquiryLogDto> list = allList.subList(firstResult, firstResult + maxResult > totalCnt ? totalCnt : firstResult + maxResult);

        List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
        for (InquiryLogDto inquiryLogDto : list) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("inputDateFrom", inquiryLogDto.getInputDateFrom());
            map.put("inputDateTo", inquiryLogDto.getInputDateTo());
            map.put("inputChargeBankId", inquiryLogDto.getInquiryChargeBankId());
            map.put("rowSummary", inquiryLogDto.getRowSummary());
            map.put("totalRowSummary", inquiryLogDto.getTotalRowSummary());
            map.put("totalRows_0", inquiryLogDto.getTotalRows(0));
            map.put("totalRows_1", inquiryLogDto.getTotalRows(1));
            map.put("totalRows_2", inquiryLogDto.getTotalRows(2));
            map.put("totalRows_3", inquiryLogDto.getTotalRows(3));
            map.put("totalRows_4", inquiryLogDto.getTotalRows(4));
            map.put("totalRows_5", inquiryLogDto.getTotalRows(5));
            map.put("cacheRows_0", inquiryLogDto.getCacheRows(0));
            map.put("cacheRows_1", inquiryLogDto.getCacheRows(1));
            map.put("cacheRows_2", inquiryLogDto.getCacheRows(2));
            map.put("cacheRows_3", inquiryLogDto.getCacheRows(3));
            map.put("cacheRows_4", inquiryLogDto.getCacheRows(4));
            map.put("cacheRows_5", inquiryLogDto.getCacheRows(5));
            map.put("sum1_0", inquiryLogDto.getSum1(0));
            map.put("sum1_1", inquiryLogDto.getSum1(1));
            map.put("sum1_2", inquiryLogDto.getSum1(2));
            map.put("sum1_3", inquiryLogDto.getSum1(3));
            map.put("sum1_4", inquiryLogDto.getSum1(4));
            map.put("sum1_5", inquiryLogDto.getSum1(5));
            map.put("sum1_6", inquiryLogDto.getSum1(6));
            map.put("sum1_7", inquiryLogDto.getSum1(7));
            map.put("sum2_0", inquiryLogDto.getSum2(0));
            map.put("sum2_1", inquiryLogDto.getSum2(1));
            map.put("sum2_2", inquiryLogDto.getSum2(2));
            map.put("sum2_3", inquiryLogDto.getSum2(3));
            map.put("sum2_4", inquiryLogDto.getSum2(4));
            map.put("sum2_5", inquiryLogDto.getSum2(5));
            map.put("sum3_0", inquiryLogDto.getSum3(0));
            map.put("sum3_1", inquiryLogDto.getSum3(1));
            map.put("sum3_2", inquiryLogDto.getSum3(2));
            map.put("sum3_3", inquiryLogDto.getSum3(3));
            map.put("sum3_4", inquiryLogDto.getSum3(4));
            map.put("sum3_5", inquiryLogDto.getSum3(5));
            map.put("bankName", CapString.isEmpty(inquiryLogDto.getBankName()) ? CapAppContext.getMessage("js.chargeQuery.msg.01") : inquiryLogDto.getBankName());
            map.put("totalsFirst", inquiryLogDto.getTotalsFirst());
            map.put("totalsSecond", inquiryLogDto.getTotalsSecond());
            map.put("totalsOne", inquiryLogDto.getTotalsOne());
            map.put("totalsTwo", inquiryLogDto.getTotalsTwo());
            map.put("totalsObuOne", inquiryLogDto.getTotalsObuOne());
            map.put("totalsObuTwo", inquiryLogDto.getTotalsObuTwo());
            map.put("totals", inquiryLogDto.getTotalsFirst() + inquiryLogDto.getTotalsSecond() + inquiryLogDto.getTotalsOne() + inquiryLogDto.getTotalsTwo()+ inquiryLogDto.getTotalsObuOne() + inquiryLogDto.getTotalsObuTwo());
            //
            map.put("connect_0", inquiryLogDto.getSum1(0) + inquiryLogDto.getTotalRows(0));
            map.put("connect_1", inquiryLogDto.getSum1(7) + inquiryLogDto.getTotalRows(1));
            map.put("connect_2", inquiryLogDto.getSum2(0) + inquiryLogDto.getTotalRows(2));
            map.put("connect_3", inquiryLogDto.getSum2(5) + inquiryLogDto.getTotalRows(3));
            map.put("connect_4", inquiryLogDto.getSum3(0) + inquiryLogDto.getTotalRows(4));
            map.put("connect_5", inquiryLogDto.getSum3(5) + inquiryLogDto.getTotalRows(5));
            map.put("sum", inquiryLogDto.getSum1(0) + inquiryLogDto.getSum1(7) + inquiryLogDto.getSum2(0) + inquiryLogDto.getSum2(5) + inquiryLogDto.getSum3(0) + inquiryLogDto.getSum3(5)
                    + inquiryLogDto.getTotalRows(0) + inquiryLogDto.getTotalRows(1)  + inquiryLogDto.getTotalRows(2) + inquiryLogDto.getTotalRows(3) + inquiryLogDto.getTotalRows(4) + inquiryLogDto.getTotalRows(5)
                    + inquiryLogDto.getCacheRows(0) + inquiryLogDto.getCacheRows(1) + inquiryLogDto.getCacheRows(2) + inquiryLogDto.getCacheRows(3) + inquiryLogDto.getCacheRows(4) + inquiryLogDto.getCacheRows(5));
            resultList.add(map);
        }
        sessionLogService.createAndWriteSessionLog(request, SessionLog.PRIMARY_STATUS_COUNT_ROW, SessionLog.SECONDARY_STATUS_NO_MORE_DESCRIPTION, request.get("action", ""), null);
        return new MapGridResult(resultList, totalCnt);
    }

    public Result getInChargeBankId(Request request) {
        AjaxFormResult result = new AjaxFormResult();
        List<InquiryLog> chargeBankIdList = inquiryLogManager.getChargeBankList();
        List<QueryBank> formQueryBankList = queryBankFactory.getQueryBankList();
        Map<String, String> map = new TreeMap<String, String>();
        for (InquiryLog currentInquiry : chargeBankIdList) {
            for (QueryBank queryBank : formQueryBankList) {
                if (queryBank.getChargeBankId().equalsIgnoreCase(currentInquiry.getInquiryChargeBankId())) {
                    map.put(queryBank.getChargeBankId(), queryBank.getChargeBankName());
                    break;
                }
            }
            if (!map.containsKey(currentInquiry.getInquiryChargeBankId())) {
                map.put(currentInquiry.getInquiryChargeBankId(), "");
            }
        }
        result.putAll(map);
        return result;
    }

    @NetchLogWritingAction(functionId = FUNCTION_ID, writeLogBeforeAction=true)
    public Result checkIfDataExist(Request request) {
        AjaxFormResult result = new AjaxFormResult();
        List<InquiryLogDto> list = queryForList(request);
        if (CollectionUtils.isEmpty(list)) {
            sessionLogService.createAndWriteSessionLog(request, SessionLog.PRIMARY_STATUS_COUNT_ROW, SessionLog.SECONDARY_STATUS_DATA_NOT_FOUND, request.get("action", ""), null);
            throw new NetchLogMessageException(CapAppContext.getMessage("js.chargeQuery.msg.02"), this.getClass(), "無資料");// 選取的範圍無資料
        }

        return result;
    }

    @HandlerType(HandlerTypeEnum.FileDownload)
    @NetchLogWritingAction(functionId = FUNCTION_ID, writeSuccessLogAfterAction=true)
    public Result downloadCsv(Request request) {
        AjaxFormResult result = new AjaxFormResult();
        List<InquiryLogDto> list = queryForList(request);
        String csvString = countRowCsvRptService.generateCsvStringForCountRow(list);
        try {
            byte[] input = csvString.getBytes("Big5");
            if (csvString != null) {
                sessionLogService.createAndWriteSessionLog(request, SessionLog.PRIMARY_STATUS_COUNT_ROW, SessionLog.SECONDARY_STATUS_NO_MORE_DESCRIPTION, request.get("action", ""), null);
                return new ByteArrayDownloadResult(request, input, "text/plain", "countRow.csv");
            }
        } catch (UnsupportedEncodingException e) {
            logger.debug(e.getMessage());
        }
        return result;
    }

    @HandlerType(HandlerTypeEnum.FileDownload)
    @NetchLogWritingAction(functionId = FUNCTION_ID, writeSuccessLogAfterAction=true)
    public Result downloadPdf(Request request) throws FileNotFoundException {
        request.put("queryMode", "1");
        ByteArrayOutputStream outputstream = countRowPdfRptService.generateReport(request);
        sessionLogService.createAndWriteSessionLog(request, SessionLog.PRIMARY_STATUS_COUNT_ROW, SessionLog.SECONDARY_STATUS_NO_MORE_DESCRIPTION, request.get("action", ""), null);
        return new ByteArrayDownloadResult(request, outputstream.toByteArray(), ContextTypeEnum.pdf.toString(), "countRow.pdf");
    }
}
