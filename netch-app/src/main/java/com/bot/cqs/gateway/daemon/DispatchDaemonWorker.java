package com.bot.cqs.gateway.daemon;

import com.bot.cqs.gateway.context.ContextLoader;
import com.bot.cqs.gateway.context.GatewayContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.bot.cqs.gateway.context.MQInstance;
import com.bot.cqs.gateway.handler.channel.impl.IBMWebSphereMQUtils;
import com.bot.cqs.gateway.service.BOTMessageDispatcher;
import com.bot.cqs.gateway.service.GatewayException;
import com.ibm.mq.MQException;
import com.ibm.mq.MQQueueManager;
import tw.com.iisi.common.message.format.util.MessageUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 此類別為一個Daemon程式主要作為系統中派送由票交所傳送過來的訊息的觸發者。
 *
 * @author Jeff Tseng
 * @since 1.0 2007/08/31
 */
public class DispatchDaemonWorker extends Thread {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 提供主控程式停止此Daemon程式的控制旗標
     */
    volatile boolean stop = false;
    
    private MQInstance dispatchInstance;
    private MQQueueManager botMQDispatchQueueManager = null;
    public static MQQueueManager checkBotMQDispatchQueueManager = null;//排程檢查TCH QM 是否斷線
    public static Map<Integer, MQQueueManager> checkBotMQDispatchQueueManagerMap = new HashMap<>();//排程檢查MAP裡的TCH QM 是否斷線
    public static int threadCount = 0;



    /**
     * Constructor，並設定Daemon實體的代號
     */
    public DispatchDaemonWorker() throws MQException {
        this.setName("DispatchDaemonWorker_" + MessageUtils.getSystemDate());
        dispatchInstance = IBMWebSphereMQUtils.DispatchInstance;
        setDispatchInstance(dispatchInstance);
        try {
            threadCount++;
            botMQDispatchQueueManager = new MQQueueManager(dispatchInstance.getQueueManager(), IBMWebSphereMQUtils.getMQProperies(IBMWebSphereMQUtils.Dispatch), IBMWebSphereMQUtils.BOTMQTCHConnMgr);
            setBotMQDispatchQueueManager(botMQDispatchQueueManager);
            checkBotMQDispatchQueueManager = botMQDispatchQueueManager;
            checkBotMQDispatchQueueManagerMap.put(threadCount, botMQDispatchQueueManager);
            if (ContextLoader.getGatewayContext().isLogFlag())
                GatewayContext.logger.debug(String.format("[DispatchDaemonWorker] new botMQDispatchQueueManager"));
        } catch (MQException e) {
            GatewayContext.logger.debug(String.format("[DispatchDaemonWorker] MQException:" + e));
            throw e;
        }
    }
    
    /**
     * @return the dispatchInstance
     */
    public MQInstance getDispatchInstance() {
        return dispatchInstance;
    }

    /**
     * @param dispatchInstance the dispatchInstance to set
     */
    public void setDispatchInstance(MQInstance dispatchInstance) {
        this.dispatchInstance = dispatchInstance;
    }

    /**
     * @return the botMQDispatchQueueManager
     */
    public MQQueueManager getBotMQDispatchQueueManager() {
        return botMQDispatchQueueManager;
    }

    /**
     * @param botMQDispatchQueueManager the botMQDispatchQueueManager to set
     */
    public void setBotMQDispatchQueueManager(MQQueueManager botMQDispatchQueueManager) {
        this.botMQDispatchQueueManager = botMQDispatchQueueManager;
    }

    /**
     * Daemon程式啟動點。每次執行時，會檢查是否外界主控程式有下達停止的指令，若無，則觸發接收票交所訊息的程式。 每次完成一個運算後，會停止1000毫秒。
     */
    public void run() {
        if (ContextLoader.getGatewayContext().isLogFlag())
            GatewayContext.logger.debug(String.format("[DispatchDaemonWorker] Run dispatch is stop:" + stop));
        while (!stop) {
            try {
                sleep(50);
                BOTMessageDispatcher.dispatch(botMQDispatchQueueManager);
            } catch (InterruptedException e) {
                if (logger.isDebugEnabled()) {
                    logger.debug("[DispatchDaemonWorker]: "+ e);
                }
            } catch (GatewayException e) {
                if ("MG933".equals(e.getErrorCode())) {
                    try {
                        sleep(50);
                    } catch (InterruptedException e1) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("[DispatchDaemonWorker]: "+e);
                        }
                    }
                }
                if (logger.isDebugEnabled()) {
                    logger.debug("[DispatchDaemonWorker]: "+e.getErrorCode() + " - " + e.getErrorMessage());
                }
            }
        }
        
        if (botMQDispatchQueueManager != null) {
            try {
                botMQDispatchQueueManager.disconnect();
            } catch (MQException e) {
            }
            botMQDispatchQueueManager = null;
        }
    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }
}
