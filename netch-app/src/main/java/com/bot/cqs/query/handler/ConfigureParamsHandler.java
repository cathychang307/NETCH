/* 
 * ConfigureParamsHandler.java
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

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;

import com.bot.cqs.query.annotation.NetchLogWritingAction;
import com.bot.cqs.query.command.ApplicationParameterCommand;
import com.bot.cqs.query.command.NetchLogContent;
import com.bot.cqs.query.dto.ResponseContent;
import com.bot.cqs.query.persistence.ApplicationParameter;
import com.bot.cqs.query.service.ApplicationParameterManager;
import com.bot.cqs.query.util.ApplicationParameterType;
import com.bot.cqs.query.util.NetchLogWritingUtil;
import com.bot.cqs.query.util.factory.ApplicationParameterFactory;
import com.iisigroup.cap.annotation.HandlerType;
import com.iisigroup.cap.annotation.HandlerType.HandlerTypeEnum;
import com.iisigroup.cap.component.Request;
import com.iisigroup.cap.component.Result;
import com.iisigroup.cap.component.impl.AjaxFormResult;
import com.iisigroup.cap.exception.CapMessageException;
import com.iisigroup.cap.security.CapSecurityContext;
import com.iisigroup.cap.utils.CapAppContext;

/**
 * <ul>
 * 系統管理
 * <li>系統查詢參數設定</li>
 * <li>快取作業參數設定</li>
 * <li>帳務作業參數設定</li>
 * </ul>
 * 
 * @since 2017年1月9日
 * @author Sunkist
 * @version
 *          <ul>
 *          <li>2017年1月9日,Sunkist,new
 *          </ul>
 */
@Controller("configureparamshandler")
public class ConfigureParamsHandler extends NetchMFormHandler {

    private static final String E020 = "E020";
    private static final String E030 = "E030";
    private static final String E040 = "E040";
    private static final String FUNCTION_ID = "functionId";
    private static final String CURRENT_VALUE = "currentValue";
    private static final String PARAMETER_NAME = "parameterName";
    private static final String ERROR_MSG_SECTION = "errorMsgSection";

    @Resource
    ApplicationParameterCommand parameterCommand;

    @Resource
    ApplicationParameterFactory applicationParameterFactory;

    @Resource
    ApplicationParameterManager applicationParameterManager;

    @HandlerType(HandlerTypeEnum.FORM)
    public Result getCurrentParamValue(Request request) {
        return new AjaxFormResult().set(CURRENT_VALUE, applicationParameterFactory.getApplicationParameter(request.get(PARAMETER_NAME)).getParameterValue());
    }

    @HandlerType(HandlerTypeEnum.FORM)
    public Result checkQueryCountBinding(Request request) {
        request.put(PARAMETER_NAME, ApplicationParameterFactory.PARAM_QUERY_MAX_ROWS);
        generalCheck(request);
        return new AjaxFormResult();
    }

    @HandlerType(HandlerTypeEnum.FORM)
    @NetchLogWritingAction(functionId = E020, writeLogBeforeAction = true, writeSuccessLogAfterAction = false)
    public Result configureQueryCount(Request request) {
        AjaxFormResult result = new AjaxFormResult();
        request.put(PARAMETER_NAME, ApplicationParameterFactory.PARAM_QUERY_MAX_ROWS);
        request.put(FUNCTION_ID, E020);

        ResponseContent resp = interlnalSubmit(request);
        String newvalue = (String) resp.getInputValue();

        String title = CapAppContext.getMessage("applicationParameterResult.title." + request.get(FUNCTION_ID));
        return result.set(ERROR_MSG_SECTION, resp.isErrorOccurs() ? resp.getResponseMessage() : "").set("title", title).set("name", title).set("value", newvalue).set("status",
                resp.getResponseMessage());
    }

    @HandlerType(HandlerTypeEnum.FORM)
    public Result checkQueryCacheBinding(Request request) {
        request.put(PARAMETER_NAME, ApplicationParameterFactory.PARAM_QUERY_CACHE_INTERVAL);
        generalCheck(request);
        return new AjaxFormResult();
    }

