package org.pesc.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;

/**
 * Created by James Whetstone (jwhetstone@ccctechcenter.org) on 3/17/16.
 */
@XmlRootElement(name = "User")
@Entity
@Table(name = "users")
public class DirectoryUser implements Serializable {

    @Column(name="address")
    private String address;

    @Column(name="telephone")
    private String phone;

    @Column(name="email")
    private String email;

    @Column(name = "title")
    private String title;

    @Column(name = "organization_id")
    private Integer organizationId;

    @Column(name = "name")
    private String name;

    @Column(name = "enabled")
    private boolean enabled;

    @Column(name = "username", unique=true)
    private String username;


    @Column(name = "password")
    private String password;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_time")
    private Date createdTime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "modified_time")
    private Date modifiedTime;

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


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Integer getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Integer organizationId) {
        this.organizationId = organizationId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public Date getModifiedTime() {
        return modifiedTime;
    }

    public void setModifiedTime(Date modifiedTime) {
        this.modifiedTime = modifiedTime;
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

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (id == null || obj == null || getClass() != obj.getClass())
            return false;
        DirectoryUser that = (DirectoryUser) obj;
        return id.equals(that.id);
    }
    @Override
    public int hashCode() {
        return id == null ? 0 : id.hashCode();
    }

}
