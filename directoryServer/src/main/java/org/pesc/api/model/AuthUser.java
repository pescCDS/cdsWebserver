package org.pesc.api.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

/**
 * Created by james on 3/17/16.
 */
public class AuthUser extends User {
    private Integer id;
    private String name;
    private Integer organizationId;
    private boolean hasSystemAdminRole;
    private boolean hasOrgAdminRole;
    private boolean hasSupportRole;

    public AuthUser(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
    }

    public AuthUser(String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
        super(username,password,enabled,accountNonExpired,credentialsNonExpired,accountNonLocked,authorities);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Integer getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Integer organizationId) {
        this.organizationId = organizationId;
    }
}
