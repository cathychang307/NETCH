
package com.bot.cqs.query.command;

import org.springframework.stereotype.Component;

import com.bot.cqs.query.persistence.QueryBank;
import com.bot.cqs.query.util.QueryBankUtil;

/**
 * 分行維護 (網頁) 對應的 command object.
 * 
 * @author Damon Lu
 */
@Component
public class QueryBankCommand extends QueryBank {

    public static final String ALL_BANK = "ALL";

    private String selectedDepartmentId;
    private String action;

    public QueryBankCommand() {

    }

    public QueryBankCommand(QueryBank queryBank) {

        setDepartmentId(queryBank.getDepartmentId());
        setDepartmentName(queryBank.getDepartmentName());
        setChargeBankId(queryBank.getChargeBankId());
        setTchId(queryBank.getTchId());
        setVersion(queryBank.getVersion());
        setMemo(queryBank.getMemo());
    }

    /**
     * 取得對應於網頁的 "action" 欄位
     * 
     * @return
     */
    public String getAction() {

        return action;
    }

    /**
     * @param action
     */
    public void setAction(String action) {

        this.action = action;
    }

    /**
     * 取得對應於網頁的 "selectedDepartmentId" 欄位
     * 
     * @return
     */
    public String getSelectedDepartmentId() {

        return selectedDepartmentId;
    }

    public void setSelectedDepartmentId(String selectedDepartmentId) {

        this.selectedDepartmentId = selectedDepartmentId;
        setDepartmentId(QueryBankUtil.getDepartmentIdFromShortDesc(selectedDepartmentId));
    }

}
