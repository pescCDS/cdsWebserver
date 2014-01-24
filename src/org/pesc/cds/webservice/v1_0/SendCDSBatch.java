
package org.pesc.cds.webservice.v1_0;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for sendCDSBatch complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="sendCDSBatch">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="psisCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="username" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="password" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="CDSBatchXML" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "sendCDSBatch", propOrder = {
    "psisCode",
    "username",
    "password",
    "cdsBatchXML"
})
public class SendCDSBatch {

    protected String psisCode;
    protected String username;
    protected String password;
    @XmlElement(name = "CDSBatchXML")
    protected String cdsBatchXML;

    /**
     * Gets the value of the psisCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPsisCode() {
        return psisCode;
    }

    /**
     * Sets the value of the psisCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPsisCode(String value) {
        this.psisCode = value;
    }

    /**
     * Gets the value of the username property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the value of the username property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUsername(String value) {
        this.username = value;
    }

    /**
     * Gets the value of the password property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the value of the password property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPassword(String value) {
        this.password = value;
    }

    /**
     * Gets the value of the cdsBatchXML property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCDSBatchXML() {
        return cdsBatchXML;
    }

    /**
     * Sets the value of the cdsBatchXML property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCDSBatchXML(String value) {
        this.cdsBatchXML = value;
    }

}
