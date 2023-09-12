package com.bot.cqs.security.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.bot.cqs.query.persistence.QueryRole;
import com.bot.cqs.query.persistence.QueryUser;
import com.iisigroup.cap.security.model.CapUserDetails;

/**
 * <pre>
 * BotUserDetails
 * </pre>
 * 
 * @since 2017年1月18日
 * @author bob peng
 * @version
 *          <ul>
 *          <li>2017年1月18日,bob peng,new
 *          </ul>
 */
@SuppressWarnings("serial")
public class EtchUserDetails extends CapUserDetails implements Serializable{

    private String chargeBankId;
    private String chargeBankName;
    private String ipAddress;
    private String departmentId;
    private String departmentName;
    private String rank;
    private QueryRole queryRole;

    public EtchUserDetails(QueryUser queryUser, String chargeBankId, String chargeBankName, String password, Map<String, String> roles) {
        this.setUserId(queryUser.getEmployeeId());
        this.setUserName(queryUser.getEmployeeName());
        this.setDepartmentId(queryUser.getDepartmentId());
        this.setDepartmentName(queryUser.getDepartmentName());
        this.setRank(queryUser.getRank());
        this.setQueryRole(queryUser.getQueryRole());
        this.setPassword(password);
        this.setChargeBankId(chargeBankId);
        this.setChargeBankName(chargeBankName);
        this.setRoles(roles);
        this.setExtraAttrib(new HashMap<String, Object>());
        this.setAuthorities(roles);
    }

    public String getChargeBankId() {
        return chargeBankId;
    }

    public void setChargeBankId(String chargeBankId) {
        this.chargeBankId = chargeBankId;
    }

    public String getChargeBankName() {
        return chargeBankName;
    }

    public void setChargeBankName(String chargeBankName) {
        this.chargeBankName = chargeBankName;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public QueryRole getQueryRole() {
        return queryRole;
    }

    public void setQueryRole(QueryRole queryRole) {
        this.queryRole = queryRole;
    }

    // =======================================

    public String getChargeBankIdAndName() {
        return getChargeBankId() + " " + (getChargeBankName() != null ? getChargeBankName() : "");
    }
    
    

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
        result = prime * result + ((getUsername() == null) ? 0 : getUsername().hashCode());
        result = prime * result + ((chargeBankId == null) ? 0 : chargeBankId.hashCode());
        result = prime * result + ((chargeBankName == null) ? 0 : chargeBankName.hashCode());
        result = prime * result + ((departmentId == null) ? 0 : departmentId.hashCode());
        result = prime * result + ((departmentName == null) ? 0 : departmentName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        EtchUserDetails other = (EtchUserDetails) obj;
        if (getUserId() == null) {
            if (other.getUserId() != null)
                return false;
        } else if (!getUserId().equals(other.getUserId()))
            return false;
        if (getUsername() == null) {
            if (other.getUsername() != null)
                return false;
        } else if (!getUsername().equals(other.getUsername()))
            return false;
        if (getUserId() == null) {
            if (other.getUsername() != null)
                return false;
        } else if (!getUsername().equals(other.getUsername()))
            return false;
        if (chargeBankId == null) {
            if (other.chargeBankId != null)
                return false;
        } else if (!chargeBankId.equals(other.chargeBankId))
            return false;
        if (chargeBankName == null) {
            if (other.chargeBankName != null)
                return false;
        } else if (!chargeBankName.equals(other.chargeBankName))
            return false;
        if (departmentId == null) {
            if (other.departmentId != null)
                return false;
        } else if (!departmentId.equals(other.departmentId))
            return false;
        if (departmentName == null) {
            if (other.departmentName != null)
                return false;
        } else if (!departmentName.equals(other.departmentName))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "EtchUserDetails [isAccountNonExpired()=" + isAccountNonExpired() + ", isCredentialsNonExpired()=" + isCredentialsNonExpired() + ", getUserId()=" + getUserId() + "]";
    }
    
    

    public QueryUser getQueryUser() {
        QueryUser user = new QueryUser();
        user.setEmployeeId(getUserId());
        user.setEmployeeName(getUserName());
        user.setDepartmentId(getDepartmentId());
        user.setDepartmentName(getDepartmentName());
        user.setRank(getRank());
        user.setQueryRole(getQueryRole());
        return user;
    }
}
