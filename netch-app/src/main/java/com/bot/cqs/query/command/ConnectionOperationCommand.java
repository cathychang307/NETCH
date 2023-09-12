
package com.bot.cqs.query.command;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.springframework.stereotype.Component;

/**
 * 連業作業 (網頁) 對應的 command object.
 * 
 * @author Damon Lu
 *
 */
@Component
public class ConnectionOperationCommand {

    public static final int ACTION_UNKNOWN = -1;
    public static final int ACTION_LOGON = 0;
    public static final int ACTION_LOGOFF = 1;

    private String inputAction;
    private int action;

    public ConnectionOperationCommand() {

        action = ACTION_UNKNOWN;
    }

    /**
     * 來自網頁的 "inputAction" 欄位
     * 
     * @return
     */
    public String getInputAction() {

        return inputAction;
    }

    /**
     * 將來自網頁的 "inputAction" 欄位寫入, 同時會設定 action 代碼.
     * <p>
     * "LOGON" ==> {@link #ACTION_LOGON}<br>
     * "LOGOFF" ==> {@link #ACTION_LOGOFF}<br>
     * Others ==> {@link #ACTION_UNKNOWN}<br>
     * 
     * @param inputAction
     */
    public void setInputAction(String inputAction) {

        if (inputAction == null) {
            this.inputAction = null;
            action = ACTION_UNKNOWN;
        } else {
            this.inputAction = inputAction.trim().toUpperCase();
        }

        if (inputAction.equals("LOGON"))
            action = ACTION_LOGON;
        else if (inputAction.equals("LOGOFF"))
            action = ACTION_LOGOFF;
        else
            action = ACTION_UNKNOWN;
    }

    /**
     * 回傳 action 代碼
     * 
     * @return ACTION_LOGON, ACTION_LOGOFF or ACTION_UNKNOWN
     * @see #setInputAction(String)
     */
    public int getAction() {

        return action;
    }

    /**
     * actioon 代碼是否有效
     * 
     * @return 代碼為 {@link #ACTION_UNKNOWN} 則為 <code>false</code>, 其餘為 <code>true</code>
     * @see #getAction()
     */
    public boolean isActionValid() {

        return getAction() != ACTION_UNKNOWN;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE, false, false);
    }
}
