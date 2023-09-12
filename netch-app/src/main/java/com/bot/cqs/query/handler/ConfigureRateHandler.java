/* 
 * ConfigureRateHandler.java
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

import com.bot.cqs.monitor.proxy.MonitorThreadLocal;
import com.bot.cqs.query.annotation.NetchLogWritingAction;
import com.bot.cqs.query.command.NetchLogContent;
import com.bot.cqs.query.command.TransactionRateCommand;
import com.bot.cqs.query.dto.ResponseContent;
import com.bot.cqs.query.persistence.SessionLog;
import com.bot.cqs.query.persistence.TransactionRate;
import com.bot.cqs.query.persistence.TransactionRateKey;
import com.bot.cqs.query.service.SessionLogService;
import com.bot.cqs.query.service.TransactionRateManager;
import com.bot.cqs.query.util.NetchLogWritingUtil;
import com.bot.cqs.query.util.factory.TransactionRateFactory;
import com.iisigroup.cap.annotation.HandlerType;
import com.iisigroup.cap.annotation.HandlerType.HandlerTypeEnum;
import com.iisigroup.cap.component.Request;
import com.iisigroup.cap.component.Result;
import com.iisigroup.cap.component.impl.AjaxFormResult;
import com.iisigroup.cap.component.impl.BeanGridResult;
import com.iisigroup.cap.db.dao.SearchSetting;
import com.iisigroup.cap.db.model.Page;
import com.iisigroup.cap.db.service.CommonService;
import com.iisigroup.cap.exception.CapFormatException;
import com.iisigroup.cap.exception.CapMessageException;
import com.iisigroup.cap.formatter.BeanFormatter;
import com.iisigroup.cap.formatter.Formatter;
import com.iisigroup.cap.security.CapSecurityContext;
import com.iisigroup.cap.utils.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

/**
 * <pre>
 * 作業管理 - 費率設定
 * </pre>
 * 
 * @since 2017年1月9日
 * @author Sunkist
 * @version
 *          <ul>
 *          <li>2017年1月9日,Sunkist,new
 *          </ul>
 */
@Controller("configureratehandler")
public class ConfigureRateHandler extends NetchMFormHandler {

    private static final String[] COLUMNS = new String[] { "transactionRate", "transactionPoundage", "transactionRecordsAtDiscount", "transactionDiscountRate", "inputRateType", "transactionName",
            "transactionType" };
    private static final String G030 = "G030";
    private static final String G040 = "G040";
    private static final String FUNCTION_ID = "functionId";
    private static final String ERROR_MSG_SECTION = "errorMsgSection";
    private static final String ACTION = "action";
    private static final String INPUT_RATE_TYPE = "inputRateType";

    @Resource
    CommonService commonService;
    @Resource
    TransactionRateCommand transactionRateCommand;
    @Resource
    TransactionRateFactory transactionRateFactory;
    @Resource
    TransactionRateManager transactionRateManager;
    @Resource
    SessionLogService sessionLogService;
    
    @Override
    NetchLogContent generateDataForLogWriting(Request request) {
        List<Object> argsList = new ArrayList<Object>();

        if (request.containsKey(INPUT_RATE_TYPE)) {
            argsList.add(INPUT_RATE_TYPE + "=" + (CapString.isEmpty(request.get(INPUT_RATE_TYPE)) ? "ALL" : request.get(INPUT_RATE_TYPE)));
        }

        List<Object> descList = getDescList(request);
        if (!descList.isEmpty()) {
            argsList.add(descList);
        }
        return new NetchLogContent(argsList);
    }

    @HandlerType(HandlerTypeEnum.FORM)
    public Result queryRateTypes(Request request) {

        AjaxFormResult result = new AjaxFormResult();

        List<TransactionRate> rates = TransactionRate.AVAILABLE_TRANSACTION_RATE;
        Map<String, String> map = new LinkedHashMap<String, String>();
        for (TransactionRate rate : rates) {
            map.put(rate.getKey().getTransactionId(), rate.getTransactionShortDesc());
        }

        result.putAll(map);
        return result;
    }

