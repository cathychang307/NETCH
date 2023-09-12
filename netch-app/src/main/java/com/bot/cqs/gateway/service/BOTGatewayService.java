package com.bot.cqs.gateway.service;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bot.cqs.gateway.context.ContextLoader;
import com.bot.cqs.gateway.context.GatewayContext;
import com.bot.cqs.gateway.handler.channel.MQRequestChannelHandler;
import com.bot.cqs.gateway.handler.channel.MQResponseChannelHandler;
import com.bot.cqs.gateway.handler.channel.impl.BOTMQRequestChannelHandler;
import com.bot.cqs.gateway.handler.channel.impl.BOTMQResponseChannelHandler;
import com.bot.cqs.gateway.handler.message.RequestMessageHandler;
import com.bot.cqs.gateway.handler.message.ResponseMessageHandler;
import com.bot.cqs.gateway.handler.message.impl.BOTEnquireRequestMessageHandler;
import com.bot.cqs.gateway.handler.message.impl.BOTEnquireResponseMessageHandler;
import com.bot.cqs.gateway.handler.message.impl.BOTLoginOrLogoffRequestMessageHandler;
import com.bot.cqs.gateway.handler.message.impl.BOTLoginOrLogoffResponseMessageHandler;
import com.bot.cqs.gateway.persistence.Cache;
import com.bot.cqs.gateway.persistence.InquiryLog;
import com.bot.cqs.gateway.service.message.ConnectMsg;
import com.bot.cqs.gateway.util.BOTGatewayServiceUtil;
import com.bot.cqs.gateway.util.log.BOTCacheWorker;
import com.bot.cqs.gateway.util.log.BOTInquiryLogWorker;
import com.bot.cqs.query.util.factory.ApplicationParameterFactory;
import com.ibm.mq.MQQueueManager;

import tw.com.iisi.common.message.format.util.MessageUtils;

/**
 * 查詢服務主程式，提供API使前端或EAI執行票信查詢
 * 
 * @author Jeff Tseng
 * @see "台灣票據票交所MQ票信查詢系統會員機構端規格說明手冊"
 * @since 1.0 2007/08/31
 */
public class BOTGatewayService {

    protected static final Logger logger = LoggerFactory.getLogger(BOTGatewayService.class);

    /**
     * 查詢電文處理程式
     */
    private static RequestMessageHandler reqEnqiureMsgHandler = null;
    /**
     * 查詢回覆電文處理程式
     */
    private static ResponseMessageHandler respEnqiureMsgHandler = null;
    /**
     * 登出入電文處理程式
     */
    private static RequestMessageHandler<?, ConnectMsg> reqSOMsgHandler = null;
    /**
     * 登出入回覆電文處理程式
     */
    private static ResponseMessageHandler<?, String> respSOMsgHandler = null;
    /**
     * 查詢電文處理通道程式
     */
    private static MQRequestChannelHandler<String, byte[], String> mqReqChannel = null;
    /**
     * 查詢回覆電文處理通道程式
     */
    private static MQResponseChannelHandler<?, byte[], String, Map> mqRespChannel = null;

    // 初始化每個處理程式，將實際的實作程式指定給處理程式介面上。此部分之後可以用Spring的介面替代。
    static {
        reqSOMsgHandler = new BOTLoginOrLogoffRequestMessageHandler();
        respSOMsgHandler = new BOTLoginOrLogoffResponseMessageHandler();
        reqEnqiureMsgHandler = new BOTEnquireRequestMessageHandler();
        respEnqiureMsgHandler = new BOTEnquireResponseMessageHandler();
        mqReqChannel = new BOTMQRequestChannelHandler();
        mqRespChannel = new BOTMQResponseChannelHandler();

        // 通道處理程式的設定以gateway/GatewayContext.xml中的<code>/gateway_context/ibm_websphere_mq/instance[@id='TCH']</code>元素為依據。
        mqReqChannel.setInstanceId("TCH");
        mqRespChannel.setInstanceId("TCH");
    }

