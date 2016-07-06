package org.pesc.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.List;
import java.util.Set;

/**
 * Created by James Whetstone (jwhetstone@ccctechcenter.org) on 4/6/16.
 */
@XmlRootElement(name="Endpoint")
@Entity
@Table(name="endpoint")
public class Endpoint {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER, targetEntity = OrganizationDTO.class, cascade = CascadeType.DETACH)
    @JoinColumn(name="organization_id")
    private OrganizationDTO organization;

    @Column(name = "delivery_confirm")
    private boolean confirmDelivery;

    @Column(name = "address")
    private String address;

    @Column(name = "error")
    private boolean error;

    @Column(name="instructions")
    private String instructions;

    @Column(name="mode")
    private String mode;

    @JoinTable(
            name="endpoint_document_format",
            joinColumns=
            @JoinColumn(name="endpoint_id", referencedColumnName="id"),
            inverseJoinColumns=
            @JoinColumn(name="document_format_id", referencedColumnName="id")
    )
    @ManyToOne(fetch = FetchType.EAGER, targetEntity = DocumentFormat.class, cascade = CascadeType.MERGE)
    private DocumentFormat documentFormat;

    @JoinTable(
            name="endpoint_delivery_methods",
            joinColumns=
            @JoinColumn(name="endpoint_id", referencedColumnName="id"),
            inverseJoinColumns=
            @JoinColumn(name="delivery_methods_id", referencedColumnName="id")
    )
    @ManyToOne(fetch = FetchType.EAGER, targetEntity = DeliveryMethod.class, cascade = CascadeType.MERGE)
    private DeliveryMethod deliveryMethod;

    @JoinTable(
            name="endpoint_document_types",
            joinColumns=
            @JoinColumn(name="endpoint_id", referencedColumnName="id"),
            inverseJoinColumns=
            @JoinColumn(name="document_types_id", referencedColumnName="id")
    )
    @ManyToOne(fetch = FetchType.EAGER, targetEntity = DocumentType.class, cascade = CascadeType.MERGE)
    private DocumentType documentType;


    @JoinTable(
            name="endpoint_departments",
            joinColumns=
            @JoinColumn(name="endpoint_id", referencedColumnName="id"),
            inverseJoinColumns=
            @JoinColumn(name="departments_id", referencedColumnName="id")
    )
    @ManyToOne(fetch = FetchType.EAGER, targetEntity = Department.class, cascade = CascadeType.MERGE)
    private Department department;


    @JoinTable(
            name="endpoint_organization",
            joinColumns=
            @JoinColumn(name="endpoint_id", referencedColumnName="id"),
            inverseJoinColumns=
            @JoinColumn(name="organization_id", referencedColumnName="id")
    )
    @ManyToMany(fetch = FetchType.EAGER, targetEntity = OrganizationDTO.class, cascade = CascadeType.DETACH)
    private Set<OrganizationDTO> organizations;


    public Set<OrganizationDTO> getOrganizations() {
        return organizations;
    }

    public void setOrganizations(Set<OrganizationDTO> organizations) {
        this.organizations = organizations;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public OrganizationDTO getOrganization() {
        return organization;
    }

    public void setOrganization(OrganizationDTO organization) {
        this.organization = organization;
    }

    public boolean isConfirmDelivery() {
        return confirmDelivery;
    }

    public void setConfirmDelivery(boolean confirmDelivery) {
        this.confirmDelivery = confirmDelivery;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public DocumentFormat getDocumentFormat() {
        return documentFormat;
    }

    public void setDocumentFormat(DocumentFormat documentFormat) {
        this.documentFormat = documentFormat;
    }

    public DeliveryMethod getDeliveryMethod() {
        return deliveryMethod;
    }

    public void setDeliveryMethod(DeliveryMethod deliveryMethod) {
        this.deliveryMethod = deliveryMethod;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public DocumentType getDocumentType() {
        return documentType;
    }

    public void setDocumentType(DocumentType documentType) {
        this.documentType = documentType;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }
}
