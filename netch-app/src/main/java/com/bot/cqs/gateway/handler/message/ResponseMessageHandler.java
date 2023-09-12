package com.bot.cqs.gateway.handler.message;

import com.bot.cqs.gateway.service.GatewayException;

/**
 * 此界面主要用於將後端傳回的查詢結果轉換成回覆訊息。
 *
 * @author Jeff Tseng
 * @since 1.0 2007/08/31
 */
public interface ResponseMessageHandler<T, S> {

    /**
     * 接受後端回傳之查詢結果，透過某些方式，將結果轉換成回覆訊息。
     * 
     * @param result
     *            後端回傳之結果
     * @param id
     *            訊息格式代碼
     * @param account
     *            查詢者帳號
     * @return 格式化後的查詢結果
     * @throws GatewayException
     *             若處理過程中有異常發生時拋出
     */
    public T parse(S result, S msgId, S id, S account) throws GatewayException;
}
