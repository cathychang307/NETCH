
package com.bot.cqs.query.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import com.bot.cqs.query.dao.SessionLogDao;
import com.bot.cqs.query.dao.TransactionRateDao;
import com.bot.cqs.query.persistence.TransactionRate;
import com.bot.cqs.query.persistence.TransactionRateKey;
import com.bot.cqs.query.service.TransactionRateManager;
import com.bot.cqs.query.util.factory.TransactionRateFactory;
import com.bot.cqs.query.web.WebAction;
import com.iisigroup.cap.utils.CapAppContext;

@Service
public class TransactionRateManagerImpl extends BasicQueryManager implements TransactionRateManager {
    @Resource
    private TransactionRateDao transactionRateDao;
    @Resource
    private TransactionRateFactory transactionRateFactory;
    @Resource
    SessionLogDao sessionLogDao;

    // public TransactionRateManagerImpl() {
    //
    // transactionRateFactory = TransactionRateFactory.newInstance();
    // }

    public void delete(TransactionRate transactionRate) {

        getTransactionRateDao().delete(transactionRate);
    }

    public List<TransactionRate> find(String transactionId) {

        return getTransactionRateDao().find(transactionId);
    }

    public TransactionRate find(TransactionRateKey key) {

        return getTransactionRateDao().find(key);
    }

    public List<TransactionRate> findAll() {

        return getTransactionRateDao().findAll();
    }

    public void reload() {

        transactionRateFactory.replaceAll(findAll());
        Logger logger = WebAction.getDefaultLogger();
        logger.info(CapAppContext.getMessage("loadTransactionRateCompleted"));
    }

    public void save(TransactionRate transactionRate) {

        getTransactionRateDao().save(transactionRate);

    }

    public void update(TransactionRate transactionRate) {

        getTransactionRateDao().update(transactionRate);

    }

    public TransactionRateDao getTransactionRateDao() {

        return transactionRateDao;
    }

    public void setTransactionRateDao(TransactionRateDao transactionRateDao) {

        this.transactionRateDao = transactionRateDao;
    }

}
