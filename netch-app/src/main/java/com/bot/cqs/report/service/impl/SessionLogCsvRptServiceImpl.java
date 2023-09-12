package com.bot.cqs.report.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.bot.cqs.query.persistence.SessionLog;
import com.bot.cqs.query.util.DateUtil;
import com.bot.cqs.report.service.SessionLogCsvRptService;

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
public class SessionLogCsvRptServiceImpl implements SessionLogCsvRptService {

    @Override
    public String generateCsvStringForQueryDetail(List<SessionLog> sessionLogList) {

        StringBuffer s = new StringBuffer();
        s.append("時間,使用者 ID,使用者 姓名,分行,角色,行為,狀態,狀態碼\n");
        String successString = "成功";
        String failString = "失敗";
        for (SessionLog log : sessionLogList) {
            s.append( DateUtil.toRocDatetime(log.getAccessDatetime()) );
            s.append( ',' );
            s.append( log.getUserId() );
            s.append( ',' );
            s.append( log.getUserName() == null ? "" : log.getUserName() );
            s.append( ',' );
            s.append( log.getDepartmentName() == null ? log.getDepartmentId()
                    : log.getDepartmentName() );
            s.append( ',' );
            s.append( log.getRoleName() == null ? log.getRoleId()
                    : log.getRoleName() );
            s.append( ',' );
            s.append( log.getPrimaryStatusDesc() );
            s.append( ',' );
            s.append( log.getSecondaryStatus().equals(
                    SessionLog.SECONDARY_STATUS_NO_MORE_DESCRIPTION ) ? successString
                    : failString );
            s.append( ',' );
            s.append( "=\"" );
            s.append(  log.getSecondaryStatus() );
            s.append( "\"");
            s.append( '\n' );
        }
        return s.toString();
    }

}
