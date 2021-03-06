//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.5 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.01.23 at 05:40:47 PM PST 
//


package org.pesc.sector.academicrecord.v1_7;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.pesc.core.coremain.v1_12.BirthType;
import org.pesc.core.coremain.v1_12.ContactsType;
import org.pesc.core.coremain.v1_12.GenderType;
import org.pesc.core.coremain.v1_12.LanguageType;
import org.pesc.core.coremain.v1_12.NameType;


/**
 * The personal and demographics attributes of a student
 * 
 * <p>Java class for TestPersonType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TestPersonType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="StudentIdentification" type="{urn:org:pesc:sector:AcademicRecord:v1.7.0}StudentIdentificationType" minOccurs="0"/>
 *         &lt;element name="Birth" type="{urn:org:pesc:core:CoreMain:v1.12.0}BirthType" minOccurs="0"/>
 *         &lt;element name="Name" type="{urn:org:pesc:core:CoreMain:v1.12.0}NameType"/>
 *         &lt;element name="AlternateName" type="{urn:org:pesc:core:CoreMain:v1.12.0}NameType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="Contacts" type="{urn:org:pesc:core:CoreMain:v1.12.0}ContactsType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="Gender" type="{urn:org:pesc:core:CoreMain:v1.12.0}GenderType" minOccurs="0"/>
 *         &lt;element name="Residency" type="{urn:org:pesc:sector:AcademicRecord:v1.7.0}ResidencyType" minOccurs="0"/>
 *         &lt;element name="Language" type="{urn:org:pesc:core:CoreMain:v1.12.0}LanguageType" maxOccurs="unbounded" minOccurs="0"/>
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
@XmlType(name = "TestPersonType", propOrder = {
    "studentIdentification",
    "birth",
    "name",
    "alternateName",
    "contacts",
    "gender",
    "residency",
    "language",
    "noteMessage"
})
public class TestPersonType {

    @XmlElement(name = "StudentIdentification")
    protected StudentIdentificationType studentIdentification;
    @XmlElement(name = "Birth")
    protected BirthType birth;
    @XmlElement(name = "Name", required = true)
    protected NameType name;
    @XmlElement(name = "AlternateName")
    protected List<NameType> alternateName;
    @XmlElement(name = "Contacts")
    protected List<ContactsType> contacts;
    @XmlElement(name = "Gender")
    protected GenderType gender;
    @XmlElement(name = "Residency")
    protected ResidencyType residency;
    @XmlElement(name = "Language")
    protected List<LanguageType> language;
    @XmlElement(name = "NoteMessage")
    protected List<String> noteMessage;

    /**
     * Gets the value of the studentIdentification property.
     * 
     * @return
     *     possible object is
     *     {@link StudentIdentificationType }
     *     
     */
    public StudentIdentificationType getStudentIdentification() {
        return studentIdentification;
    }

    /**
     * Sets the value of the studentIdentification property.
     * 
     * @param value
     *     allowed object is
     *     {@link StudentIdentificationType }
     *     
     */
    public void setStudentIdentification(StudentIdentificationType value) {
        this.studentIdentification = value;
    }

    /**
     * Gets the value of the birth property.
     * 
     * @return
     *     possible object is
     *     {@link BirthType }
     *     
     */
    public BirthType getBirth() {
        return birth;
    }

    /**
     * Sets the value of the birth property.
     * 
     * @param value
     *     allowed object is
     *     {@link BirthType }
     *     
     */
    public void setBirth(BirthType value) {
        this.birth = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link NameType }
     *     
     */
    public NameType getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link NameType }
     *     
     */
    public void setName(NameType value) {
        this.name = value;
    }

    /**
     * Gets the value of the alternateName property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the alternateName property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAlternateName().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link NameType }
     * 
     * 
     */
    public List<NameType> getAlternateName() {
        if (alternateName == null) {
            alternateName = new ArrayList<NameType>();
        }
        return this.alternateName;
    }

    /**
     * Gets the value of the contacts property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the contacts property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getContacts().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ContactsType }
     * 
     * 
     */
    public List<ContactsType> getContacts() {
        if (contacts == null) {
            contacts = new ArrayList<ContactsType>();
        }
        return this.contacts;
    }

    /**
     * Gets the value of the gender property.
     * 
     * @return
     *     possible object is
     *     {@link GenderType }
     *     
     */
    public GenderType getGender() {
        return gender;
    }

    /**
     * Sets the value of the gender property.
     * 
     * @param value
     *     allowed object is
     *     {@link GenderType }
     *     
     */
    public void setGender(GenderType value) {
        this.gender = value;
    }

    /**
     * Gets the value of the residency property.
     * 
     * @return
     *     possible object is
     *     {@link ResidencyType }
     *     
     */
    public ResidencyType getResidency() {
        return residency;
    }

    /**
     * Sets the value of the residency property.
     * 
     * @param value
     *     allowed object is
     *     {@link ResidencyType }
     *     
     */
    public void setResidency(ResidencyType value) {
        this.residency = value;
    }

    /**
     * Gets the value of the language property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the language property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLanguage().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link LanguageType }
     * 
     * 
     */
    public List<LanguageType> getLanguage() {
        if (language == null) {
            language = new ArrayList<LanguageType>();
        }
        return this.language;
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
