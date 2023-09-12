
package com.bot.cqs.query.persistence;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.iisigroup.cap.model.GenericBean;

/**
 * <code>ApplicationParameter</code> 物件對應於 table "ApplicationParameter", 每個 <code>ApplicationParameter</code> 物件都代表一個系統參數, 這些參數均存為 <code>String</code> 型態.
 * 
 * @author Damon Lu
 */
@Entity
@Table(name = "ApplicationParameter")
public class ApplicationParameter  extends GenericBean{

    private String parameterName;
    private String parameterValue;
    private String parameterDesc;

    /**
     * 系統參數的描述, 對應於資料表的 "parameter_desc" 欄位
     * 
     * @return
     */
    @Column(name = "parameter_desc")
    public String getParameterDesc() {

        return parameterDesc;
    }

    public void setParameterDesc(String parameterDesc) {

        this.parameterDesc = parameterDesc;
    }

    /**
     * 系統參數的代號, 純程式內部辨別使用 (要取得外部使用者所需的說明, 應使用 {@link #getParameterDesc()} ). 這個屬性對應於資料表的 "parameter_name" 欄位, 是 primary key
     * 
     * @return
     */
    @Id
    @Column(name = "parameter_name")
    public String getParameterName() {

        return parameterName;
    }

    public void setParameterName(String parameterName) {

        this.parameterName = parameterName;
    }

    /**
     * 系統參數的值, 對應於資料表的 "parameter_value" 欄位
     * 
     * @return
     */
    @Column(name = "parameter_value")
    public String getParameterValue() {

        return parameterValue;
    }

    public void setParameterValue(String parameterValue) {

        this.parameterValue = parameterValue;
    }

}
