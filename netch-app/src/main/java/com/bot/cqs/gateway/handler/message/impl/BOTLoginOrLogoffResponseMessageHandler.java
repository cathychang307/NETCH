package com.bot.cqs.gateway.handler.message.impl;

import com.bot.cqs.gateway.handler.message.ResponseMessageHandler;
import com.bot.cqs.gateway.service.GatewayException;
import com.bot.cqs.gateway.service.message.ConnectMsg;
import tw.com.iisi.common.message.format.MessageFormatCore;
import tw.com.iisi.common.message.format.util.MessageUtils;

import java.io.UnsupportedEncodingException;

/**
 * 此類別主要用於將主機透過MQ傳回的字串透過事先定義的訊息格式檔：messages.xml內設定的訊息格式資訊， 轉換成訊息物件。
 *
 * @author Jeff Tseng
 * @see ConnectMsg
 * @see MessageFormatCore
 * @since 1.0 2007/08/31
 */
public class BOTLoginOrLogoffResponseMessageHandler implements ResponseMessageHandler<ConnectMsg, String> {

    private MessageFormatCore core = new MessageFormatCore();

    /**
     * 接受主機回傳之查詢結果字串，透過訊息格式代碼，透過<code>tw.com.iisi.common.message.format.MessageFormatCore</code>類別， 並藉由<code>messages.xml</code>的設定，將回覆電文轉換為查詢物件。
     * 
     * @param result
     *            主機回傳之結果字串
     * @param msgId
     *            MQ訊息代碼
     * @param id
     *            訊息格式代碼
     * @param account
     *            查詢者帳號
     * @return 格式化後的查詢結果物件
     * @throws GatewayException
     *             若處理過程中有異常發生時拋出
     */
    public ConnectMsg parse(String result, String msgId, String id, String account) throws GatewayException {

        byte[] response = null;
        ConnectMsg msgr;

        if (MessageUtils.nil(result))
            throw new GatewayException("MG006");

        try {
            response = result.getBytes("Big5");
        } catch (Exception ex) {
            throw new GatewayException("MG997", new String[] { ex.getMessage() });
        }

        try {
            msgr = ConnectMsg.class.cast(core.transform(response, id, ConnectMsg.class));

            if (!msgId.equals(msgr.getAppropriative()))
                throw new GatewayException("MG010", new String[] { msgId, msgr.getAppropriative() });
        } catch (GatewayException e) {
            throw e;
        } catch (UnsupportedEncodingException e) {
            throw new GatewayException("MG997", new String[] { e.getMessage() });
        } catch (Exception e) {
            throw new GatewayException("MG999", new String[] { e.getMessage() });
        }

        return msgr;
    }
}
