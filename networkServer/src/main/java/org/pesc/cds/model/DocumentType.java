package org.pesc.cds.model;

/**
 * Created by james on 1/2/17.
 */
public enum DocumentType {
    TRANSCRIPT("Transcript"),
    TRANSCRIPT_RESPONSE("Transcript Response"),
    ACKNOWLEDGEMENT("Acknowledgement"),
    TRANSCRIPT_REQUEST("Transcript Request");

    private final String documentName;

    DocumentType(String documentName) {
        this.documentName = documentName;
    }

    public String getDocumentName() {
        return documentName;
    }
}
