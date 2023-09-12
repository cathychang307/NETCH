
package com.bot.cqs.query.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.bot.cqs.query.command.ApplicationParameterCommand;
import com.bot.cqs.query.dao.ApplicationParameterDao;
import com.bot.cqs.query.dao.SessionLogDao;
import com.bot.cqs.query.dto.ResponseContent;
import com.bot.cqs.query.persistence.ApplicationParameter;
import com.bot.cqs.query.persistence.SessionLog;
import com.bot.cqs.query.service.ApplicationParameterManager;
import com.bot.cqs.query.util.factory.ApplicationParameterFactory;
import com.bot.cqs.query.web.WebAction;
import com.bot.cqs.security.model.EtchUserDetails;
import com.iisigroup.cap.component.Request;
import com.iisigroup.cap.security.CapSecurityContext;
import com.iisigroup.cap.utils.CapAppContext;

@Service
public class ApplicationParameterManagerImpl extends BasicQueryManager implements ApplicationParameterManager {
    @Resource
    private ApplicationParameterDao applicationParameterDao;
    @Resource
    private ApplicationParameterFactory parameterFactory;

    @Resource
    SessionLogDao sessionLogDao;

    public List<ApplicationParameter> findAll() {

        return getApplicationParameterDao().findAll();
    }

    public ApplicationParameter findParameter(String parameterName) {

        return getApplicationParameterDao().findParameter(parameterName);
    }

    public void updateParameter(ApplicationParameter applicationParameter) {

        getApplicationParameterDao().updateParameter(applicationParameter);

    }

    public void reload() {

        List<ApplicationParameter> parameterList = findAll();
        parameterFactory.replaceAllApplicationParameter(parameterList);
        Logger logger = WebAction.getDefaultLogger();
        logger.info(CapAppContext.getMessage("loadParameterCompleted"));
    }

    public ApplicationParameterDao getApplicationParameterDao() {

        return applicationParameterDao;
    }

    public void setApplicationParameterDao(ApplicationParameterDao applicationParameterDao) {

        this.applicationParameterDao = applicationParameterDao;
    }

    /**
     * 依照不同的 {@link #getParameterName() parameterName} 取得相對應的 {@link SessionLog#getPrimaryStatus() logStatus}
     * 
     * @param parameter
     * @return
     */
    protected String getPrimaryStatus(ApplicationParameter parameter) {

        if (parameter.getParameterName().equals(ApplicationParameterFactory.PARAM_QUERY_MAX_ROWS))
            return SessionLog.PRIMARY_STATUS_UPDATE_QUERY_COUNT;
        else if (parameter.getParameterName().equals(ApplicationParameterFactory.PARAM_QUERY_CACHE_INTERVAL))
            return SessionLog.PRIMARY_STATUS_UPDATE_QUERY_CACHE;
        else if (parameter.getParameterName().equals(ApplicationParameterFactory.PARAM_ACCOUNT_CACULATE_TYPE))
            return SessionLog.PRIMARY_STATUS_UPDATE_CACLUATE_TYPE;
        else
            return null;
    }

    public ResponseContent updateParamAndSessionLog(Request request, ApplicationParameterCommand parameterCommand, Logger logger) {
        String responseMessage = "";
        ApplicationParameter parameter = findParameter(parameterCommand.getParameterName());

        boolean errorOccurs = false;
        Throwable exp = null;
        // SessionLog.PRIMARY_STATUS_UPDATE_APPLICATION_PARAMETER;
        String primaryStatus = getPrimaryStatus(parameterCommand);
        String secondaryStatus = null;

        if (primaryStatus == null) {
            throw new IllegalArgumentException("Illegal parameter name [" + parameterCommand.getParameterName() + "]");
        }

        try {

            parameter.setParameterValue(parameterCommand.getParameterValue());

            updateParameter(parameter);
            reload();

            // 更改完成
            responseMessage = CapAppContext.getMessage("queryCountConfigure.status.1");
            secondaryStatus = SessionLog.SECONDARY_STATUS_NO_MORE_DESCRIPTION;

        } catch (DataIntegrityViolationException ex1) {
            // 整合完整性, 例如資料已存在, 或不當的 null
            errorOccurs = true;
            exp = ex1;
            secondaryStatus = SessionLog.SECONDARY_STATUS_DATA_ALREADY_EXIST;
            responseMessage = CapAppContext.getMessage("dataExists");
        } catch (DataRetrievalFailureException ex2) {
            // 找不到資料
            errorOccurs = true;
            exp = ex2;
            secondaryStatus = SessionLog.SECONDARY_STATUS_DATA_NOT_FOUND;
            responseMessage = CapAppContext.getMessage("dataNotFound");
        } catch (OptimisticLockingFailureException ex3) {
            // 樂觀鎖定
            errorOccurs = true;
            exp = ex3;
            secondaryStatus = SessionLog.SECONDARY_STATUS_DATA_VERSION_ERROR;
            responseMessage = CapAppContext.getMessage("dataVersionError");
        } catch (DataAccessException e) {
            errorOccurs = true;
            exp = e;
            secondaryStatus = SessionLog.SECONDARY_STATUS_DATABASE_ERROR;
            responseMessage = CapAppContext.getMessage("databaseAccessFailed");
        }

        if (exp != null) {
            if (logger.isErrorEnabled()) {
                logger.error("Update parameter Error", exp);
            }
        }

        writeSessionLog(request, parameterCommand, logger, parameter, primaryStatus, secondaryStatus);

        ResponseContent resp = new ResponseContent(errorOccurs, responseMessage, parameterCommand.getParameterValue());
        resp.setParameter(parameter);
        return resp;
    }

    private void writeSessionLog(Request request, ApplicationParameterCommand parameterCommand, Logger logger, ApplicationParameter parameter, String primaryStatus, String secondaryStatus) {
        SessionLog sessionLog = WebAction.createSessionLog(request, ((EtchUserDetails) CapSecurityContext.getUser()).getQueryUser(), primaryStatus, secondaryStatus,
                parameter.getParameterDesc() + "=" + parameterCommand.getParameterValue(), parameter.getParameterName());
        try {
            sessionLogDao.saveSessionLog(sessionLog);
        } catch (DataAccessException e) {
            if (logger.isErrorEnabled()) {
                logger.error("SessionLog write error: " + sessionLog);
            }
        }
    }

}