    /**
     * 查詢服務的主要方法，透過InquiryLog物件，系統取出查詢條件後，會判斷是否可由Cache取得資料。 若Cache內無資料，則會主動向票交提出資料查詢。若查詢有錯誤，則拋出異常。 若無異常發生，表示成功，前端系統可由InquiryLog物件取出回覆資料以及查詢代碼等。
     * 
     * @param mqQueueManager
     * @param inquiryLog
     *            查詢條件，以及回覆資料
     * @throws Exception
     *             查詢異常，包含票交回傳錯誤代碼。
     */
    @SuppressWarnings("unchecked")
    public static void enquire(MQQueueManager mqQueueManager, InquiryLog inquiryLog) throws Exception {
        changeFieldValue(inquiryLog, false);

        Cache cache = null;
        if (ApplicationParameterFactory.newInstance().getQueryCacheInterval() != 0) {
            cache = BOTGatewayServiceUtil.checkAndGetFromCache(inquiryLog);
        }

        String inquiryResponseType = inquiryLog.getInquiryResponseFormat();
        inquiryLog.setInquiryResponseFormat(" ");

        InquiryLog result = null;
        boolean xFlag = false;
        boolean qFlag = false;
        boolean hFlag = false;

        String msgId = null;
        if (cache == null) {
            try {
                msgId = MessageUtils.getMessageId(20);

                inquiryLog.setSeqNo(MessageUtils.getMessageId(6));
                inquiryLog.setInquiryName(MessageUtils.helfToFull(MessageUtils.align(inquiryLog.getInquiryName(), " ", "L", 20)));
                inquiryLog.setMsgId(msgId);
                inquiryLog.setMsgFid("InquiryRequest");
                mqReqChannel.send(mqQueueManager, (String) reqEnqiureMsgHandler.format(inquiryLog, true), msgId.getBytes(), inquiryLog.getInquiryAccount());
                do {
                    result = (InquiryLog) respEnqiureMsgHandler.parse(mqRespChannel.receive(mqQueueManager, msgId.getBytes(), inquiryLog.getInquiryAccount()), msgId, "InquiryResponse",
                            inquiryLog.getInquiryAccount());
                } while (!inquiryResponseType.equalsIgnoreCase(result.getInquiryResponseFormat()) && !(qFlag && hFlag && xFlag));

                if (result != null && inquiryResponseType.equalsIgnoreCase(result.getInquiryResponseFormat())) {
                    if (!"D0000".equals(result.getInquiryErrorCode())) {
                        throw new GatewayException(result.getInquiryErrorCode());
                    } else {
                        inquiryLog.setInquiryResponseFormat(result.getInquiryResponseFormat());
                        String inquiryResponse = result.getInquiryResponse();
                        // 票交回覆訊息Big5編碼轉成畫面編碼UTF-8
                        byte[] big5 = inquiryResponse.getBytes("Big5");// unicode 轉成 Big5 編碼
                        byte[] utf8 = new String(big5, "Big5").getBytes("UTF-8");// Big5 編碼 轉回 unicode 再轉成 UTF-8 編碼
                        String utf8tr = new String(utf8, "UTF-8");// UTF-8 編碼 轉回 unicode
                        inquiryLog.setInquiryResponse(utf8tr);
                        if (ContextLoader.getGatewayContext().isLogFlag())
                            GatewayContext.logger.debug(String.format("[Enquire] 票交所回覆訊息: %s", utf8tr));

                        if (ApplicationParameterFactory.newInstance().getQueryCacheInterval() != 0) {
                            try {
                                BOTCacheWorker.cache(inquiryLog);
                            } catch (Exception ex) {
                                if (logger.isDebugEnabled()) {
                                    logger.debug(ex.getMessage());
                                }
                            }
                        }

                    }
                } else {
                    changeFieldValue(inquiryLog, true);
                    inquiryLog.setInquiryErrorCode("MG998");
                    log(inquiryLog);
                    throw new GatewayException("MG998");
                }
            } catch (GatewayException ex) {
                changeFieldValue(inquiryLog, true);
                inquiryLog.setInquiryErrorCode(ex.getErrorCode());
                log(inquiryLog);
                throw ex;
            } catch (Exception ex) {
                changeFieldValue(inquiryLog, true);
                inquiryLog.setInquiryErrorCode("MG999");
                log(inquiryLog);
                throw new GatewayException("MG999", new String[] { ex.getMessage() });
            }
            changeFieldValue(inquiryLog, true);
            inquiryLog.setInquiryCacheFlag(false);
            log(inquiryLog);

        } else {
            changeFieldValue(inquiryLog, true);
            inquiryLog.setInquiryCacheFlag(true);
            inquiryLog.setInquiryResponse(cache.getInquiryResponse());
            inquiryLog.setInquiryResponseFormat(cache.getInquiryResponseFormat());
            log(inquiryLog);
        }
    }

