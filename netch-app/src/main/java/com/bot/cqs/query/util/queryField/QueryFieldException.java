
package com.bot.cqs.query.util.queryField;

public class QueryFieldException extends Exception {

    public static final int UNKNOWN_REQUEST_FIELD_POSITION = -1;

    private String messageId;
    private String defaultMessage;
    private Object[] args;
    private int requestFieldIndex;

    public QueryFieldException(String messageId, Object[] args, String defaultMessage) {

        super(defaultMessage);
        setMessageId(messageId);
        setArgs(args);
        setDefaultMessage(defaultMessage);
        setRequestFieldIndex(UNKNOWN_REQUEST_FIELD_POSITION);
    }

    public Object[] getArgs() {

        return args;
    }

    protected void setArgs(Object[] args) {

        this.args = args;
    }

    public String getDefaultMessage() {

        return defaultMessage;
    }

    protected void setDefaultMessage(String defaultMessage) {

        this.defaultMessage = defaultMessage;
    }

    public String getMessageId() {

        return messageId;
    }

    protected void setMessageId(String messageId) {

        this.messageId = messageId;
    }

    public int getRequestFieldIndex() {

        return requestFieldIndex;
    }

    public void setRequestFieldIndex(int requestFieldIndex) {

        this.requestFieldIndex = requestFieldIndex;
    }

}
