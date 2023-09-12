/* 
 * MultiQueryHandler.java
 * 
 * Copyright (c) 2017 International Integrated System, Inc. 
 * All Rights Reserved.
 * 
 * Licensed Materials - Property of International Integrated System, Inc.
 * 
 * This software is confidential and proprietary information of 
 * International Integrated System, Inc. (&quot;Confidential Information&quot;).
 */
package com.bot.cqs.query.handler;

import com.bot.cqs.gateway.persistence.InquiryLog;
import com.bot.cqs.gateway.service.GatewayException;
import com.bot.cqs.monitor.proxy.MonitorThreadLocal;
import com.bot.cqs.query.annotation.NetchLogWritingAction;
import com.bot.cqs.query.command.MultiQueryCommand;
import com.bot.cqs.query.command.NetchLogContent;
import com.bot.cqs.query.dto.ResponseContent;
import com.bot.cqs.query.persistence.QueryBank;
import com.bot.cqs.query.persistence.QueryUser;
import com.bot.cqs.query.persistence.SessionLog;
import com.bot.cqs.query.persistence.TransactionRate;
import com.bot.cqs.query.service.EtchAuditLogService;
import com.bot.cqs.query.service.InquiryManager;
import com.bot.cqs.query.service.SessionLogService;
import com.bot.cqs.query.util.factory.QueryBankFactory;
import com.bot.cqs.query.util.inquiry.InquiryThread;
import com.bot.cqs.query.util.queryField.QueryFieldException;
import com.bot.cqs.query.util.queryField.QueryRequestDefinition;
import com.bot.cqs.query.web.WebAction;
import com.bot.cqs.security.model.EtchUserDetails;
import com.iisigroup.cap.annotation.HandlerType;
import com.iisigroup.cap.annotation.HandlerType.HandlerTypeEnum;
import com.iisigroup.cap.component.Request;
import com.iisigroup.cap.component.Result;
import com.iisigroup.cap.component.impl.AjaxFormResult;
import com.iisigroup.cap.db.utils.CapEntityUtil;
import com.iisigroup.cap.exception.CapMessageException;
import com.iisigroup.cap.security.CapSecurityContext;
import com.iisigroup.cap.utils.CapAppContext;
import com.iisigroup.cap.utils.CapBeanUtil;
import com.iisigroup.cap.utils.CapString;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <b>單筆查詢</b>, <b>多筆查詢</b> 所使用的 controller. 這個 controller 辨視輸入資料的方式是使用 <code>queryRequestDefinition</code> 屬性 ( 是一個 Map ) 中的 {@link QueryRequestDefinition } 物件, 只有定義在
 * <code>queryRequestDefinition</code> 屬性內的交易才會被執行.
 * <p>
 * <code>queryRequestDefinition</code> 的設定可以在 <i>botcqs-fields.xml</i> 中調整 *
 * 
 * @since 2017年1月9日
 * @author Sunkist
 * @version
 *          <ul>
 *          <li>2017年1月9日,Sunkist,new
 *          </ul>
 */
public class MultiQueryHandler extends NetchMFormHandler {

    private static final String A010 = "A010";
    private static final String A020 = "A020";
    private static final String ERROR_MSG_SECTION = "errorMsgSection";
    private static final String INPUT_TRANSACTION_ID = "inputTransactionId";

    public static final String EXCHANGE_KEY = MultiQueryHandler.class.getSimpleName() + "_datas";
    public static final String AVAILABLE_RATELIST = "availableRateList";

    @Resource
    MultiQueryCommand multiQueryCommand;

    @Resource
    QueryBankFactory queryBankFactory;

    @Resource
    InquiryManager inquiryManager;

    @Resource
    SessionLogService sessionLogService;

    @Resource
    EtchAuditLogService etchAuditLogService;

    int maxQueryCount;

    Map<String, QueryRequestDefinition> queryRequestDefinition;

    @Override
    NetchLogContent generateDataForLogWriting(Request request) {
        List<Object> argsList = new ArrayList<Object>();

        if (request.containsKey(INPUT_TRANSACTION_ID)) {
            argsList.add(INPUT_TRANSACTION_ID + "=" + request.get(INPUT_TRANSACTION_ID));
        }

        List<Object> descList = getDescList(request);
        if (!descList.isEmpty()) {
            argsList.add(descList);
        }
        return new NetchLogContent(argsList);
    }

    /**
     * 單筆查詢
     * 
     * @param request
     * @return
     */
    @HandlerType(HandlerTypeEnum.FORM)
    @NetchLogWritingAction(functionId = A010, writeLogBeforeAction = true, writeSuccessLogAfterAction = true)
    public Result inquiry(Request request) {
        AjaxFormResult result = new AjaxFormResult();

        ResponseContent resp = interlnalSubmit(request);
        MultiQueryCommand command = ((MultiQueryCommand) resp.getInputValue());

        result.putAll(CapBeanUtil.bean2Map(command, CapEntityUtil.getColumnName(command)));
        return result.set(ERROR_MSG_SECTION, resp.isErrorOccurs() ? resp.getResponseMessage() : "").set("status", resp.getResponseMessage());
    }

