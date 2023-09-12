package com.bot.cqs.query.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Resource;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Controller;

import com.bot.cqs.query.annotation.NetchLogWritingAction;
import com.bot.cqs.query.command.NetchLogContent;
import com.bot.cqs.query.persistence.QueryRole;
import com.bot.cqs.query.persistence.SessionLog;
import com.bot.cqs.query.service.InquiryLogManager;
import com.bot.cqs.query.service.QueryFunctionService;
import com.bot.cqs.query.service.QueryRoleService;
import com.bot.cqs.query.service.SessionLogService;
import com.bot.cqs.query.util.factory.QueryBankFactory;
import com.iisigroup.cap.component.Request;
import com.iisigroup.cap.component.Result;
import com.iisigroup.cap.component.impl.AjaxFormResult;
import com.iisigroup.cap.db.service.CommonService;
import com.iisigroup.cap.exception.CapMessageException;
import com.iisigroup.cap.utils.CapAppContext;

/**
 * <pre>
 * configureroledetailhandler
 * </pre>
 * 
 * @since 2016年12月29日
 * @author bob peng
 * @version
 *          <ul>
 *          <li>2016年12月29日,bob peng,new
 *          </ul>
 */
@Controller("configureroledetailhandler")
public class ConfigureRoleDetailHandler extends NetchMFormHandler {

    private static final String FUNCTION_ID = "C020";

    @Resource
    private CommonService commonService;
    @Resource
    private QueryRoleService queryRoleService;
    @Resource
    private InquiryLogManager inquiryLogManager;
    @Resource
    private QueryBankFactory queryBankFactory;
    @Resource
    private QueryFunctionService queryFunctionService;
    @Resource
    private SessionLogService sessionLogService;

    @Override
    NetchLogContent generateDataForLogWriting(Request request) {
        List<Object> argsList = new ArrayList<Object>();
        String action = request.get("action");
        String roleId = request.get("roleId");
        argsList.add(action);
        argsList.add(roleId);
        String returnMsg = "";
        if ("modify".equals(action)) {
            returnMsg = CapAppContext.getMessage("js.configureRoleDetail.log.msg.01");// 已讀取
        } else if ("update".equals(action)) {
            returnMsg = CapAppContext.getMessage("js.configureRoleDetail.log.msg.02");// 更改成功
        } else if ("delete".equals(action)) {
            returnMsg = CapAppContext.getMessage("js.configureRoleDetail.log.msg.03");// 刪除成功
        }
        return new NetchLogContent(argsList, returnMsg);
    }

    public Result renderFunctionOptions(Request request) {
        AjaxFormResult result = new AjaxFormResult();
        String[] functionIds = request.getParamsAsStringArray("functionIds");
        String functionOptionHtmlStr = queryFunctionService.renderFunctionOptions(functionIds);
        Map<String, String> map = new TreeMap<String, String>();
        map.put("htmlStr", functionOptionHtmlStr);
        result.putAll(map);
        return result;
    }

    @NetchLogWritingAction(functionId = FUNCTION_ID, writeLogBeforeAction = true, writeSuccessLogAfterAction = true)
    public Result loadData(Request request) {
        AjaxFormResult result = new AjaxFormResult();
        String roleId = request.get("roleId");
        QueryRole queryRole = queryRoleService.findQueryRoleByRoleId(roleId);

        Map<String, String> map = new TreeMap<String, String>();
        map.put("roleName", queryRole.getRoleName());
        map.put("roleId", queryRole.getRoleId());
        map.put("roleDesc", queryRole.getRoleDesc());
        map.put("roleEnabled", queryRole.isRoleEnabled() ? "1" : "0");
        map.put("functionIds", queryRole.getRoleFunction());
        result.putAll(map);
        return result;
    }

