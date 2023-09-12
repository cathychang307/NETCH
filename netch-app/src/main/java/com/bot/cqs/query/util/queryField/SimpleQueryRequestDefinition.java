
package com.bot.cqs.query.util.queryField;

public class SimpleQueryRequestDefinition implements QueryRequestDefinition {

    private String queryName;
    private String transactionId;
    private QueryInputFieldDefinition[] queryInputFieldDefinition;
    private String[] memo;
    private boolean displayTransactionId;

    public SimpleQueryRequestDefinition() {
        setDisplayTransactionId(true);
    }

    public boolean isDisplayTransactionId() {

        return displayTransactionId;
    }

    public void setDisplayTransactionId(boolean displayTransactionId) {

        this.displayTransactionId = displayTransactionId;
    }

    public String[] getMemo() {

        return memo;
    }

    public void setMemo(String[] memo) {

        this.memo = memo;
    }

    public QueryInputFieldDefinition[] getQueryInputFieldDefinition() {

        return queryInputFieldDefinition;
    }

    public void setQueryInputFieldDefinition(QueryInputFieldDefinition[] queryInputFieldDefinition) {

        this.queryInputFieldDefinition = queryInputFieldDefinition;
    }

    public String getQueryName() {

        return queryName;
    }

    public void setQueryName(String queryName) {

        this.queryName = queryName;
    }

    public String getTransactionId() {

        return transactionId;
    }

    public void setTransactionId(String transactionId) {

        this.transactionId = transactionId;
    }

    public String getQueryInputFieldNameExpression() {

        QueryInputFieldDefinition[] field = getQueryInputFieldDefinition();
        if (field == null || field.length == 0)
            return "[]";

        StringBuffer s = new StringBuffer('[');
        for (int i = 0; i < field.length; i++) {
            if (i > 0)
                s.append(',');

            s.append('"');
            s.append(field[i].getFieldName());
            s.append('"');
        }

        s.append('"');
        return s.toString();
    }
}
