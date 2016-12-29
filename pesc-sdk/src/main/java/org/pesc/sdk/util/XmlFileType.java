package org.pesc.sdk.util;

/**
 * Created by rgehbauer on 9/2/15.
 */
public enum XmlFileType {
    HIGH_SCHOOL_TRANSCRIPT("HighSchoolTranscript"),
    COLLEGE_TRANSCRIPT("CollegeTranscript"),
    FUNCTIONAL_ACKNOWLEDGEMENT("FunctionalAcknowledgement"),
    TRANSCRIPT_RESPONSE("TranscriptResponse"),
    TRANSCRIPT_REQUEST("TranscriptRequest"),
    DOCUMENT_INFO("DocumentInfo");

    private final String filenamePrefix;

    XmlFileType(String filenamePrefix) {
        this.filenamePrefix = filenamePrefix;
    }

    public String getFilenamePrefix() {
        return filenamePrefix;
    }
}
