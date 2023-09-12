package com.bot.cqs.query.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;

import com.bot.cqs.query.annotation.NetchLogWritingAction;
import com.bot.cqs.query.command.NetchLogContent;
import com.bot.cqs.query.exception.NetchLogMessageException;
import com.bot.cqs.query.persistence.QueryRole;
import com.bot.cqs.query.persistence.SessionLog;
import com.bot.cqs.query.service.QueryFunctionService;
import com.bot.cqs.query.service.QueryRoleService;
import com.bot.cqs.query.service.SessionLogService;
import com.iisigroup.cap.component.Request;
import com.iisigroup.cap.component.Result;
import com.iisigroup.cap.component.impl.AjaxFormResult;
import com.iisigroup.cap.exception.CapMessageException;
import com.iisigroup.cap.security.CapSecurityContext;
import com.iisigroup.cap.utils.CapAppContext;
import com.iisigroup.cap.utils.CapString;

/**
 * <pre>
 * 待辦簽單
 * </pre>
 * 
 * @since 2015/9/10
 * @author bob peng
 * @version
 *          <ul>
 *          <li>2015/9/10,bob peng,new
 *          </ul>
 */
@Controller("createrolehandler")
public class CreateRoleHandler extends NetchMFormHandler {

    private static final String FUNCTION_ID = "C010";
    
    @Resource
    private QueryRoleService queryRoleService;
    @Resource
    private QueryFunctionService queryFunctionService;
    @Resource
    private SessionLogService sessionLogService;
    
    public Result getLoginInfo(Request request) {
        AjaxFormResult result = new AjaxFormResult();
        result.set("result", CapSecurityContext.getUserId());
        return result;
    }
    
    @Override
    NetchLogContent generateDataForLogWriting(Request request) {
        List<Object> argsList = new ArrayList<Object>();
        String roleId = request.get("roleId");
        argsList.add(roleId);
        String returnMsg = CapAppContext.getMessage("js.createRole.log.msg.01"); // 新增完成
        return new NetchLogContent(argsList, returnMsg);
    }

    @NetchLogWritingAction(functionId = FUNCTION_ID, writeLogBeforeAction = true, writeSuccessLogAfterAction = true)
    public Result createQueryRole(Request request) {
        AjaxFormResult result = new AjaxFormResult();
        String roleName = request.get("roleName");
        String roleId = request.get("roleId");
        String roleDesc = request.get("roleDesc");

        if (CapString.isEmpty(roleId)) {
            throw new CapMessageException(CapAppContext.getMessage("js.createRole.errorMsg.02"), this.getClass());// [角色代號] 欄位不可為空白
        }
        if (CapString.isEmpty(roleName)) {
            throw new CapMessageException(CapAppContext.getMessage("js.createRole.errorMsg.03"), this.getClass());// [角色名稱] 欄位不可為空白
        }
        QueryRole existingQueryRole = queryRoleService.findQueryRoleByRoleId(roleId);
        if (existingQueryRole != null) {
            sessionLogService.createAndWriteSessionLog(request, SessionLog.PRIMARY_STATUS_CREATE_ROLE, SessionLog.SECONDARY_STATUS_DATA_ALREADY_EXIST, null, roleId);
            throw new NetchLogMessageException(CapAppContext.getMessage("js.createRole.errorMsg.01", new String[] { roleId }), this.getClass(), CapAppContext.getMessage("js.createRole.log.msg.02"));// [{0}]資料已存在
        }
        QueryRole queryRole = queryRoleService.createQueryRole(roleName, roleId, roleDesc);
        Map<String, String> map = new TreeMap<String, String>();
        map.put("roleName", queryRole.getRoleName());
        map.put("roleId", queryRole.getRoleId());
        map.put("roleDesc", queryRole.getRoleDesc());
        String displayStr = queryFunctionService.getDisplayFunctionStrByFunctionIds(queryRole.getRoleFunction());
        map.put("roleFunction", displayStr);
        map.put("responseMessage", CapAppContext.getMessage("js.createRole.msg.01")); // 新增成功
        map.put("roleEnabled", queryRole.isRoleEnabled() ? "1" : "0");
        result.putAll(map);
        sessionLogService.createAndWriteSessionLog(request, SessionLog.PRIMARY_STATUS_CREATE_ROLE, SessionLog.SECONDARY_STATUS_NO_MORE_DESCRIPTION, null, roleId);
        return result;
    }

    public Result findQueryRole(Request request) {
        AjaxFormResult result = new AjaxFormResult();
        String roleId = request.get("roleId");

        if (CapString.isEmpty(roleId)) {
            throw new CapMessageException(CapAppContext.getMessage("js.createRole.errorMsg.02"), this.getClass());// [角色代號] 欄位不可為空白
        }
        QueryRole queryRole = queryRoleService.findQueryRoleByRoleId(roleId);
        Map<String, String> map = new HashMap<String, String>();
        map.put("roleId", queryRole.getRoleId());
        map.put("roleName", queryRole.getRoleName());
        map.put("roleDesc", queryRole.getRoleDesc());
        result.putAll(map);
        return result;
    }

    
}
