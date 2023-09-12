package com.bot.cqs.query.service.impl;

import java.util.Arrays;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.bot.cqs.query.dao.QueryRoleDao;
import com.bot.cqs.query.dao.SessionLogDao;
import com.bot.cqs.query.exception.NetchLogMessageException;
import com.bot.cqs.query.persistence.QueryRole;
import com.bot.cqs.query.persistence.SessionLog;
import com.bot.cqs.query.service.QueryRoleService;
import com.iisigroup.cap.exception.CapMessageException;
import com.iisigroup.cap.utils.CapAppContext;
import com.iisigroup.cap.utils.CapDate;

/**
 * <pre>
 * QueryRoleServiceImpl
 * </pre>
 * 
 * @since 2016年12月30日
 * @author bob peng
 * @version
 *          <ul>
 *          <li>2016年12月30日,bob peng,new
 *          </ul>
 */
@Service
public class QueryRoleServiceImpl implements QueryRoleService {

    @Resource
    private QueryRoleDao queryRoleDao;
    @Resource
    private SessionLogDao sessionLogDao;

    @Override
    public QueryRole createQueryRole(String roleName, String roleId, String roleDesc) {
        QueryRole newRole = new QueryRole();
        newRole.setRoleId(roleId);
        newRole.setRoleName(roleName);
        newRole.setRoleDesc(roleDesc);
        newRole.setRoleFunction(newRole.getRoleFunction() == null ? "" : newRole.getRoleFunction());
        newRole.setRoleModifyDate(CapDate.getCurrentTimestamp());
        newRole.setRoleEnabled(true);
        queryRoleDao.save(newRole);
        return newRole;
    }

    @Override
    public QueryRole findQueryRoleByRoleId(String roleId) {
        return queryRoleDao.findByRoleId(roleId);
    }

    @Override
    public QueryRole updateRole(String roleId, String roleDesc, String roleEnabled, String[] functionIds, String ip) {
        QueryRole queryRole = queryRoleDao.findByRoleId(roleId);

        if (queryRole == null) {
            throw new NetchLogMessageException(CapAppContext.getMessage("js.configureRoleDetail.msg.03"), this.getClass());// 該角色不存在
        }
        if (!queryRole.isModifiable()) {
            throw new NetchLogMessageException(CapAppContext.getMessage("refuseUpdateDefaultRole"), this.getClass());// 這個角色不能被更改
        }
        if (roleDesc.length() > 50) {
            throw new NetchLogMessageException(CapAppContext.getMessage("js.configureRoleDetail.msg.04"), this.getClass());// 資料格式或長度不符
        }
        queryRole.setRoleDesc(roleDesc);
        queryRole.setVersion(queryRole.getVersion() + 1);
        queryRole.setRoleEnabled("1".equals(roleEnabled) ? true : false);
        Arrays.sort(functionIds);
        String roleFunction = parseFunctionIds(functionIds);
        if (roleFunction.length() > 255) {
            throw new CapMessageException(CapAppContext.getMessage("js.configureRoleDetail.msg.04"), this.getClass());// 資料格式或長度不符
        }
        queryRole.setRoleFunction(parseFunctionIds(functionIds));
        queryRoleDao.save(queryRole);
        sessionLogDao.createAndWriteSessionLog(SessionLog.PRIMARY_STATUS_UPDATE_ROLE, SessionLog.SECONDARY_STATUS_NO_MORE_DESCRIPTION, null, queryRole.getRoleId(), ip);
        return queryRole;
    }

    private String parseFunctionIds(String[] functionIds) {
        StringBuffer sb = new StringBuffer();
        boolean first = true;
        for (String id : functionIds) {
            if (first) {
                first = false;
            } else {
                sb.append(",");
            }
            sb.append(id);
        }
        return sb.toString();
    }

    @Override
    public QueryRole deleteRole(String roleId, String ip) {
        QueryRole queryRole = queryRoleDao.findByRoleId(roleId);
        if (queryRole == null) {
            throw new NetchLogMessageException(CapAppContext.getMessage("js.configureRoleDetail.msg.03"), this.getClass());// 該角色不存在
        }
        if (!queryRole.isModifiable()) {
            throw new NetchLogMessageException(CapAppContext.getMessage("refuseUpdateDefaultRole"), this.getClass());// 這個角色不能被更改
        }
        queryRoleDao.delete(queryRole);
        sessionLogDao.createAndWriteSessionLog(SessionLog.PRIMARY_STATUS_DELETE_ROLE, SessionLog.SECONDARY_STATUS_NO_MORE_DESCRIPTION, null, queryRole.getRoleId(), ip);
        return queryRole;
    }

}
