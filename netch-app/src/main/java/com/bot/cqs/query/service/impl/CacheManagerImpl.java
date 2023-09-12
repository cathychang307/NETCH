/* 
 * CacheManagerImpl.java
 * 
 * Copyright (c) 2021 International Integrated System, Inc. 
 * All Rights Reserved.
 * 
 * Licensed Materials - Property of International Integrated System, Inc.
 * 
 * This software is confidential and proprietary information of 
 * International Integrated System, Inc. (&quot;Confidential Information&quot;).
 */
package com.bot.cqs.query.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.bot.cqs.gateway.dao.CacheDao;
import com.bot.cqs.gateway.persistence.Cache;
import com.bot.cqs.query.service.CacheManager;

/**
 * <pre>
 * For Cache txn
 * </pre>
 * 
 * @since 2021/4/8
 * @author Sunkist Wang
 * @version
 *          <ul>
 *          <li>2021/4/8,Sunkist Wang,new
 *          </ul>
 */
@Service("cacheManagerImpl")
public class CacheManagerImpl implements CacheManager {
    /**
     * Cache 的 DAO
     */
    @Resource
    private CacheDao dao;

    /*
     * (non-Javadoc)
     * 
     * @see com.bot.cqs.query.service.CacheManager#findByDummyKey(java.lang.String)
     */
    public Cache findByDummyKey(String formatCacheKey) {
        return dao.findByDummyKey(formatCacheKey);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.bot.cqs.query.service.CacheManager#findAll()
     */
    public List<Cache> findAll() {
        return dao.findAll();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.bot.cqs.query.service.CacheManager#deleteCache(com.bot.cqs.gateway.persistence.Cache)
     */
    public void deleteCache(Cache cache) {
        dao.deleteCache(cache);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.bot.cqs.query.service.CacheManager#insertCache(com.bot.cqs.gateway.persistence.Cache)
     */
    public void insertCache(Cache cache) {
        dao.insertCache(cache);
    }
}
