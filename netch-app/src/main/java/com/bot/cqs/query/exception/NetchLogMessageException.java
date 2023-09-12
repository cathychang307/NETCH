package com.bot.cqs.query.exception;

import com.iisigroup.cap.exception.CapMessageException;

@SuppressWarnings("serial")
public class NetchLogMessageException extends CapMessageException {

    String returnMsg;

    public String getReturnMsg() {
        return returnMsg;
    }

    public NetchLogMessageException setReturnMsg(String returnMsg) {
        this.returnMsg = returnMsg;
        return this;
    }

    /**
     * Instantiates a new cap exception.
     */
    public NetchLogMessageException() {
        super();
    }

    /**
     * Instantiates a new cap exception.
     * 
     * @param causeClass
     *            the cause class
     */
    @SuppressWarnings("rawtypes")
    public NetchLogMessageException(Class causeClass) {
        super();
        super.setCauseSource(causeClass);
    }

    /**
     * Instantiates a new cap exception.
     * 
     * @param message
     *            the message
     * @param causeClass
     *            the cause class
     */
    @SuppressWarnings("rawtypes")
    public NetchLogMessageException(String message, Class causeClass) {
        super(message, causeClass);
    }

    /**
     * Instantiates a new cap exception.
     * 
     * @param cause
     *            the throwable
     * @param causeClass
     *            the cause class
     */
    @SuppressWarnings("rawtypes")
    public NetchLogMessageException(Throwable cause, Class causeClass) {
        super(cause, causeClass);
    }

    /**
     * Instantiates a new cap exception.
     * 
     * @param message
     *            the message
     * @param cause
     *            the cause
     * @param causeClass
     *            the cause class
     */
    @SuppressWarnings("rawtypes")
    public NetchLogMessageException(String message, Throwable cause, Class causeClass) {
        super(message, cause, causeClass);
    }
    
    /**
    * Instantiates a new cap exception.
    * 
    * @param causeClass
    *            the cause class
    */
   @SuppressWarnings("rawtypes")
   public NetchLogMessageException(Class causeClass, String returnMsg) {
       super();
       super.setCauseSource(causeClass);
       this.returnMsg = returnMsg;
   }

   /**
    * Instantiates a new cap exception.
    * 
    * @param message
    *            the message
    * @param causeClass
    *            the cause class
    */
   @SuppressWarnings("rawtypes")
   public NetchLogMessageException(String message, Class causeClass, String returnMsg) {
       super(message, causeClass);
       this.returnMsg = returnMsg;
   }

   /**
    * Instantiates a new cap exception.
    * 
    * @param cause
    *            the throwable
    * @param causeClass
    *            the cause class
    */
   @SuppressWarnings("rawtypes")
   public NetchLogMessageException(Throwable cause, Class causeClass, String returnMsg) {
       super(cause, causeClass);
       this.returnMsg = returnMsg;
   }

   /**
    * Instantiates a new cap exception.
    * 
    * @param message
    *            the message
    * @param cause
    *            the cause
    * @param causeClass
    *            the cause class
    */
   @SuppressWarnings("rawtypes")
   public NetchLogMessageException(String message, Throwable cause, Class causeClass, String returnMsg) {
       super(message, cause, causeClass);
       this.returnMsg = returnMsg;
   }

}
