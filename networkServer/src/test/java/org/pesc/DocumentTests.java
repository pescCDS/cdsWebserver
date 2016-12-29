package org.pesc;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.pesc.cds.service.FileProcessorService;
import org.pesc.sdk.message.documentinfo.v1_0.DocumentInfo;
import org.pesc.sdk.message.documentinfo.v1_0.DocumentInfoType;
import org.pesc.sdk.message.documentinfo.v1_0.DocumentInfoValidator;
import org.pesc.sdk.message.documentinfo.v1_0.DocumentTypeCode;
import org.pesc.sdk.message.transcriptrequest.v1_4.TranscriptRequest;
import org.pesc.sdk.message.transcriptresponse.v1_4.TranscriptResponse;
import org.pesc.sdk.sector.academicrecord.v1_9.HoldReasonType;
import org.pesc.sdk.sector.academicrecord.v1_9.ResponseHoldType;
import org.pesc.sdk.sector.academicrecord.v1_9.ResponseStatusType;
import org.pesc.sdk.util.ValidationUtils;
import org.pesc.sdk.util.XmlFileType;
import org.pesc.sdk.util.XmlSchemaVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.naming.OperationNotSupportedException;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = NetworkServerApplication.class, webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
@ActiveProfiles("test")
public class DocumentTests {

    public static Logger logger = LoggerFactory.getLogger(DocumentTests.class);

    @Autowired
    FileProcessorService fileProcessorService;



    @Test
    public void testTranscriptResponseCreation() throws Exception {

        Unmarshaller u = ValidationUtils.createUnmarshaller("org.pesc.sdk.message.transcriptrequest.v1_4.impl");

        Schema schema = ValidationUtils.getSchema(XmlFileType.TRANSCRIPT_REQUEST, XmlSchemaVersion.V1_4_0);
        u.setSchema(schema);

        TranscriptRequest transcriptRequest = null;
        DocumentInfo documentInfo = null;
        try {
            transcriptRequest = (TranscriptRequest) u.unmarshal(getClass().getClassLoader().getResource("xmlTranscript_request.xml"));
        } catch (Exception e) {
            logger.error("Error Unmarshalling TranscriptRequest", e);
            throw e;
        }

        createAndValidateTranscriptResponse(transcriptRequest);
    }

    private void createAndValidateTranscriptResponse(TranscriptRequest transcriptRequest) throws JAXBException, SAXException, OperationNotSupportedException {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, 3);

        ResponseHoldType hold = fileProcessorService.createResponseHold(HoldReasonType.FINANCIAL, cal.getTime());


        TranscriptResponse response = fileProcessorService.buildBaseTranscriptResponse(transcriptRequest.getTransmissionData().getDestination(),
                transcriptRequest.getTransmissionData().getSource(),
                "1",
                "1-request-tracking-id",
                "docid",
                ResponseStatusType.HOLD,
                Arrays.asList(new ResponseHoldType[]{hold}),
                transcriptRequest.getRequests().get(0).getRequestedStudent()
        );

        Marshaller marshaller = ValidationUtils.createMarshaller("org.pesc.sdk.message.transcriptresponse.v1_4.impl");
        marshaller.setSchema(ValidationUtils.getSchema(XmlFileType.TRANSCRIPT_RESPONSE, XmlSchemaVersion.V1_4_0));

        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        marshaller.marshal(response, byteArrayOutputStream);

        ValidationUtils.validateDocument(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()), XmlFileType.TRANSCRIPT_RESPONSE, XmlSchemaVersion.V1_4_0);

    }


}
