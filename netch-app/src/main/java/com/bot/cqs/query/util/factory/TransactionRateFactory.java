
package com.bot.cqs.query.util.factory;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
// import java.util.Set;
import java.util.TreeMap;
// import java.util.TreeSet;

import org.springframework.stereotype.Service;

import com.bot.cqs.query.persistence.TransactionRate;

/**
 * <pre>
 * TransactionRateFactory
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
public class TransactionRateFactory {

    private final Map<String, Map<Date, TransactionRate>> transactionRateMap;
    private final List<TransactionRate> transactionRateList;

    // private final Set<String> transactionTypeSet;

    protected TransactionRateFactory() {
        DEFAULT_TRANSACTION_RATE_FACTORY = this;
        transactionRateMap = new TreeMap<String, Map<Date, TransactionRate>>();
        transactionRateList = new ArrayList<TransactionRate>();
        // transactionTypeSet = new TreeSet<String>();
    }

    /**
     * 取得指定日期的費率
     * 
     * @param transactionId
     *            交易類型代碼, 如果沒有指定 ( null ), 則取得全部交易類別
     * @param date
     *            若指定日期, 回傳該日期適用的費率, 若無指定, 則使用系統日
     * @return
     */
    public synchronized List<TransactionRate> getTransactionRate(String transactionId, Date date) {

        Date date1 = (date == null ? new Date() : date);

        List<TransactionRate> result = new ArrayList<TransactionRate>();
        if (transactionId != null) {
            TransactionRate rate = find(transactionId, date1);
            if (rate != null)
                result.add(rate);
            return result;
        }

        Iterator<String> ite = transactionRateMap.keySet().iterator();
        while (ite.hasNext()) {
            TransactionRate rate = find(ite.next(), date1);
            if (rate != null)
                result.add(rate);
        }

        return result;
    }

    private TransactionRate find(String transactionId, Date date) {

        List<TransactionRate> history = getTransactionRateHistory(transactionId, date);
        if (history == null || history.size() == 0)
            return null;
        else
            return history.get(history.size() - 1);
    }

    /**
     * 取得歷史費率列表
     * 
     * @param transactionId
     *            交易類型代碼, 如果沒有指定 ( null ), 則取得全部交易類別
     * @param date
     *            若指定日期, 回傳該日期以前的歷史記錄, 若無指定, 則取得全部記錄
     * @return
     */
    public synchronized List<TransactionRate> getTransactionRateHistory(String transactionId, Date date) {

        if (transactionId == null && date == null)
            return getTransactionRateList();

        if (transactionId != null)
            return findHistory(transactionId, date);

        List<TransactionRate> result = new ArrayList<TransactionRate>();
        Iterator<String> ite = transactionRateMap.keySet().iterator();
        while (ite.hasNext()) {
            List<TransactionRate> array = findHistory(ite.next(), date);
            if (array != null)
                result.addAll(array);
        }

        return result;
    }

    private List<TransactionRate> findHistory(String transactionId, Date date) {

        Map<Date, TransactionRate> subMap = transactionRateMap.get(transactionId);
        if (subMap == null)
            return null;

        Iterator<Date> ite = subMap.keySet().iterator();
        List<TransactionRate> result = new ArrayList<TransactionRate>();
        while (ite.hasNext()) {
            Date effectDate = ite.next();
            if (date == null || effectDate.compareTo(date) <= 0)
                result.add(subMap.get(effectDate));

        }

        return result;
    }

    /**
     * 取得有效費率列表. 有效的意思是, 尚未過時的 ( 目前適用費率加上未來費率 )
     * 
     * @param transactionId
     *            交易類型代碼, 如果沒有指定 ( null ), 則取得全部交易類別
     * @param date
     *            若指定日期, 回傳該日期起的有效費率, 若無指定, 則使用系統日
     * @return
     */
    public synchronized List<TransactionRate> getTransactionRateEffective(String transactionId, Date date) {

        Date date1 = (date == null ? new Date() : date);

        if (transactionId != null) {
            List<TransactionRate> array = findEffective(transactionId, date1);
            if (array != null)
                return array;
            return new ArrayList<TransactionRate>();
        }

        List<TransactionRate> result = new ArrayList<TransactionRate>();
        Iterator<String> ite = transactionRateMap.keySet().iterator();
        while (ite.hasNext()) {
            List<TransactionRate> array = findEffective(ite.next(), date1);
            if (array != null)
                result.addAll(array);
        }

        return result;
    }

    public synchronized List<TransactionRate> findEffective(String transactionId, Date date) {

        Map<Date, TransactionRate> subMap = transactionRateMap.get(transactionId);
        if (subMap == null)
            return null;

        Iterator<Date> ite = subMap.keySet().iterator();
        List<TransactionRate> result = new ArrayList<TransactionRate>();
        TransactionRate currentRate = null;
        while (ite.hasNext()) {
            Date effectDate = ite.next();
            TransactionRate rate = subMap.get(effectDate);

            // 要找出日期較小的最近一筆 ==> 這代表目前適用的
            if (effectDate.compareTo(date) <= 0) {
                currentRate = rate;
                continue;
            }

            // 日期較大的一概加入, 這代表未來費率
            // 加入前先把目前費率加入, 如果有的話
            if (currentRate != null) {
                result.add(currentRate);
                currentRate = null;
            }
            result.add(rate);
        }

        if (result.size() == 0 && currentRate != null)
            result.add(currentRate);

        return result;
    }

    /**
     * 更新所有費率
     * 
     * @param transactionRateList
     */
    public synchronized void replaceAll(List<TransactionRate> transactionRateList) {

        transactionRateMap.clear();

        String lastId = null;
        Map<Date, TransactionRate> subMap = null;
        if (transactionRateList != null) {
            for (TransactionRate rate : transactionRateList) {

                subMap = transactionRateMap.get(rate.getKey().getTransactionId());

                if (subMap == null) {
                    subMap = new TreeMap<Date, TransactionRate>();
                    transactionRateMap.put(rate.getKey().getTransactionId(), subMap);
                }
                subMap.put(rate.getKey().getTransactionRateEffectDate(), rate);
            }
        }
        resetTransactionRateList();
    }

    /**
     * 取得所有 (歷史) 費率
     * 
     * @return
     */
    public synchronized List<TransactionRate> getTransactionRateList() {

        return transactionRateList;
    }

    /**
     * 取得所有 (歷史) 費率
     * 
     * @return
     */
    public synchronized TransactionRate[] getTransactionRateArray() {

        TransactionRate[] array = new TransactionRate[transactionRateList.size()];
        return transactionRateList.toArray(array);
    }

    /**
     * 取得所有交易類別名稱
     * 
     * @return
     */
    // public synchronized Set<String> getTransactionTypeSet() {
    //
    // return transactionTypeSet;
    // }
    private void resetTransactionRateList() {

        transactionRateList.clear();

        Iterator<String> ite1 = transactionRateMap.keySet().iterator();
        while (ite1.hasNext()) {

            String transId = ite1.next();
            Map<Date, TransactionRate> subMap = transactionRateMap.get(transId);
            Iterator<Date> ite2 = subMap.keySet().iterator();
            while (ite2.hasNext()) {
                Date effectDate = ite2.next();
                TransactionRate rate = subMap.get(effectDate);
                transactionRateList.add(rate);
                // transactionTypeSet.add( rate.getKey().getTransactionId() + "
                // "
                // + rate.getTransactionName() );
            }
        }
    }

    // ------------- get a new instance of QueryBankFactory
    // in this case, return a default factory only
    private static TransactionRateFactory DEFAULT_TRANSACTION_RATE_FACTORY = new TransactionRateFactory();

    public static TransactionRateFactory newInstance() {

        return DEFAULT_TRANSACTION_RATE_FACTORY;
    }
}
