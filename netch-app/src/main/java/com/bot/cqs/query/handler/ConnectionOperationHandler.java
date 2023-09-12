/* 
 * ConnectionOperationHandler.java
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

import com.bot.cqs.gateway.context.ContextLoader;
import com.bot.cqs.gateway.context.MQInstance;
import com.bot.cqs.gateway.handler.channel.impl.IBMWebSphereMQUtils;
import com.bot.cqs.gateway.service.BOTGatewayService;
import com.bot.cqs.gateway.service.GatewayException;
import com.bot.cqs.query.annotation.NetchLogWritingAction;
import com.bot.cqs.query.command.ConnectionOperationCommand;
import com.bot.cqs.query.command.NetchLogContent;
import com.bot.cqs.query.persistence.SessionLog;
import com.bot.cqs.query.service.SessionLogService;
import com.bot.cqs.query.web.WebAction;
import com.bot.cqs.security.model.EtchUserDetails;
import com.ibm.mq.MQException;
import com.ibm.mq.MQQueueManager;
import com.iisigroup.cap.annotation.HandlerType;
import com.iisigroup.cap.annotation.HandlerType.HandlerTypeEnum;
import com.iisigroup.cap.component.Request;
import com.iisigroup.cap.component.Result;
import com.iisigroup.cap.component.impl.AjaxFormResult;
import com.iisigroup.cap.exception.CapMessageException;
import com.iisigroup.cap.security.CapSecurityContext;
import com.iisigroup.cap.utils.CapAppContext;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 * 系統管理 - 連線作業
 * </pre>
 * 
 * @since 2017年1月9日
 * @author Sunkist
 * @version
 *          <ul>
 *          <li>2017年1月9日,Sunkist,new
 *          </ul>
 */
@Controller("connectionoperationhandler")
public class ConnectionOperationHandler extends NetchMFormHandler {

    private static final String E010 = "E010";

    @Resource
    ConnectionOperationCommand command;

    @Resource
    SessionLogService sessionLogService;

    @HandlerType(HandlerTypeEnum.FORM)
    public Result checkBinding(Request request) {
        command.setInputAction(request.get("inputAction"));
        if (command.isActionValid()) {
            return new AjaxFormResult();
        }
        throw new CapMessageException(CapAppContext.getMessage("invalidFormat.1", new Object[] { CapAppContext.getMessage("connectionOperation.title") }), this.getClass());
    }

    @HandlerType(HandlerTypeEnum.FORM)
    @NetchLogWritingAction(functionId = E010, writeLogBeforeAction = true, writeSuccessLogAfterAction = true)
    public Result run(Request request) {

        AjaxFormResult connResult = new AjaxFormResult();
        ConnectionOperationCommand command = new ConnectionOperationCommand();
        command.setInputAction(request.get("inputAction"));
        logger.debug("[ConnectionOperationCommand] inputAction=" + request.get("inputAction"));

        MQInstance tchInstance = (MQInstance) ContextLoader.getGatewayContext().getIbmWebSphereMQ().getInstances().get(IBMWebSphereMQUtils.TCH);
        MQQueueManager botMQTCHQueueManager = null;
        try {
            botMQTCHQueueManager = new MQQueueManager(tchInstance.getQueueManager(), IBMWebSphereMQUtils.getMQProperies(IBMWebSphereMQUtils.TCH), IBMWebSphereMQUtils.BOTMQTCHConnMgr);
        } catch (MQException e) {
            logger.debug(e.getMessage());
        }
        
        String primaryStatus = null;
        String secondaryStatus = null;
        String responseMessage = null;
        boolean isPass = false;
        try {
            logger.debug("[ConnectionOperationCommand] Action=" + command.getAction());
            if (command.getAction() == ConnectionOperationCommand.ACTION_LOGON) {
                primaryStatus = SessionLog.PRIMARY_STATUS_INQUIRY_LOGON;
                logger.debug("[ConnectionOperationCommand] BOTGatewayService logon.");
                BOTGatewayService.logon(botMQTCHQueueManager);
            } else {
                primaryStatus = SessionLog.PRIMARY_STATUS_INQUIRY_LOGOFF;
                logger.debug("[ConnectionOperationCommand] BOTGatewayService logoff.");
                BOTGatewayService.logoff(botMQTCHQueueManager);
            }
            secondaryStatus = SessionLog.SECONDARY_STATUS_NO_MORE_DESCRIPTION;
            // 成功
            responseMessage = CapAppContext.getMessage("connectionOperation.success");
            isPass = true;
            logger.debug("[ConnectionOperationCommand] BOTGatewayService logoff:" + isPass);
        } catch (GatewayException e) {
            logger.debug(e.getMessage());
            secondaryStatus = SessionLog.SECONDARY_STATUS_INQUIRY_SESSION_FAILED;
            // 失敗
            responseMessage = CapAppContext.getMessage("connectionOperation.fail");
            connResult.set("errorMessage", e.getErrorMessage());
            logger.error("[ConnectionOperationCommand] GatewayException: "+e + e.toString());
        } catch (Exception e) {
            logger.debug(e.getMessage());
            if (logger.isErrorEnabled()) {
                logger.error("[ConnectionOperationCommand] Exception: "+e + e.toString());
            }
            // 失敗
            responseMessage = CapAppContext.getMessage("connectionOperation.fail");
            connResult.set("errorMessage", e.getMessage());
        }
        logger.debug("[ConnectionOperationCommand] BOTGatewayService logon responseMessage=" + responseMessage);

        SessionLog sessionLog = WebAction.createSessionLog(request, ((EtchUserDetails) CapSecurityContext.getUser()).getQueryUser(), primaryStatus, secondaryStatus, null, null);
        try {
            sessionLogService.writeSessionLog(sessionLog);
        } catch (DataAccessException e) {
            logger.debug(e.getMessage());
            if (logger.isErrorEnabled()) {
                logger.error("[ConnectionOperationCommand] SessionLog write error: " + e);
            }
        }

        connResult.set("action", command.getInputAction());
        connResult.set("status", CapAppContext.getMessage("connectionOperation.status." + (isPass ? "1" : "0")));
        // connResult.set("command", command.toString());

        setReturnMessage(request, responseMessage);
        logger.debug("[ConnectionOperationCommand] BOTGatewayService logon ReturnMessage=" + responseMessage);
        return connResult.set("errorMsgSection", !isPass ? responseMessage : "");
    }

    @Override
    NetchLogContent generateDataForLogWriting(Request request) {
        List<Object> argsList = new ArrayList<Object>();
        argsList.add("action=" + request.get("inputAction"));
        // argsList.add("connCommand=" + command);
        return new NetchLogContent(argsList);
    }
}