    /**
     * 查詢服務的主要方法，透過InquiryLog物件，系統取出查詢條件後，會判斷是否可由Cache取得資料。 若Cache內無資料，則會主動向票交提出資料查詢。若查詢有錯誤，則拋出異常。 若無異常發生，表示成功，前端系統可由InquiryLog物件取出回覆資料以及查詢代碼等。
     * 
     * @param mqQueueManager
     * @param inquiryLog
     *            查詢條件，以及回覆資料
     * @param isOBU
     *            true: 表示查詢OBU; false: 同enquire(InquiryLog inquiryLog) API
     * @throws Exception
     *             查詢異常，包含票交回傳錯誤代碼。
     */
    @SuppressWarnings("unchecked")
    public static void enquire(MQQueueManager mqQueueManager, InquiryLog inquiryLog, boolean isOBU) throws Exception {
        changeFieldValue(inquiryLog, false);

        Cache cache = null;
        if (ApplicationParameterFactory.newInstance().getQueryCacheInterval() != 0) {
            cache = BOTGatewayServiceUtil.checkAndGetFromCache(inquiryLog);
        }

        String inquiryResponseType = inquiryLog.getInquiryResponseFormat();
        inquiryLog.setInquiryResponseFormat(" ");

        InquiryLog result = null;
        boolean xFlag = false;
        boolean qFlag = false;
        boolean hFlag = false;

        String msgId = null;
        if (cache == null) {
            try {
                msgId = MessageUtils.getMessageId(20);
                // String msgId = "00725115020982356350";

                // inquiryLog.setSeqNo("106135");
                inquiryLog.setSeqNo(MessageUtils.getMessageId(6));
                int inquiryNameLength = 20;
                if (isOBU)
                    inquiryNameLength = 60;
                inquiryLog.setInquiryName(helfToFull(MessageUtils.align(inquiryLog.getInquiryName(), " ", "L", inquiryNameLength), inquiryNameLength));
                inquiryLog.setMsgId(msgId);
                inquiryLog.setMsgFid((isOBU ? "OBU" : "") + "InquiryRequest");
                if (ContextLoader.getGatewayContext().isLogFlag())
                    GatewayContext.logger.debug(String.format("[MsgId_%s] [Enquire] 傳送給票交所資訊 inquiryName: %s, inquiryAccount: %s", BOTGatewayServiceUtil.msgId2Hex(msgId.getBytes()), inquiryLog.getInquiryName(), inquiryLog.getInquiryAccount()));
                mqReqChannel.send(mqQueueManager, (String) reqEnqiureMsgHandler.format(inquiryLog, true), msgId.getBytes(), inquiryLog.getInquiryAccount());
                do {
                    result = (InquiryLog) respEnqiureMsgHandler.parse(mqRespChannel.receive(mqQueueManager, msgId.getBytes(), msgId.getBytes(), inquiryLog.getInquiryAccount()), msgId,
                            (isOBU ? "OBU" : "") + "InquiryResponse", inquiryLog.getInquiryAccount());
                    if (ContextLoader.getGatewayContext().isLogFlag())
                        GatewayContext.logger.debug(String.format("[MsgId_%s] [Enquire] 接收票交所回覆結果: %s, inquiryErrorCode: %s, inquiryAccount: %s.", BOTGatewayServiceUtil.msgId2Hex(msgId.getBytes()), result, result.getInquiryErrorCode(), inquiryLog.getInquiryAccount()));
                } while (!inquiryResponseType.equalsIgnoreCase(result.getInquiryResponseFormat()) && !(qFlag && hFlag && xFlag));

                if (result != null && inquiryResponseType.equalsIgnoreCase(result.getInquiryResponseFormat())) {
                    if (!"D0000".equals(result.getInquiryErrorCode())) {
                        throw new GatewayException(result.getInquiryErrorCode());
                    } else {
                        inquiryLog.setInquiryResponseFormat(result.getInquiryResponseFormat());
                        String inquiryResponse = result.getInquiryResponse();
                        byte[] big5 = inquiryResponse.getBytes("Big5");// unicode 轉成 Big5 編碼
                        byte[] utf8 = new String(big5, "Big5").getBytes("UTF-8");// Big5 編碼 轉回 unicode 再轉成 UTF-8 編碼
                        String utf8tr = new String(utf8, "UTF-8");// UTF-8 編碼 轉回 unicode
                        inquiryLog.setInquiryResponse(utf8tr);
                        if (ContextLoader.getGatewayContext().isLogFlag())
                            GatewayContext.logger.debug(String.format("[MsgId_%s] [Enquire] 票交所回覆訊息: %s", BOTGatewayServiceUtil.msgId2Hex(msgId.getBytes()), utf8tr));

                        if (ApplicationParameterFactory.newInstance().getQueryCacheInterval() != 0) {
                            try {
                                BOTCacheWorker.cache(inquiryLog);
                            } catch (Exception ex) {
                                if (logger.isDebugEnabled()) {
                                    logger.debug(ex.getMessage());
                                }
                            }
                        }

                    }
                } else {
                    changeFieldValue(inquiryLog, true);
                    inquiryLog.setInquiryErrorCode("MG998");
                    log(inquiryLog);
                    throw new GatewayException("MG998");
                }
            } catch (GatewayException ex) {
                changeFieldValue(inquiryLog, true);
                inquiryLog.setInquiryErrorCode(ex.getErrorCode());
                log(inquiryLog);
                throw ex;
            } catch (Exception ex) {
                logger.debug(ex.getMessage());
                changeFieldValue(inquiryLog, true);
                inquiryLog.setInquiryErrorCode("MG999");
                log(inquiryLog);
                throw new GatewayException("MG999", new String[] { ex.getMessage() });
            }
            changeFieldValue(inquiryLog, true);
            inquiryLog.setInquiryCacheFlag(false);
            log(inquiryLog);
        } else {
            changeFieldValue(inquiryLog, true);
            inquiryLog.setInquiryCacheFlag(true);
            inquiryLog.setInquiryResponse(cache.getInquiryResponse());
            inquiryLog.setInquiryResponseFormat(cache.getInquiryResponseFormat());
            log(inquiryLog);
        }
    }

