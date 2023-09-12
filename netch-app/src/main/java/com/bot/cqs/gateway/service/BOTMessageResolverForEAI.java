package com.bot.cqs.gateway.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.GregorianCalendar;
import java.util.Map;

import com.bot.cqs.gateway.util.BOTGatewayServiceUtil;
import com.bot.cqs.query.persistence.QueryBank;
import com.bot.cqs.query.service.QueryBankManager;
import com.iisigroup.cap.utils.CapAppContext;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XPP3Reader;
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
import com.bot.cqs.gateway.persistence.InquiryLog;
import com.ibm.mq.MQQueueManager;
import com.iisigroup.cap.utils.CapString;

import org.springframework.beans.factory.annotation.Autowired;
import tw.com.iisi.common.message.format.util.MessageUtils;

/**
 * 此類別主要用於接收EAI系統傳送過來的請求電文（XML格式），並將電文轉換成票交所的查詢電文。 傳送後等候回傳結果，並將結果以EAI定義的回覆格式回覆給EAI處理。
 *
 * @author Jeff Tseng
 * @see MQInstance
 * @see MQRequestChannelHandler
 * @see MQResponseChannelHandler
 * @see "IBM MQ 開發指南"
 * @since 1.0 2007/08/31
 */
public class BOTMessageResolverForEAI {

    protected static final Logger logger = LoggerFactory.getLogger(BOTMessageResolverForEAI.class);
    private static XPP3Reader reader = new XPP3Reader();
    private static MQRequestChannelHandler<String, byte[], String> mqReqChannel = null;
    private static MQResponseChannelHandler<?, byte[], String, Map> mqRespChannel = null;
    private static MQInstance instance = null;
    @Autowired
    private static QueryBankManager queryBankManager = null;

    static {
        ContextLoader.init();
        mqReqChannel = new BOTMQRequestChannelHandler();
        mqRespChannel = new BOTMQResponseChannelHandler();
        queryBankManager = (QueryBankManager) CapAppContext.getBean("queryBankManagerImpl");
        queryBankManager.reload();

        // 通道處理程式的設定以META-INF/GatewayContext.xml中的<code>/gateway_context/ibm_websphere_mq/instance[@id='EAI']</code>元素為依據。
        mqReqChannel.setInstanceId("EAI");
        mqRespChannel.setInstanceId("EAI");
    }