    @HandlerType(HandlerTypeEnum.FORM)
    @NetchLogWritingAction(functionId = E030, writeLogBeforeAction = true, writeSuccessLogAfterAction = false)
    public Result configureQueryCache(Request request) {
        AjaxFormResult result = new AjaxFormResult();
        request.put(PARAMETER_NAME, ApplicationParameterFactory.PARAM_QUERY_CACHE_INTERVAL);
        request.put(FUNCTION_ID, E030);

        ResponseContent resp = interlnalSubmit(request);
        String newvalue = (String) resp.getInputValue();

        String title = CapAppContext.getMessage("applicationParameterResult.title." + request.get(FUNCTION_ID));
        return result.set(ERROR_MSG_SECTION, resp.isErrorOccurs() ? resp.getResponseMessage() : "").set("title", title).set("name", title).set("value", newvalue).set("status",
                resp.getResponseMessage());
    }

    @HandlerType(HandlerTypeEnum.FORM)
    public Result checkAccountCaculateTypeBinding(Request request) {
        request.put(PARAMETER_NAME, ApplicationParameterFactory.PARAM_ACCOUNT_CACULATE_TYPE);
        generalCheck(request);
        return new AjaxFormResult();
    }

    @HandlerType(HandlerTypeEnum.FORM)
    @NetchLogWritingAction(functionId = E040, writeLogBeforeAction = true, writeSuccessLogAfterAction = false)
    public Result configureAccountCaculateType(Request request) {
        AjaxFormResult result = new AjaxFormResult();
        request.put(PARAMETER_NAME, ApplicationParameterFactory.PARAM_ACCOUNT_CACULATE_TYPE);
        request.put(FUNCTION_ID, E040);

        ResponseContent resp = interlnalSubmit(request);
        String newvalue = CapAppContext.getMessage("accountCaculateType.type." + resp.getInputValue());

        String title = CapAppContext.getMessage("applicationParameterResult.title." + request.get(FUNCTION_ID));
        return result.set(ERROR_MSG_SECTION, resp.isErrorOccurs() ? resp.getResponseMessage() : "").set("title", title).set("name", title).set("value", newvalue).set("status",
                resp.getResponseMessage());
    }

    @Override
    NetchLogContent generateDataForLogWriting(Request request) {
        List<Object> argsList = new ArrayList<Object>();
        argsList.add("newvalue=" + request.get("newvalue").trim());
        List<Object> descList = getDescList(request);
        if (!descList.isEmpty()) {
            argsList.add(descList);
        }
        return new NetchLogContent(argsList);
    }

    private void generalCheck(Request request) {
        parameterCommand.setParameterName(request.get(PARAMETER_NAME));
        parameterCommand.setParameterValue(request.get("newvalue").trim());
        int rc = applicationParameterFactory.checkParameterStatus(parameterCommand);
        switch (rc) {
        case 0:
            break;
        case -2:
            ApplicationParameterType type = applicationParameterFactory.getApplicationParameterType(parameterCommand.getParameterName());
            throw new CapMessageException(CapAppContext.getMessage("invalidFormat.2", new Object[] { CapAppContext.getMessage("applicationParameterResult.newvalue"), type.getDescription() }),
                    this.getClass());
        default:
            throw new CapMessageException(CapAppContext.getMessage("undefinedAction"), this.getClass());
        }

        ApplicationParameter parameter = applicationParameterFactory.getApplicationParameter(parameterCommand.getParameterName());
        // request.put("applicationParameter", parameter);

        if (parameter == null) {
            throw new CapMessageException(CapAppContext.getMessage("dataNotFound"), this.getClass());
        }

        parameter.setParameterValue(parameterCommand.getParameterValue());

    }

    private ResponseContent interlnalSubmit(Request request) {
        ApplicationParameterCommand parameterCommand = new ApplicationParameterCommand();
        parameterCommand.setParameterName(request.get(PARAMETER_NAME));
        parameterCommand.setParameterValue(request.get("newvalue").trim());

        ResponseContent resp = applicationParameterManager.updateParamAndSessionLog(request, parameterCommand, logger);

        List<Object> descList = getDescList(request);

        descList.add(CapSecurityContext.getUserId());
        descList.add(request.get(FUNCTION_ID));
        descList.add(CapAppContext.getMessage("menu." + request.get(FUNCTION_ID)));

        descList.add(request.get(PARAMETER_NAME));
        descList.add(resp.getParameter().getParameterValue());
        descList.add(resp.getParameter().getParameterDesc());
        setDescList(request, descList);

        NetchLogContent c = new NetchLogContent(descList);
        c.setReturnMsg(resp.getResponseMessage());
        NetchLogWritingUtil.writeLogAfterAction(c);

        setReturnMessage(request, resp.getResponseMessage());
        return resp;
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
