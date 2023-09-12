
package com.bot.cqs.query.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.bot.cqs.query.command.SessionLogCommand;
import com.bot.cqs.query.dao.SessionLogDao;
import com.bot.cqs.query.persistence.SessionLog;
import com.bot.cqs.query.util.factory.ApplicationParameterFactory;
import com.bot.cqs.query.web.WebAction;
import com.bot.cqs.security.model.EtchUserDetails;
import com.iisigroup.cap.component.Request;
import com.iisigroup.cap.db.dao.impl.GenericDaoImpl;
import com.iisigroup.cap.security.CapSecurityContext;

/**
 * <pre>
 * SessionLogDaoImpl
 * </pre>
 * 
 * @since 2016年12月30日
 * @author bob peng
 * @version
 *          <ul>
 *          <li>2016年12月30日,bob peng,new
 *          </ul>
 */
@Repository
public class SessionLogDaoImpl extends GenericDaoImpl<SessionLog> implements SessionLogDao {

    private ApplicationParameterFactory applicationParameterFactory;

    public SessionLogDaoImpl() {
        super();
        applicationParameterFactory = ApplicationParameterFactory.newInstance();
    }

    public void saveSessionLog(SessionLog sessionLog) {
        save(sessionLog);
    }

    public List<SessionLog> findSessionLog(SessionLogCommand sessionLogCommand) {

        // TODO
        return null;

        // DetachedCriteria detachedCriteria = DetachedCriteria.forClass( SessionLog.class );
        //
        // if ( StringUtils.hasText( sessionLogCommand.getUserId() ) )
        // detachedCriteria.add( Restrictions.eq(
        // "userId",
        // sessionLogCommand.getUserId() ) );
        //
        // if ( StringUtils.hasText( sessionLogCommand.getDepartmentId() ) )
        // detachedCriteria.add( Restrictions.eq(
        // "departmentId",
        // sessionLogCommand.getDepartmentId() ) );
        //
        // if ( sessionLogCommand.getAccessDateFrom() != null )
        // detachedCriteria.add( Restrictions.ge(
        // "accessDatetime",
        // sessionLogCommand.getAccessDateFrom() ) );
        //
        // if ( sessionLogCommand.getAccessDateTo() != null )
        // detachedCriteria.add( Restrictions.le(
        // "accessDatetime",
        // sessionLogCommand.getAccessDateTo() ) );
        //
        // if ( sessionLogCommand.getTargetPrimaryStatus() != null
        // && sessionLogCommand.getTargetPrimaryStatus().size() > 0 )
        // detachedCriteria.add( Restrictions.in(
        // "primaryStatus",
        // sessionLogCommand.getTargetPrimaryStatus() ) );
        //
        // detachedCriteria.addOrder( Order.asc( "accessDatetime" ) );
        // return getHibernateTemplate().findByCriteria(
        // detachedCriteria,
        // 0,
        // applicationParameterFactory.getQueryMaxRows() );
    }

    @Override
    public void createAndWriteSessionLog(String primaryStatus, String secondaryStatus, String memo, String correlationId, String ip) {
        SessionLog sessionLog = WebAction.createSessionLogInternal(CapSecurityContext.<EtchUserDetails> getUser().getQueryUser(), primaryStatus, secondaryStatus, memo, correlationId, ip);
        save(sessionLog);
    }

    @Override
    public void createAndWriteSessionLog(String primaryStatus, String secondaryStatus, String memo, String correlationId, String ip, EtchUserDetails user) {
        SessionLog sessionLog = WebAction.createSessionLogInternal(user.getQueryUser(), primaryStatus, secondaryStatus, memo, correlationId, ip);
        save(sessionLog);
    }
    
    @Override
    public void createAndWriteSessionLog(Request request, String primaryStatus, String secondaryStatus, String memo, String correlationId) {
        SessionLog sessionLog = WebAction.createSessionLog(request, CapSecurityContext.<EtchUserDetails> getUser().getQueryUser(), primaryStatus, secondaryStatus, memo, correlationId);
        save(sessionLog);
    }
}