    /**
     * 透過IBM MQ API，建立MQ連結，並開啟GatewayContext中instance中各Queue Name屬性指向的Queue，接收於Queue中的訊息，並以訊息中的CorrelationId比對是否為正確接收的訊息。
     * @param botMQEAIQueueManager
     * @param botMQTCHQueueManager
     */
    public static void resolve(MQQueueManager botMQEAIQueueManager, MQQueueManager botMQTCHQueueManager) {
        Map request = null;
        String requestFromEAI = null;
        String errorMsg = null;
        InquiryLog inquiryLog = null;
        String replyToQueueName = "";
        try {
            instance = IBMWebSphereMQUtils.EAIInstance;
            request = (Map) mqRespChannel.receive(botMQEAIQueueManager, "EAI");//接收EAI
            if (request != null && request.get("replyToQueueName") != null) {
                replyToQueueName = (String) request.get("replyToQueueName");
            }
            if (request == null) {
            } else if (request != null && request.get("data") == null) {
                errorHandle(botMQEAIQueueManager, "MG991", (MessageUtils.nil(errorMsg) ? "票信查詢系統無回應" : errorMsg), (request.get("corrId") != null ? (byte[]) request.get("corrId") : null), replyToQueueName);
            } else if (request != null && request.get("data") != null) {
                if (ContextLoader.getGatewayContext().isLogFlag()) {
                    GatewayContext.logger.debug(String.format("[EAI resolve] Receive EAI %s Queue data: %s", instance.getInboundQueue(), (request.get("data") != null ? request.get("data") : "")));
                }
                if (request.get("putDateTime") != null) {
                    GregorianCalendar gregorianCalendar = (GregorianCalendar) request.get("putDateTime");
                    boolean isTimeout = timeover(gregorianCalendar, 3);
                    if (isTimeout) {
                        if (ContextLoader.getGatewayContext().isLogFlag()) {
                            GatewayContext.logger
                                    .debug(String.format("[CorrId_%s] [EAI resolve] This message is timeover and ignore it.", BOTGatewayServiceUtil.msgId2Hex((byte[]) request.get("corrId"))));
                        }
                        return;
                    }
                }
                requestFromEAI = (String) request.get("data");
                inquiryLog = parseRequest(requestFromEAI);
                
//                inquiryLog.setCorrId()
//                inquiryLog.set();
                try {

                    String inquiryTxCode = inquiryLog.getInquiryTxCode();
                    boolean isOBU = false;
                    if (!CapString.isEmpty(inquiryTxCode) && inquiryTxCode.matches("4132|4133|4135|4136"))
                        isOBU = true;

                    BOTGatewayService.enquire(botMQTCHQueueManager, inquiryLog, isOBU);// 送票交
                    
                    if (ContextLoader.getGatewayContext().isLogFlag()) {
                        GatewayContext.logger.debug(String
                                .format("[CorrId_%s] [EAI resolve] Enquire TCH inquiryTxCode: %s, response: %s", BOTGatewayServiceUtil.msgId2Hex((byte[]) request.get("corrId")), inquiryTxCode,
                                        (inquiryLog.getInquiryResponse() != null ? formatResponse(inquiryLog.getInquiryResponse()) : "")));
                    }
                    if (inquiryLog != null && inquiryLog.getInquiryResponse() != null) {// 回覆
                        byte[] corrIdByte = (byte[]) request.get("corrId");
                        try {
                            mqReqChannel.send(botMQEAIQueueManager, formatResponse(inquiryLog.getInquiryResponse(), ""), corrIdByte, corrIdByte, replyToQueueName, "EAI");// 轉EAI格式送EAI
                            if (ContextLoader.getGatewayContext().isLogFlag()) {
                                GatewayContext.logger.debug(String
                                        .format("[CorrId_%s] [EAI resolve] Reply to EAI Queue: %s", BOTGatewayServiceUtil.msgId2Hex((byte[]) request.get("corrId")), replyToQueueName));
                            }
                        } catch (GatewayException e) {
                            errorHandle(botMQEAIQueueManager, e.getErrorCode(), "票信查詢系統回傳錯誤，" + e.getMessage(), corrIdByte, replyToQueueName);
                        }
                    } else {
                        errorHandle(botMQEAIQueueManager, "MG991", "票信查詢系統無回應", (request.get("corrId") != null ? (byte[]) request.get("corrId") : null), replyToQueueName);
                    }
                } catch (GatewayException e) {
                    errorHandle(botMQEAIQueueManager, e.getErrorCode(), "enquire 票信查詢系統回傳錯誤，" + e.getMessage(), (request.get("corrId") != null ? (byte[]) request.get("corrId") : null), replyToQueueName);
                } catch (Exception e) {
                    errorHandle(botMQEAIQueueManager, "MG999", "enquire 票信查詢系統回傳錯誤，" + e.getMessage(), (request.get("corrId") != null ? (byte[]) request.get("corrId") : null), replyToQueueName);
                }
            }
        } catch (GatewayException e) {
            if (!"MG933".equals(e.getErrorCode())) {
                errorMsg = e.getMessage();
                errorHandle(botMQEAIQueueManager, e.getErrorCode(), "票信查詢系統回傳錯誤，" + errorMsg, (request != null && request.get("corrId") != null ? (byte[]) request.get("corrId") : null), replyToQueueName);
            }
        } catch (Exception e) {
            if (logger.isDebugEnabled()) {
                logger.debug("EAI Resolve Exception:" + e.getMessage());
            }
        }
    }

