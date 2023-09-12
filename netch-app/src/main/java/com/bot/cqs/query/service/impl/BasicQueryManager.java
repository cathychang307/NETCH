
package com.bot.cqs.query.service.impl;

import org.springframework.context.MessageSource;

import com.bot.cqs.query.service.QueryManager;

public class BasicQueryManager implements QueryManager {

    private MessageSource messageSource;

    public MessageSource getMessageSource() {

        return messageSource;
    }

    public void setMessageSource(MessageSource messageSource) {

        this.messageSource = messageSource;
    }

}
