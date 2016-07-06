package org.pesc.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;

/**
 * Created by James Whetstone (jwhetstone@ccctechcenter.org) on 4/7/16.
 */
@XmlRootElement(name="SchoolCode")
@Entity
@Table(name = "school_codes")
public class SchoolCode implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Integer id;

    @Column(name="code")
    private String code;

    @Column(name="code_type")
    private String codeType;

    @Column(name = "organization_id")
    private Integer organizationId;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCodeType() {
        return codeType;
    }

    public void setCodeType(String codeType) {
        this.codeType = codeType;
    }

    public Integer getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Integer organizationId) {
        this.organizationId = organizationId;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) { return false; }
        if (obj == this) { return true; }
        if (obj.getClass() != getClass()) {
            return false;
        }
        SchoolCode rhs = (SchoolCode) obj;
        return new EqualsBuilder()
                .appendSuper(super.equals(obj))
                .append(code, rhs.code)
                .append(codeType, rhs.codeType)
                .isEquals();
    }
    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(code).append(codeType).toHashCode();
    }


}