    /**
     * 系統服務的票交所系統簽入方法。若有錯誤，則拋出異常。
     * 
     * @param mqQueueManager
     * @throws Exception
     *             簽入異常，包含票交回傳錯誤代碼。
     */
    public static void logon(MQQueueManager mqQueueManager) throws Exception {
        if (ContextLoader.getGatewayContext().isLogFlag())
            GatewayContext.logger.info("執行系統登入(LOGON:L001)");
        doService(mqQueueManager, "L001");
        if (ContextLoader.getGatewayContext().isLogFlag())
            GatewayContext.logger.info("系統登入成功");
    }

    /**
     * 系統服務的票交所系統簽出方法。若有錯誤，則拋出異常。
     * 
     * @param mqQueueManager
     * @throws Exception
     *             簽出異常，包含票交回傳錯誤代碼。
     */
    public static void logoff(MQQueueManager mqQueueManager) throws Exception {
        if (ContextLoader.getGatewayContext().isLogFlag())
            GatewayContext.logger.info("執行系統登出(LOGOFF:L003)");
        doService(mqQueueManager, "L003");
        if (ContextLoader.getGatewayContext().isLogFlag())
            GatewayContext.logger.info("系統登出成功");
    }

    /**
     * 依據交易代碼實際進行對票交所請求。主要用於登出、登入作業上。
     * 
     * @param mqQueueManager
     * @param trxCode
     *            交易代碼
     * @throws Exception
     *             執行服務異常，包含票交回傳錯誤代碼。
     */
    private static void doService(MQQueueManager mqQueueManager, String trxCode) throws Exception {
        ConnectMsg msg = null;

        String msgId = null;

        try {
            msg = new ConnectMsg();
            msgId = MessageUtils.getMessageId(20);

            msg.setAppropriative(new String(msgId));
            msg.setSequenceNo(MessageUtils.getMessageId(6));
            msg.setTrxCode(trxCode);
            msg.setTrxDate(MessageUtils.getDay());
            msg.setTrxTime(MessageUtils.getTime());
            //傳送
            mqReqChannel.send(mqQueueManager, (String) reqSOMsgHandler.format(msg, true), msgId.getBytes(), "System");
            if (ContextLoader.getGatewayContext().isLogFlag())
                GatewayContext.logger.debug(String.format("[MsgId_%s] [doService] MQ send trxCode: %s", BOTGatewayServiceUtil.msgId2Hex(msgId.getBytes()), trxCode));
            //接收
            ConnectMsg result = (ConnectMsg) respSOMsgHandler.parse((String) mqRespChannel.receive(mqQueueManager, msgId.getBytes(), "System"), new String(msgId), msg.getTrxCode(), "System");
            if (ContextLoader.getGatewayContext().isLogFlag())
                GatewayContext.logger.debug(String.format("[MsgId_%s] [doService] MQ receive result: %s", BOTGatewayServiceUtil.msgId2Hex(msgId.getBytes()), result.getErrCode()));
            if (result != null) {
                if (!"D0000".equals(result.getErrCode())) {
                    throw new GatewayException(result.getErrCode());
                }
            }
        } catch (Exception ex) {
            logger.debug(ex.getMessage());
            if (ContextLoader.getGatewayContext().isLogFlag())
                GatewayContext.logger.error("[doService] Exception:" + ex);
            throw ex;
        }
    }

