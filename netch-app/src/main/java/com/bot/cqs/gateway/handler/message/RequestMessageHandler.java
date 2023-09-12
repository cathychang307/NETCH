package com.bot.cqs.gateway.handler.message;

import com.bot.cqs.gateway.service.GatewayException;

/**
 * 此界面主要用於將輸入的請求參數轉換成請求訊息之用。
 *
 * @author Jeff Tseng
 * @since 1.0 2007/08/31
 */
public interface RequestMessageHandler<T, S> {

    /**
     * 接受使用者輸入之查詢條件轉成請求訊息。
     * 
     * @param queryCommand
     *            使用者輸入之查詢條件
     * @param initQueryFlag
     *            組合之字串是否為主查詢條件
     * @return 格式化後的查詢訊息
     * @throws GatewayException
     *             若處理過程中有異常發生時拋出
     */
    public T format(S queryCommand, boolean initQueryFlag) throws GatewayException;
}
