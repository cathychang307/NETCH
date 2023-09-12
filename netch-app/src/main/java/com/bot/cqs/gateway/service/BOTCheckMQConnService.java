package com.bot.cqs.gateway.service;

import com.bot.cqs.gateway.context.ContextLoader;
import com.bot.cqs.gateway.context.GatewayContext;
import com.bot.cqs.gateway.context.MQInstance;
import com.bot.cqs.gateway.context.ThreadPool;
import com.bot.cqs.gateway.daemon.DispatchDaemonWorker;
import com.bot.cqs.gateway.daemon.EAIDaemonWorker;
import com.bot.cqs.gateway.handler.channel.MQRequestChannelHandler;
import com.bot.cqs.gateway.handler.channel.impl.BOTMQRequestChannelHandler;
import com.bot.cqs.gateway.handler.channel.impl.IBMWebSphereMQUtils;
import com.bot.cqs.gateway.service.listener.MQEAIListener;
import com.bot.cqs.gateway.service.listener.MQTCHListener;
import com.ibm.mq.*;
import com.ibm.mq.constants.CMQC;
import com.iisigroup.cap.utils.CapAppContext;
import org.quartz.DisallowConcurrentExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
@DisallowConcurrentExecution
public class BOTCheckMQConnService {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    private static MQEAIListener mqEaiListener = null;
    private static MQTCHListener mqTchListener = null;
    private static boolean RestartMQConn = false;


    // 初始化每個處理程式，將實際的實作程式指定給處理程式介面上。此部分之後可以用Spring的介面替代。
    static {
        ContextLoader.init();
        //MQ Listener init
        mqEaiListener = CapAppContext.getBean("mqEAIListener");
        mqTchListener = CapAppContext.getBean("mqTCHListener");
    }

    /**
     * 系統每隔設定的時間重新啟動TCH/EAI連線。若系統有發生錯誤，則等候waitSec秒後會重試。總共重試次數為retryLoop次。
     *
     * @param retryLoop 重試次數
     * @param waitSec   重試等候秒數
     */
    public void restartMQConnService(int retryLoop, int waitSec) {
        if (ContextLoader.getGatewayContext().isLogFlag())
            GatewayContext.logger.debug("==============RestartMQConnService Job Start==============");
        RestartMQConn = true;
        String instanceName = IBMWebSphereMQUtils.TCH;
        MQInstance instance = IBMWebSphereMQUtils.TCHInstance;
        boolean isTCHConnect = false;
        //TCH
        try {
            stopTCHMQConnections();
            isTCHConnect = startTCHMQConnections(instance);
        } catch (MQException ex) {
            mq2009RetryLoop(instanceName, retryLoop, waitSec, instance, ex);
        }
        //EAI
        instanceName = IBMWebSphereMQUtils.EAI;
        instance = IBMWebSphereMQUtils.EAIInstance;
        boolean isEAIConnect = false;
        try {
            stopEAIMQConnection();
            isEAIConnect = startEAIMQConnection(instance);
        } catch (MQException ex) {
            mq2009RetryLoop(instanceName, retryLoop, waitSec, instance, ex);
        }
        if (isTCHConnect && isEAIConnect) {
            RestartMQConn = false;
        }
        if (ContextLoader.getGatewayContext().isLogFlag())
            GatewayContext.logger.debug("==============RestartMQConnService Job End==============");
    }

    /**
     * 系統每隔設定的時間確認TCH&EAI連線是否正常。若系統有發生錯誤，則等候waitSec秒後會重試。總共重試次數為retryLoop次。
     *
     * @param retryLoop 重試次數
     * @param waitSec   重試等候秒數
     */
    public void checkALLConnService(int retryLoop, int waitSec) {
        GatewayContext.logger.debug("==============CheckALLConnService Job Start==============");
        if (!RestartMQConn) {
            //檢查TCH連線
            if (ContextLoader.getGatewayContext().isLogFlag())
                GatewayContext.logger.debug("=====Check TCH Connection Start=====");
            checkConnService(IBMWebSphereMQUtils.TCH, retryLoop, waitSec);
            if (ContextLoader.getGatewayContext().isLogFlag())
                GatewayContext.logger.debug("=====Check TCH Connection End=====");

            //檢查EAI連線
            if (ContextLoader.getGatewayContext().isLogFlag())
                GatewayContext.logger.debug("=====Check EAI Connection Start=====");
            boolean eaiRestartOk = checkConnService(IBMWebSphereMQUtils.EAI, retryLoop, waitSec);
            //若EAI檢查連線正常或沒有重啟連線成功，才繼續檢查EAI啟用的TCH連線狀態
            if (!eaiRestartOk) {
                GatewayContext.logger.debug("[EAI_TCH] 開始檢查EAI啟用的TCH連線狀態.");
                checkConnService("EAI_TCH", retryLoop, waitSec);
            }
            if (ContextLoader.getGatewayContext().isLogFlag())
                GatewayContext.logger.debug("=====Check EAI Connection End=====");

        } else {
            GatewayContext.logger.debug("=====RestartMQConnService Running, Stop CheckALLConnService Job=====");
        }
        GatewayContext.logger.debug("==============CheckALLConnService Job End==============");
    }

