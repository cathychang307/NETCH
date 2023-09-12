package com.bot.cqs.report.service;

import java.util.List;

import com.bot.cqs.gateway.persistence.InquiryLog;

/**
 * 
 * @author bob peng
 *
 */
public interface QueryDetailCsvRptService {

    String generateCsvStringForQueryDetail (List<InquiryLog> inquiryLogList);

}
