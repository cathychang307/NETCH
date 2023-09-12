package com.bot.cqs.gateway.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * 此類別為訊息閘道異常類別，透過<code>classpath:/i18n/gateway/GatewayMessage.properties</code>取得正確的錯誤訊息。
 *
 * @author Jeff Tseng
 * @since 1.0 2007/08/31
 */
public class GatewayException extends Exception {

    private static final long serialVersionUID = -3788651072047288469L;

    protected static final Logger logger = LoggerFactory.getLogger(GatewayException.class);

    /**
     * 錯誤代碼
     */
    private String errorCode;
    /**
     * 錯誤訊息
     */
    private String errorMessage;
    /**
     * 預設Locale為zh_TW
     */
    private static final Locale DEFAULT_LOCALE = new Locale("zh", "TW");
    /**
     * 票交所錯誤訊息對應表
     */
    private static ResourceBundle MESSAGE_SOURCE = null;
    /**
     * 初始化票交所錯誤訊息對應表
     */
    static {
        try {
            MESSAGE_SOURCE = ResourceBundle.getBundle("/i18n/gateway/GatewayMessage", Locale.TAIWAN);
        } catch (Exception ex) {
            if (logger.isDebugEnabled()) {
                logger.debug(ex.getMessage());
            }
        }
    }

    /**
     * 以錯誤代碼建構<code>GatewayException</code>
     */
    public GatewayException(String errorCode) {
        this(errorCode, null, DEFAULT_LOCALE);
    }

    /**
     * 以錯誤代碼以及訊息所需參數建構<code>GatewayException</code>
     */
    public GatewayException(String errorCode, Object[] args) {
        this(errorCode, args, DEFAULT_LOCALE);
    }

    /**
     * 以錯誤代碼以及指定的Locale建構<code>GatewayException</code>
     */
    public GatewayException(String errorCode, Locale locale) {
        this(errorCode, null, locale);
    }

    /**
     * 以錯誤代碼、訊息所需參數以及指定的Locale建構<code>GatewayException</code>
     */
    public GatewayException(String errorCode, Object[] args, Locale locale) {
        super(errorCode + " : " + MessageFormat.format(MESSAGE_SOURCE.getString(errorCode), args));
        this.errorCode = errorCode;
        this.errorMessage = MessageFormat.format(MESSAGE_SOURCE.getString(errorCode), args);
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