    /**
     * 系統每隔設定的時間確認EAI/TCH連線是否正常。若系統有發生錯誤，則等候waitSec秒後會重試。總共重試次數為retryLoop次。
     *
     * @param instanceName 連線實體名稱
     * @param retryLoop    重試次數
     * @param waitSec      重試等候秒數
     * @return isReStartOk 是否重啟成功
     *
     */
    private boolean checkConnService(String instanceName, int retryLoop, int waitSec) {
        boolean isRestartOk = false;
        int threadSize = 0;
        MQInstance instance = null;
        Map<Integer, MQQueueManager> checkBotMQQueueManagerMap = new HashMap<>();
        if (IBMWebSphereMQUtils.EAI.equalsIgnoreCase(instanceName)) {
            instance = IBMWebSphereMQUtils.EAIInstance;
            checkBotMQQueueManagerMap = EAIDaemonWorker.checkBotMQEAIQueueManagerMap;
            threadSize = ((ThreadPool) ContextLoader.getGatewayContext().getThreadPools().get("EAI")).getSize();
            boolean checkEAIThreadQMIsNull = checkThreadQMIsNUll(threadSize, checkBotMQQueueManagerMap);
            if (checkBotMQQueueManagerMap.size() != 0 && checkEAIThreadQMIsNull)
                mqEaiListener.init();
        } else {
            instance = IBMWebSphereMQUtils.TCHInstance;
            if ("EAI_TCH".equalsIgnoreCase(instanceName)) {
                checkBotMQQueueManagerMap = EAIDaemonWorker.botMQTCHQueueManagerThreadMap;
                threadSize = ((ThreadPool) ContextLoader.getGatewayContext().getThreadPools().get("EAI")).getSize();
            } else if (IBMWebSphereMQUtils.TCH.equalsIgnoreCase(instanceName)) {
                checkBotMQQueueManagerMap = DispatchDaemonWorker.checkBotMQDispatchQueueManagerMap;
                threadSize = ((ThreadPool) ContextLoader.getGatewayContext().getThreadPools().get("TCH")).getSize();
            }
            boolean checkTCHThreadQMIsNUll = checkThreadQMIsNUll(threadSize, checkBotMQQueueManagerMap);
            if (checkBotMQQueueManagerMap.size() != 0 &&checkTCHThreadQMIsNUll)
                mqTchListener.init();
        }
        try {
            boolean isAlive = checkThreadQueueManagerIsAlive(instance, threadSize, checkBotMQQueueManagerMap);
            if (!isAlive) {
                isRestartOk = true;
                boolean isConnect = false;
                if (ContextLoader.getGatewayContext().isLogFlag())
                    GatewayContext.logger.debug(String.format("[%s]AP啟動MQ異常，MQ開始嘗試重新連線. ", instanceName));
                if (IBMWebSphereMQUtils.EAI.equalsIgnoreCase(instance.getId())) {
                    stopEAIMQConnection();
                    isConnect = startEAIMQConnection(instance);
                } else {
                    //TCH斷線，則EAI也會使用到TCH連線，因此EAI也需要一併重啟連線
                    stopTCHMQConnections();
                    stopEAIMQConnection();
                    boolean isTCHConnect = startTCHMQConnections(instance);
                    boolean isEAIConnect = startEAIMQConnection(IBMWebSphereMQUtils.EAIInstance);
                    if (isTCHConnect && isEAIConnect) {
                        isConnect = true;
                    }
                }
                if (ContextLoader.getGatewayContext().isLogFlag())
                    GatewayContext.logger.debug(String.format("[%s]AP啟動MQ異常，MQ重新連線%s. ", instanceName, isConnect ? "成功" : "失敗"));
            }
        } catch (MQException ex) {
            isRestartOk = mq2009RetryLoop(instanceName, retryLoop, waitSec, instance, ex);
        }
        return isRestartOk;
    }

