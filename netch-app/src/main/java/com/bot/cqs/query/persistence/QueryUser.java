
package com.bot.cqs.query.persistence;

public class QueryUser {

    private String employeeId;
    private String employeeName;
    private String departmentId;
    private String departmentName;
    private String rank;
    private QueryRole queryRole;

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

    public String getEmployeeId() {

        return employeeId;
    }

    public void setEmployeeId(String employeeId) {

        this.employeeId = employeeId;
    }

    public String getEmployeeName() {

        return employeeName;
    }

    public void setEmployeeName(String employeeName) {

        this.employeeName = employeeName;
    }

    public String getRank() {

        return rank;
    }

    public void setRank(String rank) {

        this.rank = rank;
    }

    public boolean hasFunctionAuthority(QueryFunction function) {
        if (getQueryRole() == null)
            return false;
        else
            return getQueryRole().hasFunctionAuthority(function);
    }

    public QueryRole getQueryRole() {

        return queryRole;
    }

    public void setQueryRole(QueryRole queryRole) {

        this.queryRole = queryRole;
    }

}