    private static boolean timeover(GregorianCalendar putDateTime, int timeout) {
        boolean flag = false;
        if (System.currentTimeMillis() - putDateTime.getTimeInMillis() > 0 && (System.currentTimeMillis() - putDateTime.getTimeInMillis()) / (1000 * 60 * timeout) > 0) {
            flag = true;
        }
        return flag;
    }

    /**
     * 將EAI傳送過來的請求電文解析並組成InquiryLog物件。
     * 
     * @param requestXML
     *            EAI傳送過來的請求電文
     * @return InquiryLog物件
     */
    private static InquiryLog parseRequest(String requestXML) {
        XPP3Reader reader = new XPP3Reader();
        ByteArrayInputStream bais = null;
        InquiryLog inquiryLog = null;
        Document doc = null;
        String inquiryEndTag = "</inquiry>";

        if (requestXML != null && requestXML.contains(inquiryEndTag.substring(0, inquiryEndTag.length()))) {
            if (ContextLoader.getGatewayContext().isLogFlag())
                GatewayContext.logger.info("EAI查詢請求XML：" + requestXML);

            instance = (MQInstance) ContextLoader.getGatewayContext().getIbmWebSphereMQ().getInstances().get("EAI");
            requestXML = requestXML.substring(0, requestXML.indexOf(inquiryEndTag) + inquiryEndTag.length());

            inquiryLog = new InquiryLog();
            try {
                bais = new ByteArrayInputStream(requestXML.getBytes(instance.getEncoding()));
                doc = reader.read(bais);

                inquiryLog.setInquiryAccount(doc.selectSingleNode("/inquiry/header/account").getStringValue());
                inquiryLog.setInquiryQryBankId(doc.selectSingleNode("/inquiry/header/bank_id").getStringValue());
                inquiryLog.setInquiryDate(doc.selectSingleNode("/inquiry/header/trans_date").getStringValue());
                inquiryLog.setInquiryTime(doc.selectSingleNode("/inquiry/header/trans_time").getStringValue());
                inquiryLog.setInquiryTxCode(doc.selectSingleNode("/inquiry/header/trans_code").getStringValue());
                QueryBank queryBank = queryBankManager.find(inquiryLog.getInquiryQryBankId());
                if (queryBank != null) {
                    inquiryLog.setInquiryChargeBankId(queryBank.getChargeBankId() != null ? queryBank.getChargeBankId() : "");
                    inquiryLog.setInquiryTchId(queryBank.getTchId() != null ? queryBank.getTchId() : "");
                }

                inquiryLog.setInquiryId(doc.selectSingleNode("/inquiry/body/id").getStringValue());
                inquiryLog.setInquiryBizId(doc.selectSingleNode("/inquiry/body/biz_id").getStringValue());
                inquiryLog.setInquiryName(doc.selectSingleNode("/inquiry/body/name").getStringValue());
                inquiryLog.setInquiryBankCode(doc.selectSingleNode("/inquiry/body/bank_no").getStringValue());
                inquiryLog.setInquiryBankAccount(doc.selectSingleNode("/inquiry/body/account_no").getStringValue());

                inquiryLog.setInquiryResponseFormat("X");
            } catch (Exception e) {
                if (logger.isDebugEnabled()) {
                    logger.debug("parseRequest:" + e.getMessage());
                }
            } finally {
                if (doc != null)
                    doc.clearContent();
                doc = null;
                if (bais != null)
                    try {
                        bais.close();
                    } catch (IOException e) {
                        ;
                    }
                bais = null;
            }
        }

        return inquiryLog;
    }
    /**
     * 將票交所回覆之查詢結果（XML）套進回傳EAI的XML訊息格式中。
     *
     * @param result
     *            票交所回覆之XML格式結果
     * @param errorMsg
     *            票交所回覆errorMsg
     * @return 回傳EAI的XML回覆字串
     */
    private static String formatResponse(String result, String errorMsg) {
        boolean isMQError2009 = !CapString.isEmpty(errorMsg) && errorMsg.contains("MQJE001") && errorMsg.contains("2009");
        boolean isMQError2033 = !CapString.isEmpty(errorMsg) && errorMsg.contains("MQJE001") && errorMsg.contains("2033");
        if (!isMQError2009 && !isMQError2033) {
            if (ContextLoader.getGatewayContext().isLogFlag()) {
                try {
                    GatewayContext.logger.debug(String.format("[EAI formatResponse] 將票交所回覆XML結果轉為回傳給EAI XML格式: %s", new String(result.getBytes("UTF-8"))));
                } catch (UnsupportedEncodingException e) {
                    if (logger.isDebugEnabled()) {
                        logger.debug(e.getMessage());
                    }
                }
            }
        }
        return formatResponse(result);
    }


