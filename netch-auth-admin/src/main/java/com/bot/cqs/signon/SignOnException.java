
package com.bot.cqs.signon;

import org.springframework.security.core.AuthenticationException;

public class SignOnException extends AuthenticationException {

    private String secondaryStatus;

    private static final long serialVersionUID = 8277974417608227981L;

    public SignOnException(String secondaryStatus) {

        this("", null, secondaryStatus);
    }

    public SignOnException(String message, String secondaryStatus) {

        this(message, null, secondaryStatus);
    }

    public SignOnException(Throwable cause, String secondaryStatus) {

        this("", cause, secondaryStatus);
    }

    public SignOnException(String message, Throwable cause, String secondaryStatus) {

        super(message, cause);
        setSecondaryStatus(secondaryStatus);
    }

    /**
     * 登入失敗的代碼. 這個代碼主要是用來取得 message ( 由 MessagrSource ) 與搭配 primaryStatus ( {@link tw.com.bot.cqs.query.persistence.SessionLog#PRIMARY_STATUS_LOGIN SessionLog.PRIMARY_STATUS_LOGIN } ) 來寫 Log
     * 
     * @return
     */
    public String getSecondaryStatus() {

        return secondaryStatus;
    }

    private void setSecondaryStatus(String secondaryStatus) {

        this.secondaryStatus = secondaryStatus;
    }

}
