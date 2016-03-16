package org.pesc.service.xml;

/**
 * Created by rgehbauer on 9/2/15.
 */
public enum XmlSchemaVersion {
    V1_3_0("v1.3.0"),
    V1_4_0("v1.4.0");

    private final String versionText;

    XmlSchemaVersion(String versionText) {
        this.versionText = versionText;
    }

    public String getVersionText() {
        return versionText;
    }
}
