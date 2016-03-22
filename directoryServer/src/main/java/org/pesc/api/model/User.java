package org.pesc.api.model;

/**
 * Created by james on 3/17/16.
 */
public class User {
    private String username;
    private String name;
    private boolean hasSystemAdminRole;
    private boolean hasOrgAdminRole;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isHasSystemAdminRole() {
        return hasSystemAdminRole;
    }

    public void setHasSystemAdminRole(boolean hasSystemAdminRole) {
        this.hasSystemAdminRole = hasSystemAdminRole;
    }

    public boolean isHasOrgAdminRole() {
        return hasOrgAdminRole;
    }

    public void setHasOrgAdminRole(boolean hasOrgAdminRole) {
        this.hasOrgAdminRole = hasOrgAdminRole;
    }
}
