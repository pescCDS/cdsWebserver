package org.pesc.cds.model;

/**
 * Created by james on 1/2/17.
 */
public enum DocumentType {
    COLLEGE_TRANSCRIPT("College Transcript"),
    HIGHSCHOOL_TRANSCRIPT("Highschool Transcript"),
    TRANSCRIPT_RESPONSE("Transcript Response"),
    FUNCTIONAL_ACKNOWLEDGEMENT("Functional Acknowledgement"),
    TRANSCRIPT_REQUEST("Transcript Request"),
    TRANSCRIPT_ACKNOWLEDGEMENT("Transcript Acknowledgement");

    private final String documentName;

    DocumentType(String documentName) {
        this.documentName = documentName;
    }

    public String getDocumentName() {
        return documentName;
    }
}
