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
 * <p>Java class for AcademicProgramTypeType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="AcademicProgramTypeType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Concentration"/>
 *     &lt;enumeration value="Focus"/>
 *     &lt;enumeration value="Major"/>
 *     &lt;enumeration value="Minor"/>
 *     &lt;enumeration value="SecondMajor"/>
 *     &lt;enumeration value="Specialization"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "AcademicProgramTypeType")
@XmlEnum
public enum AcademicProgramTypeType {

    @XmlEnumValue("Concentration")
    CONCENTRATION("Concentration"),
    @XmlEnumValue("Focus")
    FOCUS("Focus"),
    @XmlEnumValue("Major")
    MAJOR("Major"),
    @XmlEnumValue("Minor")
    MINOR("Minor"),
    @XmlEnumValue("SecondMajor")
    SECOND_MAJOR("SecondMajor"),
    @XmlEnumValue("Specialization")
    SPECIALIZATION("Specialization");
    private final String value;

    AcademicProgramTypeType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static AcademicProgramTypeType fromValue(String v) {
        for (AcademicProgramTypeType c: AcademicProgramTypeType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