    /**
     * 執行交易日誌紀錄程式
     * 
     * @param inquiryLog
     *            交易日誌物件
     */
    private static void log(InquiryLog inquiryLog) {
        BOTInquiryLogWorker logWorker = new BOTInquiryLogWorker();
        try{
            logWorker.setInquiryLog(inquiryLog);
            logWorker.start();
        } catch(Exception ex) {
            logger.debug(ex.getMessage());
        }
    }

    /**
     * 以個人條件查詢時，因訊息格式定義上，BizId及Id兩欄位位置相反，所以正式處理成電文時，必須先將兩欄位交換才能成功。 而紀錄交易日誌前，必須將兩欄位再對調回來，日誌交易才不會有錯誤。是否對調回來的決定透過recovery旗標決定。
     * 
     * @param inquiryLog
     * @param recovery
     */
    private static void changeFieldValue(InquiryLog inquiryLog, boolean recovery) {
        String id = null;

        if ("4111".equals(inquiryLog.getInquiryTxCode()) || "4114".equals(inquiryLog.getInquiryTxCode()) || "4121".equals(inquiryLog.getInquiryTxCode())
                || "4123".equals(inquiryLog.getInquiryTxCode())) {

            if (recovery) {
                id = inquiryLog.getInquiryBizId();
                inquiryLog.setInquiryBizId(null);
                inquiryLog.setInquiryId(id);
            } else {
                id = inquiryLog.getInquiryId();
                inquiryLog.setInquiryId(null);
                inquiryLog.setInquiryBizId(id);
            }
        }
    }

    private static String helfToFull(String input, int length) {
        String HALF_WIDTH_TABLE = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ ";
        char[] FULL_WIDTH_TABLE = { 65296, 65297, 65298, 65299, 65300, 65301, 65302, 65303, 65304, 65305, 65313, 65314, 65315, 65316, 65317, 65318, 65319, 65320, 65321, 65322, 65323, 65324, 65325,
                65326, 65327, 65328, 65329, 65330, 65331, 65332, 65333, 65334, 65335, 65336, 65337, 65338, '　' };
        char[] tmp = new char[length];
        for (int i = 0; i < length; i++) {
            tmp[i] = '　';
        }
        if (input == null) {
            return new String(tmp);
        }
        char[] inputChar = input.toCharArray();
        for (int i = 0; i < inputChar.length; i++) {
            if (HALF_WIDTH_TABLE.indexOf(inputChar[i]) >= 0) {
                tmp[i] = FULL_WIDTH_TABLE[HALF_WIDTH_TABLE.indexOf(inputChar[i])];
            } else
                tmp[i] = inputChar[i];
        }
        return new String(tmp);
    }

}
