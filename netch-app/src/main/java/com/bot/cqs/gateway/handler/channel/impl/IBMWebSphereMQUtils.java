package com.bot.cqs.gateway.handler.channel.impl;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.bot.cqs.gateway.context.GatewayContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.bot.cqs.gateway.context.ContextLoader;
import com.bot.cqs.gateway.context.MQInstance;
import com.bot.cqs.gateway.handler.message.filter.MQMessageFilter;
import com.ibm.mq.MQEnvironment;
import com.ibm.mq.MQException;
import com.ibm.mq.MQSimpleConnectionManager;
import com.ibm.mq.constants.CMQC;
import com.iisigroup.cap.utils.CapString;

/**
 * 此類別主要用於初始取得建立MQ的Connection Pool Manager。
 *
 * @author Jeff Tseng
 * @see "IBM MQ 開發指南"
 * @since 1.0 2007/08/31
 */
public class IBMWebSphereMQUtils {

    public static Map<String, String> CCSID_ENCODING = null;

    protected static final Logger logger = LoggerFactory.getLogger(IBMWebSphereMQUtils.class);

    /**
     * 系統預設的MQ Connection Pool Manager實體。
     */
    public static MQSimpleConnectionManager BOTMQEAIConnMgr = new MQSimpleConnectionManager();
    public static MQSimpleConnectionManager BOTMQTCHConnMgr = new MQSimpleConnectionManager();
    private static OutputStreamWriter writer = null;
    private static File logFile = null;
    public static MQInstance EAIInstance;
    public static MQInstance TCHInstance;
    public static MQInstance DispatchInstance;
    public final static String EAI = "EAI";
    public final static String TCH = "TCH";
    public final static String Dispatch = "Dispatch";

    static {
        ContextLoader.init();
        //EAI
        EAIInstance = (MQInstance) ContextLoader.getGatewayContext().getIbmWebSphereMQ().getInstances().get(EAI);
        //TCH
        TCHInstance = (MQInstance) ContextLoader.getGatewayContext().getIbmWebSphereMQ().getInstances().get(TCH);
        //Dispatch
        DispatchInstance = (MQInstance) ContextLoader.getGatewayContext().getIbmWebSphereMQ().getInstances().get(Dispatch);
        //ENCODING
        CCSID_ENCODING = new HashMap<String, String>();
        CCSID_ENCODING.put("950", "Big5");
        //初始MQException類別中，異常日誌紀錄的路徑。
        MQException.logExclude(MQException.MQRC_NO_MSG_AVAILABLE);
        try {
            logFile = new File(ContextLoader.getGatewayContext().getLogLocation());
            if (!logFile.exists())
                logFile.createNewFile();
            MQException.log = new OutputStreamWriter(new FileOutputStream(logFile));
        } catch (FileNotFoundException e1) {
            if (logger.isDebugEnabled()) {
                logger.debug(e1.getMessage());
            }
        } catch (IOException e) {
            if (logger.isDebugEnabled()) {
                logger.debug(e.getMessage());
            }
        }

    }


    public static void initEAI() {
        //EAI
        // 初始化Connection Pool Manager的最大值、未使用最大值，以及啟動模式等。並註冊於MQenvironment中
        BOTMQEAIConnMgr.setActive(MQSimpleConnectionManager.MODE_AUTO);
        BOTMQEAIConnMgr.setTimeout(3600000);
        if (ContextLoader.getGatewayContext().getIbmWebSphereMQ() != null) {
            BOTMQEAIConnMgr.setMaxConnections(ContextLoader.getGatewayContext().getIbmWebSphereMQ().getPoolMaxSize());
            BOTMQEAIConnMgr.setMaxUnusedConnections(ContextLoader.getGatewayContext().getIbmWebSphereMQ().getUnusedMaxSize());
        } else {
            BOTMQEAIConnMgr.setMaxConnections(100);
            BOTMQEAIConnMgr.setMaxUnusedConnections(75);
        }
        if (ContextLoader.getGatewayContext().isLogFlag()) {
            GatewayContext.logger.debug("initEAI");
        }
    }

