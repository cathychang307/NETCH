
package com.bot.cqs.query.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.bot.cqs.query.dao.TransactionRateDao;
import com.bot.cqs.query.persistence.TransactionRate;
import com.bot.cqs.query.persistence.TransactionRateKey;
import com.iisigroup.cap.db.constants.SearchMode;
import com.iisigroup.cap.db.dao.SearchSetting;
import com.iisigroup.cap.db.dao.impl.GenericDaoImpl;

@Repository
public class TransactionRateDaoImpl extends GenericDaoImpl<TransactionRate> implements TransactionRateDao {

    public List<TransactionRate> find(String transactionId) {
        SearchSetting search = createSearchTemplete();
        search.addSearchModeParameters(SearchMode.EQUALS, "transactionId", transactionId);
        return find(search);
    }

    public TransactionRate find(TransactionRateKey key) {

        return findById(TransactionRate.class, key);
    }

    public List<TransactionRate> findAll() {
        SearchSetting search = createSearchTemplete();
        search.setMaxResults(Integer.MAX_VALUE);
        // 這些資料重要, 不要限筆數, 不然會影響營運
        return find(search);
    }

    public void update(TransactionRate transactionRate) {
        save(transactionRate);
    }

}
