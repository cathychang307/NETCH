
package com.bot.cqs.query.persistence;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.context.support.ResourceBundleMessageSource;

import com.bot.cqs.query.util.DateUtil;
import com.iisigroup.cap.model.GenericBean;
import com.iisigroup.cap.utils.CapAppContext;

/**
 * <code>SessionLog</code> 物件對應於 table "SessionLog", 代表了網站功能使用的記錄, 記錄的內容主要是 <code>primaryStatus</code> 與 <code>secondaryStatus</code>.
 * <p>
 * primaryStatus 長度為 2, 代表了 "行為", 例如登入, 登出, 查詢. secondaryStatus 長度為 4, 用於記錄 "結果", 例如 成功, 連線失敗, 更新失敗等等.
 * <p>
 * primaryStatus 與 secondaryStatus 的訊息描述可由同路徑下的 SessionLog_message.properties 取得, 這些訊息最好簡潔有力, 以免在資料查詢時 (通常是網頁) 凌亂不堪.
 * <p>
 * 另外, 附屬的參考資訊有 correlationId 與 memo ( 均非必要, 可為 null ).<br>
 * 1. correlationId 是為了 方便關連到別的資料表的 primary key 所保留的欄位, 例如, 這是一筆票信查詢的 log, 那這個欄位記錄一個查詢序號是合理的作法. 這個欄位格式依各網頁功能自訂. <br>
 * 2. memo 基本上無用途, 純粹用於簡單的註解.
 * 
 * @author Damon Lu
 */

@Entity
@Table(name = "SessionLog")
@org.hibernate.annotations.GenericGenerator(name = "hibernate-uuid", strategy = "uuid")
public class SessionLog  extends GenericBean{

    private static final String PRIMARY_STATUS_MESSAGE_PREFIX = SessionLog.class.getSimpleName() + "_primaryStatus_";
    private static final String SECONDARY_STATUS_MESSAGE_PREFIX = SessionLog.class.getSimpleName() + "_secondaryStatus_";

