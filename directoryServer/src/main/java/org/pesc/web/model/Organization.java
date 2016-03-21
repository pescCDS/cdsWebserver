package org.pesc.web.model;

/**
 * Created by james on 2/23/16.
 */
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Organization")
public class Organization {
    String name;
    String id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