    @NetchLogWritingAction(functionId = FUNCTION_ID, writeLogBeforeAction = true, writeSuccessLogAfterAction = true)
    public Result updateRole(Request request) {
        AjaxFormResult result = new AjaxFormResult();

        String roleId = request.get("roleId");
        String roleDesc = request.get("roleDesc");
        String roleEnabled = request.get("roleEnabled");
        String[] functionIds = request.getParamsAsStringArray("functionIds");
        String ip = request.getServletRequest().getRemoteAddr() + "#" + request.getServletRequest().getRemotePort();

        try {
            QueryRole queryRole = queryRoleService.updateRole(roleId, roleDesc, roleEnabled, functionIds, ip);
            Map<String, String> map = new TreeMap<String, String>();
            map.put("roleName", queryRole.getRoleName());
            map.put("roleId", queryRole.getRoleId());
            map.put("roleDesc", queryRole.getRoleDesc());
            String displayStr = queryFunctionService.getDisplayFunctionStrByFunctionIds(queryRole.getRoleFunction());
            map.put("roleFunction", displayStr);
            map.put("responseMessage", CapAppContext.getMessage("js.configureRoleDetail.msg.01")); // update success
            map.put("roleEnabled", queryRole.isRoleEnabled() ? "1" : "0");
            result.putAll(map);
            return result;
        } catch (CapMessageException e) {
            // sessionLog
            String msg = e.getMessage();
            if (CapAppContext.getMessage("js.configureRoleDetail.msg.03").equals(msg)) {
                sessionLogService.createAndWriteSessionLog(request, SessionLog.PRIMARY_STATUS_UPDATE_ROLE, SessionLog.SECONDARY_STATUS_DATA_NOT_FOUND, "", roleId);
            } else if (CapAppContext.getMessage("js.configureRoleDetail.msg.04").equals(msg)) {
                sessionLogService.createAndWriteSessionLog(request, SessionLog.PRIMARY_STATUS_UPDATE_ROLE, SessionLog.SECONDARY_STATUS_INVALID_DATA, "", roleId);
            } else if (CapAppContext.getMessage("refuseUpdateDefaultRole").equals(msg)) {
                sessionLogService.createAndWriteSessionLog(request, SessionLog.PRIMARY_STATUS_UPDATE_ROLE, SessionLog.SECONDARY_STATUS_REFUSE_UPDATE, "", roleId);
            }
            throw e;
        }

    }

    @NetchLogWritingAction(functionId = FUNCTION_ID, writeLogBeforeAction = true, writeSuccessLogAfterAction = true)
    public Result deleteRole(Request request) {
        AjaxFormResult result = new AjaxFormResult();
        String ip = request.getServletRequest().getRemoteAddr() + "#" + request.getServletRequest().getRemotePort();
        String roleId = request.get("roleId");

        try {
            QueryRole queryRole = queryRoleService.deleteRole(roleId, ip);
            Map<String, String> map = new TreeMap<String, String>();
            map.put("roleName", queryRole.getRoleName());
            map.put("roleId", queryRole.getRoleId());
            map.put("roleDesc", queryRole.getRoleDesc());
            String displayStr = queryFunctionService.getDisplayFunctionStrByFunctionIds(queryRole.getRoleFunction());
            map.put("roleFunction", displayStr);
            map.put("responseMessage", CapAppContext.getMessage("js.configureRoleDetail.msg.02")); // 刪除成功
            map.put("roleEnabled", queryRole.isRoleEnabled() ? "1" : "0");
            result.putAll(map);
            return result;
        } catch (CapMessageException e) {
            // sessionLog
            String msg = e.getMessage();
            if (CapAppContext.getMessage("js.configureRoleDetail.msg.03").equals(msg)) {
                sessionLogService.createAndWriteSessionLog(request, SessionLog.PRIMARY_STATUS_DELETE_ROLE, SessionLog.SECONDARY_STATUS_DATA_NOT_FOUND, "", roleId);
            } else if (CapAppContext.getMessage("refuseUpdateDefaultRole").equals(msg)) {
                sessionLogService.createAndWriteSessionLog(request, SessionLog.PRIMARY_STATUS_DELETE_ROLE, SessionLog.SECONDARY_STATUS_REFUSE_UPDATE, "", roleId);
            }
            throw e;
        }
    }

}
