/* 
 * ConfigureBranchHandler.java
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
import com.bot.cqs.query.command.QueryBankCommand;
import com.bot.cqs.query.dto.ResponseContent;
import com.bot.cqs.query.persistence.QueryBank;
import com.bot.cqs.query.persistence.SessionLog;
import com.bot.cqs.query.service.QueryBankManager;
import com.bot.cqs.query.service.SessionLogService;
import com.bot.cqs.query.util.factory.QueryBankFactory;
import com.bot.cqs.query.util.idValidation.BotBranchIdValidation;
import com.iisigroup.cap.annotation.HandlerType;
import com.iisigroup.cap.annotation.HandlerType.HandlerTypeEnum;
import com.iisigroup.cap.component.Request;
import com.iisigroup.cap.component.Result;
import com.iisigroup.cap.component.impl.AjaxFormResult;
import com.iisigroup.cap.component.impl.BeanGridResult;
import com.iisigroup.cap.db.constants.SearchMode;
import com.iisigroup.cap.db.dao.SearchSetting;
import com.iisigroup.cap.db.model.Page;
import com.iisigroup.cap.db.service.CommonService;
import com.iisigroup.cap.db.utils.CapEntityUtil;
import com.iisigroup.cap.exception.CapMessageException;
import com.iisigroup.cap.utils.CapAppContext;
import com.iisigroup.cap.utils.CapBeanUtil;
import com.iisigroup.cap.utils.CapString;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * <pre>
 * 作業管理 - 分行基本資料維護
 * </pre>
 * 
 * @since 2017年1月9日
 * @author Sunkist
 * @version
 *          <ul>
 *          <li>2017年1月9日,Sunkist,new
 *          </ul>
 */
@Controller("configurebranchhandler")
public class ConfigureBranchHandler extends NetchMFormHandler {

    private static final String G010 = "G010";
    private static final String G020 = "G020";
    private static final String FUNCTION_ID = "functionId";
    private static final String ERROR_MSG_SECTION = "errorMsgSection";
    private static final String ACTION = "action";
    private static final String SELECTED_DEPARTMENT_ID = "selectedDepartmentId";

    @Resource
    CommonService commonService;

    @Resource
    QueryBankCommand queryBankCommand;

    @Resource
    QueryBankFactory queryBankFactory;

    @Resource
    QueryBankManager queryBankManager;

    @Resource
    SessionLogService sessionLogService;

    @Override
    NetchLogContent generateDataForLogWriting(Request request) {
        List<Object> argsList = new ArrayList<Object>();

        if (request.containsKey(SELECTED_DEPARTMENT_ID)) {
            argsList.add(SELECTED_DEPARTMENT_ID + "=" + (CapString.isEmpty(request.get(SELECTED_DEPARTMENT_ID)) ? "ALL" : request.get(SELECTED_DEPARTMENT_ID)));
        }

        queryBankCommand = CapBeanUtil.map2Bean(request, queryBankCommand);
        if (!CapString.isEmpty(queryBankCommand.getDepartmentId())) {
            argsList.add(queryBankCommand);
        }

        List<Object> descList = getDescList(request);
        if (!descList.isEmpty()) {
            argsList.add(descList);
        }
        return new NetchLogContent(argsList);
    }

    @HandlerType(HandlerTypeEnum.FORM)
    public Result queryBranches(Request request) {

        AjaxFormResult result = new AjaxFormResult();

        List<QueryBank> banks = queryBankFactory.getQueryBankList();
        Map<String, String> map = new LinkedHashMap<String, String>();
        for (QueryBank bank : banks) {
            map.put(bank.getDepartmentId(), bank.getShortDesc());
        }

        result.putAll(map);
        return result;
    }