    /**
     * 檢查各Thread MQQueueManager是否為Null
     *
     * @param threadSize Thread Size
     * @param checkBotMQQueueManagerMap checkBotMQQueueManagerMap
     * @return
     */
    private boolean checkThreadQMIsNUll(int threadSize, Map<Integer, MQQueueManager> checkBotMQQueueManagerMap) {
        boolean threadQMIsNUll = false;
        for (int i = 0; i < threadSize; i++) {
            MQQueueManager botMQQueueManager = checkBotMQQueueManagerMap.get(i+1);
            if (botMQQueueManager == null) {
                threadQMIsNUll = true;
            }
        }
        return threadQMIsNUll;
    }

    /**
     * 檢查各Thread MQQueueManager連線狀態是否活著
     * 
     * @param instance MQInstance
     * @param threadSize Thread Size
     * @param checkBotMQQueueManagerMap checkBotMQQueueManagerMap
     * @throws MQException 若處理過程中有異常發生時拋出
     */
    private boolean checkThreadQueueManagerIsAlive(MQInstance instance, int threadSize, Map<Integer, MQQueueManager> checkBotMQQueueManagerMap) throws MQException {
        boolean isAlive = false;
        if (checkBotMQQueueManagerMap.size() != 0) {
            for (int i = 0; i < threadSize; i++) {
                MQQueueManager mqQueueManager = checkBotMQQueueManagerMap.get(i + 1);
                try {
                    boolean isCheckOk = doCheck(mqQueueManager, instance);
                    if (isCheckOk) {
                        isAlive = isCheckOk;
                        if (ContextLoader.getGatewayContext().isLogFlag())
                            GatewayContext.logger.debug(String.format("[%s-Thread %s]檢查MQ連線正常.", instance.getId(), i + 1));
                    }
                } catch (MQException ex) {
                    boolean isMQError2009 = ex.completionCode == 2 && ex.reasonCode == CMQC.MQRC_CONNECTION_BROKEN;
                    if (isMQError2009) {
                        if (IBMWebSphereMQUtils.EAI.equalsIgnoreCase(instance.getId())) {
                            EAIDaemonWorker.checkBotMQEAIQueueManager = mqQueueManager;
                        } else if (IBMWebSphereMQUtils.TCH.equalsIgnoreCase(instance.getId())) {
                            DispatchDaemonWorker.checkBotMQDispatchQueueManager = mqQueueManager;
                        }
                        if (ContextLoader.getGatewayContext().isLogFlag())
                            GatewayContext.logger.debug(String.format("[%s-Thread %s]檢查MQ連線異常，開始重啟連線.", instance.getId(), i + 1));
                        throw ex;
                    }
                }
            }
        } else {
            if (ContextLoader.getGatewayContext().isLogFlag())
                GatewayContext.logger.debug(String.format("[%s-Thread %s]檢查MQ連線異常，開始重啟連線.", instance.getId(), null));
        }
        return isAlive;
    }

    /**
     * 系統每隔設定的時間確認EAI/TCH MQ連線狀態
     *
     * @param mqQueueManager MQQueueManager
     * @param instance       MQInstance
     * @throws MQException 若處理過程中有異常發生時拋出
     */
    private boolean doCheck(MQQueueManager mqQueueManager, MQInstance instance) throws MQException {
        boolean isCheckOk = false;
        if (mqQueueManager != null && instance != null) {
            MQRequestChannelHandler<String, byte[], String> mqReqChannel = new BOTMQRequestChannelHandler();
            if (IBMWebSphereMQUtils.EAI.equalsIgnoreCase(instance.getId())) {
                mqReqChannel.setInstanceId(IBMWebSphereMQUtils.EAI);
            } else if (IBMWebSphereMQUtils.TCH.equalsIgnoreCase(instance.getId())) {
                mqReqChannel.setInstanceId(IBMWebSphereMQUtils.TCH);
            }

            MQQueue queue = null;
            MQMessage retrievedMessage = null;
            MQGetMessageOptions gmo = new MQGetMessageOptions();
            try {
                int openOptions = CMQC.MQOO_FAIL_IF_QUIESCING | CMQC.MQOO_INPUT_SHARED | CMQC.MQOO_BROWSE;
                queue = mqQueueManager.accessQueue(instance.getInboundQueue(), openOptions);
                retrievedMessage = new MQMessage();
                gmo.options = CMQC.MQGMO_NO_WAIT | CMQC.MQGMO_BROWSE_FIRST;
                queue.get(retrievedMessage, gmo);
                isCheckOk = true;
            } catch (MQException e) {
                boolean isMQ2009 = e.completionCode == 2 && e.reasonCode == CMQC.MQRC_CONNECTION_BROKEN;
                boolean isMQ2033 = e.completionCode == 2 && e.reasonCode == CMQC.MQRC_NO_MSG_AVAILABLE;
                if (isMQ2009) {
                    throw e;
                } else if (isMQ2033) {
                    isCheckOk = true;
                }
            } finally {
                if (queue != null) {
                    try {
                        queue.close();
                    } catch (MQException e) {
                    }
                    queue = null;
                }
                mqReqChannel = null;
            }
        }
        return isCheckOk;
    }

