package org.pesc.api.model;

/**
 * Created by james on 3/17/16.
 */
public class User {
    private String username;
    private String name;
    private String address;
    private String phone;
    private String title;
    private Integer organization_id;
    private boolean hasSystemAdminRole;
    private boolean hasOrgAdminRole;
    private boolean hasSupportRole;

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

    public boolean isHasSupportRole() {
        return hasSupportRole;
    }

    public void setHasSupportRole(boolean hasSupportRole) {
        this.hasSupportRole = hasSupportRole;
    }

    public boolean isHasOrgAdminRole() {
        return hasOrgAdminRole;
    }

    public void setHasOrgAdminRole(boolean hasOrgAdminRole) {
        this.hasOrgAdminRole = hasOrgAdminRole;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getOrganization_id() {
        return organization_id;
    }

    public void setOrganization_id(Integer organization_id) {
        this.organization_id = organization_id;
    }
}