    @HandlerType(HandlerTypeEnum.GRID)
    @NetchLogWritingAction(functionId = G010, writeLogBeforeAction = true, writeSuccessLogAfterAction = true)
    public BeanGridResult query(SearchSetting search, Request request) {
        String selectedDepartmentId = request.get(SELECTED_DEPARTMENT_ID);
        if (!CapString.isEmpty(selectedDepartmentId)) {
            search.addSearchModeParameters(SearchMode.EQUALS, "departmentId", selectedDepartmentId);
        }
        Page<QueryBank> page = commonService.findPage(QueryBank.class, search);
        String getPrepareStatement = MonitorThreadLocal.threadLocal.get();
        setReturnMessage(request, CapAppContext.getMessage("common.completed"));
        sessionLogService.createAndWriteSessionLog(request, SessionLog.PRIMARY_STATUS_QUERY_BRANCH, SessionLog.SECONDARY_STATUS_NO_MORE_DESCRIPTION, null, selectedDepartmentId);
        return new BeanGridResult(page.getContent(), page.getTotalRow(), null);
    }

    @HandlerType(HandlerTypeEnum.GRID)
    @NetchLogWritingAction(functionId = G020, writeLogBeforeAction = true, writeSuccessLogAfterAction = true)
    public BeanGridResult queryForMaintain(SearchSetting search, Request request) {
        String selectedDepartmentId = request.get(SELECTED_DEPARTMENT_ID);
        if (!CapString.isEmpty(selectedDepartmentId)) {
            search.addSearchModeParameters(SearchMode.EQUALS, "departmentId", selectedDepartmentId);
        }
        Page<QueryBank> page = commonService.findPage(QueryBank.class, search);
        String getPrepareStatement = MonitorThreadLocal.threadLocal.get();
        setReturnMessage(request, CapAppContext.getMessage("common.completed"));
        sessionLogService.createAndWriteSessionLog(request, SessionLog.PRIMARY_STATUS_QUERY_BRANCH, SessionLog.SECONDARY_STATUS_NO_MORE_DESCRIPTION, null, selectedDepartmentId);
        return new BeanGridResult(page.getContent(), page.getTotalRow(), null);
    }

    @HandlerType(HandlerTypeEnum.FORM)
    public Result checkIfDataExist(Request request) {
        return generalCheck(request);
    }

    @HandlerType(HandlerTypeEnum.FORM)
    @NetchLogWritingAction(functionId = G020, writeLogBeforeAction = true, writeSuccessLogAfterAction = true)
    public Result configure(Request request) {
        AjaxFormResult result = new AjaxFormResult();

        ResponseContent resp = interlnalSubmit(request);
        QueryBankCommand command = ((QueryBankCommand) resp.getInputValue());

        result.putAll(CapBeanUtil.bean2Map(command, CapEntityUtil.getColumnName(command)));
        return result.set(ERROR_MSG_SECTION, resp.isErrorOccurs() ? resp.getResponseMessage() : "").set("status", resp.getResponseMessage());
    }

    private AjaxFormResult generalCheck(Request request) {
        AjaxFormResult result = new AjaxFormResult();
        queryBankCommand = CapBeanUtil.map2Bean(request, queryBankCommand);
        QueryBank bank = queryBankFactory.getQueryBank(queryBankCommand.getDepartmentId());
        if (!"save".equalsIgnoreCase(queryBankCommand.getAction())) {
            if (bank == null) {
                return result.set(ERROR_MSG_SECTION, CapAppContext.getMessage("js.branchConfigure.msg.05"));
            } else {
                return result.putAll(CapBeanUtil.bean2Map(bank, CapEntityUtil.getColumnName(bank)));
            }
        } else {
            if (bank != null) {
                result.putAll(CapBeanUtil.bean2Map(queryBankCommand, CapEntityUtil.getColumnName(queryBankCommand)));
                return result.set(ERROR_MSG_SECTION, CapAppContext.getMessage("dataExists")).set("status", CapAppContext.getMessage("dataExists"));
            }
        }
        return result;
    }

