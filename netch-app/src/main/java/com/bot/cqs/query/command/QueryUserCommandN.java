
package com.bot.cqs.query.command;

import com.bot.cqs.query.persistence.QueryUser;

public class QueryUserCommandN extends QueryUser {

    private String virtualRole;

    private String password;

    public QueryUserCommandN() {
        // super.setDepartmentId("0040107");
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getVirtualRole() {

        return virtualRole;
    }

    public void setVirtualRole(String virtualRole) {

        this.virtualRole = virtualRole;

    }
}