    /**
     * 停止並釋放MQ Connection Pool Manager實體。
     */
    public static void releaseEAI() {
        BOTMQEAIConnMgr.setActive(MQSimpleConnectionManager.MODE_INACTIVE);
        if (writer != null) {
            try {
                writer.close();
            } catch (IOException e) {
                if (logger.isDebugEnabled()) {
                    logger.debug(e.getMessage());
                }
            }
            writer = null;
        }
        if (ContextLoader.getGatewayContext().isLogFlag()) {
            GatewayContext.logger.debug("releaseEAI");
        }
    }

    public static void initTCH() {
        //TCH
        // 初始化Connection Pool Manager的最大值、未使用最大值，以及啟動模式等。並註冊於MQenvironment中
        BOTMQTCHConnMgr.setActive(MQSimpleConnectionManager.MODE_AUTO);
        BOTMQTCHConnMgr.setTimeout(3600000);
        if (ContextLoader.getGatewayContext().getIbmWebSphereMQ() != null) {
            BOTMQTCHConnMgr.setMaxConnections(ContextLoader.getGatewayContext().getIbmWebSphereMQ().getPoolMaxSize());
            BOTMQTCHConnMgr.setMaxUnusedConnections(ContextLoader.getGatewayContext().getIbmWebSphereMQ().getUnusedMaxSize());
        } else {
            BOTMQTCHConnMgr.setMaxConnections(100);
            BOTMQTCHConnMgr.setMaxUnusedConnections(75);
        }
        MQEnvironment.setDefaultConnectionManager(BOTMQTCHConnMgr);
        if (ContextLoader.getGatewayContext().isLogFlag()) {
            GatewayContext.logger.debug("initTCH");
        }
    }

    /**
     * 停止並釋放MQ Connection Pool Manager實體。
     */
    public static void releaseTCH() {
        BOTMQTCHConnMgr.setActive(MQSimpleConnectionManager.MODE_INACTIVE);
        if (writer != null) {
            try {
                writer.close();
            } catch (IOException e) {
                if (logger.isDebugEnabled()) {
                    logger.debug(e.getMessage());
                }
            }
            writer = null;
        }
        if (ContextLoader.getGatewayContext().isLogFlag()) {
            GatewayContext.logger.debug("releaseTCH");
        }
    }

    /**
     * 透過MQ CCSID的設定值，取得對應於Java內的編碼字串。
     * 
     * @param ccsid
     *            MQ CCSID
     * @return Java內的編碼字串
     */
    public static String getCCSIDEncodingMap(String ccsid) {
        return (String) CCSID_ENCODING.get(ccsid);
    }

    /**
     * 透過Java Reflection機制，取得訊息過濾程式實體。
     * 
     * @param filter
     *            訊息過濾程式名稱
     * @return MQMessageFilter實體
     */
    public static MQMessageFilter getFilter(String filter) {
        try {
            return (MQMessageFilter) Class.forName("com.bot.cqs.gateway.handler.message.filter.impl." + filter).newInstance();
        } catch (InstantiationException e) {
            if (logger.isDebugEnabled()) {
                logger.debug(e.getMessage());
            }
        } catch (IllegalAccessException e) {
            if (logger.isDebugEnabled()) {
                logger.debug(e.getMessage());
            }
        } catch (ClassNotFoundException e) {
            if (logger.isDebugEnabled()) {
                logger.debug(e.getMessage());
            }
        }
        return null;
    }

    public static Properties getMQProperies(String instanceId) {
        MQInstance instance = (MQInstance) ContextLoader.getGatewayContext().getIbmWebSphereMQ().getInstances().get(instanceId);
        Properties props = new Properties();

        if ("client".equalsIgnoreCase(instance.getConnMode())) {

            props.put(CMQC.HOST_NAME_PROPERTY, instance.getHost());
            props.put(CMQC.PORT_PROPERTY, Integer.parseInt(instance.getPort()));
            props.put(CMQC.CHANNEL_PROPERTY, instance.getChannel());
            if (!CapString.isEmpty(instance.getUserId())) {
                props.put(CMQC.USER_ID_PROPERTY, instance.getUserId());
            }
            if (!CapString.isEmpty(instance.getPsswwdd())) {
                props.put(CMQC.PASSWORD_PROPERTY, instance.getPsswwdd());
            }
        }

        if (Integer.parseInt(instance.getCcsid()) > 0) {
            props.put(CMQC.CCSID_PROPERTY, Integer.parseInt(instance.getCcsid()));
        }
        return props;
    }


}