    private ResponseContent interlnalSubmit(Request request) {
        boolean errorOccurs = false;
        String responseMessage = "";
        QueryBankCommand queryBankCommand = CapBeanUtil.map2Bean(request, new QueryBankCommand());
        String departmentId = queryBankCommand.getDepartmentId();
        QueryBank queryBank = queryBankManager.find(departmentId);

        // delete
        if ("delete".equalsIgnoreCase(queryBankCommand.getAction())) {
            if (queryBank == null) {
                setReturnMessage(request, CapAppContext.getMessage("js.branchConfigure.msg.05"));

                sessionLogService.createAndWriteSessionLog(request, SessionLog.PRIMARY_STATUS_DELETE_BRANCH, SessionLog.SECONDARY_STATUS_DATA_NOT_FOUND, null, departmentId);
                throw new CapMessageException(CapAppContext.getMessage("js.branchConfigure.msg.05"), this.getClass());
                // return new ResponseContent(errorOccurs, getReturnMsg(request), queryBankCommand);
            }

            queryBankManager.delete(queryBank);
            queryBankManager.reload();
            setReturnMessage(request, CapAppContext.getMessage("js.branchConfigure.msg.03"));

            sessionLogService.createAndWriteSessionLog(request, SessionLog.PRIMARY_STATUS_DELETE_BRANCH, SessionLog.SECONDARY_STATUS_NO_MORE_DESCRIPTION, null, departmentId);
            return new ResponseContent(errorOccurs, getReturnMsg(request), new QueryBankCommand(queryBank));
        }

        String emptyFieldName = null;
        if (CapString.isEmpty(queryBankCommand.getDepartmentId())) {
            emptyFieldName = CapAppContext.getMessage("js.branchConfigure.grid.title.01");
        } else if (CapString.isEmpty(queryBankCommand.getDepartmentName())) {
            emptyFieldName = CapAppContext.getMessage("js.branchConfigure.grid.title.02");
        } else if (CapString.isEmpty(queryBankCommand.getChargeBankId())) {
            emptyFieldName = CapAppContext.getMessage("js.branchConfigure.grid.title.03");
        } else if (CapString.isEmpty(queryBankCommand.getTchId())) {
            emptyFieldName = CapAppContext.getMessage("js.branchConfigure.grid.title.05");
        }

        if (emptyFieldName != null) {
            String message = CapAppContext.getMessage("valueRequired.1", new Object[] { emptyFieldName });
            setReturnMessage(request, message);
            throw new CapMessageException(message, this.getClass());
        }

        String invalidFieldName = null;
        if (!BotBranchIdValidation.isBranchIdValid(queryBankCommand.getChargeBankId())) {
            invalidFieldName = CapAppContext.getMessage("js.branchConfigure.grid.title.03");
        } else if (!BotBranchIdValidation.isTchIdValid(queryBankCommand.getTchId())) {
            invalidFieldName = CapAppContext.getMessage("js.branchConfigure.grid.title.05");
        }

        if (invalidFieldName != null) {
            String message = CapAppContext.getMessage("invalidFormat.1", new Object[] { invalidFieldName });
            setReturnMessage(request, message);
            throw new CapMessageException(message, this.getClass());
        }

        Throwable exp = null;
        String primaryStatus = null;
        String secondaryStatus = null;

        try {

            if ("save".equalsIgnoreCase(queryBankCommand.getAction())) {
                QueryBank targetBank = new QueryBank();
                CapBeanUtil.copyBean(queryBankCommand, targetBank);
                primaryStatus = SessionLog.PRIMARY_STATUS_CREATE_BRANCH;
                queryBankManager.save(targetBank);
                setReturnMessage(request, CapAppContext.getMessage("js.branchConfigure.msg.01"));
            } else {
                CapBeanUtil.copyBean(queryBankCommand, queryBank);
                primaryStatus = SessionLog.PRIMARY_STATUS_UPDATE_BRANCH;
                queryBankManager.update(queryBank);
                setReturnMessage(request, CapAppContext.getMessage("js.branchConfigure.msg.02"));
            }

            queryBankManager.reload();
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

        sessionLogService.createAndWriteSessionLog(request, primaryStatus, secondaryStatus, null, queryBankCommand.getDepartmentId());

        return new ResponseContent(errorOccurs, getReturnMsg(request), queryBankCommand);
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
