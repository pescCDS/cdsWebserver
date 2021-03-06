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


/**
 * <p>Java class for RaceEthnicityType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RaceEthnicityType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="RaceEthnicityCode" type="{urn:org:pesc:core:CoreMain:v1.12.0}RaceEthnicityCodeType" minOccurs="0"/>
 *         &lt;element name="LocalRaceEthnicityCode" type="{urn:org:pesc:core:CoreMain:v1.12.0}LocalRaceEthnicityCodeType" maxOccurs="unbounded" minOccurs="0"/>
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
@XmlType(name = "RaceEthnicityType", propOrder = {
    "raceEthnicityCode",
    "localRaceEthnicityCode",
    "noteMessage"
})
public class RaceEthnicityType {

    @XmlElement(name = "RaceEthnicityCode")
    protected String raceEthnicityCode;
    @XmlElement(name = "LocalRaceEthnicityCode")
    protected List<String> localRaceEthnicityCode;
    @XmlElement(name = "NoteMessage")
    protected List<String> noteMessage;

    /**
     * Gets the value of the raceEthnicityCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRaceEthnicityCode() {
        return raceEthnicityCode;
    }

    /**
     * Sets the value of the raceEthnicityCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRaceEthnicityCode(String value) {
        this.raceEthnicityCode = value;
    }

    /**
     * Gets the value of the localRaceEthnicityCode property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the localRaceEthnicityCode property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLocalRaceEthnicityCode().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getLocalRaceEthnicityCode() {
        if (localRaceEthnicityCode == null) {
            localRaceEthnicityCode = new ArrayList<String>();
        }
        return this.localRaceEthnicityCode;
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
