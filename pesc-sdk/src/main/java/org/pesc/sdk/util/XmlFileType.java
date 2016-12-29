package org.pesc.sdk.util;

/**
 * Created by rgehbauer on 9/2/15.
 */
public enum XmlFileType {
    HIGH_SCHOOL_TRANSCRIPT("HighSchoolTranscript"),
    COLLEGE_TRANSCRIPT("CollegeTranscript"),
    FUNCTIONAL_ACKNOWLEDGEMENT("FunctionalAcknowledgement");

    private final String filenamePrefix;

    XmlFileType(String filenamePrefix) {
        this.filenamePrefix = filenamePrefix;
    }

    public String getFilenamePrefix() {
        return filenamePrefix;
    }
}
