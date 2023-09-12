
package com.bot.cqs.query.service;

import org.springframework.context.MessageSource;

public interface QueryManager {

    public void setMessageSource(MessageSource messageSource);

    public MessageSource getMessageSource();
}
