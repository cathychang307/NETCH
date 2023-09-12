
package com.bot.cqs.query.util;

import java.util.regex.Pattern;

import com.bot.cqs.query.persistence.QueryRole;

public class QueryRoleUtil {

    public static final Pattern ROLE_PROTECT_PATTERN = Pattern.compile("59040000");

    public static boolean isModifiable(QueryRole role) {

        if (role == null)
            return false;
        if (role.getRoleId() == null)
            return false;
        return !ROLE_PROTECT_PATTERN.matcher(role.getRoleId()).matches();
    }
}
