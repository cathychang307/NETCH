package com.bot.cqs.gateway.service.message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * 此類別為票交所錯誤訊息代碼與詳細訊息對應類別。透過錯誤代碼，讀取classpath:/i18n/gateway/TCHErrorMessage.properties檔案，取得詳細訊息內容。
 * 
 * @author Jeff Tseng
 * @since 1.0 2007/08/31
 */
public class TCHErrorMsg {

    protected static final Logger logger = LoggerFactory.getLogger(TCHErrorMsg.class);

    /**
     * 票交所錯誤訊息對應表
     */
    private static ResourceBundle MESSAGE_SOURCE = null;
    /**
     * 初始化票交所錯誤訊息對應表
     */
    static {
        try {
            MESSAGE_SOURCE = ResourceBundle.getBundle("/i18n/gateway/TCHErrorMessage", Locale.TAIWAN);
        } catch (Exception ex) {
            if (logger.isDebugEnabled()) {
                logger.debug(ex.getMessage());
            }
        }
    }

    /**
     * 取得票交所主機錯誤訊息方法。透過傳入的錯誤代碼，可取得中文的錯誤訊息，若找不到對應，則以ZZ999為鍵值，找出回傳無此錯誤代碼，請進一步向票交所詢問的訊息。
     * 
     * @param errorCode
     *            票交所錯誤代碼
     * @return String，繁體中文錯誤訊息
     */
    public static String getErrorMsg(String errorCode) {
        String result = null;
        try {
            result = MESSAGE_SOURCE.getString(errorCode);
        } catch (Exception ex) {
            result = MESSAGE_SOURCE.getString("ZZ999");
        }
        return result;
    }
}