    private Page<TransactionRate> getTransactionRatePage(Request request) {

        transactionRateCommand = CapBeanUtil.map2Bean(request, new TransactionRateCommand());

        try {
            transactionRateCommand.caculateInputDate();
        } catch (IllegalArgumentException e) {
            throw new CapMessageException(CapAppContext.getMessage("invalidFormat.1", new Object[] { "日期" }), this.getClass());
        }

        transactionRateCommand.parseInputRateType();

        List<Object> descList = getDescList(request);
        descList.add(CapString.isEmpty(transactionRateCommand.getInputRateType()) ? "ALL" : transactionRateCommand.getInputRateType());
        if (transactionRateCommand.getInputDate() != null) {
            descList.add(CapDate.formatDate(transactionRateCommand.getInputDate(), "YYY/MM/DD"));
        }
        descList.add(transactionRateCommand.getInputQueryTypeDesc());
        setDescList(request, descList);

        String inputRateType = transactionRateCommand.getInputRateType();
        String transactionType = null;
        if (!CapString.isEmpty(inputRateType)) {
            transactionType = inputRateType;
        }

        List<TransactionRate> rateList = null;
        if (transactionRateCommand.getInputQueryType() != null && transactionRateCommand.getInputQueryType().equals(TransactionRateCommand.QUERY_CURRENT)) {
            rateList = transactionRateFactory.getTransactionRate(transactionType, transactionRateCommand.getInputDate());
        } else if (transactionRateCommand.getInputQueryType() != null && transactionRateCommand.getInputQueryType().equals(TransactionRateCommand.QUERY_HISTORY)) {
            rateList = transactionRateFactory.getTransactionRateHistory(transactionType, transactionRateCommand.getInputDate());
        } else {
            rateList = transactionRateFactory.getTransactionRateEffective(transactionType, null);
        }

        List<Object> argsList = new ArrayList<Object>();
        argsList.add(CapSecurityContext.getUserId());
        argsList.add(request.get(FUNCTION_ID));
        argsList.add(CapAppContext.getMessage("menu." + request.get(FUNCTION_ID)));

        argsList.addAll(getDescList(request));

        NetchLogContent c = new NetchLogContent(argsList);
        c.setReturnMsg(CapAppContext.getMessage("common.completed"));
        NetchLogWritingUtil.writeLogAfterAction(c);

        if (rateList == null || rateList.size() == 0) {
            sessionLogService.createAndWriteSessionLog(request, SessionLog.PRIMARY_STATUS_QUERY_RATE, SessionLog.SECONDARY_STATUS_DATA_NOT_FOUND,
                    "", transactionRateCommand.getTransactionType());
        }
        return new Page<TransactionRate>(rateList, 0, 0, 0);
    }

    class RateKeyFormatter implements BeanFormatter {
        private static final long serialVersionUID = 1L;

        @SuppressWarnings("unchecked")
        public String reformat(Object in) throws CapFormatException {
            String result = "";
            if (in instanceof TransactionRate) {
                TransactionRate rate = (TransactionRate) in;
                result = rate.getKey().getTransactionId();
            }
            return result;
        }
    }

    class EffectDateRocYMFormatter implements BeanFormatter {
        private static final long serialVersionUID = 1L;

        @SuppressWarnings("unchecked")
        public String reformat(Object in) throws CapFormatException {
            String result = "";
            if (in instanceof TransactionRate) {
                TransactionRate rate = (TransactionRate) in;
                result = rate.getKey().getEffectDateRocYM();
            }
            return result;
        }
    }

    class EffectDateRocYMDFormatter implements BeanFormatter {
        private static final long serialVersionUID = 1L;

        @SuppressWarnings("unchecked")
        public String reformat(Object in) throws CapFormatException {
            String result = "";
            if (in instanceof TransactionRate) {
                TransactionRate rate = (TransactionRate) in;
                result = rate.getKey().getEffectDateRocYMD();
            }
            return result;
        }
    }

    class EffectDateRocYearFormatter implements BeanFormatter {
        private static final long serialVersionUID = 1L;

        @SuppressWarnings("unchecked")
        public String reformat(Object in) throws CapFormatException {
            String result = "";
            if (in instanceof TransactionRate) {
                TransactionRate rate = (TransactionRate) in;
                result = rate.getKey().getEffectDateRocYear();
            }
            return result;
        }
    }