    /**
     * 發生com.ibm.mq.MQException: MQJE001: 完成碼 '2'，原因 '2009'，則等候waitSec秒後會重試MQ連線，若無法嘗試後依舊無法連線則繼續重試，最多重試次數為retryLoop次。
     *
     * @param instanceName 連線實體名稱
     * @param retryLoop    重試次數
     * @param waitSec      重試等候秒數
     * @param instance     MQInstance
     * @param ex           MQException
     */
    private boolean mq2009RetryLoop(String instanceName, int retryLoop, int waitSec, MQInstance instance, MQException ex) {
        int count = 0;
        boolean isConnect = false;
        do {
            boolean isMQError2009 = ex.completionCode == 2 && ex.reasonCode == CMQC.MQRC_CONNECTION_BROKEN;
            if (isMQError2009) {
                count++;
                try {
                    if (ContextLoader.getGatewayContext().isLogFlag())
                        GatewayContext.logger.debug(String.format("[%s]等待%s秒後，MQ開始嘗試第%s次重新連線. ", instanceName, waitSec, count));
                    Thread.sleep(waitSec * 1000);//second
                    isConnect = retryConnection(instance, ex);
                    if (isConnect) {
                        if (ContextLoader.getGatewayContext().isLogFlag())
                            GatewayContext.logger.debug(String.format("[%s]MQ連線嘗試第%s次重新連線成功. ", instanceName, count));
                        break;
                    } else {
                        if (ContextLoader.getGatewayContext().isLogFlag())
                            GatewayContext.logger.debug(String.format("[%s]MQ連線嘗試第%s次重新連線失敗. ", instanceName, count));
                    }
                } catch (Exception e1) {
                    logger.debug(e1.getMessage());
                    break;
                }
            } else {
                break;
            }
        } while (count < retryLoop);
        return isConnect;
    }

    /**
     * 發生com.ibm.mq.MQException: MQJE001: 完成碼 '2'，原因 '2009'則重試MQ連線，
     *
     * @param instance MQInstance
     * @param e        MQException
     */
    private boolean retryConnection(MQInstance instance, MQException e) {
        boolean isMQ2009 = e.completionCode == 2 && e.reasonCode == CMQC.MQRC_CONNECTION_BROKEN;
        boolean isConnect = false;
        if (isMQ2009 && instance != null) {
            if (ContextLoader.getGatewayContext().isLogFlag())
                GatewayContext.logger.debug(String.format("[%s][Retry MQ Connection] %s Queue Manager connect retry, MQException: %s ", instance.getId(), instance.getId(), e.getMessage()));
            try {
                if (IBMWebSphereMQUtils.EAI.equalsIgnoreCase(instance.getId())) {
                    stopEAIMQConnection();
                    isConnect = startEAIMQConnection(instance);
                } else {
                    //TCH斷線，則EAI也會使用到TCH連線，因此EAI也需要一併重啟連線
                    stopTCHMQConnections();
                    stopEAIMQConnection();
                    boolean isTCHConnect = startTCHMQConnections(instance);
                    boolean isEAIConnect = startEAIMQConnection(IBMWebSphereMQUtils.EAIInstance );
                    if (isTCHConnect && isEAIConnect) {
                        isConnect = true;
                    }
                }
            } catch (MQException ex) {
            }
        }
        return isConnect;
    }

    /**
     * 開始TCH的MQ連線
     */
    private boolean startTCHMQConnections(MQInstance instance) throws MQException {
        //開始TCH連線
        mqTchListener.init();
        return doCheck(DispatchDaemonWorker.checkBotMQDispatchQueueManager, instance);
    }

    /**
     * 開始EAI的MQ連線
     */
    private boolean startEAIMQConnection(MQInstance instance) throws MQException {
        //EAI連線
        mqEaiListener.init();
        return doCheck(EAIDaemonWorker.checkBotMQEAIQueueManager, instance);
    }

    /**
     * 停止TCH的MQ連線
     */
    private void stopTCHMQConnections() {
        //停下TCH
        mqTchListener.destroy();
        if (ContextLoader.getGatewayContext().isLogFlag())
            GatewayContext.logger.debug("[TCH] Stop TCH MQ Connection.");
    }

    /**
     * 停止EAI的MQ連線
     */
    private void stopEAIMQConnection() {
        //停下EAI
        mqEaiListener.destroy();
        if (ContextLoader.getGatewayContext().isLogFlag())
            GatewayContext.logger.debug("[EAI] Stop EAI MQ Connection.");
    }

}
