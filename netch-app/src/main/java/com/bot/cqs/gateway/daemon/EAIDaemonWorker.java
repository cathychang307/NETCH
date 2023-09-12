package com.bot.cqs.gateway.daemon;

import com.bot.cqs.gateway.context.ContextLoader;
import com.bot.cqs.gateway.context.GatewayContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.bot.cqs.gateway.context.MQInstance;
import com.bot.cqs.gateway.handler.channel.impl.IBMWebSphereMQUtils;
import com.bot.cqs.gateway.service.BOTMessageResolverForEAI;
import com.ibm.mq.MQException;
import com.ibm.mq.MQQueueManager;
import tw.com.iisi.common.message.format.util.MessageUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 此類別為一個Daemon程式主要作為系統中由EAI傳送過來的訊息的觸發者。
 *
 * @author Jeff Tseng
 * @since 1.0 2007/08/31
 */
public class EAIDaemonWorker extends Thread {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 提供主控程式停止此Daemon程式的控制旗標
     */
    volatile boolean stop = false;
    
    private MQInstance eaiInstance;
    private MQInstance tchInstance;
    private MQQueueManager botMQEAIQueueManager = null;
    private MQQueueManager botMQTCHQueueManager = null;
    public static MQQueueManager checkBotMQEAIQueueManager = null;//排程檢查EAI QM 是否斷線
    public static Map<Integer, MQQueueManager> checkBotMQEAIQueueManagerMap = new HashMap<>();//排程檢查MAP裡的EAI QM 是否斷線
    public static Map<Integer, MQQueueManager> botMQTCHQueueManagerThreadMap = new HashMap<>();
    public static int threadCount = 0;
    

    /**
     * Constructor，並設定Daemon實體的代號
     */
    public EAIDaemonWorker() throws MQException {
        this.setName("EAIDaemonWorker" + MessageUtils.getSystemDate());
        eaiInstance = IBMWebSphereMQUtils.EAIInstance;
        tchInstance = IBMWebSphereMQUtils.TCHInstance;
        setEaiInstance(eaiInstance);
        try {
            threadCount++;
            botMQEAIQueueManager = new MQQueueManager(eaiInstance.getQueueManager(), IBMWebSphereMQUtils.getMQProperies(IBMWebSphereMQUtils.EAI), IBMWebSphereMQUtils.BOTMQEAIConnMgr);
            setBotMQEAIQueueManager(botMQEAIQueueManager);
            botMQTCHQueueManager = new MQQueueManager(tchInstance.getQueueManager(), IBMWebSphereMQUtils.getMQProperies(IBMWebSphereMQUtils.TCH), IBMWebSphereMQUtils.BOTMQTCHConnMgr);
            setBotMQTCHQueueManager(botMQTCHQueueManager);
            checkBotMQEAIQueueManager = botMQEAIQueueManager;
            checkBotMQEAIQueueManagerMap.put(threadCount, botMQEAIQueueManager);
            botMQTCHQueueManagerThreadMap.put(threadCount, botMQTCHQueueManager);
            if (ContextLoader.getGatewayContext().isLogFlag())
                GatewayContext.logger.debug(String.format("[EAIDaemonWorker] new botMQEAIQueueManager and botMQTCHQueueManager"));
        } catch (MQException e) {
            GatewayContext.logger.debug(String.format("[EAIDaemonWorker] MQException:" + e));
            throw e;
        }
    }

    /**
     * @return the eaiInstance
     */
    public MQInstance getEaiInstance() {
        return eaiInstance;
    }

    /**
     * @param eaiInstance the eaiInstance to set
     */
    public void setEaiInstance(MQInstance eaiInstance) {
        this.eaiInstance = eaiInstance;
    }

    /**
     * @return the botMQEAIQueueManager
     */
    public MQQueueManager getBotMQEAIQueueManager() {
        return botMQEAIQueueManager;
    }

    /**
     * @param botMQEAIQueueManager the botMQEAIQueueManager to set
     */
    public void setBotMQEAIQueueManager(MQQueueManager botMQEAIQueueManager) {
        this.botMQEAIQueueManager = botMQEAIQueueManager;
    }
    
    

    /**
     * @return the tchInstance
     */
    public MQInstance getTchInstance() {
        return tchInstance;
    }

    /**
     * @param tchInstance the tchInstance to set
     */
    public void setTchInstance(MQInstance tchInstance) {
        this.tchInstance = tchInstance;
    }

    /**
     * @return the botMQTCHQueueManager
     */
    public MQQueueManager getBotMQTCHQueueManager() {
        return botMQTCHQueueManager;
    }

    /**
     * @param botMQTCHQueueManager the botMQTCHQueueManager to set
     */
    public void setBotMQTCHQueueManager(MQQueueManager botMQTCHQueueManager) {
        this.botMQTCHQueueManager = botMQTCHQueueManager;
    }

    /**
     * Daemon程式啟動點。每次執行時，會檢查是否外界主控程式有下達停止的指令，若無，則觸發接收EAI訊息的程式。 每次完成一個運算後，會停止1000毫秒。
     */
    public void run() {
        if (ContextLoader.getGatewayContext().isLogFlag())
            GatewayContext.logger.debug(String.format("[EAIDaemonWorker] Run resolver for EAI is stop:" + stop));
        while (!stop) {
            try {
                sleep(50);
                BOTMessageResolverForEAI.resolve(botMQEAIQueueManager, botMQTCHQueueManager);
            } catch (InterruptedException e) {
                if (logger.isDebugEnabled()) {
                    logger.debug("[EAIDaemonWorker]: "+ e.getMessage());
                }
            } catch (Exception e) {
                if (logger.isDebugEnabled()) {
                    logger.debug("[EAIDaemonWorker]: "+ e.getMessage());
                }
            }
        }
        
        if (botMQEAIQueueManager != null) {
            try {
                botMQEAIQueueManager.disconnect();
            } catch (MQException e) {
            }
            botMQEAIQueueManager = null;
        }
        if (botMQTCHQueueManager != null) {
            try {
                botMQTCHQueueManager.disconnect();
            } catch (MQException e) {
            }
            botMQTCHQueueManager = null;
        }
        
    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }
}