    class EffectDateMonthFormatter implements BeanFormatter {
        private static final long serialVersionUID = 1L;

        @SuppressWarnings("unchecked")
        public String reformat(Object in) throws CapFormatException {
            String result = "";
            if (in instanceof TransactionRate) {
                TransactionRate rate = (TransactionRate) in;
                result = rate.getKey().getEffectDateMonth();
            }
            return result;
        }
    }

    @HandlerType(HandlerTypeEnum.GRID)
    @NetchLogWritingAction(functionId = G030, writeLogBeforeAction = true, writeSuccessLogAfterAction = false)
    public BeanGridResult query(SearchSetting search, Request request) {
        request.put(FUNCTION_ID, G030);
        Page<TransactionRate> page = getTransactionRatePage(request);
        String getPrepareStatement = MonitorThreadLocal.threadLocal.get();
        sessionLogService.createAndWriteSessionLog(request, SessionLog.PRIMARY_STATUS_QUERY_RATE, SessionLog.SECONDARY_STATUS_NO_MORE_DESCRIPTION, null, transactionRateCommand.getTransactionType());
        Map<String, Formatter> formatters = new HashMap<String, Formatter>();
        formatters.put("key.transactionId", new RateKeyFormatter());
        formatters.put("key.effectDateRocYMD", new EffectDateRocYMDFormatter());
        return new BeanGridResult(page.getContent(), page.getTotalRow(), formatters);
    }

    @HandlerType(HandlerTypeEnum.GRID)
    @NetchLogWritingAction(functionId = G040, writeLogBeforeAction = true, writeSuccessLogAfterAction = false)
    public BeanGridResult queryForMaintain(SearchSetting search, Request request) {
        request.put(FUNCTION_ID, G040);
        Page<TransactionRate> page = getTransactionRatePage(request);
        String getPrepareStatement = MonitorThreadLocal.threadLocal.get();
        sessionLogService.createAndWriteSessionLog(request, SessionLog.PRIMARY_STATUS_QUERY_RATE, SessionLog.SECONDARY_STATUS_NO_MORE_DESCRIPTION, null, transactionRateCommand.getTransactionType());
        Map<String, Formatter> formatters = new HashMap<String, Formatter>();
        formatters.put("key.transactionId", new RateKeyFormatter());
        formatters.put("key.effectDateRocYM", new EffectDateRocYMFormatter());
        formatters.put("key.effectDateRocYMD", new EffectDateRocYMDFormatter());
        formatters.put("key.effectDateRocYear", new EffectDateRocYearFormatter());
        formatters.put("key.effectDateMonth", new EffectDateMonthFormatter());
        return new BeanGridResult(page.getContent(), page.getTotalRow(), formatters);
    }

    @HandlerType(HandlerTypeEnum.FORM)
    public Result checkIfDataExist(Request request) {
        return generalCheck(request);
    }

    private AjaxFormResult generalCheck(Request request) {
        AjaxFormResult result = new AjaxFormResult();

        TransactionRateCommand transactionRateCommand = getTransactionRateCommand(request);

        List<TransactionRate> rateList = transactionRateFactory.getTransactionRateEffective(transactionRateCommand.getInputRateType(), transactionRateCommand.getInputDate());
        if (rateList == null || rateList.size() < 1) {
            if (!"save".equalsIgnoreCase(transactionRateCommand.getAction())) {
                return result.set(ERROR_MSG_SECTION, CapAppContext.getMessage("js.rateConfigure.msg.05"));
            }
        } else {
            // 找到的只是目前日期"適用的費率", 有可能是以前日期
            // 還是要比一下日期
            if (transactionRateCommand.getInputDate().equals(rateList.get(0).getKey().getTransactionRateEffectDate())) {
                if ("save".equalsIgnoreCase(transactionRateCommand.getAction())) {
                    result.set(ERROR_MSG_SECTION, CapAppContext.getMessage("dataExists")).set("status", CapAppContext.getMessage("dataExists"));
                }
                transactionRateCommand = new TransactionRateCommand(rateList.get(0));
                result.putAll(CapBeanUtil.bean2Map(transactionRateCommand, COLUMNS));
                result = transformatResult(result, transactionRateCommand);
                return result;
            }
        }
        return result;
    }

