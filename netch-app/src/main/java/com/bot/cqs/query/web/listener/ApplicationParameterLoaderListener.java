
package com.bot.cqs.query.web.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.bot.cqs.query.service.ApplicationParameterManager;

public class ApplicationParameterLoaderListener implements ServletContextListener {

    public void contextDestroyed(ServletContextEvent sce) {
    }

    public void contextInitialized(ServletContextEvent sce) {
        WebApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(sce.getServletContext());
        ApplicationParameterManager applicationParameterManager = (ApplicationParameterManager) ctx.getBean("applicationParameterManagerImpl");
        applicationParameterManager.reload();
    }

}
