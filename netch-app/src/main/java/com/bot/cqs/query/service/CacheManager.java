/* 
 * CacheManager.java
 * 
 * Copyright (c) 2021 International Integrated System, Inc. 
 * All Rights Reserved.
 * 
 * Licensed Materials - Property of International Integrated System, Inc.
 * 
 * This software is confidential and proprietary information of 
 * International Integrated System, Inc. (&quot;Confidential Information&quot;).
 */
package com.bot.cqs.query.service;

import java.util.List;

import com.bot.cqs.gateway.persistence.Cache;

/**
 * <pre>
 * Interface for Cache txn
 * </pre>
 * 
 * @since 2021/4/8
 * @author Sunkist Wang
 * @version
 *          <ul>
 *          <li>2021/4/8,Sunkist Wang,new
 *          </ul>
 */
public interface CacheManager {

    /**
     * 依 dummyKey 找單筆 Cache
     * 
     * @param formatCacheKey
     * @return Cache
     */
    Cache findByDummyKey(String formatCacheKey);

    /**
     * mapping to dao findAll()
     * 
     * @return Cache List
     */
    List<Cache> findAll();

    /**
     * mapping to dao deleteCache(Cache cache)
     * 
     * @param cache
     */
    void deleteCache(Cache cache);

    /**
     * mapping to dao insertCache(Cache cache)
     * 
     * @param cache
     */
    void insertCache(Cache cache);

}
