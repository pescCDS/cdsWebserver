package org.pesc.api.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * Created by James Whetstone (jwhetstone@ccctechcenter.org) on 6/13/16.
 */
@XmlRootElement(name = "InstitutionServiceProviderRelation")
public class InstitutionServiceProviderRelation implements Serializable {

    private int institutionID;


    private int serviceProviderID;

    public int getInstitutionID() {
        return institutionID;
    }

    public void setInstitution_id(int institutionID) {
        this.institutionID = institutionID;
    }

    public int getServiceProviderID() {
        return serviceProviderID;
    }

    public void setServiceProviderID(int serviceProviderID) {
        this.serviceProviderID = serviceProviderID;
    }
}
