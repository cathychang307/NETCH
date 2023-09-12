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
import com.bot.cqs.gateway.daemon.EAIDaemonWorker;
import com.bot.cqs.gateway.handler.channel.impl.IBMWebSphereMQUtils;
import com.ibm.mq.MQException;
import com.ibm.mq.MQQueueManager;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
public class MQEAIListener{

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    private static final long serialVersionUID = 127377106824472638L;
    /**
     * MQ接聽器訊息處理時用的ThreadPool Manager
     */
    private ThreadPoolExecutor EAIThreadPool;
    /**
     * EAI MQ接聽器訊息處理時用的Threads
     */
    private List<EAIDaemonWorker> eThreads;

    /**
     * Java Servlet初始方法，透過<code>Executors</code>將EAI、TCH的Thread Pooling初始。
     */
    public void init() {
    	eThreads = new ArrayList<EAIDaemonWorker>();
        final String EAI = "EAI";
        IBMWebSphereMQUtils.initEAI();
        /**
         * Callback Method，在container shutdown時，會關閉已經配置好的thread pool物件。
         */

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                EAIThreadPool.shutdown();
            }
        });
        try {
            EAIThreadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(((ThreadPool) ContextLoader.getGatewayContext().getThreadPools().get(EAI)).getSize());
        } catch (Exception e) {
            EAIThreadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(30);
            if (logger.isDebugEnabled()) {
                logger.debug(e.getMessage());
            }
        }
        for (int i = 0; i < ((ThreadPool) ContextLoader.getGatewayContext().getThreadPools().get(EAI)).getSize(); i++) {
            try {
                eThreads.add(new EAIDaemonWorker());
            } catch (MQException e) {
                break;
            }
            EAIThreadPool.execute(eThreads.get(i));
        }
    }

    /**
     * Java Servlet方法，透過<code>ThreadPoolExecutor</code>的shutdown方法將EAI、TCH的Thread Pooling清除並結束。
     */
    public void destroy() {
        IBMWebSphereMQUtils.releaseEAI();
        for (int i = 0; i < ((ThreadPool) ContextLoader.getGatewayContext().getThreadPools().get("EAI")).getSize(); i++) {
        	if (CollectionUtils.isEmpty(eThreads) || (eThreads !=null && eThreads.get(i) == null)) {
        		continue;
        	}
            eThreads.get(i).setStop(true);
            eThreads.get(i).interrupt();
            try {
                eThreads.get(i).join(2000);
            } catch (InterruptedException e) {
                if (logger.isDebugEnabled()) {
                    logger.debug(e.getMessage());
                }
            }
            //關閉EAI Daemon 一併啟動的botMQTCHQueueManager
            Map<Integer, MQQueueManager> botMQTCHQueueManagerMap = EAIDaemonWorker.botMQTCHQueueManagerThreadMap;
            MQQueueManager botMQTCHQueueManager = botMQTCHQueueManagerMap.get(i + 1);
            if (botMQTCHQueueManager != null) {
                try {
                    botMQTCHQueueManager.disconnect();
                } catch (MQException e) {
                }
            }
        }
        EAIDaemonWorker.threadCount = 0;
        EAIThreadPool.shutdown();
    }

}
