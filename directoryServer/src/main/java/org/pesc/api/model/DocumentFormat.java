package org.pesc.api.model;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by James Whetstone (jwhetstone@ccctechcenter.org) on 4/6/16.
 */
@XmlRootElement(name = "DocumentFormat")
@Entity
@Table(name="document_format")
public class DocumentFormat {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "name")
    String name;

    @Column(name = "description")
    String description;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
