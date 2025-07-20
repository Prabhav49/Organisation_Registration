package com.prabhav.employee.entity;

public enum Role {
    SUPER_ADMIN("SUPER_ADMIN", "Full system access with all privileges"),
    ADMIN("ADMIN", "Administrative access to manage organizations and users"),
    HR("HR", "HR management access for specific organization"),
    EMPLOYEE("EMPLOYEE", "Basic employee access");

    private final String roleName;
    private final String description;

    Role(String roleName, String description) {
        this.roleName = roleName;
        this.description = description;
    }

    public String getRoleName() {
        return roleName;
    }

    public String getDescription() {
        return description;
    }
}
