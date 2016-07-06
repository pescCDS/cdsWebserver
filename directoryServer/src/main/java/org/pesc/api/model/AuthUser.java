package org.pesc.api.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.Set;

/**
 * Extending the Spring User, which is used as the principal during and after authentication so that the additional
 * properties can be used in method level security SpEL expressions in the service layer.
 * Created by James Whetstone (jwhetstone@ccctechcenter.org) on 3/17/16.
 */
public class AuthUser extends User {
    private Integer id;
    private Integer organizationId;
    private Set<OrganizationType> organizationTypes;

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

    public Integer getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Integer organizationId) {
        this.organizationId = organizationId;
    }

    public Set<OrganizationType> getOrganizationTypes() {
        return organizationTypes;
    }

    public void setOrganizationTypes(Set<OrganizationType> organizationTypes) {
        this.organizationTypes = organizationTypes;
    }
}