    /**
     * 登入
     */
    public static final String PRIMARY_STATUS_LOGIN = "01";
    /**
     * 登出
     */
    public static final String PRIMARY_STATUS_LOGOUT = "02";
    /**
     * 新增分行資料
     */
    public static final String PRIMARY_STATUS_CREATE_BRANCH = "07";
    /**
     * 更改分行資料
     */
    public static final String PRIMARY_STATUS_UPDATE_BRANCH = "08";
    /**
     * 刪除分行資料
     */
    public static final String PRIMARY_STATUS_DELETE_BRANCH = "09";
    /**
     * 查詢分行資料
     */
    public static final String PRIMARY_STATUS_QUERY_BRANCH = "10";
    /**
     * 新增費率設定
     */
    public static final String PRIMARY_STATUS_CREATE_RATE = "11";
    /**
     * 更改費率設定
     */
    public static final String PRIMARY_STATUS_UPDATE_RATE = "12";
    /**
     * 刪除費率設定
     */
    public static final String PRIMARY_STATUS_DELETE_RATE = "13";
    /**
     * 查詢費率設定
     */
    public static final String PRIMARY_STATUS_QUERY_RATE = "14";
    /**
     * 新增角色
     */
    public static final String PRIMARY_STATUS_CREATE_ROLE = "15";
    /**
     * 更改角色
     */
    public static final String PRIMARY_STATUS_UPDATE_ROLE = "16";
    /**
     * 刪除角色
     */
    public static final String PRIMARY_STATUS_DELETE_ROLE = "17";
    /**
     * 查詢角色
     */
    public static final String PRIMARY_STATUS_QUERY_ROLE = "18";
    /**
     * 查詢連線記錄
     */
    public static final String PRIMARY_STATUS_QUERY_SESSIONLOG = "19";
    /**
     * 查詢登入登出記錄
     */
    public static final String PRIMARY_STATUS_QUERY_SESSIONLOG_LOGIN = "20";
    /**
     * 新增系統查詢參數
     */
    public static final String PRIMARY_STATUS_CREATE_QUERY_COUNT = "21";
    /**
     * 更改系統查詢參數
     */
    public static final String PRIMARY_STATUS_UPDATE_QUERY_COUNT = "22";
    /**
     * 刪除系統查詢參數
     */
    public static final String PRIMARY_STATUS_DELETE_QUERY_COUNT = "23";
    /**
     * 查詢系統查詢參數
     */
    public static final String PRIMARY_STATUS_QUERY_QUERY_COUNT = "24";
    /**
     * 新增快取作業參數
     */
    public static final String PRIMARY_STATUS_CREATE_QUERY_CACHE = "25";
    /**
     * 更改快取作業參數
     */
    public static final String PRIMARY_STATUS_UPDATE_QUERY_CACHE = "26";
    /**
     * 刪除快取作業參數
     */
    public static final String PRIMARY_STATUS_DELETE_QUERY_CACHE = "27";
    /**
     * 查詢快取作業參數
     */
    public static final String PRIMARY_STATUS_QUERY_QUERY_CACHE = "28";
    /**
     * 新增帳務作業參數
     */
    public static final String PRIMARY_STATUS_CREATE_CACLUATE_TYPE = "29";
    /**
     * 更改帳務作業參數
     */
    public static final String PRIMARY_STATUS_UPDATE_CACLUATE_TYPE = "30";
    /**
     * 刪除帳務作業參數
     */
    public static final String PRIMARY_STATUS_DELETE_CACLUATE_TYPE = "31";
    /**
     * 查詢帳務作業參數
     */
    public static final String PRIMARY_STATUS_QUERY_CACLUATE_TYPE = "32";
    /**
     * 查詢查詢明細
     */
    public static final String PRIMARY_STATUS_QUERY_INQUIRYLOG = "33";
    /**
     * 查詢分行查詢明細
     */
    public static final String PRIMARY_STATUS_QUERY_INQUIRYLOG_BY_BRANCH = "34";
    /**
     * 查詢月收費檔
     */
    public static final String PRIMARY_STATUS_CHARGE_INQUIRYLOG = "35";
    /**
     * 分行查詢月收費檔
     */
    public static final String PRIMARY_STATUS_CHARGE_INQUIRYLOG_BY_BRANCH = "36";
    /**
     * 查詢統計
     */
    public static final String PRIMARY_STATUS_COUNT_ROW = "37";
    /**
     * 分行查詢統計
     */
    public static final String PRIMARY_STATUS_COUNT_ROW_BY_BRANCH = "38";
    /**
     * 連線作業－登入
     */
    public static final String PRIMARY_STATUS_INQUIRY_LOGON = "71";
    /**
     * 連線作業－登出
     */
    public static final String PRIMARY_STATUS_INQUIRY_LOGOFF = "72";
    /**
     * 個人一類票信查詢
     */
    public static final String PRIMARY_STATUS_INQUIRY_4111 = "81";
    /**
     * 公司一類票信查詢
     */
    public static final String PRIMARY_STATUS_INQUIRY_4112 = "82";
    /**
     * 帳戶一類票信查詢
     */
    public static final String PRIMARY_STATUS_INQUIRY_4113 = "83";
    /**
     * 個人二類票信查詢
     */
    public static final String PRIMARY_STATUS_INQUIRY_4114 = "84";
    /**
     * 公司二類票信查詢
     */
    public static final String PRIMARY_STATUS_INQUIRY_4115 = "85";
    /**
     * 帳戶二類票信查詢
     */
    public static final String PRIMARY_STATUS_INQUIRY_4116 = "86";
    /**
     * 個人甲類票信查詢
     */
    public static final String PRIMARY_STATUS_INQUIRY_4121 = "87";
    /**
     * 公司甲類票信查詢
     */
    public static final String PRIMARY_STATUS_INQUIRY_4122 = "88";
    /**
     * 個人乙類票信查詢
     */
    public static final String PRIMARY_STATUS_INQUIRY_4123 = "89";
    /**
     * 公司乙類票信查詢
     */
    public static final String PRIMARY_STATUS_INQUIRY_4124 = "90";
    /**
     * 公司OBU一類票信查詢
     */
    public static final String PRIMARY_STATUS_INQUIRY_4132 = "91";
    /**
     * 帳戶OBU一類票信查詢
     */
    public static final String PRIMARY_STATUS_INQUIRY_4133 = "92";
    /**
     * 公司OBU二類票信查詢
     */
    public static final String PRIMARY_STATUS_INQUIRY_4135 = "93";
    /**
     * 帳戶OBU二類票信查詢
     */
    public static final String PRIMARY_STATUS_INQUIRY_4136 = "94";

    /*
     * Secondary Status 編寫原則. 1. 跟 primary status 有關連 (相依) 的錯誤, 則前二碼與 primary status 相同. 例如 login 的 primary status = '01', 則 login 失敗的 secondary status 用 '01xx' 2. 共通性的錯誤, 例如 DB 的 access error 等, 是所有
     * function 都有可能發生的, 則另訂 secondary status, 目前由 X 起自行分類編號. (Xxxx)
     */

