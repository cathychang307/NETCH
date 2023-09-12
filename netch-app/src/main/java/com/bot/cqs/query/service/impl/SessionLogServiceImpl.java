/* 
 * SessionLogServiceImpl.java
 * 
 * Copyright (c) 2017 International Integrated System, Inc. 
 * All Rights Reserved.
 * 
 * Licensed Materials - Property of International Integrated System, Inc.
 * 
 * This software is confidential and proprietary information of 
 * International Integrated System, Inc. (&quot;Confidential Information&quot;).
 */
package com.bot.cqs.query.service.impl;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import com.bot.cqs.query.dao.SessionLogDao;
import com.bot.cqs.query.persistence.SessionLog;
import com.bot.cqs.query.service.SessionLogService;
import com.bot.cqs.query.web.WebAction;
import com.bot.cqs.security.model.EtchUserDetails;
import com.iisigroup.cap.component.Request;
import com.iisigroup.cap.security.CapSecurityContext;

/**
 * <pre>
 * Write SessionLog Implementation
 * </pre>
 * 
 * @since 2017年1月18日
 * @author Sunkist
 * @version
 *          <ul>
 *          <li>2017年1月18日,Sunkist,new
 *          </ul>
 */
@Service
public class SessionLogServiceImpl implements SessionLogService {

    @Resource
    SessionLogDao sessionLogDao;

    public void createAndWriteSessionLog(Request request, String primaryStatus, String secondaryStatus, String memo, String correlationId) {
        createAndWriteSessionLog((HttpServletRequest) request.getServletRequest(), primaryStatus, secondaryStatus, memo, correlationId);
    }

    @Override
    public void createAndWriteSessionLog(HttpServletRequest servletRequest, String primaryStatus, String secondaryStatus, String memo, String correlationId) {
        String ip = servletRequest.getRemoteAddr() + "#" + servletRequest.getRemotePort();
        SessionLog sessionLog = WebAction.createSessionLogInternal(CapSecurityContext.<EtchUserDetails> getUser().getQueryUser(), primaryStatus, secondaryStatus, memo, correlationId, ip);
        sessionLogDao.save(sessionLog);
    }

    @Override
    public void createAndWriteSessionLog(HttpServletRequest servletRequest, EtchUserDetails user, String primaryStatus, String secondaryStatus, String memo, String correlationId) {
        String ip = servletRequest.getRemoteAddr() + "#" + servletRequest.getRemotePort();
        SessionLog sessionLog = WebAction.createSessionLogInternal(user.getQueryUser(), primaryStatus, secondaryStatus, memo, correlationId, ip);
        sessionLogDao.save(sessionLog);
    }

    @Override
    public void createAndWriteSessionLog(String ip, EtchUserDetails user, String primaryStatus, String secondaryStatus, String memo, String correlationId) {
        SessionLog sessionLog = WebAction.createSessionLogInternal(user.getQueryUser(), primaryStatus, secondaryStatus, memo, correlationId, ip);
        sessionLogDao.save(sessionLog);
    }
    
    public void writeSessionLog(SessionLog sessionLog) {
        sessionLogDao.saveSessionLog(sessionLog);
    }

    @Override
    public void createAndWriteSessionLogById(HttpServletRequest request, String userId, String departmentId, String roleId, String primaryStatus, String secondaryStatus, String memo, String correlationId) {
        SessionLog sessionLog = WebAction.createSessionLogById(request, userId, departmentId, roleId, primaryStatus, secondaryStatus, memo, correlationId);
        sessionLogDao.save(sessionLog);
    }
}
