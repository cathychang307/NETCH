/* 
 * SessionLogService.java
 * 
 * Copyright (c) 2017 International Integrated System, Inc. 
 * All Rights Reserved.
 * 
 * Licensed Materials - Property of International Integrated System, Inc.
 * 
 * This software is confidential and proprietary information of 
 * International Integrated System, Inc. (&quot;Confidential Information&quot;).
 */
package com.bot.cqs.query.service;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import com.bot.cqs.query.persistence.SessionLog;
import com.bot.cqs.security.model.EtchUserDetails;
import com.iisigroup.cap.component.Request;

/**
 * <pre>
 * Write SessionLog Service
 * </pre>
 * 
 * @since 2017年1月18日
 * @author Sunkist
 * @version
 *          <ul>
 *          <li>2017年1月18日,Sunkist,new
 *          </ul>
 */
public interface SessionLogService {

    /**
     * Write SessionLog
     * 
     * @param sessionLog
     */
    public void writeSessionLog(SessionLog sessionLog);
    
    /**
     * create And Write SessionLog
     * @param request
     * @param primaryStatus
     * @param secondaryStatus
     * @param memo
     * @param correlationId
     */
    public void createAndWriteSessionLog(Request request, String primaryStatus, String secondaryStatus, String memo, String correlationId);
    
    /**
     * 
     * @param request
     * @param primaryStatus
     * @param secondaryStatus
     * @param memo
     * @param correlationId
     */
    public void createAndWriteSessionLog(HttpServletRequest request, String primaryStatus, String secondaryStatus, String memo, String correlationId);

    /**
     * 
     * @param request
     * @param user
     * @param primaryStatus
     * @param secondaryStatus
     * @param memo
     * @param correlationId
     */
    public void createAndWriteSessionLog(HttpServletRequest request, EtchUserDetails user, String primaryStatus, String secondaryStatus, String memo, String correlationId);
    
    /**
     * 
     * @param request
     * @param user
     * @param primaryStatus
     * @param secondaryStatus
     * @param memo
     * @param correlationId
     */
    public void createAndWriteSessionLog(String ip, EtchUserDetails user, String primaryStatus, String secondaryStatus, String memo, String correlationId);

    public void createAndWriteSessionLogById(HttpServletRequest request, String userId, String departmentId, String roleId, String primaryStatus, String secondaryStatus, String memo, String correlationId);

}