    /**
     * 執行完畢
     */
    public static final String SECONDARY_STATUS_NO_MORE_DESCRIPTION = "0000";
    /**
     * 授權連線失敗
     */
    public static final String SECONDARY_STATUS_LOGIN_CONNECTION_FAILE = "0101";
    /**
     * 授權連線逾時
     */
    public static final String SECONDARY_STATUS_LOGIN_TIMEOUT = "0102";
    /**
     * 沒有可使用的角色
     */
    public static final String SECONDARY_STATUS_LOGIN_NO_WORKABLE_ROLE = "0103";
    /**
     * 系統授權失敗
     */
    public static final String SECONDARY_STATUS_LOGIN_REFUSED_BY_AA = "0104";
    /**
     * 授權資訊不足
     */
    public static final String SECONDARY_STATUS_LOGIN_UNSATIFIED_AUTHORIZATION_INFO = "0105";
    /**
     * 系統轉移失敗
     */
    public static final String SECONDARY_STATUS_LOGIN_CHECK_JUMPER_FAILED = "0106";
    /**
     * SSL 連線失敗
     */
    public static final String SECONDARY_STATUS_LOGIN_SSL_INIT_FAILED = "0107";

    /**
     * 連線作業失敗
     */
    public static final String SECONDARY_STATUS_INQUIRY_SESSION_FAILED = "7001";

    /**
     * 資料轉換錯誤
     */
    public static final String SECONDARY_STATUS_INQUIRY_TRANSLATE_ERROR = "8001";
    /**
     * 資料輸入不完整
     */
    public static final String SECONDARY_STATUS_INQUIRY_INPUT_UNSATIFIED = "8002";
    /**
     * 查詢失敗
     */
    public static final String SECONDARY_STATUS_INQUIRY_RESPONSE_ERROR = "8003";

    /**
     * 資料庫操作失敗
     */
    public static final String SECONDARY_STATUS_DATABASE_ERROR = "X001";
    /**
     * 資料庫連線失敗
     */
    public static final String SECONDARY_STATUS_DATABASE_CONNECTION_ERROR = "X002";
    /**
     * 資料不存在
     */
    public static final String SECONDARY_STATUS_DATA_NOT_FOUND = "X011";
    /**
     * 資料已經存在
     */
    public static final String SECONDARY_STATUS_DATA_ALREADY_EXIST = "X012";
    /**
     * 資料版本別不符
     */
    public static final String SECONDARY_STATUS_DATA_VERSION_ERROR = "X013";
    /**
     * 資料不允許更改
     */
    public static final String SECONDARY_STATUS_REFUSE_UPDATE = "X014";
    /**
     * 資料格式不符
     */
    public static final String SECONDARY_STATUS_INVALID_DATA = "X015";

    private String uuid;
    private String userId;
    private String userName;
    private String departmentId;
    private String departmentName;
    private String roleId;
    private String roleName;
    private String rank;
    private Date accessDatetime;
    private String accessIp;
    private String primaryStatus;
    private String secondaryStatus;
    private String correlationId;
    private String memo;

    /**
     * 由 hibernate 產生的隨機編碼, 對應至資料表的 "uuid" 欄位, 是 primary key
     * 
     * @return
     */
    @Id
    @GeneratedValue(generator = "hibernate-uuid")
    @Column(name = "uuid")
    public String getUuid() {

        return uuid;
    }

    public void setUuid(String uuid) {

        this.uuid = uuid;
    }

    /**
     * 執行時間, 對應至資料表的 "access_datetime" 欄位
     * 
     * @return
     */
    @Column(name = "access_datetime")
    public Date getAccessDatetime() {

        return accessDatetime;
    }

    public void setAccessDatetime(Date accessDatetime) {

        this.accessDatetime = accessDatetime;
    }

    /**
     * 將 {@link #getAccessDatetime() 作業時間} 轉為民國年格式
     * 
     * @return
     * @see getAccessDatetime()
     * @see DateUtil#toRocDatetime( java.util.Date )
     */
     @Transient
     public String getAccessDatetimeRocString() {
     return DateUtil.toRocDatetime( getAccessDatetime() );
     }

    /**
     * 取得使用者的 IP address, 對應於資料表的 "access_ip" 欄位
     * 
     * @return
     */
    @Column(name = "access_ip")
    public String getAccessIp() {

        return accessIp;
    }

    public void setAccessIp(String accessIp) {

        this.accessIp = accessIp;
    }

    /**
     * 這項本項作業相關連的 ID 值 <i>(非必要)</i>, 對應於資料表的 "correlation_id" 欄位
     * 
     * @return
     */
    @Column(name = "correlation_id")
    public String getCorrelationId() {

        return correlationId;
    }

