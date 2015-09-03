package org.pesc.cds.xml;

/**
 * Created by rgehbauer on 9/2/15.
 */
public enum XmlFileType {
    HIGH_SCHOOL_TRANSCRIPT("HighSchoolTranscript"),
    COLLEGE_TRANSCRIPT("CollegeTranscript");

    private final String filenamePrefix;

    XmlFileType(String filenamePrefix) {
        this.filenamePrefix = filenamePrefix;
    }

    public String getFilenamePrefix() {
        return filenamePrefix;
    }
}
