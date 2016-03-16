
package org.pesc.web.model;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for OrganizationContact complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="OrganizationContact">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="contact_id" type="{http://www.w3.org/2001/XMLSchema}positiveInteger"/>
 *         &lt;element name="contact_name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="contact_title" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="contact_type" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="email" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="phone_1" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="phone_2" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="street_address_1" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="street_address_2" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="street_address_3" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="street_address_4" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="city" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="state" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="zip" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="country" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="directory" type="{urn:org:pesc:EdExchange:v1.0.9}Organization"/>
 *         &lt;element name="created_time" type="{urn:org:pesc:EdExchange:v1.0.9}Timestamp"/>
 *         &lt;element name="modified_time" type="{urn:org:pesc:EdExchange:v1.0.9}Timestamp"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OrganizationContact", propOrder = {
        "contactId",
        "contactName",
        "contactTitle",
        "contactType",
        "email",
        "phone1",
        "phone2",
        "streetAddress1",
        "streetAddress2",
        "streetAddress3",
        "streetAddress4",
        "city",
        "state",
        "zip",
        "country",
        "directory"
})
public class OrganizationContact {

  @XmlElement(name = "contact_id", required = true, type = String.class)
  @XmlSchemaType(name = "positiveInteger")
  protected Integer contactId;
  @XmlElement(name = "contact_name", required = true)
  protected String contactName;
  @XmlElement(name = "contact_title", required = true)
  protected String contactTitle;
  @XmlElement(name = "contact_type", required = true)
  protected String contactType;
  @XmlElement(required = true)
  protected String email;
  @XmlElement(name = "phone_1", required = true)
  protected String phone1;
  @XmlElement(name = "phone_2", required = true)
  protected String phone2;
  @XmlElement(name = "street_address_1", required = true)
  protected String streetAddress1;
  @XmlElement(name = "street_address_2", required = true)
  protected String streetAddress2;
  @XmlElement(name = "street_address_3", required = true)
  protected String streetAddress3;
  @XmlElement(name = "street_address_4", required = true)
  protected String streetAddress4;
  @XmlElement(required = true)
  protected String city;
  @XmlElement(required = true)
  protected String state;
  @XmlElement(required = true)
  protected String zip;
  @XmlElement(required = true)
  protected String country;

  /**
   * Gets the value of the contactId property.
   *
   * @return
   *     possible object is
   *     {@link String }
   *
   */
  public Integer getContactId() {
    return contactId;
  }

  /**
   * Sets the value of the contactId property.
   *
   * @param value
   *     allowed object is
   *     {@link String }
   *
   */
  public void setContactId(Integer value) {
    this.contactId = value;
  }

  /**
   * Gets the value of the contactName property.
   *
   * @return
   *     possible object is
   *     {@link String }
   *
   */
  public String getContactName() {
    return contactName;
  }

  /**
   * Sets the value of the contactName property.
   *
   * @param value
   *     allowed object is
   *     {@link String }
   *
   */
  public void setContactName(String value) {
    this.contactName = value;
  }

  /**
   * Gets the value of the contactTitle property.
   *
   * @return
   *     possible object is
   *     {@link String }
   *
   */
  public String getContactTitle() {
    return contactTitle;
  }

  /**
   * Sets the value of the contactTitle property.
   *
   * @param value
   *     allowed object is
   *     {@link String }
   *
   */
  public void setContactTitle(String value) {
    this.contactTitle = value;
  }

  /**
   * Gets the value of the contactType property.
   *
   * @return
   *     possible object is
   *     {@link String }
   *
   */
  public String getContactType() {
    return contactType;
  }

  /**
   * Sets the value of the contactType property.
   *
   * @param value
   *     allowed object is
   *     {@link String }
   *
   */
  public void setContactType(String value) {
    this.contactType = value;
  }

  /**
   * Gets the value of the email property.
   *
   * @return
   *     possible object is
   *     {@link String }
   *
   */
  public String getEmail() {
    return email;
  }

