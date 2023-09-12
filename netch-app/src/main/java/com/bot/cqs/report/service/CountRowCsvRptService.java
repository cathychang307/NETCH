package com.bot.cqs.report.service;

import java.util.List;

import com.bot.cqs.query.dto.InquiryLogDto;

/**
 * 
 * @author bob peng
 *
 */
public interface CountRowCsvRptService {

    String generateCsvStringForCountRow(List<InquiryLogDto> list);

}
