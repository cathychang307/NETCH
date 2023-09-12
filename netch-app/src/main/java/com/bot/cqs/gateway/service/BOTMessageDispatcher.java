package com.bot.cqs.gateway.service;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import com.bot.cqs.gateway.util.BOTGatewayServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bot.cqs.gateway.context.ContextLoader;
import com.bot.cqs.gateway.context.GatewayContext;
import com.bot.cqs.gateway.context.MQInstance;
import com.bot.cqs.gateway.handler.channel.MQRequestChannelHandler;
import com.bot.cqs.gateway.handler.channel.MQResponseChannelHandler;
import com.bot.cqs.gateway.handler.channel.impl.BOTMQRequestChannelHandler;
import com.bot.cqs.gateway.handler.channel.impl.BOTMQResponseChannelHandler;
import com.bot.cqs.gateway.handler.channel.impl.IBMWebSphereMQUtils;
import com.ibm.mq.MQQueueManager;

import tw.com.iisi.common.message.format.util.MessageUtils;

/**
 * 此類別主要用於建立與TCH MQ之間的通訊通道，將TCH傳回的資料重新產生一個MQMessage，並將Correlation Id設置進去，以供查詢訊息取得正確回應。
 *
 * @author Jeff Tseng
 * @see MQRequestChannelHandler
 * @see MQResponseChannelHandler
 * @see "IBM MQ 開發指南"
 * @since 1.0 2007/08/31
 */
public class BOTMessageDispatcher {

    protected static final Logger logger = LoggerFactory.getLogger(BOTMessageDispatcher.class);

    /**
     * 查詢電文處理通道程式
     */
    private static MQRequestChannelHandler<String, byte[], String> mqReqChannel = null;
    /**
     * 查詢回覆電文處理通道程式
     */
    private static MQResponseChannelHandler<?, byte[], String, Map> mqRespChannel = null;

    private static MQInstance instance = null;


    // 初始化每個處理程式，將實際的實作程式指定給處理程式介面上。此部分之後可以用Spring的介面替代。
    static {
        ContextLoader.init();
        mqReqChannel = new BOTMQRequestChannelHandler();
        mqRespChannel = new BOTMQResponseChannelHandler();

        mqReqChannel.setInstanceId("Dispatch");
        mqRespChannel.setInstanceId("Dispatch");
    }

    /**
     * 透過IBM MQ API，建立MQ連結，並開啟GatewayContext中instance中各Queue Name屬性指向的Queue，接收於Queue中的訊息。 並判斷訊息是否為查詢類訊息，若為真，則產生新的MQ訊息，並以原始訊息中的會員保留欄位值作為新訊息的CorrelationId。
     * @param mqQueueManager
     * @throws GatewayException
     *             若處理過程中有異常發生時拋出
     */
    public static void dispatch(MQQueueManager mqQueueManager) throws GatewayException {
        Map request = null;
        String result = null;

        try {
//            request = (Map) mqRespChannel.browse("System", "BOTMsgMQMessageFilter");
//            if (ContextLoader.getGatewayContext().isLogFlag())
//                GatewayContext.logger.debug(String.format("[CorrId_%s] [Dispatch] System browse.", BOTGatewayServiceUtil.msgId2Hex((byte[]) request.get("corrId")) : "")));
            instance = IBMWebSphereMQUtils.DispatchInstance;
            request = (Map) mqRespChannel.receive(mqQueueManager, "System");// 取票交所回覆的MQ訊息
            if (request != null && request.containsKey("data")) {
                if (ContextLoader.getGatewayContext().isLogFlag()) {
                    GatewayContext.logger.debug(String.format("[Dispatch] receive %s Queue message.", instance.getInboundQueue()));
                }
                result = (String) request.get("data");
                putReGeneratedMsg(mqQueueManager, result);
            }
        } catch (GatewayException e) {
            if (!"MG933".equals(e.getErrorCode()))
                throw e;
        }

        result = null;
        request = null;
    }

    /**
     * 判斷原始訊息是否為查詢類訊息，若為真，則產生新的MQ訊息，並以原始訊息中的會員保留欄位值作為新訊息的CorrelationId。
     * 
     * @param mqQueueManager
     * @param msg
     *            原始訊息
     * @throws GatewayException
     *             若處理過程中有異常發生時拋出
     */
    private static void putReGeneratedMsg(MQQueueManager mqQueueManager, String msg) throws GatewayException {

        byte[] responseData;
        byte[] corrleationId = null;
        if (!MessageUtils.nil(msg)) {
            // 取得原始訊息中交易代碼值，並判斷是否為查詢類交易。若為真，重新組成回覆訊息。
            if (isnumeric(msg.substring(21, 25)) || !isConnService(msg.substring(21, 25))) {
                try {
                    responseData = msg.getBytes(instance.getEncoding());
                    corrleationId = MessageUtils.subArray(responseData, 25, 20);
                    mqReqChannel.send(mqQueueManager, msg, corrleationId, corrleationId, "System");
                    if (ContextLoader.getGatewayContext().isLogFlag())
                        GatewayContext.logger.debug(String.format("[MsgId_%s] [Dispatch putReGeneratedMsg] send msg data to %s Queue, responseData: %s.", BOTGatewayServiceUtil.msgId2Hex(corrleationId), instance.getOutboundQueue(), responseData));
                } catch (UnsupportedEncodingException e) {
                    throw new GatewayException("MG997", new String[] { e.getMessage() });
                }
            } else {
                byte[] msgId = MessageUtils.getMessageId(20).getBytes();
                mqReqChannel.send(mqQueueManager, msg, msgId, "System");
                if (ContextLoader.getGatewayContext().isLogFlag())
                    GatewayContext.logger.debug(String.format("[MsgId_%s] [Dispatch putReGeneratedMsg] send msg data to %s Queue: %s.",BOTGatewayServiceUtil.msgId2Hex(msgId), instance.getOutboundQueue(), msg));
            }

            corrleationId = null;
            responseData = null;
        }

    }

    /**
     * 判斷交易代碼欄位的值是否正確
     * 
     * @param input
     *            交易代碼欄位的值
     * @return boolean
     */
    private static boolean isnumeric(String input) {
        boolean flag = false;

        try {
            Integer.parseInt(input);
            flag = true;
        } catch (Exception ex) {
            if (logger.isDebugEnabled()) {
                logger.debug(ex.getMessage());
            }
        }

        return flag;
    }

    /**
     * 判斷交易代碼欄位的值是否為通訊類交易代碼：T001、S001、S005
     * 
     * @param input
     *            交易代碼欄位的值
     * @return boolean
     */
    private static boolean isConnService(String input) {
        boolean flag = false;

        if ("S001".equals(input) || "S005".equals(input) || "T001".equals(input))
            flag = true;

        return flag;
    }
}
