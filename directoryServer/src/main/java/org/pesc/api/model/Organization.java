package org.pesc.api.model;

/**
 * Created by james on 2/23/16.
 */
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Date;


@XmlRootElement(name = "Organization")
@Entity
@Table(name = "organization")
public class Organization implements Serializable {

    @Column(name = "name")
    private String name;


    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_time")
    private Date created_time;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "modified_time")
    private Date modified_time;

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
    private String short_description;

    @Column(name="type")
    private int type;

    @Column(name="enabled")
    private boolean enabled;

    @Column(name="active")
    private boolean active;


    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public long setId(Integer id) {
        return id;
    }

    public Date getCreatedTime() {
        return created_time;
    }

    public void setCreatedTime(Date created_time) {
        this.created_time = created_time;
    }

    public Date getModifiedTime() {
        return modified_time;
    }

    public void setModifiedTime(Date modified_time) {
        this.modified_time = modified_time;
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
        return short_description;
    }

    public void setShortDescription(String short_description) {
        this.short_description = short_description;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
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

}
