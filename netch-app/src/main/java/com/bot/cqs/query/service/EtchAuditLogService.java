package com.bot.cqs.query.service;

import com.iisigroup.cap.component.Request;

import java.net.UnknownHostException;
import java.util.Properties;

/**
 * <pre>
 * EtchAuditLogService
 * </pre>
 *
 * @author cathy chang
 * @version
 *          <ul>
 *          <li>2020年9月1日,cathy chang,new
 *          </ul>
 * @since 2020年9月1日
 */
public interface EtchAuditLogService {

    public void saveEtchAuditLog(Request request, String functionName, String functionId, int queryCount) throws UnknownHostException;

    public Properties getLocalHostInfo();

    }
