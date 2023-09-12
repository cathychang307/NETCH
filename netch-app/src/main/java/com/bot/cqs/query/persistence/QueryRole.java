
package com.bot.cqs.query.persistence;

import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import com.bot.cqs.query.util.QueryRoleUtil;
import com.iisigroup.cap.model.GenericBean;

/**
 * <code>QueryRole</code> 物件對應於 table "QueryRole", 每筆資料都代表一個角色. 與 {@link QueryFunction} 搭配使用可以產生各角色所擁有的功能表與權限
 * 
 * @author Damon Lu
 * @see QueryFunction
 */
@Entity
@Table(name = "QueryRole")
public class QueryRole extends GenericBean{

    /**
     * 預設的角色優先順序, 無作用
     */
    public static final int DEFAULT_ROLE_ORDER = 50;

    private String roleId;
    private String roleName;
    private String roleFunction;
    private Date roleModifyDate;
    private boolean roleEnabled;
    private int roleOrder;
    private String roleDesc;
    private int version;

    private Map<String, QueryFunction> executableFunction;

    /**
     * 預設建構元, 會產生一個含有角色{@link QueryFunction 功能表}的集合, 以便後續使用
     * 
     * @see getExecutableFunctionMap()
     * @see getExecutableFunction( String )
     */
    public QueryRole() {

        executableFunction = new TreeMap<String, QueryFunction>();
        setRoleOrder(DEFAULT_ROLE_ORDER);
    }

    /**
     * 角色修改日期, 對應於資料表的 "role_modify_date" 欄位
     * 
     * @return
     */
    @Column(name = "role_modify_date")
    public Date getRoleModifyDate() {

        return roleModifyDate;
    }

    public void setRoleModifyDate(Date roleModifyDate) {

        this.roleModifyDate = roleModifyDate;
    }

    /**
     * 角色是否啟用, 對應於資料表的 "role_enabled" 欄位, 如果未啟用則登入時應略過此角色
     * 
     * @return
     */
    @Column(name = "role_enabled", columnDefinition = "int")
    public boolean isRoleEnabled() {

        return roleEnabled;
    }

    public void setRoleEnabled(boolean roleEnabled) {

        this.roleEnabled = roleEnabled;
    }

    /**
     * 角色所擁用的功能, 對應於資料表的 "role_function" 欄位.
     * <p>
     * 本欄位以逗號 "," 區隔各功能代碼
     * 
     * @return
     */
    @Column(name = "role_function")
    public String getRoleFunction() {

        return roleFunction;
    }

    public void setRoleFunction(String roleFunction) {

        this.roleFunction = roleFunction;
    }

    /**
     * 角色 ID, 對應於資料表的 "role_id" 欄位, 是 primary key. 建議這裡只建台銀 single signon 已擁有的代號.
     * 
     * @return
     */
    @Id
    @Column(name = "role_id")
    public String getRoleId() {

        return roleId;
    }

    public void setRoleId(String roleId) {

        this.roleId = roleId;
    }

    /**
     * 角色名稱, 對應於資料表的 "role_name" 欄位, 這個名稱可以顯示在角色維護功能中.
     * 
     * @return
     */
    @Column(name = "role_name")
    public String getRoleName() {

        return roleName;
    }

    public void setRoleName(String roleName) {

        this.roleName = roleName;
    }

    /**
     * 角色優先順序, 對應於資料表的 "role_order" 欄位, 目前無作用
     * 
     * @return
     */
    @Column(name = "role_order", columnDefinition = "int")
    public int getRoleOrder() {

        return roleOrder;
    }

    public void setRoleOrder(int roleOrder) {

        this.roleOrder = roleOrder;
    }

    /**
     * 角色描述, 對應於資料表的 "role_desc" 欄位, 這個描述可以顯示在角色維護功能中
     * 
     * @return
     */
    @Column(name = "role_desc")
    public String getRoleDesc() {

        return roleDesc;
    }

    public void setRoleDesc(String roleDesc) {

        this.roleDesc = roleDesc;
    }

    /**
     * 版本別, 對應於資料表的 "version" 欄位, 用來避免多人同時修改同一筆資料
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
     * 取得可執行的功能集合, 可由此取得本角色擁有的全部功能
     * 
     * @return
     */
    @Transient
    public Map<String, QueryFunction> getExecutableFunctionMap() {

        return executableFunction;
    }

    protected void setExecutableFunctionMap(Map<String, QueryFunction> executableFunction) {

        this.executableFunction = executableFunction;
    }

    /**
     * 依指定 ID 取得可執行的功能
     * 
     * @param functionId
     * @return
     */
    public QueryFunction getExecutableFunction(String functionId) {

        return getExecutableFunctionMap().get(functionId);
    }

    public void setExecutableFunction(String functionId, QueryFunction function) {

        getExecutableFunctionMap().put(functionId, function);
    }

    /**
     * 加入一個功能至 {@link #getExecutableFunctionMap() 可執行的功能集合}
     * 
     * @param function
     */
    public void addExecutableFunction(QueryFunction function) {

        if (function != null)
            setExecutableFunction(function.getFunctionId(), function);
    }

    /**
     * 由 {@link #getExecutableFunctionMap() 可執行的功能集合} 移除一個功能
     * 
     * @param functionId
     * @return
     */
    public QueryFunction removeExecutableFunction(String functionId) {

        return getExecutableFunctionMap().remove(functionId);

    }

    /**
     * 本角色是否可被更改, 如果是 <code>false</code>, 則應用程式應拒絕修改的請求. 這個旗標的用意是, 避免系統維護人員不小心改錯角色, 造成無人可登入維護的麻煩.
     * 
     * @return
     */
    @Transient
    public boolean isModifiable() {

        return QueryRoleUtil.isModifiable(this);
    }

    /**
     * 檢查這個 role 是否有執行 function 的權限
     * 
     * @param function
     * @return
     */
    @Transient
    public boolean hasFunctionAuthority(QueryFunction function) {

        if (function == null)
            return false;

        QueryFunction targetFunction = getExecutableFunction(function.getFunctionId());
        return targetFunction != null;
    }

    /**
     * 覆寫 toString() 方法, 以提供一個格式化的角色資訊
     */
    @Override
    public String toString() {

        StringBuffer s = new StringBuffer();
        s.append(getClass().getSimpleName());
        s.append("{");
        s.append(getRoleId());
        s.append(", ");
        s.append(getRoleName());
        s.append(", [");
        s.append(getRoleFunction());
        s.append("], ");
        s.append(getRoleModifyDate());
        s.append(", ");
        s.append(isRoleEnabled());
        s.append("}");
        return s.toString();
    }
}
