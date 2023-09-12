package com.bot.cqs.query.persistence;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.iisigroup.cap.model.GenericBean;

/**
 * <pre>
 * EtchAuditLog
 * </pre>
 * @since  2021年2月2日
 * @author cathy
 * @version <ul>
 *           <li>2021年2月2日,cathy,new
 *          </ul>
 */
@Entity
@Table(name = "AP_ETCH_AUDIT_LOG")
public class EtchAuditLog extends GenericBean {

    @Id
    @Column(name = "oid")
    private String oid;

    /** 使用者帳號/員工編號 */
    @Column(name = "user_id")
    private String userId;

    /** 來源IP/設備Hostname/終端機ID */
    @Column(name = "source_ip")
    private String sourceIp;

    /** 日期時間 */
    @Column(name = "execute_date")
    private Timestamp executeDate;

    /** 所連線目標資料庫/伺服器IP */
    @Column(name = "target_ip")
    private String targetIp;

    /** 系統檔案名稱或存取物件對象名稱 (如TABLE/VIEW名稱) */
    @Column(name = "access_object")
    private String accessObject;

    /** 存取系統檔案之帳號或存取DB物件之帳號 */
    @Column(name = "access_account")
    private String accessAccount;

    /** 執行程式名稱/交易代號/交易名稱 */
    @Column(name = "function_id")
    private String functionId;

    /** 記載動作類別，如 A:新增/ D:刪除/ E:修改/ Q:查詢 R:報表/ O:匯出下載/ P:列印 等 */
    @Column(name = "action_id")
    private String actionId;

    /** 如程式參數、檔案處理語法或資料庫指令(SQL語法)等 */
    @Column(name = "sql_script")
    private String sqlScript;

    /** 執行成功或失敗(Success/Failure) */
    @Column(name = "execute_status")
    private String executeStatus;

    /** 記載執行成功所回傳之資料筆數 */
    @Column(name = "data_count")
    private String dataCount;

    /** 記載執行成功所回傳之資料內容 */
    @Column(name = "execute_result")
    private String executeResult;

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSourceIp() {
        return sourceIp;
    }

    public void setSourceIp(String sourceIp) {
        this.sourceIp = sourceIp;
    }

    public Timestamp getExecuteDate() {
        return executeDate;
    }

    public void setExecuteDate(Timestamp executeDate) {
        this.executeDate = executeDate;
    }

    public String getTargetIp() {
        return targetIp;
    }

    public void setTargetIp(String targetIp) {
        this.targetIp = targetIp;
    }

    public String getAccessObject() {
        return accessObject;
    }

    public void setAccessObject(String accessObject) {
        this.accessObject = accessObject;
    }

    public String getAccessAccount() {
        return accessAccount;
    }

    public void setAccessAccount(String accessAccount) {
        this.accessAccount = accessAccount;
    }

    public String getFunctionId() {
        return functionId;
    }

    public void setFunctionId(String functionId) {
        this.functionId = functionId;
    }

    public String getSqlScript() {
        return sqlScript;
    }

    public void setSqlScript(String sqlScript) {
        this.sqlScript = sqlScript;
    }

    public String getExecuteStatus() {
        return executeStatus;
    }

    public void setExecuteStatus(String executeStatus) {
        this.executeStatus = executeStatus;
    }

    public String getDataCount() {
        return dataCount;
    }

    public void setDataCount(String dataCount) {
        this.dataCount = dataCount;
    }

    public String getExecuteResult() {
        return executeResult;
    }

    public void setExecuteResult(String executeResult) {
        this.executeResult = executeResult;
    }

    public String getActionId() {
        return actionId;
    }

    public void setActionId(String actionId) {
        this.actionId = actionId;
    }

}