    /**
     * 多筆查詢
     * 
     * @param request
     * @return
     */
    @HandlerType(HandlerTypeEnum.FORM)
    @NetchLogWritingAction(functionId = A020, writeLogBeforeAction = true, writeSuccessLogAfterAction = true)
    public Result multiInquiry(Request request) {
        AjaxFormResult result = new AjaxFormResult();

        ResponseContent resp = interlnalSubmit(request);
        MultiQueryCommand command = ((MultiQueryCommand) resp.getInputValue());

        result.putAll(CapBeanUtil.bean2Map(command, CapEntityUtil.getColumnName(command)));
        return result.set(ERROR_MSG_SECTION, resp.isErrorOccurs() ? resp.getResponseMessage() : "").set("status", resp.getResponseMessage());
    }

    /**
     * 單/多筆查詢畫面結果點選列印則紀錄軌跡記錄
     *
     * @param request
     * @return
     */
    public void printWriteLog(Request request) {
        // 寫 EtchAuditLog
        String functionName = this.getClass().getSimpleName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName();
        wirteEtchAuditLog(request, functionName, 0);
    }

    private ResponseContent interlnalSubmit(Request request) {

        MultiQueryCommand multiQueryCommand = CapBeanUtil.map2Bean(request, new MultiQueryCommand());
        multiQueryCommand.parseInputTransactionId();
        String transactionId = multiQueryCommand.getTransactionId();
        String inputTransactionId = multiQueryCommand.getInputTransactionId();
        String queryAction = multiQueryCommand.getAction();
        String executeStatus = "Success";

        if (!TransactionRate.isTransactionIdValid(transactionId)) {
            throw new CapMessageException(CapAppContext.getMessage("invalidTransactonId.1", new Object[] { transactionId }), this.getClass());
        }

        // 取得 SessionLog 的 primaryCode
        String primaryStatus = null;
        try {
            primaryStatus = getPrimaryStatus(transactionId);
        } catch (Exception e) {
            // 無 primary status, 寫不成 SessionLog
            // setReturnMessage(request, "primary status not found");
            if (logger.isErrorEnabled()) {
                logger.error("Primary status of SessionLog not found [" + transactionId + "]", e);
            }
            throw new CapMessageException(CapAppContext.getMessage("inquiryTranslateError", new Object[] { transactionId }), this.getClass());
        }

        // user 所屬的部門要找的到, 取得付費行與交換所 ID
        // 沒有就不允許查詢
        QueryUser user = CapSecurityContext.<EtchUserDetails> getUser().getQueryUser();
        // WebAction.getDefaultLogger().warn(queryBankFactory.getQueryBank(user.getDepartmentId()).getShortDesc()+ user.getEmployeeId()+ user.getEmployeeName());
        QueryBank bank = queryBankFactory.getQueryBank(user.getDepartmentId());
        if (bank == null) {
            throw new CapMessageException(CapAppContext.getMessage("chargeBankIdNotFound"), this.getClass());
        }

        Map<String, Object> model = new HashMap<String, Object>();
        model.put("maxQueryCount", maxQueryCount);
        model.put(INPUT_TRANSACTION_ID, inputTransactionId);
        model.put("user", user);
        model.put("bank", bank);
        Map<String, String[]> paramMap = request.getServletRequest().getParameterMap();
        model.put("paramMap", paramMap);

        QueryRequestDefinition queryDef = getQueryRequestDefinition().get(transactionId);

        if (queryDef == null) {
            // setReturnMessage(request, CapAppContext.getMessage("js.multiQuery.msg.04"));
            throw new CapMessageException(CapAppContext.getMessage("loadModuleFailed.1", new Object[] { transactionId + "def" }), this.getClass());
        }

        model.put("queryDef", queryDef);

        boolean errorOccurs = false;

        // 送到輸入資料頁面
        if (!"query".equalsIgnoreCase(queryAction)) {
            ((HttpServletRequest) request.getServletRequest()).getSession(true).setAttribute(EXCHANGE_KEY, model);
            setReturnMessage(request, CapAppContext.getMessage("js.multiQuery.msg.05"));
            return new ResponseContent(errorOccurs, getReturnMsg(request), multiQueryCommand);
        }

        // 以 request 資料取得 InquiryLog, GatewayService 類別要用
        InquiryLog[] inquiryLogs = null;
        try {

            inquiryLogs = inquiryManager.getInquiryLogFromParams(request, paramMap, queryDef, user, bank, getMaxQueryCount());
        } catch (QueryFieldException qfe) {

            String errorMessage = CapAppContext.getMessage(qfe.getMessageId(), qfe.getArgs());
            if (CapString.isEmpty(errorMessage)) {
                errorMessage = qfe.getDefaultMessage();
            }
            if (errorMessage != null && qfe.getRequestFieldIndex() != QueryFieldException.UNKNOWN_REQUEST_FIELD_POSITION) {
                errorMessage = CapAppContext.getMessage("multiQuery.inquiry.which") + (qfe.getRequestFieldIndex() + 1) + CapAppContext.getMessage("multiQuery.inquiry.rowInquiry")+ CapAppContext.getMessage("multiQuery.inquiry.inputError") + errorMessage;
            }
            errorOccurs = true;
            setReturnMessage(request, errorMessage);
            return new ResponseContent(errorOccurs, getReturnMsg(request), multiQueryCommand);

        } catch (Exception e) {

            if (logger.isErrorEnabled()) {
                logger.error(CapAppContext.getMessage("js.multiQuery.msg.03"), e);
            }
            errorOccurs = true;
            setReturnMessage(request, CapAppContext.getMessage("js.multiQuery.msg.03"));
            return new ResponseContent(errorOccurs, getReturnMsg(request), multiQueryCommand);
        }

        if (inquiryLogs.length == 0) {
            errorOccurs = true;
            setReturnMessage(request, CapAppContext.getMessage("js.multiQuery.msg.01"));
            return new ResponseContent(errorOccurs, getReturnMsg(request), multiQueryCommand);
        }

        InquiryThread[] inquiryThreads = inquiryManager.getInquiryResult(inquiryLogs);
        model.put("inquiryThreads", inquiryThreads);
        String getPrepareStatement = MonitorThreadLocal.threadLocal.get();

        // Put model in session scope
        ((HttpServletRequest) request.getServletRequest()).getSession(true).setAttribute(EXCHANGE_KEY, model);
        setReturnMessage(request, CapAppContext.getMessage("js.multiQuery.msg.02"));
        // setReturnMessage(request, CapAppContext.getMessage("multiQuery.result.count", new Object[] { inquiryThreads.length }));

        // 個別寫 SessionLog
        for (int i = 0; i < inquiryThreads.length; i++) {
            String secondaryStatus;
            String memo = null;
            if (inquiryThreads[i].getThrowable() == null)
                secondaryStatus = SessionLog.SECONDARY_STATUS_NO_MORE_DESCRIPTION;
            else {
                secondaryStatus = SessionLog.SECONDARY_STATUS_INQUIRY_RESPONSE_ERROR;
                if (inquiryThreads[i].getThrowable() instanceof GatewayException) {
                    memo = ((GatewayException) inquiryThreads[i].getThrowable()).getErrorCode();
                }
            }
            SessionLog sessionLog = WebAction.createSessionLog(request, user, primaryStatus, secondaryStatus, memo, inquiryThreads[i].getInquiryLog().getInquiryLogKey());
            sessionLog.setAccessDatetime(inquiryThreads[i].getInquiryLog().getInquiryStartDatetime());

            sessionLogService.writeSessionLog(sessionLog);
        }

        // 寫 EtchAuditLog
        String functionName = this.getClass().getSimpleName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName();
        wirteEtchAuditLog(request, functionName, inquiryThreads.length);
        return new ResponseContent(errorOccurs, getReturnMsg(request), multiQueryCommand);
    }

