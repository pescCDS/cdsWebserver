//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.5 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.01.23 at 05:40:47 PM PST 
//


package org.pesc.core.coremain.v1_12;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for BirthType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="BirthType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="BirthDate" type="{urn:org:pesc:core:CoreMain:v1.12.0}BirthDateType" minOccurs="0"/>
 *         &lt;element name="Birthday" type="{urn:org:pesc:core:CoreMain:v1.12.0}BirthdayType" minOccurs="0"/>
 *         &lt;element name="BirthYear" type="{urn:org:pesc:core:CoreMain:v1.12.0}BirthYearType" minOccurs="0"/>
 *         &lt;element name="BirthCity" type="{urn:org:pesc:core:CoreMain:v1.12.0}BirthCityType" minOccurs="0"/>
 *         &lt;element name="BirthStateProvinceCode" type="{urn:org:pesc:core:CoreMain:v1.12.0}BirthStateProvinceCodeType" minOccurs="0"/>
 *         &lt;element name="BirthCountry" type="{urn:org:pesc:core:CoreMain:v1.12.0}BirthCountryCodeType" minOccurs="0"/>
 *         &lt;element name="Age" type="{urn:org:pesc:core:CoreMain:v1.12.0}AgeType" minOccurs="0"/>
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
@XmlType(name = "BirthType", propOrder = {
    "birthDate",
    "birthday",
    "birthYear",
    "birthCity",
    "birthStateProvinceCode",
    "birthCountry",
    "age",
    "noteMessage"
})
public class BirthType {

    @XmlElement(name = "BirthDate")
    protected XMLGregorianCalendar birthDate;
    @XmlElement(name = "Birthday")
    protected XMLGregorianCalendar birthday;
    @XmlElement(name = "BirthYear")
    protected XMLGregorianCalendar birthYear;
    @XmlElement(name = "BirthCity")
    protected String birthCity;
    @XmlElement(name = "BirthStateProvinceCode")
    protected StateProvinceCodeType birthStateProvinceCode;
    @XmlElement(name = "BirthCountry")
    protected CountryCodeType birthCountry;
    @XmlElement(name = "Age")
    protected BigInteger age;
    @XmlElement(name = "NoteMessage")
    protected List<String> noteMessage;

    /**
     * Gets the value of the birthDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getBirthDate() {
        return birthDate;
    }

    /**
     * Sets the value of the birthDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setBirthDate(XMLGregorianCalendar value) {
        this.birthDate = value;
    }

    /**
     * Gets the value of the birthday property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getBirthday() {
        return birthday;
    }

    /**
     * Sets the value of the birthday property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setBirthday(XMLGregorianCalendar value) {
        this.birthday = value;
    }

    /**
     * Gets the value of the birthYear property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getBirthYear() {
        return birthYear;
    }

    /**
     * Sets the value of the birthYear property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setBirthYear(XMLGregorianCalendar value) {
        this.birthYear = value;
    }

    /**
     * Gets the value of the birthCity property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBirthCity() {
        return birthCity;
    }

    /**
     * Sets the value of the birthCity property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBirthCity(String value) {
        this.birthCity = value;
    }

    /**
     * Gets the value of the birthStateProvinceCode property.
     * 
     * @return
     *     possible object is
     *     {@link StateProvinceCodeType }
     *     
     */
    public StateProvinceCodeType getBirthStateProvinceCode() {
        return birthStateProvinceCode;
    }

    /**
     * Sets the value of the birthStateProvinceCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link StateProvinceCodeType }
     *     
     */
    public void setBirthStateProvinceCode(StateProvinceCodeType value) {
        this.birthStateProvinceCode = value;
    }

    /**
     * Gets the value of the birthCountry property.
     * 
     * @return
     *     possible object is
     *     {@link CountryCodeType }
     *     
     */
    public CountryCodeType getBirthCountry() {
        return birthCountry;
    }

    /**
     * Sets the value of the birthCountry property.
     * 
     * @param value
     *     allowed object is
     *     {@link CountryCodeType }
     *     
     */
    public void setBirthCountry(CountryCodeType value) {
        this.birthCountry = value;
    }

    /**
     * Gets the value of the age property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getAge() {
        return age;
    }

    /**
     * Sets the value of the age property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setAge(BigInteger value) {
        this.age = value;
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