package com.bot.cqs.gateway.handler.message.impl;

import com.bot.cqs.gateway.handler.message.RequestMessageHandler;
import com.bot.cqs.gateway.service.GatewayException;
import com.bot.cqs.gateway.service.message.ServiceSessionMsg;
import tw.com.iisi.common.message.format.MessageFormatCore;

import java.io.UnsupportedEncodingException;

/**
 * 此類別主要用於將使用者或前端系統輸入的TPing、啟動服務、暫停服務請求參數以及部分分行資訊，透過事先定義的訊息格式檔：messages.xml 將請求參數組裝成串列字串，透過IBM MQ上傳至票交所進行票信資料查詢。
 *
 * @author Jeff Tseng
 * @see ServiceSessionMsg
 * @see MessageFormatCore
 * @since 1.0 2007/08/31
 */
public class BOTConnServiceRequestMessageHandler implements RequestMessageHandler<String, ServiceSessionMsg> {

    private MessageFormatCore core = new MessageFormatCore();

    /**
     * 接受使用者或前端系統輸入之查詢條件，透過查詢種類、查詢方式，透過<code>tw.com.iisi.common.message.format.MessageFormatCore</code>類別， 並藉由<code>messages.xml</code>的設定，將查詢條件轉換為查詢電文。
     * 
     * @param queryCommand
     *            使用者或前端系統輸入之查詢條件
     * @param initQueryFlag
     *            組合之字串是否為主查詢條件
     * @return 格式化後的查詢字串
     * @throws GatewayException
     *             若處理過程中有異常發生時拋出
     */
    public String format(ServiceSessionMsg queryCommand, boolean initQueryFlag) throws GatewayException {
        try {
            return new String(core.transform(queryCommand, queryCommand.getTrxCode()), "Big5");
        } catch (GatewayException e) {
            throw e;
        } catch (UnsupportedEncodingException e) {
            throw new GatewayException("MG997", new String[] { e.getMessage() });
        } catch (Exception e) {
            throw new GatewayException("MG999", new String[] { e.getMessage() });
        }
    }
}