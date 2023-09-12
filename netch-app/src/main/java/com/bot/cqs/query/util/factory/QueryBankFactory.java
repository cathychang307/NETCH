
package com.bot.cqs.query.util.factory;

import com.bot.cqs.query.persistence.QueryBank;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * <pre>
 * QueryBankFactory
 * </pre>
 * 
 * @since 2017年1月3日
 * @author bob peng
 * @version
 *          <ul>
 *          <li>2017年1月3日,bob peng,new
 *          </ul>
 */
@Service
public class QueryBankFactory {

    private final Map<String, QueryBank> queryBankMap;
    private final List<QueryBank> queryBankList;

    protected QueryBankFactory() {
        DEFAULT_QUERY_BANK_FACTORY = this;
        queryBankMap = new TreeMap<String, QueryBank>();
        queryBankList = new ArrayList<QueryBank>();
    }

    public synchronized QueryBank getQueryBank(String departmentId) {

        return queryBankMap.get(departmentId);
    }

    public synchronized void replaceAllQueryBank(List<QueryBank> queryBankList) {

        queryBankMap.clear();
        if (queryBankList != null) {
            for (QueryBank bank : queryBankList)
                queryBankMap.put(bank.getDepartmentId(), bank);
        }
        resetQueryBankList();

    }

    public synchronized List<QueryBank> getQueryBankList() {

        return queryBankList;
    }

    public synchronized QueryBank[] getQueryBankArray() {

        QueryBank[] array = new QueryBank[queryBankList.size()];
        return queryBankList.toArray(array);
    }

    private void resetQueryBankList() {

        queryBankList.clear();
        Iterator<String> ite = queryBankMap.keySet().iterator();
        while (ite.hasNext()) {
            QueryBank bank = queryBankMap.get(ite.next());
            queryBankList.add(bank);

            // 設定 chargeBankName
            String chargeBankId = bank.getChargeBankId();
            if (chargeBankId != null && chargeBankId.length() > 6) {
                String chargeDepartmentId = chargeBankId.substring(3, 6);
                QueryBank chargeBank = queryBankMap.get(chargeDepartmentId);
                if (chargeBank != null)
                    bank.setChargeBankName(chargeBank.getDepartmentName());
            }
        }
    }

    // ------------- get a new instance of QueryBankFactory
    // in this case, return a default factory only

    private static QueryBankFactory DEFAULT_QUERY_BANK_FACTORY = new QueryBankFactory();

    public static QueryBankFactory newInstance() {

        return DEFAULT_QUERY_BANK_FACTORY;
    }

}
