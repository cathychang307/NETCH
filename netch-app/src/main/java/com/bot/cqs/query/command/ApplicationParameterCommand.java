package com.bot.cqs.query.command;

import org.springframework.stereotype.Component;

import com.bot.cqs.query.persistence.ApplicationParameter;

/**
 * 
 * @author bob peng
 *
 */
@Component
public class ApplicationParameterCommand extends ApplicationParameter {

    public ApplicationParameterCommand() {

    }

    public ApplicationParameterCommand(ApplicationParameter applicationParameter) {
        setParameterName(applicationParameter.getParameterName());
        setParameterValue(applicationParameter.getParameterValue());
        setParameterDesc(applicationParameter.getParameterDesc());
    }

}