    public void setCorrelationId(String correlationId) {

        this.correlationId = correlationId;
    }

    /**
     * 部門 / 分行 ID, 對應於資料表的 "department_id" 欄位
     * 
     * @return
     */
    @Column(name = "department_id")
    public String getDepartmentId() {

        return departmentId;
    }

    public void setDepartmentId(String departmentId) {

        this.departmentId = departmentId;
    }

    /**
     * 部門 / 分行 名稱, 對應於資料表的 "department_name" 欄位
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
     * 角色 ID, 對應於資料表的 "role_id" 欄位
     * 
     * @return
     */
    @Column(name = "role_id")
    public String getRoleId() {

        return roleId;
    }

    public void setRoleId(String roleId) {

        this.roleId = roleId;
    }

    /**
     * 角色名稱, 對應於資料表的 "role_name" 欄位
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
     * 行為代碼, 長度為2, 對應於資料表的 "primary_status" 欄位
     * 
     * @return
     */
    @Column(name = "primary_status", columnDefinition = "char", length = 2)
    public String getPrimaryStatus() {

        return primaryStatus;
    }

    public void setPrimaryStatus(String primaryStatus) {

        this.primaryStatus = primaryStatus;
    }

    /**
     * 行為說明
     * 
     * @return
     */
    @Transient
    public String getPrimaryStatusDesc() {
        return CapAppContext.getMessage(PRIMARY_STATUS_MESSAGE_PREFIX + getPrimaryStatus());
    }

    /**
     * 使用者職級, 對應於資料表的 "rank" 欄位
     * 
     * @return
     */
    @Column(name = "rank")
    public String getRank() {

        return rank;
    }

    public void setRank(String rank) {

        this.rank = rank;
    }

    /**
     * 執行結果, 長度為 4, 對應於資料表的 "secondary_status" 欄位
     * 
     * @return
     */
    @Column(name = "secondary_status", columnDefinition = "char", length = 4)
    public String getSecondaryStatus() {

        return secondaryStatus;
    }

    public void setSecondaryStatus(String secondaryStatus) {

        this.secondaryStatus = secondaryStatus;
    }

    /**
     * 執行的結果是否為 {@link #SECONDARY_STATUS_NO_MORE_DESCRIPTION 正常執行結束}
     * 
     * @return
     */
    @Transient
    public boolean isSecondaryStatusNormal() {

        return SECONDARY_STATUS_NO_MORE_DESCRIPTION.equals(getSecondaryStatus());
    }

    /**
     * 取得執行結果的描述
     * 
     * @return
     */
    @Transient
    public String getSecondaryStatusDesc() {
        return CapAppContext.getMessage(SECONDARY_STATUS_MESSAGE_PREFIX + getSecondaryStatus());
    }

    /**
     * 使用者 ID, 對應於資料表的 "user_id" 欄位
     * 
     * @return
     */
    @Column(name = "user_id")
    public String getUserId() {

        return userId;
    }

    public void setUserId(String userId) {

        this.userId = userId;
    }

    /**
     * 使用者姓名, 對應於資料表的 "user_name" 欄位
     * 
     * @return
     */
    @Column(name = "user_name")
    public String getUserName() {

        return userName;
    }

    public void setUserName(String userName) {

        this.userName = userName;
    }

    /**
     * 附註, 對應於資料表的 "user_name" 欄位. 請留意長度, 不要超過 DB 的定義 (目前為 30)
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
     * 覆寫 toString() 方法, 取得格式化的 <code>SessionLog</code> 字串
     */
    public String toString() {

        StringBuffer s = new StringBuffer(getClass().getName());
        s.append("{userId=");
        s.append(getUserId());
        s.append(",userName=");
        s.append(getUserName());
        s.append(",departmentId=");
        s.append(getDepartmentId());
        s.append(",departmentName=");
        s.append(getDepartmentName());
        s.append(",roleId=");
        s.append(getRoleId());
        s.append(",roleName=");
        s.append(getRoleName());
        s.append(",rank=");
        s.append(getRank());
        s.append(",accessDatetime=");
        s.append(getAccessDatetime());
        s.append(",accessIp=");
        s.append(getAccessIp());
        s.append(",primaryStatus=");
        s.append(getPrimaryStatus());
        s.append(",secondaryStatus=");
        s.append(getSecondaryStatus());
        s.append(",correlationId=");
        s.append(getCorrelationId());
        s.append(",memo=");
        s.append(getMemo());
        s.append('}');
        return s.toString();
    }

}
