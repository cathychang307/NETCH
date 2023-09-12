package com.bot.cqs.report.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.bot.cqs.gateway.persistence.InquiryLog;
import com.bot.cqs.report.service.QueryDetailCsvRptService;

/**
 * <pre>
 * CsvFileServiceImpl
 * </pre>
 * 
 * @since 2017年1月4日
 * @author bob peng
 * @version
 *          <ul>
 *          <li>2017年1月4日,bob peng,new
 *          </ul>
 */
@Service
public class QueryDetailCsvRptServiceImpl implements QueryDetailCsvRptService {

    @Override
    public String generateCsvStringForQueryDetail(List<InquiryLog> inquiryLogList) {

        StringBuffer s = new StringBuffer();
        s.append("查詢日期,查詢者 ID,查詢者姓名,查詢類型,查詢條件,快取\n");

        for (InquiryLog log : inquiryLogList) {
            s.append(log.getInquiryDate() == null ? "" : log.getInquiryDate());
            s.append(',');
            s.append(log.getInquiryAccount() == null ? "" : log.getInquiryAccount());
            s.append(',');
            s.append(log.getInquiryUserName() == null ? "" : log.getInquiryUserName());
            s.append(',');
            s.append(log.getInquiryTxCode() == null ? "" : log.getInquiryTxCode());
            s.append(',');
            s.append(log.getConditions() == null ? "" : log.getConditions());
            s.append(',');
            s.append(log.getInquiryCacheFlag() ? "Y" : "N");
            s.append('\n');
        }
        return s.toString();
    }

}
