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
 * <p>Java class for ImmunizationStatusCodeType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ImmunizationStatusCodeType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="EighthInnoculation"/>
 *     &lt;enumeration value="FifthInnoculation"/>
 *     &lt;enumeration value="FirstInnoculation"/>
 *     &lt;enumeration value="FourthInnoculation"/>
 *     &lt;enumeration value="HadTheDisease"/>
 *     &lt;enumeration value="MedicalExemption"/>
 *     &lt;enumeration value="NinthInnoculation"/>
 *     &lt;enumeration value="PersonalExemption"/>
 *     &lt;enumeration value="ReligiousExemption"/>
 *     &lt;enumeration value="SecondInnoculation"/>
 *     &lt;enumeration value="SeventhInnoculation"/>
 *     &lt;enumeration value="SixthInnoculation"/>
 *     &lt;enumeration value="ThirdInnoculation"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ImmunizationStatusCodeType")
@XmlEnum
public enum ImmunizationStatusCodeType {

    @XmlEnumValue("EighthInnoculation")
    EIGHTH_INNOCULATION("EighthInnoculation"),
    @XmlEnumValue("FifthInnoculation")
    FIFTH_INNOCULATION("FifthInnoculation"),
    @XmlEnumValue("FirstInnoculation")
    FIRST_INNOCULATION("FirstInnoculation"),
    @XmlEnumValue("FourthInnoculation")
    FOURTH_INNOCULATION("FourthInnoculation"),
    @XmlEnumValue("HadTheDisease")
    HAD_THE_DISEASE("HadTheDisease"),
    @XmlEnumValue("MedicalExemption")
    MEDICAL_EXEMPTION("MedicalExemption"),
    @XmlEnumValue("NinthInnoculation")
    NINTH_INNOCULATION("NinthInnoculation"),
    @XmlEnumValue("PersonalExemption")
    PERSONAL_EXEMPTION("PersonalExemption"),
    @XmlEnumValue("ReligiousExemption")
    RELIGIOUS_EXEMPTION("ReligiousExemption"),
    @XmlEnumValue("SecondInnoculation")
    SECOND_INNOCULATION("SecondInnoculation"),
    @XmlEnumValue("SeventhInnoculation")
    SEVENTH_INNOCULATION("SeventhInnoculation"),
    @XmlEnumValue("SixthInnoculation")
    SIXTH_INNOCULATION("SixthInnoculation"),
    @XmlEnumValue("ThirdInnoculation")
    THIRD_INNOCULATION("ThirdInnoculation");
    private final String value;

    ImmunizationStatusCodeType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ImmunizationStatusCodeType fromValue(String v) {
        for (ImmunizationStatusCodeType c: ImmunizationStatusCodeType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}