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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.pesc.cds.service.SerializationService;
import org.pesc.cds.service.TranscriptAcknowledgementService;
import org.pesc.cds.service.TranscriptService;
import org.pesc.sdk.message.collegetranscript.v1_6.CollegeTranscript;
import org.pesc.sdk.message.transcriptacknowledgement.v1_3.Acknowledgment;
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
import java.io.ByteArrayOutputStream;

/**
 * Created by james on 1/12/17.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = NetworkServerApplication.class, webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
@ActiveProfiles("test")
public class TranscriptAcknowledgementTests {
    public static Logger logger = LoggerFactory.getLogger(TranscriptAcknowledgementTests.class);

    @Autowired
    TranscriptAcknowledgementService transcriptAcknowledgementService;

    @Autowired
    TranscriptService transcriptService;

    @Autowired
    private SerializationService serializationService;

    private static final String ackDocID = "1";

    @Test
    public void testVerification() throws JAXBException, SAXException, OperationNotSupportedException {


        CollegeTranscript collegeTranscript = transcriptService.fromURL(getClass().getClassLoader().getResource("college-transcript.xml"), false);



        Acknowledgment ack =
                transcriptAcknowledgementService.buildBaseTranscriptAcknowledgement(collegeTranscript.getTransmissionData().getDestination(),
                        collegeTranscript.getTransmissionData().getSource(), collegeTranscript, ackDocID, collegeTranscript.getTransmissionData().getDocumentID());

        transcriptAcknowledgementService.verifyTranscript(ack, collegeTranscript);


        //serialize it

        Marshaller marshaller = serializationService.createTranscriptAckMarshaller();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        marshaller.marshal(ack, byteArrayOutputStream);


        //logger.info(byteArrayOutputStream.toString());


    }



}
