package com.bot.cqs.report.service.impl;

import com.bot.cqs.query.dto.InquiryLogDto;
import com.bot.cqs.report.service.ChargeQueryCsvRptService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
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
public class ChargeQueryCsvRptServiceImpl implements ChargeQueryCsvRptService {

    @Override
    public String generateCsvStringForChargeQuery(List<InquiryLogDto> logList) {

        DecimalFormat df = new DecimalFormat("######0.00");//Format小數點兩位數
        StringBuffer s = new StringBuffer();
        s.append("分行名稱,分行代號,總筆數,總金額,一類筆數,一類金額,二類筆數,二類金額,甲類筆數,甲類金額,乙類筆數,乙類金額,OBU總金額(USD),OBU一類筆數,OBU一類金額(USD),OBU二類筆數,OBU二類金額(USD)\n");

        for (InquiryLogDto log : logList) {
            s.append(log.getBankName() == null ? "查無分行名稱" : log.getBankName());
            s.append(',');
            // 避免excel自動轉成數值型態。
            s.append(log.getInquiryChargeBankId() == null ? "查無分行代號" : "=\"" + log.getInquiryChargeBankId() + "\"");
            s.append(',');
            s.append(log.getSum1(0) + log.getSum1(7) + log.getSum2(0) + log.getSum2(5)+ log.getSum3(0) + log.getSum3(5));//總筆數
            s.append(',');
            s.append(log.getTotalsFirst() + log.getTotalsSecond() + log.getTotalsOne() + log.getTotalsTwo());//總金額
            s.append(',');
            s.append(log.getSum1(0));// 一類筆數
            s.append(',');
            s.append(log.getTotalsFirst());// 一類金額
            s.append(',');
            s.append(log.getSum1(7));// 二類筆數
            s.append(',');
            s.append(log.getTotalsSecond());// 二類金額
            s.append(',');
            s.append(log.getSum2(0)); // 甲類筆數
            s.append(',');
            s.append(log.getTotalsOne()); // 甲類金額
            s.append(',');
            s.append(log.getSum2(5)); // 乙類筆數
            s.append(',');
            s.append(log.getTotalsTwo()); // 乙類金額
            s.append(',');
            s.append(df.format(new BigDecimal(log.getTotalsObuOne() + log.getTotalsObuTwo()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()));//OBU總金額
            s.append(',');
            s.append(log.getSum3(0));//OBU一類筆數
            s.append(',');
            s.append(df.format(new BigDecimal(log.getTotalsObuOne()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()));//OBU一類金額
            s.append(',');
            s.append(log.getSum3(5));//OBU二類筆數
            s.append(',');
            s.append(df.format(new BigDecimal(log.getTotalsObuTwo()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()));//OBU二類金額
            s.append('\n');
        }
        return s.toString();
    }

}
