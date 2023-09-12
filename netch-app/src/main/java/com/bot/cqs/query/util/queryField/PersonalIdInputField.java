
package com.bot.cqs.query.util.queryField;

import com.bot.cqs.query.util.idValidation.ForeignerIdValidation;
import com.bot.cqs.query.util.idValidation.PersonalIdValidation;

public class PersonalIdInputField extends AbstractQueryInputFieldDefinition {

    public PersonalIdInputField() {

        super();
        setMinLength(10);
        setMaxLength(10);
    }

    @Override
    protected StringBuffer customizeData(StringBuffer text) {

        return text;
    }

    @Override
    protected StringBuffer onInternalDataCheck(StringBuffer text) throws QueryFieldException {

        String id = text.toString();
        if (PersonalIdValidation.isPersonalIdValid(id) || ForeignerIdValidation.isForeignerIdValid(id))
            return text;
        else
            throw createQueryFieldException("invalidFormat.1", new Object[] { getFieldDesc() }, "invalidFormat");
    }

}