    private TransactionRateCommand getTransactionRateCommand(Request request) {

        transactionRateCommand = CapBeanUtil.map2Bean(request, new TransactionRateCommand());
        transactionRateCommand.setKey(new TransactionRateKey());
        transactionRateCommand.parseAction();

        transactionRateCommand.caculateInputDate();
        transactionRateCommand.getKey().setTransactionRateEffectDate(transactionRateCommand.getInputDate());

        transactionRateCommand.parseInputRateType();
        transactionRateCommand.getKey().setTransactionId(transactionRateCommand.getInputRateType());

        return transactionRateCommand;
    }

    @HandlerType(HandlerTypeEnum.FORM)
    @NetchLogWritingAction(functionId = G040, writeLogBeforeAction = true, writeSuccessLogAfterAction = true)
    public Result configure(Request request) {
        AjaxFormResult result = new AjaxFormResult();

        ResponseContent resp = interlnalSubmit(request);
        TransactionRateCommand command = ((TransactionRateCommand) resp.getInputValue());

        result.putAll(CapBeanUtil.bean2Map(command, COLUMNS));
        result = transformatResult(result, command);
        return result.set(ERROR_MSG_SECTION, resp.isErrorOccurs() ? resp.getResponseMessage() : "").set("status", resp.getResponseMessage());
    }

    private AjaxFormResult transformatResult(AjaxFormResult result, TransactionRateCommand transactionRateCommand) {
        result.set("effectDateRocYMD", transactionRateCommand.getKey().getEffectDateRocYMD())
                .set("transactionRate", CapMath.getBigDecimal(Double.toString((Double) result.get("transactionRate"))).setScale(2, BigDecimal.ROUND_HALF_UP))
                .set("transactionPoundage", CapMath.getBigDecimal(Double.toString((Double) result.get("transactionPoundage"))).setScale(2, BigDecimal.ROUND_HALF_UP))
                .set("transactionDiscountRate", CapMath.getBigDecimal(CapMath.multiply(Double.toString((Double) result.get("transactionDiscountRate")), "100")).setScale(1, BigDecimal.ROUND_HALF_UP));
        return result;
    }

