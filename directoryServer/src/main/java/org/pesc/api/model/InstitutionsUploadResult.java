package org.pesc.api.model;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by James Whetstone (jwhetstone@ccctechcenter.org) on 5/10/16.
 */
@XmlRootElement(name = "InstitutionUploadResult")
@Entity
@Table(name = "institution_upload_results")
public class InstitutionsUploadResult implements Serializable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "message")
    private String message;

    @Column(name = "institution_id")
    private Integer institutionID;

    @Column(name = "institution_name")
    private String institutionName;

    @Column(name = "organization_id")
    private Integer organizationID;

    @Column(name = "institution_upload_id")
    private Integer institutionUploadID;

    @Column(name = "line_number")
    private Integer lineNumber;

    @Column(name = "outcome")
    private String outcome;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_time")
    private Date createdTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getInstitutionID() {
        return institutionID;
    }

    public void setInstitutionID(Integer institutionID) {
        this.institutionID = institutionID;
    }

    public String getInstitutionName() {
        return institutionName;
    }

    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }

    public Integer getOrganizationID() {
        return organizationID;
    }

    public void setOrganizationID(Integer organizationID) {
        this.organizationID = organizationID;
    }

    public Integer getInstitutionUploadID() {
        return institutionUploadID;
    }

    public void setInstitutionUploadID(Integer institutionUploadID) {
        this.institutionUploadID = institutionUploadID;
    }

    public String getOutcome() {
        return outcome;
    }

    public void setOutcome(String outcome) {
        this.outcome = outcome;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }


    public Integer getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(Integer lineNumber) {
        this.lineNumber = lineNumber;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (id == null || obj == null || getClass() != obj.getClass())
            return false;
        InstitutionsUploadResult that = (InstitutionsUploadResult) obj;
        return id.equals(that.id);
    }
    @Override
    public int hashCode() {
        return id == null ? 0 : id.hashCode();
    }
}
