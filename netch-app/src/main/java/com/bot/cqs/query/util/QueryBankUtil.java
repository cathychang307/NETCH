
package com.bot.cqs.query.util;

public class QueryBankUtil {

    public static String getDepartmentIdFromShortDesc(String bankDesc) {

        if (bankDesc == null)
            return null;

        int pos = bankDesc.indexOf(' ');
        if (pos == -1)
            return bankDesc;
        else
            return bankDesc.substring(0, pos);
    }
}
