package com.bot.cqs.query.service;

import java.util.List;

import org.slf4j.Logger;

import com.bot.cqs.query.command.ApplicationParameterCommand;
import com.bot.cqs.query.dto.ResponseContent;
import com.bot.cqs.query.persistence.ApplicationParameter;
import com.iisigroup.cap.component.Request;

public interface ApplicationParameterManager extends QueryManager {

    /**
     * 取得所有應用程式參數
     * 
     * @return
     */
    public List<ApplicationParameter> findAll();

    /**
     * 取得指定的應用程式參數
     * 
     * @param parameterName
     * @return
     */
    public ApplicationParameter findParameter(String parameterName);

    /**
     * 更新一個應用程式參數
     * 
     * @param applicationParameter
     */
    public void updateParameter(ApplicationParameter applicationParameter);

    /**
     * 重新讀取所有應用程式參數
     *
     */
    public void reload();

    /**
     * Update ApplicationParameter and SessionLog
     * 
     * @param request
     * @param command
     * @param logger
     * @return ResponseContent
     */
    public ResponseContent updateParamAndSessionLog(Request request, ApplicationParameterCommand command, Logger logger);

}
