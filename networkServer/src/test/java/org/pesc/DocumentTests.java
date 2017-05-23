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

package org.pesc;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.pesc.cds.domain.Transaction;
import org.pesc.cds.service.FileProcessorService;
import org.pesc.cds.service.SerializationService;
import org.pesc.sdk.message.collegetranscript.v1_6.CollegeTranscript;
import org.pesc.sdk.message.functionalacknowledgement.v1_2.Acknowledgment;
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
import org.xml.sax.SAXException;

import javax.naming.OperationNotSupportedException;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Calendar;

import static junit.framework.TestCase.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = NetworkServerApplication.class, webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
@ActiveProfiles("test")
public class DocumentTests {

    public static Logger logger = LoggerFactory.getLogger(DocumentTests.class);

    @Autowired
    FileProcessorService fileProcessorService;

    @Autowired
    private SerializationService serializationService;

    @Test
    public void testFunctionalAckCreation() throws JAXBException, SAXException, OperationNotSupportedException {

        Transaction transaction = new Transaction();
        transaction.setSenderId(4);
        transaction.setRecipientId(5);

        Acknowledgment ack = fileProcessorService.createFunctionalAcknowledgement(transaction, "ack_1");

        Marshaller marshaller = serializationService.createFunctionalAckMarshaller();

        marshaller.setSchema(ValidationUtils.getSchema(XmlFileType.FUNCTIONAL_ACKNOWLEDGEMENT, XmlSchemaVersion.V1_2_0));

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        marshaller.marshal(ack, byteArrayOutputStream);

       ValidationUtils.validateDocument(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()),
                XmlFileType.FUNCTIONAL_ACKNOWLEDGEMENT,
                XmlSchemaVersion.V1_2_0);

    }

    @Test
    public void testTranscriptResponseCreation() throws JAXBException, SAXException, OperationNotSupportedException {

        Unmarshaller u = serializationService.createTranscriptRequestUnmarshaller(true, false);

        TranscriptRequest transcriptRequest = null;
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

        Marshaller marshaller = serializationService.createTranscriptResponseMarshaller();

        marshaller.setSchema(ValidationUtils.getSchema(XmlFileType.TRANSCRIPT_RESPONSE, XmlSchemaVersion.V1_4_0));

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        marshaller.marshal(response, byteArrayOutputStream);

        ValidationUtils.validateDocument(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()), XmlFileType.TRANSCRIPT_RESPONSE, XmlSchemaVersion.V1_4_0);


    }


    @Test
    public void testJSONSerializationOfJAXBPESCCollegeTranscript() throws JAXBException, SAXException, OperationNotSupportedException {

        Unmarshaller unmarshaller =  serializationService.createTranscriptUnmarshaller(false, false);

        Object object = unmarshaller.unmarshal(getClass().getClassLoader().getResourceAsStream("college-transcript.xml"));

        Marshaller marshaller = serializationService.createTranscriptMarshaller(true);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        marshaller.marshal(object, outputStream);

        System.out.print(outputStream.toString());

        InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

        unmarshaller =  serializationService.createTranscriptUnmarshaller(false, true);

        object = unmarshaller.unmarshal(inputStream);

        Assert.assertTrue("Failed to unmarshall JSON transcript from PESC College Transcript object.", object instanceof CollegeTranscript);

        CollegeTranscript collegeTranscript = (CollegeTranscript)object;

        Assert.assertTrue("Student's first name is incorrect.", collegeTranscript.getStudent().getPerson().getName().getFirstName().equals("John"));


    }



}
