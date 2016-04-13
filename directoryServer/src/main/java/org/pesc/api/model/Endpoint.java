package org.pesc.api.model;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Set;

/**
 * Created by james on 4/6/16.
 */
@XmlRootElement(name="Endpoint")
@Entity
@Table(name="endpoint")
public class Endpoint {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "organization_id")
    private Integer organizationId;

    @Column(name = "delivery_confirm")
    private boolean confirmDelivery;

    @Column(name = "address")
    private String address;

    @Column(name = "error")
    private boolean error;

    @Column(name="instructions")
    private String instructions;


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
}
