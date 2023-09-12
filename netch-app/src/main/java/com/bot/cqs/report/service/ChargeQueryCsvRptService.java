package com.bot.cqs.report.service;

import java.util.List;

import com.bot.cqs.query.dto.InquiryLogDto;

/**
 * 
 * @author bob peng
 *
 */
public interface ChargeQueryCsvRptService {

    String generateCsvStringForChargeQuery(List<InquiryLogDto> list);

}
