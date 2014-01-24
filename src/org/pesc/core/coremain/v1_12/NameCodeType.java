//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.5 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.01.23 at 05:40:47 PM PST 
//


package org.pesc.core.coremain.v1_12;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for NameCodeType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="NameCodeType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Casual"/>
 *     &lt;enumeration value="Formal"/>
 *     &lt;enumeration value="Former"/>
 *     &lt;enumeration value="LawSchoolApplicationSource"/>
 *     &lt;enumeration value="Legal"/>
 *     &lt;enumeration value="MedicalSchoolApplicationSource"/>
 *     &lt;enumeration value="Nickname"/>
 *     &lt;enumeration value="Other"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "NameCodeType")
@XmlEnum
public enum NameCodeType {

    @XmlEnumValue("Casual")
    CASUAL("Casual"),
    @XmlEnumValue("Formal")
    FORMAL("Formal"),
    @XmlEnumValue("Former")
    FORMER("Former"),
    @XmlEnumValue("LawSchoolApplicationSource")
    LAW_SCHOOL_APPLICATION_SOURCE("LawSchoolApplicationSource"),
    @XmlEnumValue("Legal")
    LEGAL("Legal"),
    @XmlEnumValue("MedicalSchoolApplicationSource")
    MEDICAL_SCHOOL_APPLICATION_SOURCE("MedicalSchoolApplicationSource"),
    @XmlEnumValue("Nickname")
    NICKNAME("Nickname"),
    @XmlEnumValue("Other")
    OTHER("Other");
    private final String value;

    NameCodeType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static NameCodeType fromValue(String v) {
        for (NameCodeType c: NameCodeType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}