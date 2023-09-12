
package com.bot.cqs.query.util.queryField;

public class SimpleQueryInputFieldDefinition extends AbstractQueryInputFieldDefinition {

    public SimpleQueryInputFieldDefinition() {

        super();
    }

    @Override
    protected StringBuffer customizeData(StringBuffer text) {

        return text;
    }

    @Override
    protected StringBuffer onInternalDataCheck(StringBuffer text) throws QueryFieldException {

        return text;
    }

}
