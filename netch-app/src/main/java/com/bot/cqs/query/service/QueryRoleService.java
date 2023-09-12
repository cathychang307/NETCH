package com.bot.cqs.query.service;

import com.bot.cqs.query.persistence.QueryRole;

/**
 * 
 * @author bob peng
 *
 */
public interface QueryRoleService {

    QueryRole createQueryRole(String roleName, String roleId, String roleDesc);

    QueryRole findQueryRoleByRoleId(String roleId);

    QueryRole updateRole(String roleId, String roleDesc, String roleEnabled, String[] functionIds, String ip);

    QueryRole deleteRole(String roleId, String ip);

}
