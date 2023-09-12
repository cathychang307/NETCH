
package com.bot.cqs.query.web.listener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.bot.cqs.query.persistence.TransactionRate;
import com.bot.cqs.query.persistence.TransactionRateKey;
import com.bot.cqs.query.service.TransactionRateManager;
import com.iisigroup.cap.utils.CapAppContext;

public class TransactionRateLoaderListener implements ServletContextListener {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    public void contextDestroyed(ServletContextEvent sce) {

    }

    public void contextInitialized(ServletContextEvent sce) {
        WebApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(sce.getServletContext());
        TransactionRateManager transactionRateManager = (TransactionRateManager) ctx.getBean("transactionRateManagerImpl");
        transactionRateManager.reload();
        readTransactionType();
    }

    private void readTransactionType() {

        String filename = TransactionRate.DEFAULT_MESSAGE_SOURCE_BASENAME + ".properties";
        InputStream fis = null;
        BufferedReader input = null;
        try {
            fis = CapAppContext.getResource(filename).getInputStream();
            if (fis == null) {
                throw new RuntimeException("Initialize failed: " + filename + " not found");
            }

            input = new BufferedReader(new InputStreamReader(fis));
            String s;
            String prefix = TransactionRate.TRANSACTION_NAME_PREFIX;
            int prefixLen = prefix.length();
            List<TransactionRate> availableRateList = new ArrayList<TransactionRate>();
            while ((s = input.readLine()) != null) {
                s = s.trim();
                if (s.startsWith(prefix)) {

                    String id = s.substring(prefixLen, prefixLen + 4);

                    TransactionRateKey key = new TransactionRateKey();
                    key.setTransactionId(id);

                    TransactionRate rate = new TransactionRate();
                    rate.setKey(key);

                    availableRateList.add(rate);
                }
            }

            TransactionRate.AVAILABLE_TRANSACTION_RATE.addAll(availableRateList);
        } catch (IOException e) {
            throw new RuntimeException("Initialize failed: read " + filename + " failed", e);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (Exception e) {
                    if (logger.isDebugEnabled()) {
                        logger.debug(e.getMessage());
                    }
                }
            }
            if (fis != null) {
                try {
                    fis.close();
                } catch (Exception e) {
                    if (logger.isDebugEnabled()) {
                        logger.debug(e.getMessage());
                    }
                }
            }
        }
    }
}
