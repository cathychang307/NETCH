
package com.bot.cqs.query.util.queryField;

import com.bot.cqs.query.util.idValidation.BusinessIdValidation;

public class ObuBusinessIdInputField extends AbstractQueryInputFieldDefinition {

    public ObuBusinessIdInputField() {

        super();
        setMinLength(8);
        setMaxLength(8);
    }

    @Override
    protected StringBuffer onInternalDataCheck(StringBuffer text) throws QueryFieldException {

        String id = text.toString();
        if (BusinessIdValidation.isObuBusinessIdValid(id))
            return text;
        else
            throw createQueryFieldException("invalidFormat.1", new Object[] { getFieldDesc() }, "invalidFormat");
    }

    @Override
    protected StringBuffer customizeData(StringBuffer text) {

        return text;
    }

}
