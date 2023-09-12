
package com.bot.cqs.query.command;

import com.bot.cqs.query.persistence.QueryRole;

/**
 * 角色維護 (網頁) 對應的 command object.
 * 
 * @author Damon Lu
 */
public class QueryRoleCommand extends QueryRole {

    private String action;

    /**
     * 取得對應於網頁的 "action" 欄位
     * 
     * @return
     */
    public String getAction() {

        return action;
    }

    public void setAction(String action) {

        this.action = action;
    }

}
