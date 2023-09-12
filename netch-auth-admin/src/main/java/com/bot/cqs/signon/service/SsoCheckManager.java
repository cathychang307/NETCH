
package com.bot.cqs.signon.service;

import java.util.Map;

import com.bot.cqs.query.persistence.QueryRole;
import com.bot.cqs.query.persistence.QueryUser;
import com.bot.cqs.signon.SignOnException;

public interface SsoCheckManager {

    public QueryUser checkJumperId(String employeeId, String jumperId) throws SignOnException;

    public Map<String, String> checkInfoId(QueryUser user, Map<String, QueryRole> roleMap) throws SignOnException;

}
