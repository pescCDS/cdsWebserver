package org.pesc.cds.model;

/**
 * Created by james on 1/2/17.
 */
public enum DocumentFormat {
    PESCXML("PESCXML"),
    JSON("JSON"),
    PDF("PDF"),
    IMAGE("Image"),
    TEXT("Text"),
    BINARY("Binary"),
    EDI("EDI");

    private final String formatName;

    DocumentFormat(String formatName) {
        this.formatName = formatName;
    }

    public String getFormatName() {
        return formatName;
    }

}
