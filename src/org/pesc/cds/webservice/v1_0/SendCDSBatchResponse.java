
package org.pesc.cds.webservice.v1_0;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for sendCDSBatchResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="sendCDSBatchResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="sendCDSBatch" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "sendCDSBatchResponse", propOrder = {
    "sendCDSBatch"
})
public class SendCDSBatchResponse {

    protected String sendCDSBatch;

    /**
     * Gets the value of the sendCDSBatch property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSendCDSBatch() {
        return sendCDSBatch;
    }

    /**
     * Sets the value of the sendCDSBatch property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSendCDSBatch(String value) {
        this.sendCDSBatch = value;
    }

}
