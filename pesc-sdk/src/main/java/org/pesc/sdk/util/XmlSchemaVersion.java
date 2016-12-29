package org.pesc.sdk.util;

/**
 * Created by rgehbauer on 9/2/15.
 */
public enum XmlSchemaVersion {
    V1_5_0("v1.5.0"),
    V1_6_0("v1.6.0"),
    V1_2_0("v1.2.0");

    private final String versionText;

    XmlSchemaVersion(String versionText) {
        this.versionText = versionText;
    }

    public String getVersionText() {
        return versionText;
    }
}