    /**
     * 寫 EtchAuditLog
     *
     * @param request
     * @param functionName
     */
    public void wirteEtchAuditLog(Request request, String functionName, int quertyCount) {
        try {
            etchAuditLogService.saveEtchAuditLog(request, functionName, "", quertyCount);
        } catch (UnknownHostException e) {
            logger.debug(e.getMessage());
        }
    }

    private String getPrimaryStatus(String transactionId) throws NoSuchFieldException, IllegalAccessException {

        Field field = SessionLog.class.getField("PRIMARY_STATUS_INQUIRY_" + transactionId);
        return (String) field.get(null);
    }

    private List<Object> getDescList(Request request) {
        List<Object> descList = (ArrayList<Object>) request.getObject(getClass().getSimpleName() + "_RequestDescription");
        if (descList == null) {
            descList = new ArrayList<Object>();
            setDescList(request, descList);
        }
        return descList;
    }

    private void setDescList(Request request, List<Object> descList) {
        request.put(getClass().getSimpleName() + "_RequestDescription", descList);
    }

    /**
     * 查詢的筆數
     * 
     * @return
     */
    public int getMaxQueryCount() {

        return maxQueryCount;
    }

    /**
     * 單次最大查詢筆數.
     * <p>
     * 單筆查詢請設 1, 多筆查詢請設 2 以上
     * 
     * @param maxQueryCount
     */
    public void setMaxQueryCount(int maxQueryCount) {

        this.maxQueryCount = maxQueryCount;
    }

    /**
     * 取得查詢輸入欄位的基本定義
     * 
     * @return
     */
    public Map<String, QueryRequestDefinition> getQueryRequestDefinition() {

        return queryRequestDefinition;
    }

    public void setQueryRequestDefinition(Map<String, QueryRequestDefinition> queryRequestDefinition) {

        this.queryRequestDefinition = queryRequestDefinition;
    }

}
