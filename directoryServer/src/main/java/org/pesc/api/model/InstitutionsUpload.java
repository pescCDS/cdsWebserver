package org.pesc.api.model;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by James Whetstone (jwhetstone@ccctechcenter.org) on 3/17/16.
 */
@XmlRootElement(name = "InstitutionUpload")
@Entity
@Table(name = "institution_uploads")
public class InstitutionsUpload implements Serializable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "organization_id")
    private Integer organizationId;

    @Column(name = "user_id")
    private Integer userId;


    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "start_time")
    private Date startTime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "end_time")
    private Date endTime;


    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_time")
    private Date createdTime;

    @Column(name = "input_path")
    private String inputPath;

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

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getInputPath() {
        return inputPath;
    }

    public void setInputPath(String inputPath) {
        this.inputPath = inputPath;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (id == null || obj == null || getClass() != obj.getClass())
            return false;
        InstitutionsUpload that = (InstitutionsUpload) obj;
        return id.equals(that.id);
    }
    @Override
    public int hashCode() {
        return id == null ? 0 : id.hashCode();
    }

}