    private ResponseContent interlnalSubmit(Request request) {
        boolean errorOccurs = false;
        String responseMessage = "";
        TransactionRateCommand transactionRateCommand = getTransactionRateCommand(request);

        List<TransactionRate> existRateList = transactionRateFactory.getTransactionRate(transactionRateCommand.getInputRateType(), transactionRateCommand.getInputDate());
        TransactionRateKey key = transactionRateCommand.getKey();
        TransactionRate rate = transactionRateManager.find(key);

        // delete
        if ("delete".equalsIgnoreCase(transactionRateCommand.getAction())) {
            if (rate == null) {
                setReturnMessage(request, CapAppContext.getMessage("js.rateConfigure.msg.05"));

                sessionLogService.createAndWriteSessionLog(request, SessionLog.PRIMARY_STATUS_DELETE_RATE, SessionLog.SECONDARY_STATUS_DATA_NOT_FOUND,
                        CapDate.formatDate(transactionRateCommand.getInputDate(), "YYY/MM/DD"), key.getTransactionId());
                throw new CapMessageException(CapAppContext.getMessage("js.rateConfigure.msg.05"), this.getClass());
                // return new ResponseContent(errorOccurs, getReturnMsg(request), ransactionRateCommand);
            }

            if (rate.isModifiable()) {
                transactionRateManager.delete(rate);
                transactionRateManager.reload();
            }
            setReturnMessage(request, CapAppContext.getMessage("js.rateConfigure.msg.03"));

            sessionLogService.createAndWriteSessionLog(request, SessionLog.PRIMARY_STATUS_DELETE_RATE, SessionLog.SECONDARY_STATUS_NO_MORE_DESCRIPTION,
                    CapDate.formatDate(transactionRateCommand.getInputDate(), "YYY/MM/DD"), key.getTransactionId());
            return new ResponseContent(errorOccurs, getReturnMsg(request), transactionRateCommand);
        }

        String invalidFieldName = null;
        try {
            invalidFieldName = CapAppContext.getMessage("js.rateConfigure.grid.title.04");// "一般費率";
            transactionRateCommand.parseInputRate();
            invalidFieldName = CapAppContext.getMessage("js.rateConfigure.grid.title.05");// "手續費";
            transactionRateCommand.parseInputPoundage();
            invalidFieldName = CapAppContext.getMessage("js.rateConfigure.grid.title.07");// "折扣門檻";
            transactionRateCommand.parseInputRecordsAtDiscount();
            invalidFieldName = CapAppContext.getMessage("js.rateConfigure.grid.title.08");// "折扣費率";
            transactionRateCommand.parseInputDiscountRate();
        } catch (RuntimeException e) {
            if (logger.isErrorEnabled()) {
                logger.error(e.getMessage());
            }
            String message = CapAppContext.getMessage("invalidFormat.1", new Object[] { invalidFieldName });
            setReturnMessage(request, message);
            throw new CapMessageException(message, this.getClass());
        }

        Throwable exp = null;
        String primaryStatus = null;
        String secondaryStatus = null;

        try {

            if ("save".equalsIgnoreCase(transactionRateCommand.getAction())) {
                TransactionRate targetRate = new TransactionRate();
                CapBeanUtil.copyBean(transactionRateCommand, targetRate);
                primaryStatus = SessionLog.PRIMARY_STATUS_CREATE_RATE;
                transactionRateManager.save(targetRate);
                setReturnMessage(request, CapAppContext.getMessage("js.rateConfigure.msg.01"));
            } else {
                CapBeanUtil.copyBean(transactionRateCommand, rate);
                primaryStatus = SessionLog.PRIMARY_STATUS_UPDATE_RATE;
                if (rate.isModifiable()) {
                    transactionRateManager.update(rate);
                }
                setReturnMessage(request, CapAppContext.getMessage("js.rateConfigure.msg.02"));
            }

            transactionRateManager.reload();
            secondaryStatus = SessionLog.SECONDARY_STATUS_NO_MORE_DESCRIPTION;

        } catch (DataIntegrityViolationException ex1) {
            // 整合完整性, 例如資料已存在, 或不當的 null
            errorOccurs = true;
            exp = ex1;
            secondaryStatus = SessionLog.SECONDARY_STATUS_DATA_ALREADY_EXIST;
            setReturnMessage(request, CapAppContext.getMessage("dataExists", new Object[] {}));
        } catch (DataRetrievalFailureException ex2) {
            // 找不到資料
            errorOccurs = true;
            exp = ex2;
            secondaryStatus = SessionLog.SECONDARY_STATUS_DATA_NOT_FOUND;
            setReturnMessage(request, CapAppContext.getMessage("dataNotFound", new Object[] {}));
        } catch (OptimisticLockingFailureException ex3) {
            // 樂觀鎖定
            errorOccurs = true;
            exp = ex3;
            secondaryStatus = SessionLog.SECONDARY_STATUS_DATA_VERSION_ERROR;
            setReturnMessage(request, CapAppContext.getMessage("dataVersionError", new Object[] {}));
        } catch (InvalidDataAccessResourceUsageException ex4) {
            // 資料過長
            errorOccurs = true;
            exp = ex4;
            secondaryStatus = SessionLog.SECONDARY_STATUS_INVALID_DATA;
            setReturnMessage(request, CapAppContext.getMessage("invalidDataResource", new Object[] {}));
        }

        if (exp != null) {
            setReturnMessage(request, responseMessage);
            if (logger.isDebugEnabled()) {
                logger.debug("Configure Branch Error", exp);
            }
        }

        sessionLogService.createAndWriteSessionLog(request, primaryStatus, secondaryStatus, CapDate.formatDate(transactionRateCommand.getInputDate(), "YYY/MM/DD"),
                transactionRateCommand.getInputRateType());

        return new ResponseContent(errorOccurs, getReturnMsg(request), transactionRateCommand);
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

}
