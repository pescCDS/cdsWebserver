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
    /*
    CREATE TABLE endpoint (
            id INT(11) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
    organization_id INT(11) UNSIGNED NOT NULL COMMENT 'foreign key to the organization table',
    document_format_id INT(11) UNSIGNED NOT NULL COMMENT 'foreign key to the document_format table',
    webservice_url VARCHAR(256),
    delivery_method_id INT(11) UNSIGNED NOT NULL COMMENT 'foreign key to the delivery_methods record primary key',
    delivery_confirm BIT(1) NULL,
    error BIT(1) NULL,
    operational_status ENUM('INACTIVE','ACTIVE','OUTAGE') NOT NULL,
    PRIMARY KEY (id),
    KEY fk_endpoint_organization_idx (organization_id),
    CONSTRAINT fk_endpoint_organization FOREIGN KEY (organization_id) REFERENCES organization (id)
    ON DELETE RESTRICT ON UPDATE RESTRICT,
    KEY fk_endpoint_document_format_idx (document_format_id),
    CONSTRAINT fk_endpoint_document_format FOREIGN KEY (document_format_id) REFERENCES document_format (id)
    ON DELETE RESTRICT ON UPDATE RESTRICT,
    KEY fk_endpoint_delivery_method_idx (delivery_method_id),
    CONSTRAINT fk_endpoint_delivery_method FOREIGN KEY (delivery_method_id) REFERENCES delivery_methods (id)
    ON DELETE RESTRICT ON UPDATE RESTRICT
    );
    */


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
    @ManyToMany(fetch = FetchType.EAGER, targetEntity = Role.class, cascade = CascadeType.MERGE)
    private Set<DocumentFormat> documentFormats;

    @JoinTable(
            name="endpoint_delivery_methods",
            joinColumns=
            @JoinColumn(name="endpoint_id", referencedColumnName="id"),
            inverseJoinColumns=
            @JoinColumn(name="delivery_methods_id", referencedColumnName="id")
    )
    @ManyToMany(fetch = FetchType.EAGER, targetEntity = Role.class, cascade = CascadeType.MERGE)
    private Set<DeliveryMethod> deliveryMethods;


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

    public Set<DocumentFormat> getDocumentFormats() {
        return documentFormats;
    }

    public void setDocumentFormats(Set<DocumentFormat> documentFormats) {
        this.documentFormats = documentFormats;
    }

    public Set<DeliveryMethod> getDeliveryMethods() {
        return deliveryMethods;
    }

    public void setDeliveryMethods(Set<DeliveryMethod> deliveryMethods) {
        this.deliveryMethods = deliveryMethods;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }
}
