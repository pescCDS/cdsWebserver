package org.pesc.cds.utils;

/**
 * Created by james on 1/5/17.
 */
public class ErrorUtils {

    public static String getNoEndpointFoundMessage(Integer recipientID, String fileFormat, String documentType, String department) {
        return String.format("No endpoint URI exists for the given EDExchange ID (%d), document format (%s), document type (%s) and department (%s).",
                recipientID,
                fileFormat,
                documentType,
                department);
    }
}
