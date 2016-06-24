package org.pesc.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by james on 5/12/16.
 */
@XmlRootElement(name = "Organization")
@Entity
@Table(name = "organization")
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrganizationDTO {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    Integer id;

    @Column(name = "name")
    String name;

    @Column(name = "enabled")
    Boolean enabled;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
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
}
