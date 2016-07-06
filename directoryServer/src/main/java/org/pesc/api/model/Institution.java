package org.pesc.api.model;

/**
 * Created by James Whetstone (jwhetstone@ccctechcenter.org) on 2/23/16.
 */

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;
import java.util.Set;


@XmlRootElement(name = "Institution")
@Entity
@Table(name = "organization")
public class Institution implements Serializable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;


    @JoinTable(
            name="institutions_service_providers",
            joinColumns=
            @JoinColumn(name="institution_id", referencedColumnName="id"),
            inverseJoinColumns=
            @JoinColumn(name="service_provider_id", referencedColumnName="id")
    )
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    private Set<Organization> serviceProviders;

    public Set<Organization> getServiceProviders() {
        return serviceProviders;
    }

    public void setServiceProviders(Set<Organization> serviceProviders) {
        this.serviceProviders = serviceProviders;
    }

    public Integer setId(Integer id) {
        return this.id = id;
    }


    @Override
    public int hashCode() {
        return id == null ? 0 : id.hashCode();
    }

    public Integer getId() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (id == null || obj == null || getClass() != obj.getClass())
            return false;
        Institution that = (Institution) obj;
        return id.equals(that.id);
    }

}
