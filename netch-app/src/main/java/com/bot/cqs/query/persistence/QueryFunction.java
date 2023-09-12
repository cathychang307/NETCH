
package com.bot.cqs.query.persistence;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.iisigroup.cap.model.GenericBean;

/**
 * <code>QueryFunction</code> 物件對應於 table "QueryFunction", 每一筆資料均包含了 <code>ID</code>, <code>名稱</code>與<code>本功能的 URI</code>.
 * 
 * @author Damon Lu
 */
@Entity
@Table(name = "QueryFunction")
public class QueryFunction  extends GenericBean{

    private String functionId;
    private String functionName;
    private String functionUri;
    private Date functionModifyDate;
    private boolean functionEnabled;
    private boolean menu;
    private String parentFunctionId;

    /**
     * 功能修改日期, 對應於資料表的 "function_modify_date" 欄位, 暫不使用
     * 
     * @return
     */
    @Column(name = "function_modify_date")
    public Date getFunctionModifyDate() {

        return functionModifyDate;
    }

    public void setFunctionModifyDate(Date functionModifyDate) {

        this.functionModifyDate = functionModifyDate;
    }

    /**
     * 功能是否啟用, 對應於資料表的 "function_enabled" 欄位. 如未啟用, 則不應加入使用者功能選單中.
     * 
     * @return
     */
    @Column(name = "function_enabled", columnDefinition = "int")
    public boolean isFunctionEnabled() {

        return functionEnabled;
    }

    public void setFunctionEnabled(boolean functionEnabled) {

        this.functionEnabled = functionEnabled;
    }

    /**
     * 功能的 ID, 對應於資料表的 "function_id" 欄位, 長度為 4, 是 primary key.
     * 
     * @return
     */
    @Id
    @Column(name = "function_id", columnDefinition = "char", length = 4)
    public String getFunctionId() {

        return functionId;
    }

    public void setFunctionId(String functionId) {

        this.functionId = functionId;
    }

    /**
     * 功能的名稱, 對應於資料表的 "function_name" 欄位, 這個名稱應顯示在使用者功能選單中
     * 
     * @return
     */
    @Column(name = "function_name")
    public String getFunctionName() {

        return functionName;
    }

    public void setFunctionName(String functionName) {

        this.functionName = functionName;
    }

    /**
     * 功能的 URI (網址), 對應於資料表的 "function_uri" 欄位, 這個值會影響選取功能後的連結
     * 
     * @return
     */
    @Column(name = "function_uri")
    public String getFunctionUri() {

        return functionUri;
    }

    public void setFunctionUri(String functionUri) {

        this.functionUri = functionUri;
    }

    /**
     * 這個值代表本功能是否為一目錄, 對應於資料表的 "is_menu" 欄位, 如果是, 使用者功能選單上應依此物件新增一個目錄, 且 {@link #getFunctionUri() URI} 不應做使用
     * 
     * @return
     * @see getParentFunctionId()
     */
    @Column(name = "is_menu", columnDefinition = "int")
    public boolean isMenu() {

        return menu;
    }

    public void setMenu(boolean menu) {

        this.menu = menu;
    }

    /**
     * 父目錄的 ID, 對應於資料表的 "parent_function_id" 欄位, 父目錄的 {@link #isMenu() menu} 屬性應為 <code>true</code>
     * 
     * @return
     * @see isMenu()
     */
    @Column(name = "parent_function_id", columnDefinition = "char", length = 4)
    public String getParentFunctionId() {

        return parentFunctionId;
    }

    public void setParentFunctionId(String parentFunctionId) {

        this.parentFunctionId = parentFunctionId;
    }

    /**
     * 覆寫 <code>toString()</code> 方法, 提供本 function 的格式化資料
     */
    @Override
    public String toString() {

        StringBuffer s = new StringBuffer();
        s.append(getClass().getSimpleName());
        s.append("{");
        s.append(getFunctionId());
        s.append("(");
        s.append(getParentFunctionId());
        s.append("), ");
        s.append(getFunctionName());
        s.append(", ");
        s.append(getFunctionUri());
        s.append(", ");
        s.append(getFunctionModifyDate());
        s.append(", ");
        s.append(isFunctionEnabled());
        s.append(", ");
        s.append(isMenu());
        s.append("}");
        return s.toString();
    }

}
