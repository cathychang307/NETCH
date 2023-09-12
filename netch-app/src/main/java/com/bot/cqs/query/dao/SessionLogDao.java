
package com.bot.cqs.query.dao;

import java.util.List;

import com.bot.cqs.query.command.SessionLogCommand;
import com.bot.cqs.query.persistence.SessionLog;
import com.bot.cqs.security.model.EtchUserDetails;
import com.iisigroup.cap.component.Request;
import com.iisigroup.cap.db.dao.GenericDao;

/**
 * 存取 {@link SessionLog 連線紀錄} 的物件
 *
 * @since 2016年12月30日
 * @author bob peng
 * @version
 *          <ul>
 *          <li>2016年12月30日,bob peng,new
 *          </ul>
 */
public interface SessionLogDao extends GenericDao<SessionLog> {

    /**
     * 寫入一筆 {@link SessionLog 連線紀錄}
     * 
     * @param sessionLog
     */
    public void saveSessionLog(SessionLog sessionLog);

    /**
     * 讀取指定範圍的{@link SessionLog 連線紀錄}.
     * 
     * @param sessionLogCommand
     *            以此物件來指定要查詢的範圍, 條件可以由 {@link SessionLogCommand#getAccessDateFrom() 查詢起日}, {@link SessionLogCommand#getAccessDateTo() 查詢訖日}, {@link SessionLogCommand#getDepartmentId() 部門/分行代碼},
     *            {@link SessionLogCommand#getUserId() 使用者 ID}, {@link SessionLogCommand#getPrimaryStatus() 連線行為} 來指定
     * @return
     * @see SessionLogCommand
     */
    public List<SessionLog> findSessionLog(SessionLogCommand sessionLogCommand);

    public void createAndWriteSessionLog(String primaryStatus, String secondaryStatus, String memo, String correlationId, String ip);
    
    public void createAndWriteSessionLog(String primaryStatus, String secondaryStatus, String memo, String correlationId, String ip, EtchUserDetails user);
    
    public void createAndWriteSessionLog(Request request, String primaryStatus, String secondaryStatus, String memo, String correlationId);

}
