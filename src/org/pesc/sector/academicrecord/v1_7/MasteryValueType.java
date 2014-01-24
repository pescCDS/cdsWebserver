//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.5 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.01.23 at 05:40:47 PM PST 
//


package org.pesc.sector.academicrecord.v1_7;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * An indicator of the studrent's competence in a subject area
 * 
 * <p>Java class for MasteryValueType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="MasteryValueType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="MasteredIndicator" type="{urn:org:pesc:core:CoreMain:v1.12.0}MasteredIndicatorType"/>
 *         &lt;element name="CutScore" type="{urn:org:pesc:core:CoreMain:v1.12.0}CutScoreType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MasteryValueType", propOrder = {
    "masteredIndicator",
    "cutScore"
})
public class MasteryValueType {

    @XmlElement(name = "MasteredIndicator")
    protected boolean masteredIndicator;
    @XmlElement(name = "CutScore")
    protected BigDecimal cutScore;

    /**
     * Gets the value of the masteredIndicator property.
     * 
     */
    public boolean isMasteredIndicator() {
        return masteredIndicator;
    }

    /**
     * Sets the value of the masteredIndicator property.
     * 
     */
    public void setMasteredIndicator(boolean value) {
        this.masteredIndicator = value;
    }

    /**
     * Gets the value of the cutScore property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getCutScore() {
        return cutScore;
    }

    /**
     * Sets the value of the cutScore property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setCutScore(BigDecimal value) {
        this.cutScore = value;
    }

}