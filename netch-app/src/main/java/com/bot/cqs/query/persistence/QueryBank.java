
package com.bot.cqs.query.persistence;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import com.bot.cqs.query.util.idValidation.BotBranchIdValidation;
import com.iisigroup.cap.model.GenericBean;

/**
 * <code>QueryBank</code> 物件對應於 table "QueryBank", 每個 <code>QueryBank</code> 物件都含有一個 單位 / 分行 與付費行資訊
 * 
 * @author Damon Lu
 */
@Entity
@Table(name = "QueryBank")
public class QueryBank  extends GenericBean{

    private String departmentId;
    private String departmentName;
    private String chargeBankId;
    private String chargeBankName;
    private String tchId;
    private String memo;
    private int version;

    /**
     * 單位 / 分行 ID, 對應於資料表的 "department_id" 欄位, 為 primary key
     * 
     * @return
     */
    @Id
    @Column(name = "department_id")
    public String getDepartmentId() {

        return departmentId;
    }

    public void setDepartmentId(String departmentId) {

        this.departmentId = departmentId;
    }

    /**
     * 單位 / 分行 台稱, 對應於資料表的 "department_name" 欄位
     * 
     * @return
     */
    @Column(name = "department_name")
    public String getDepartmentName() {

        return departmentName;
    }

    public void setDepartmentName(String departmentName) {

        this.departmentName = departmentName;
    }

    /**
     * 付費行 ID, 對應於資料表的 "charge_bank_id" 欄位, 長度為 7 位, 且 {@link BotBranchIdValidation#isBranchIdValid(String) 檢查碼需符合}
     * 
     * @return
     * @see BotBranchIdValidation#isBranchIdValid(String)
     */
    @Column(name = "charge_bank_id")
    public String getChargeBankId() {

        return chargeBankId;
    }

    public void setChargeBankId(String chargeBankId) {

        this.chargeBankId = chargeBankId;
    }

    /**
     * 交換所代碼, 對應於資料表的 "tch_id" 欄位, 長度為 2 位
     * 
     * @return
     */
    @Column(name = "tch_id", columnDefinition = "char", length = 2)
    public String getTchId() {

        return tchId;
    }

    public void setTchId(String tchId) {

        this.tchId = tchId;
    }

    /**
     * 版本別, 對應於資料表的 "version" 欄位, 用途是預防多人同時修改同一筆資料.
     * 
     * @return
     */
    @Version
    @Column(name = "version")
    public int getVersion() {

        return version;
    }

    public void setVersion(int version) {

        this.version = version;
    }

    /**
     * 單位的附註說明, 對應於資料表的 "memo" 欄位
     * 
     * @return
     */
    @Column(name = "memo")
    public String getMemo() {

        return memo;
    }

    public void setMemo(String memo) {

        this.memo = memo;
    }

    /**
     * 取得一個 單位 ID + 單位名稱 的資訊, 以便列表顯示
     * 
     * @return
     */
    @Transient
    public String getShortDesc() {

        return getDepartmentId() + " " + getDepartmentName();
    }

    /**
     * 取得一個 付費行 ID + 付費行名稱 的資訊, 以便列表顯示
     * 
     * @return
     */
    @Transient
    public String getChargeBankShortDesc() {

        return getChargeBankId() + " " + (getChargeBankName() != null ? getChargeBankName() : "");
    }

    /**
     * 付費行名稱, 這個欄位沒有對應資料表欄位, 必須由其它程式來填入.
     * <p>
     * <b>例如</b>: 以付款行 ID 再去查詢單位代號與名稱
     * 
     * @return
     */
    @Transient
    public String getChargeBankName() {

        return chargeBankName;
    }

    public void setChargeBankName(String chargeBankName) {

        this.chargeBankName = chargeBankName;
    }

}
