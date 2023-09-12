package com.bot.cqs.report.service.impl;

import com.bot.cqs.query.dto.InquiryLogDto;
import com.bot.cqs.report.service.CountRowCsvRptService;
import org.springframework.stereotype.Service;

import java.util.List;

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
public class CountRowCsvRptServiceImpl implements CountRowCsvRptService {

    @Override
    public String generateCsvStringForCountRow(List<InquiryLogDto> logList) {

        StringBuffer s = new StringBuffer();
        s.append("分行名稱,分行代號,總筆數,一類連線,一類計費,一類快取,二類連線,二類計費,二類快取,甲類連線,甲類計費,甲類快取,乙類連線,乙類計費,乙類快取,OBU一類連線,OBU一類計費,OBU一類快取,OBU二類連線,OBU二類計費,OBU二類快取\n");

        for (InquiryLogDto log : logList) {
            s.append(log.getBankName() == null ? "查無分行名稱" : log.getBankName());
            s.append(',');
            // 避免excel自動轉成數值型態。
            s.append(log.getInquiryChargeBankId() == null ? "查無分行代號" : "=\""
                    + log.getInquiryChargeBankId() + "\"");
            s.append(',');
            s.append(log.getSum1(0) + log.getSum1(7) + log.getSum2(0)
                    + log.getSum2(5) + log.getSum3(0) + log.getSum3(5)
                    + log.getTotalRows(0) + log.getTotalRows(1) + log.getTotalRows(2)
                    + log.getTotalRows(3) + log.getTotalRows(4) + log.getTotalRows(5)
                    + log.getCacheRows(0) + log.getCacheRows(1) + log.getCacheRows(2)
                    + log.getCacheRows(3) + log.getCacheRows(4) + log.getCacheRows(5));
            s.append(',');
            s.append(log.getSum1(0) + log.getTotalRows(0)); // 一類連線
            s.append(',');
            s.append(log.getSum1(0)); // 一類計費
            s.append(',');
            s.append(log.getCacheRows(0)); // 一類快取
            s.append(',');
            s.append(log.getSum1(7) + log.getTotalRows(1)); // 二類連線
            s.append(',');
            s.append(log.getSum1(7)); // 二類計費
            s.append(',');
            s.append(log.getCacheRows(1)); // 二類快取
            s.append(',');
            s.append(log.getSum2(0) + log.getTotalRows(2)); // 甲類連線
            s.append(',');
            s.append(log.getSum2(0)); // 甲類計費
            s.append(',');
            s.append(log.getCacheRows(2)); // 甲類快取
            s.append(',');
            s.append(log.getSum2(5) + log.getTotalRows(3)); // 乙類連線
            s.append(',');
            s.append(log.getSum2(5));  // 乙類計費
            s.append(',');
            s.append(log.getCacheRows(3));// 乙類快取
            s.append(',');
            s.append(log.getSum3(0) + log.getTotalRows(4));//OBU一類連線
            s.append(',');
            s.append(log.getSum3(0));//OBU一類計費
            s.append(',');
            s.append(log.getCacheRows(4));//OBU一類快取
            s.append(',');
            s.append(log.getSum3(5) + log.getTotalRows(5));//OBU二類連線
            s.append(',');
            s.append(log.getSum3(5));//OBU二類計費
            s.append(',');
            s.append(log.getCacheRows(5));//OBU二類快取
            s.append('\n');
        }
        return s.toString();
    }

}
