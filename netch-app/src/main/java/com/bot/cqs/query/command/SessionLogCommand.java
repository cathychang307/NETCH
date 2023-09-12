
package com.bot.cqs.query.command;

import java.util.Date;
import java.util.List;

import com.bot.cqs.query.persistence.SessionLog;
import com.bot.cqs.query.util.DateUtil;
import com.bot.cqs.query.util.QueryBankUtil;

/**
 * 作業查詢狀況 (網頁) 對應的 command object
 * 
 * @author Damon Lu
 *
 */
public class SessionLogCommand extends SessionLog {

    private String inputAccessDateFrom;
    private String inputAccessDateTo;
    private Date accessDateFrom;
    private Date accessDateTo;
    private String selectedDepartmentId;
    private List<String> targetPrimaryStatus;
    private List<String> targetSecondaryStatus;

    private String outputMethod;

    /**
     * 查詢起日
     * 
     * @return
     */
    public Date getAccessDateFrom() {

        return accessDateFrom;
    }

    public void setAccessDateFrom(Date accessDateFrom) {

        this.accessDateFrom = accessDateFrom;
    }

    /**
     * 查詢訖日
     * 
     * @return
     */
    public Date getAccessDateTo() {

        return accessDateTo;
    }

    public void setAccessDateTo(Date accessDateTo) {

        this.accessDateTo = accessDateTo;
    }

    /**
     * 對應於網頁的 "inputAccessDateFrom" 欄位
     * 
     * @return
     */
    public String getInputAccessDateFrom() {

        return inputAccessDateFrom;
    }

    public void setInputAccessDateFrom(String inputAccessDateFrom) {

        this.inputAccessDateFrom = inputAccessDateFrom;
    }

    /**
     * 對應於網頁的 "inputAccessDateTo" 欄位
     * 
     * @return
     */
    public String getInputAccessDateTo() {

        return inputAccessDateTo;
    }

    public void setInputAccessDateTo(String inputAccessDateTo) {

        this.inputAccessDateTo = inputAccessDateTo;
    }

    /**
     * 對應於網頁的 "selectedDepartmentId" 欄位
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

    /**
     * 這個屬性用於 SessionLogDao / SessionLogManager , 以指定 primaryStatus 的方式來做特定行為的查詢.
     * 
     * @return
     */
    public List<String> getTargetPrimaryStatus() {

        return targetPrimaryStatus;
    }

    public void setTargetPrimaryStatus(List<String> targetPrimaryStatus) {

        this.targetPrimaryStatus = targetPrimaryStatus;
    }

    /**
     * 保留未使用
     * 
     * @return
     */
    public List<String> getTargetSecondaryStatus() {

        return targetSecondaryStatus;
    }

    public void setTargetSecondaryStatus(List<String> targetSecondaryStatus) {

        this.targetSecondaryStatus = targetSecondaryStatus;
    }

    public void caculateInputAccessDate() {

        setAccessDateFrom(DateUtil.getDate(getInputAccessDateFrom(), DateUtil.DATE_STYLE_FROM));
        setAccessDateTo(DateUtil.getDate(getInputAccessDateTo(), DateUtil.DATE_STYLE_TO));
    }

    /**
     * 指定輸出方式, 目前可能有 CSV, PDF, 網頁輸出(預設)
     * 
     * @return
     */
    public String getOutputMethod() {

        return outputMethod;
    }

    public void setOutputMethod(String outputMethod) {

        this.outputMethod = outputMethod;
    }

}
