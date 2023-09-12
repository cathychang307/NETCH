
package com.bot.cqs.query.service;

import java.util.List;

import com.bot.cqs.query.persistence.TransactionRate;
import com.bot.cqs.query.persistence.TransactionRateKey;

public interface TransactionRateManager {

    public List<TransactionRate> find(String transactionId);

    public TransactionRate find(TransactionRateKey key);

    public List<TransactionRate> findAll();

    public void update(TransactionRate transactionRate);

    public void delete(TransactionRate transactionRate);

    public void save(TransactionRate transactionRate);

    public void reload();
}