    /**
     * 將票交所回覆之查詢結果（XML）套進回傳EAI的XML訊息格式中。
     * 
     * @param result
     *            票交所回覆之XML格式結果
     * @return 回傳EAI的XML回覆字串
     */
    private static String formatResponse(String result) {
        ByteArrayInputStream bais = null;

        SAXReader reader = new SAXReader();
        Document document = null;
        Document resultDoc = null;

        if (result != null) {
            resultDoc = DocumentHelper.createDocument();
            resultDoc.add(DocumentHelper.createElement("result-xml"));
            try {
                bais = new ByteArrayInputStream(result.getBytes("UTF-8"));
                document = reader.read(bais);
                resultDoc.getRootElement().add(document.getRootElement().createCopy());
            } catch (UnsupportedEncodingException e) {
                if (logger.isDebugEnabled()) {
                    logger.debug(e.getMessage());
                }
            } catch (DocumentException e) {
                if (logger.isDebugEnabled()) {
                    logger.debug(e.getMessage());
                }
            } finally {
                if (document != null)
                    document.clearContent();
                document = null;

                if (bais != null) {
                    try {
                        bais.close();
                    } catch (IOException e) {
                        ;
                    }
                    bais = null;
                }
            }
            if (resultDoc != null) {
                return resultDoc.asXML();
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * 若處理過程中有任何錯誤狀況發生，則必須將錯誤狀況組成XML字傳回傳給EAI
     * 
     * @param errorCode
     *            錯誤代碼
     * @param errorMsg
     *            錯誤訊息
     * @param corrId
     *            MQ訊息之CorrelationId
     * @param replyToQueueName
     *            MQ訊息之replyToQueueName
     */
    private static void errorHandle(MQQueueManager botMQEAIQueueManager, String errorCode, String errorMsg, byte[] corrId, String replyToQueueName) {
        StringBuffer buf = null;
        boolean isMQError2009 = false;
        boolean isMQError2033 = false;
        try {
            isMQError2009 = errorMsg.contains("MQJE001") && errorMsg.contains("2009");
            isMQError2033 = errorMsg.contains("MQJE001") && errorMsg.contains("2033");
            buf = new StringBuffer();
            buf.append("<error-code>").append(errorCode).append("</error-code>");
            //buf.append("<error-msg>").append("查詢失敗，錯誤原因：").append(errorMsg).append("</error-msg>");
            if (!isMQError2009 && !isMQError2033) {
                if (ContextLoader.getGatewayContext().isLogFlag())
                    GatewayContext.logger.debug(String.format("[CorrId_%s] [EAI errorHandle] error-code: %s.", BOTGatewayServiceUtil.msgId2Hex(corrId), errorCode));
            }
            mqReqChannel.send(botMQEAIQueueManager, formatResponse(buf.toString(), errorMsg), corrId, corrId, replyToQueueName, "EAI");
        } catch (GatewayException e) {
            isMQError2009 = e.getErrorMessage().contains("MQJE001") && e.getErrorMessage().contains("2009");
            isMQError2033 = e.getErrorMessage().contains("MQJE001") && e.getErrorMessage().contains("2033");
            if (!isMQError2009 && !isMQError2033) {
                if (ContextLoader.getGatewayContext().isLogFlag()) {
                    GatewayContext.logger.error(String.format("[EAI errorHandle] error exception: %s", e));
                }
            }
        }
    }
}
