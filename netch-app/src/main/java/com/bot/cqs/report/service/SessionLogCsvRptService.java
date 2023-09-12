package com.bot.cqs.report.service;

import java.util.List;

import com.bot.cqs.query.persistence.SessionLog;

/**
 * 
 * @author bob peng
 *
 */
public interface SessionLogCsvRptService {

    String generateCsvStringForQueryDetail (List<SessionLog> inquiryLogList);

}