  /**
   * Sets the value of the email property.
   *
   * @param value
   *     allowed object is
   *     {@link String }
   *
   */
  public void setEmail(String value) {
    this.email = value;
  }

  /**
   * Gets the value of the phone1 property.
   *
   * @return
   *     possible object is
   *     {@link String }
   *
   */
  public String getPhone1() {
    return phone1;
  }

  /**
   * Sets the value of the phone1 property.
   *
   * @param value
   *     allowed object is
   *     {@link String }
   *
   */
  public void setPhone1(String value) {
    this.phone1 = value;
  }

  /**
   * Gets the value of the phone2 property.
   *
   * @return
   *     possible object is
   *     {@link String }
   *
   */
  public String getPhone2() {
    return phone2;
  }

  /**
   * Sets the value of the phone2 property.
   *
   * @param value
   *     allowed object is
   *     {@link String }
   *
   */
  public void setPhone2(String value) {
    this.phone2 = value;
  }

  /**
   * Gets the value of the streetAddress1 property.
   *
   * @return
   *     possible object is
   *     {@link String }
   *
   */
  public String getStreetAddress1() {
    return streetAddress1;
  }

  /**
   * Sets the value of the streetAddress1 property.
   *
   * @param value
   *     allowed object is
   *     {@link String }
   *
   */
  public void setStreetAddress1(String value) {
    this.streetAddress1 = value;
  }

  /**
   * Gets the value of the streetAddress2 property.
   *
   * @return
   *     possible object is
   *     {@link String }
   *
   */
  public String getStreetAddress2() {
    return streetAddress2;
  }

  /**
   * Sets the value of the streetAddress2 property.
   *
   * @param value
   *     allowed object is
   *     {@link String }
   *
   */
  public void setStreetAddress2(String value) {
    this.streetAddress2 = value;
  }

  /**
   * Gets the value of the streetAddress3 property.
   *
   * @return
   *     possible object is
   *     {@link String }
   *
   */
  public String getStreetAddress3() {
    return streetAddress3;
  }

  /**
   * Sets the value of the streetAddress3 property.
   *
   * @param value
   *     allowed object is
   *     {@link String }
   *
   */
  public void setStreetAddress3(String value) {
    this.streetAddress3 = value;
  }

  /**
   * Gets the value of the streetAddress4 property.
   *
   * @return
   *     possible object is
   *     {@link String }
   *
   */
  public String getStreetAddress4() {
    return streetAddress4;
  }

  /**
   * Sets the value of the streetAddress4 property.
   *
   * @param value
   *     allowed object is
   *     {@link String }
   *
   */
  public void setStreetAddress4(String value) {
    this.streetAddress4 = value;
  }

  /**
   * Gets the value of the city property.
   *
   * @return
   *     possible object is
   *     {@link String }
   *
   */
  public String getCity() {
    return city;
  }

  /**
   * Sets the value of the city property.
   *
   * @param value
   *     allowed object is
   *     {@link String }
   *
   */
  public void setCity(String value) {
    this.city = value;
  }

  /**
   * Gets the value of the state property.
   *
   * @return
   *     possible object is
   *     {@link String }
   *
   */
  public String getState() {
    return state;
  }

  /**
   * Sets the value of the state property.
   *
   * @param value
   *     allowed object is
   *     {@link String }
   *
   */
  public void setState(String value) {
    this.state = value;
  }

  /**
   * Gets the value of the zip property.
   *
   * @return
   *     possible object is
   *     {@link String }
   *
   */
  public String getZip() {
    return zip;
  }

  /**
   * Sets the value of the zip property.
   *
   * @param value
   *     allowed object is
   *     {@link String }
   *
   */
  public void setZip(String value) {
    this.zip = value;
  }

  /**
   * Gets the value of the country property.
   *
   * @return
   *     possible object is
   *     {@link String }
   *
   */
  public String getCountry() {
    return country;
  }

  /**
   * Sets the value of the country property.
   *
   * @param value
   *     allowed object is
   *     {@link String }
   *
   */
  public void setCountry(String value) {
    this.country = value;
  }



}
