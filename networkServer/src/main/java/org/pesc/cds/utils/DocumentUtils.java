/*
 * Copyright (c) 2017. California Community Colleges Technology Center
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.pesc.cds.utils;

import org.pesc.cds.model.DocumentType;
import org.pesc.sdk.util.ValidationUtils;
import org.pesc.sdk.util.XmlFileType;
import org.pesc.sdk.util.XmlSchemaVersion;
import org.xml.sax.SAXException;

import javax.naming.OperationNotSupportedException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by James Whetstone on 1/4/17.
 */
public class DocumentUtils {

    /**
     * For any document that is NOT valid, ie. that does not conform to the required XML schema, a SAXException will be
     * thrown.  In a controller, this will get converted to a HTTP 400, with a JSON payload that looks something like this:
     *
     * {"timestamp":1483550531075,"status":400,"error":"Invalid PESC XML","exception":"org.xml.sax.SAXParseException","message":"Premature end of file.","path":"/api/v1/documents/test","line_number":100,"column_number":23}
     * @param documentType The document type
     * @param xmlStream
     * @throws IOException
     * @throws SAXException
     * @throws OperationNotSupportedException
     */
    public static void validate(String documentType, InputStream xmlStream) throws IOException, SAXException, OperationNotSupportedException {
        if (DocumentType.TRANSCRIPT_REQUEST.getDocumentName().equalsIgnoreCase(documentType)) {
            ValidationUtils.validateDocument(xmlStream, XmlFileType.TRANSCRIPT_REQUEST, XmlSchemaVersion.V1_4_0);
        }
        else if (DocumentType.TRANSCRIPT_RESPONSE.getDocumentName().equalsIgnoreCase(documentType)) {
            ValidationUtils.validateDocument(xmlStream, XmlFileType.TRANSCRIPT_RESPONSE, XmlSchemaVersion.V1_4_0);
        }
        else if (DocumentType.TRANSCRIPT_ACKNOWLEDGEMENT.getDocumentName().equalsIgnoreCase(documentType)) {
            ValidationUtils.validateDocument(xmlStream, XmlFileType.TRANSCRIPT_ACKNOWLEDGEMENT, XmlSchemaVersion.V1_3_0);
        }
        else if (DocumentType.COLLEGE_TRANSCRIPT.getDocumentName().equalsIgnoreCase(documentType)) {
            ValidationUtils.validateDocument(xmlStream, XmlFileType.COLLEGE_TRANSCRIPT, XmlSchemaVersion.V1_6_0);
        }
        else if (DocumentType.HIGHSCHOOL_TRANSCRIPT.getDocumentName().equalsIgnoreCase(documentType)) {
            ValidationUtils.validateDocument(xmlStream, XmlFileType.HIGH_SCHOOL_TRANSCRIPT, XmlSchemaVersion.V1_5_0);
        }

    }
}
