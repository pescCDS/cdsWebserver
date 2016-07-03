package org.pesc.api.model;

/**
 * Created by james on 2/23/16.
 */

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;


@XmlRootElement(name = "Organization")
@Entity
@Table(name = "organization")
@JsonPropertyOrder({"name", "website", "street", "city", "state", "zip", "organizationTypes", "schoolCodes"})
@ApiModel
public class Organization implements Serializable {

    @Column(name = "name")
    @ApiModelProperty(position = 1, required = true, value = "The free form name of the organization.")
    private String name;


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

    @Column(name = "website")
    private String website;

    @Column(name = "street")
    private String street;
    @Column(name = "city")
    private String city;

    @Column(name = "state")
    private String state;

    @Column(name = "zip")
    private String zip;


    @Column(name = "telephone")
    private String telephone;

    @Column(name = "short_description")
    private String shortDescription;

    @JoinTable(
            name = "org_orgtype",
            joinColumns =
            @JoinColumn(name = "organization_id", referencedColumnName = "id"),
            inverseJoinColumns =
            @JoinColumn(name = "organization_type_id", referencedColumnName = "id")
    )
    @ManyToMany(fetch = FetchType.EAGER, targetEntity = OrganizationType.class, cascade = CascadeType.MERGE)
    private Set<OrganizationType> organizationTypes;

    @ApiModelProperty(value = "Enabled", allowableValues = "true/false")
    @Column(name = "enabled")
    private boolean enabled;

    @Column(name = "active")
    private boolean active;


    @OneToMany(fetch = FetchType.EAGER,
            targetEntity = SchoolCode.class,
            cascade = {CascadeType.REMOVE, CascadeType.ALL},
            orphanRemoval = true)
    @JoinColumn(name = "organization_id")
    private Set<SchoolCode> schoolCodes;

    @OneToMany(fetch = FetchType.EAGER,
            targetEntity = Contact.class,
            cascade = {CascadeType.REMOVE, CascadeType.ALL},
            orphanRemoval = true)
    @JoinColumn(name = "organization_id")
    private Set<Contact> contacts;


    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public Integer setId(Integer id) {
        return this.id = id;
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

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public Set<OrganizationType> getOrganizationTypes() {
        return organizationTypes;
    }

    public void setOrganizationTypes(Set<OrganizationType> organizationTypes) {
        this.organizationTypes = organizationTypes;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }


    public Set<SchoolCode> getSchoolCodes() {
        return schoolCodes;
    }

    public void setSchoolCodes(Set<SchoolCode> schoolCodes) {
        this.schoolCodes = schoolCodes;
    }


    public Set<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(Set<Contact> contacts) {
        this.contacts = contacts;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (id == null || obj == null || getClass() != obj.getClass())
            return false;
        Organization that = (Organization) obj;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id == null ? 0 : id.hashCode();
    }

    /*
    @Override
    public boolean equals(Object obj) {
        if (obj == null) { return false; }
        if (obj == this) { return true; }
        if (obj.getClass() != getClass()) {
            return false;
        }
        Organization rhs = (Organization) obj;
        return new EqualsBuilder()
                .appendSuper(super.equals(obj))
                .append(id, rhs.id)
                .append(name, rhs.name)
                .append(street, rhs.street)
                .append(city, rhs.city)
                .append(state, rhs.state)
                .append(zip, rhs.zip)
                .append(website, rhs.website)
                .isEquals();
    }
    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(id).append(name).append(street).append(city).append(state).append(zip).append(website).toHashCode();
    }
    */

}
