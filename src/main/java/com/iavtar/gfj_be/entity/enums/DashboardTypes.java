package com.iavtar.gfj_be.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.iavtar.gfj_be.entity.Dashboard;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum DashboardTypes {

    SUPER_ADMIN_DASHBOARD("super_admin_dashboard"),
    BUSINESS_ADMIN_DASHBOARD("business_admin_dashboard"),
    AGENT_DASHBOARD("agent_dashboard");

    private String value;

    @JsonValue
    public String getValue() {
        return this.value;
    }

    @JsonCreator
    public DashboardTypes from(String data) {
        for (DashboardTypes dashboard : DashboardTypes.values()) {
            if (dashboard.value.equalsIgnoreCase(data)) {
                return dashboard;
            } else {
                throw new IllegalArgumentException(data + " Dashboard not present");
            }
        }
        return null;
    }

}
