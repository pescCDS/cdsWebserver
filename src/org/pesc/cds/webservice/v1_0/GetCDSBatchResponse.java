
package org.pesc.cds.webservice.v1_0;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getCDSBatchResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getCDSBatchResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="getCDSBatch" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getCDSBatchResponse", propOrder = {
    "getCDSBatch"
})
public class GetCDSBatchResponse {

    protected String getCDSBatch;

    /**
     * Gets the value of the getCDSBatch property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGetCDSBatch() {
        return getCDSBatch;
    }

    /**
     * Sets the value of the getCDSBatch property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGetCDSBatch(String value) {
        this.getCDSBatch = value;
    }

}
