//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.5 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.01.23 at 05:40:47 PM PST 
//


package org.pesc.sector.academicrecord.v1_7;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Information about a constructed response item
 * 
 * <p>Java class for EssayType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="EssayType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="EssayText" type="{urn:org:pesc:core:CoreMain:v1.12.0}EssayTextType" minOccurs="0"/>
 *         &lt;element name="EssayPrompt" type="{urn:org:pesc:core:CoreMain:v1.12.0}PromptType" minOccurs="0"/>
 *         &lt;element name="EssayURL" type="{urn:org:pesc:core:CoreMain:v1.12.0}URLAddressType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EssayType", propOrder = {
    "essayText",
    "essayPrompt",
    "essayURL"
})
public class EssayType {

    @XmlElement(name = "EssayText")
    protected String essayText;
    @XmlElement(name = "EssayPrompt")
    protected String essayPrompt;
    @XmlElement(name = "EssayURL")
    protected String essayURL;

    /**
     * Gets the value of the essayText property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEssayText() {
        return essayText;
    }

    /**
     * Sets the value of the essayText property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEssayText(String value) {
        this.essayText = value;
    }

    /**
     * Gets the value of the essayPrompt property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEssayPrompt() {
        return essayPrompt;
    }

    /**
     * Sets the value of the essayPrompt property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEssayPrompt(String value) {
        this.essayPrompt = value;
    }

    /**
     * Gets the value of the essayURL property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEssayURL() {
        return essayURL;
    }

    /**
     * Sets the value of the essayURL property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEssayURL(String value) {
        this.essayURL = value;
    }

}
