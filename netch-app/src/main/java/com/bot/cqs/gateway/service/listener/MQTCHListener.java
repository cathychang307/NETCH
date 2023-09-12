/* 
 * MQListener.java
 * 
 * Copyright (c) 2017 International Integrated System, Inc. 
 * All Rights Reserved.
 * 
 * Licensed Materials - Property of International Integrated System, Inc.
 * 
 * This software is confidential and proprietary information of 
 * International Integrated System, Inc. (&quot;Confidential Information&quot;).
 */
package com.bot.cqs.gateway.service.listener;

import com.bot.cqs.gateway.context.ContextLoader;
import com.bot.cqs.gateway.context.ThreadPool;
import com.bot.cqs.gateway.daemon.DispatchDaemonWorker;
import com.bot.cqs.gateway.handler.channel.impl.IBMWebSphereMQUtils;

import com.ibm.mq.MQException;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 此類別為一Java Servlet。主要作為處理接收MQ訊息的Daemon啟動及結束主控程式。
 *
 * @author Jeff Tseng
 * @see Executors
 * @see ThreadPoolExecutor
 * @since 1.0 2007/08/31
 */
public class MQTCHListener {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    private static final long serialVersionUID = 127377106824472638L;
    /**
     * MQ接聽器訊息處理時用的ThreadPool Manager
     */
    private ThreadPoolExecutor TCHThreadPool;
    /**
     * TCH MQ接聽器訊息處理時用的Threads
     */
    private List<DispatchDaemonWorker> tThreads;

    /**
     * Java Servlet初始方法，透過<code>Executors</code>將EAI、TCH的Thread Pooling初始。
     */
    public void init() {
    	tThreads = new ArrayList<DispatchDaemonWorker>();
        final String TCH = "TCH";
        IBMWebSphereMQUtils.initTCH();
        /**
         * Callback Method，在container shutdown時，會關閉已經配置好的thread pool物件。
         */

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                TCHThreadPool.shutdown();
            }
        });
        try {
            TCHThreadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(((ThreadPool) ContextLoader.getGatewayContext().getThreadPools().get(TCH)).getSize());
        } catch (Exception e) {
            TCHThreadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(30);
            if (logger.isDebugEnabled()) {
                logger.debug(e.getMessage());
            }
        }
        for (int i = 0; i < ((ThreadPool) ContextLoader.getGatewayContext().getThreadPools().get(TCH)).getSize(); i++) {
            try {
                tThreads.add(new DispatchDaemonWorker());
            } catch (MQException e) {
                break;
            }
            TCHThreadPool.execute(tThreads.get(i));
        }

    }

    /**
     * Java Servlet方法，透過<code>ThreadPoolExecutor</code>的shutdown方法將EAI、TCH的Thread Pooling清除並結束。
     */
    public void destroy() {
        IBMWebSphereMQUtils.releaseTCH();
        for (int i = 0; i < ((ThreadPool) ContextLoader.getGatewayContext().getThreadPools().get("TCH")).getSize(); i++) {
        	if (CollectionUtils.isEmpty(tThreads) || (tThreads !=null && tThreads.get(i) == null)) {
        		continue;
        	}
            tThreads.get(i).setStop(true);
            tThreads.get(i).interrupt();
            try {
                tThreads.get(i).join(2000);
            } catch (InterruptedException e) {
                if (logger.isDebugEnabled()) {
                    logger.debug(e.getMessage());
                }
            }
        }
        DispatchDaemonWorker.threadCount = 0;
        TCHThreadPool.shutdown();
    }
}
