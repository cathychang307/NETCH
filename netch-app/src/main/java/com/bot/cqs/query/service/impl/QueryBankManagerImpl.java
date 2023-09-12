
package com.bot.cqs.query.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import com.bot.cqs.query.dao.QueryBankDao;
import com.bot.cqs.query.dao.SessionLogDao;
import com.bot.cqs.query.persistence.QueryBank;
import com.bot.cqs.query.service.QueryBankManager;
import com.bot.cqs.query.util.factory.QueryBankFactory;
import com.bot.cqs.query.web.WebAction;
import com.iisigroup.cap.utils.CapAppContext;

@Service
public class QueryBankManagerImpl extends BasicQueryManager implements QueryBankManager {

    @Resource
    private QueryBankDao queryBankDao;
    @Resource
    private QueryBankFactory queryBankFactory;

    @Resource
    SessionLogDao sessionLogDao;

    public QueryBank delete(QueryBank queryBank) {
        getQueryBankDao().delete(queryBank);
        return queryBank;
    }

    public QueryBank find(String departmentId) {

        return getQueryBankDao().find(departmentId);
    }

    public List<QueryBank> findAll() {

        return getQueryBankDao().findAll();
    }

    public List<QueryBank> findByChargeBankId(String chargeBankId) {

        return getQueryBankDao().findByChargeBankId(chargeBankId);
    }

    // public void reload( String departmentId ) {
    //
    // QueryBank currentQueryBank = find( departmentId );
    // if ( currentQueryBank != null ) {
    // queryBankFactory.replaceQueryBank( currentQueryBank );
    // Logger logger = WebAction.getDefaultLogger();
    // logger.info( getMessageSource().getMessage(
    // "loadBank.1",
    // new Object[] { departmentId },
    // "QueryBank [" + departmentId + "] loaded.",
    // null ) );
    // }
    // }

    public void reload() {

        List<QueryBank> bankList = findAll();
        queryBankFactory.replaceAllQueryBank(bankList);
        Logger logger = WebAction.getDefaultLogger();
        logger.info(CapAppContext.getMessage("loadBankCompleted"));

    }

    public Object save(QueryBank queryBank) {
        getQueryBankDao().save(queryBank);
        return queryBank;
    }

    public QueryBank update(QueryBank queryBank) {

        return getQueryBankDao().update(queryBank);
    }

    public QueryBankDao getQueryBankDao() {

        return queryBankDao;
    }

    public void setQueryBankDao(QueryBankDao queryBankDao) {

        this.queryBankDao = queryBankDao;
    }

}
