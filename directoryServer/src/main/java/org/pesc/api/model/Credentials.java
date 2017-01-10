/*
 * Copyright (c) 2017. California Community Colleges Technology Center
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.pesc.api.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * Created by James Whetstone (jwhetstone@ccctechcenter.org) on 4/1/16.
 */
@Entity
@Table(name = "users")
public class Credentials {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER, targetEntity = Organization.class, cascade = CascadeType.DETACH)
    @JoinColumn(name="organization_id")
    private Organization organization;

    @Column(name = "username", unique=true)
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "enabled")
    private boolean enabled;

    @JoinTable(
            name="users_roles",
            joinColumns=
            @JoinColumn(name="users_id", referencedColumnName="id"),
            inverseJoinColumns=
            @JoinColumn(name="roles_id", referencedColumnName="id")
    )
    @ManyToMany(fetch = FetchType.EAGER, targetEntity = Role.class, cascade = CascadeType.MERGE)
    private Set<Role> roles;

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }
}
