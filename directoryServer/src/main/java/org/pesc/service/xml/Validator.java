package org.pesc.service.xml;

import org.xml.sax.SAXException;

import javax.naming.OperationNotSupportedException;
import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Validator class to service validation endpoints.
 *
 * Created by rgehbauer on 9/1/15.
 */
public class Validator {

    private static Logger logger = Logger.getLogger(Validator.class.getName());

    /**
     * Validates a PESC XML transcript against the appropriate schema specified by the fileType and version.
     * @param is InputStream containing the contents of the file to be validated
     * @param fileType whether the PESC Transcript is a High School or College transcript
     * @param version which version of the HS/College schema to validate with
     * @throws OperationNotSupportedException thrown if
     * @throws SAXException
     */
    public static void validatePESCXMLTranscript(InputStream is, XmlFileType fileType, XmlSchemaVersion version) throws OperationNotSupportedException, SAXException {
        String xsdResourceName = new StringBuilder("/xsd/")
                .append(fileType.getFilenamePrefix())
                .append("_")
                .append(version.getVersionText())
                .append(".xsd").toString();

        URL schemaFile = Validator.class.getResource(xsdResourceName);
        if(schemaFile == null) {
            throw new OperationNotSupportedException("Unknown schema: " + xsdResourceName);
        }
        Source xmlFile = new StreamSource(is);
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        try {
            Schema schema = schemaFactory.newSchema(schemaFile);
            javax.xml.validation.Validator validator = schema.newValidator();
            validator.validate(xmlFile);
        } catch (IOException e) {
            // Not sure how we could get here.
            logger.log(Level.SEVERE, "Unexpected IOException", e);
        }
    }

}
