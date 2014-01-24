//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.5 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.01.23 at 05:40:47 PM PST 
//


package org.pesc.core.coremain.v1_12;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * Immunizations Type
 * 
 * <p>Java class for ImmunizationsType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ImmunizationsType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ImmunizationDate" type="{urn:org:pesc:core:CoreMain:v1.12.0}ImmunizationDateType" minOccurs="0"/>
 *         &lt;element name="ImmunizationCode" type="{urn:org:pesc:core:CoreMain:v1.12.0}ImmunizationCodeType" minOccurs="0"/>
 *         &lt;element name="ImmunizationStatusCode" type="{urn:org:pesc:core:CoreMain:v1.12.0}ImmunizationStatusCodeType" minOccurs="0"/>
 *         &lt;element name="NoteMessage" type="{urn:org:pesc:core:CoreMain:v1.12.0}NoteMessageType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ImmunizationsType", propOrder = {
    "immunizationDate",
    "immunizationCode",
    "immunizationStatusCode",
    "noteMessage"
})
public class ImmunizationsType {

    @XmlElement(name = "ImmunizationDate")
    protected XMLGregorianCalendar immunizationDate;
    @XmlElement(name = "ImmunizationCode")
    protected String immunizationCode;
    @XmlElement(name = "ImmunizationStatusCode")
    protected ImmunizationStatusCodeType immunizationStatusCode;
    @XmlElement(name = "NoteMessage")
    protected List<String> noteMessage;

    /**
     * Gets the value of the immunizationDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getImmunizationDate() {
        return immunizationDate;
    }

    /**
     * Sets the value of the immunizationDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setImmunizationDate(XMLGregorianCalendar value) {
        this.immunizationDate = value;
    }

    /**
     * Gets the value of the immunizationCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getImmunizationCode() {
        return immunizationCode;
    }

    /**
     * Sets the value of the immunizationCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setImmunizationCode(String value) {
        this.immunizationCode = value;
    }

    /**
     * Gets the value of the immunizationStatusCode property.
     * 
     * @return
     *     possible object is
     *     {@link ImmunizationStatusCodeType }
     *     
     */
    public ImmunizationStatusCodeType getImmunizationStatusCode() {
        return immunizationStatusCode;
    }

    /**
     * Sets the value of the immunizationStatusCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link ImmunizationStatusCodeType }
     *     
     */
    public void setImmunizationStatusCode(ImmunizationStatusCodeType value) {
        this.immunizationStatusCode = value;
    }

    /**
     * Gets the value of the noteMessage property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the noteMessage property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getNoteMessage().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getNoteMessage() {
        if (noteMessage == null) {
            noteMessage = new ArrayList<String>();
        }
        return this.noteMessage;
    }

}