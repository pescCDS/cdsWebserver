package org.pesc.sdk.message.transcriptrequest.v1_2;

import org.junit.Test;
import org.pesc.sdk.core.coremain.v1_12.CountryCodeType;
import org.pesc.sdk.core.coremain.v1_12.DocumentTypeCodeType;
import org.pesc.sdk.core.coremain.v1_12.SeverityCodeType;
import org.pesc.sdk.core.coremain.v1_12.StateProvinceCodeType;
import org.pesc.sdk.core.coremain.v1_12.TransmissionTypeType;
import org.pesc.sdk.message.documentinfo.v1_0.DocumentInfo;
import org.pesc.sdk.message.documentinfo.v1_0.DocumentInfoValidator;
import org.pesc.sdk.message.documentinfo.v1_0.DocumentTypeCode;
import org.pesc.sdk.message.functionalacknowledgment.v1_0.ValidationResponse;
import org.pesc.sdk.sector.academicrecord.v1_7.AcademicAwardsReportedType;
import org.pesc.sdk.sector.academicrecord.v1_7.AddressType;
import org.pesc.sdk.sector.academicrecord.v1_7.AttendanceType;
import org.pesc.sdk.sector.academicrecord.v1_7.ContactsType;
import org.pesc.sdk.sector.academicrecord.v1_7.EmailType;
import org.pesc.sdk.sector.academicrecord.v1_7.OrganizationType;
import org.pesc.sdk.sector.academicrecord.v1_7.PersonType;
import org.pesc.sdk.sector.academicrecord.v1_7.PhoneType;
import org.pesc.sdk.sector.academicrecord.v1_7.ReleaseAuthorizedMethodType;
import org.pesc.sdk.sector.academicrecord.v1_7.RequestType;
import org.pesc.sdk.sector.academicrecord.v1_7.RequestedStudentType;
import org.pesc.sdk.sector.academicrecord.v1_7.SourceDestinationType;
import org.pesc.sdk.sector.academicrecord.v1_7.TransmissionDataType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created with IntelliJ IDEA.
 * User: sallen
 * Date: 5/15/2014
 * Time: 2:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class TranscriptRequestTest {
    public static Logger logger = LoggerFactory.getLogger(TranscriptRequestTest.class);

    @Test
    public void testBasicXml() throws Exception {
        JAXBContext jc = JAXBContext.newInstance("org.pesc.sdk.message.transcriptrequest.v1_2.impl");
        Unmarshaller u = jc.createUnmarshaller();
        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        URL transcriptRequestSchemaUrl = getClass().getClassLoader().getResource("xsd/pesc/TranscriptRequest_v1.2.0.xsd");
        Schema schema = sf.newSchema(transcriptRequestSchemaUrl);
        u.setSchema(schema);
        String[] files = {"org/pesc/sdk/message/transcriptrequest/v1_2/basic_001.xml", "org/pesc/sdk/message/transcriptrequest/v1_2/basic_002.xml"};
        for(int i=0; i<2; i++) {
            TranscriptRequest transcriptRequest = null;
            try {
                transcriptRequest = (TranscriptRequest) u.unmarshal(getClass().getClassLoader().getResource(files[i]));
            } catch (Exception e) {
                logger.error("Error Unmarshalling TranscriptRequest", e);
                throw e;
            }

            Marshaller m = jc.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            m.marshal(transcriptRequest, byteArrayOutputStream);
            logger.info(byteArrayOutputStream.toString());
        }
        assertTrue(true);
    }

    @Test
    public void testRequiredOnlyXml() throws Exception {
        JAXBContext jc = JAXBContext.newInstance("org.pesc.sdk.message.transcriptrequest.v1_2.impl");
        Unmarshaller transcriptRequestUnmarshaller = jc.createUnmarshaller();
        SchemaFactory transcriptRequestSchemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        URL transcriptRequestSchemaUrl = getClass().getClassLoader().getResource("xsd/pesc/TranscriptRequest_v1.2.0.xsd");
        Schema transcriptRequestXsdSchema = transcriptRequestSchemaFactory.newSchema(transcriptRequestSchemaUrl);
        transcriptRequestUnmarshaller.setSchema(transcriptRequestXsdSchema);
        String transcriptRequestXml = "org/pesc/sdk/message/transcriptrequest/v1_2/RequiredOnly_001.xml";
        TranscriptRequest transcriptRequest = null;
        DocumentInfo DocumentInfo = null;
        try {
            transcriptRequest = (TranscriptRequest) transcriptRequestUnmarshaller.unmarshal(getClass().getClassLoader().getResource(transcriptRequestXml));
            JAXBContext documentInfoContext = JAXBContext.newInstance("org.pesc.sdk.message.documentinfo.v1_0.impl");
            Unmarshaller documentInfoUnmarshaller = documentInfoContext.createUnmarshaller();
            SchemaFactory documentinfochemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            URL documentinfochemaUrl = getClass().getClassLoader().getResource("xsd/pesc/DocumentInfo_v1.0.0.xsd");
            Schema documentInfoXsdSchema = documentinfochemaFactory.newSchema(documentinfochemaUrl);
            documentInfoUnmarshaller.setSchema(documentInfoXsdSchema);
            DocumentInfo = (DocumentInfo)documentInfoUnmarshaller.unmarshal((Node) transcriptRequest.getTransmissionData().getUserDefinedExtensions().getAny());
        } catch (Exception e) {
            logger.error("Error Unmarshalling TranscriptRequest", e);
            throw e;
        }

        Marshaller m = jc.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        m.marshal(transcriptRequest, byteArrayOutputStream);
        logger.info(byteArrayOutputStream.toString());
        testTranscriptRequestContentRequired(transcriptRequest);
        testdocumentInfoContentRequired(DocumentInfo);
        assertTrue(true);
    }

    @Test
    public void testInvalidExtension() throws Exception {
        JAXBContext jc = JAXBContext.newInstance("org.pesc.sdk.message.transcriptrequest.v1_2.impl");
        Unmarshaller transcriptRequestUnmarshaller = jc.createUnmarshaller();
        SchemaFactory transcriptRequestSchemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        URL transcriptRequestSchemaUrl = getClass().getClassLoader().getResource("xsd/pesc/TranscriptRequest_v1.2.0.xsd");
        Schema transcriptRequestXsdSchema = transcriptRequestSchemaFactory.newSchema(transcriptRequestSchemaUrl);
        transcriptRequestUnmarshaller.setSchema(transcriptRequestXsdSchema);
        String transcriptRequestXml = "org/pesc/sdk/message/transcriptrequest/v1_2/InvalidExtension_001.xml";
        TranscriptRequest transcriptRequest = null;
        try {
            transcriptRequest = (TranscriptRequest) transcriptRequestUnmarshaller.unmarshal(getClass().getClassLoader().getResource(transcriptRequestXml));
            JAXBContext documentInfoContext = JAXBContext.newInstance("org.pesc.sdk.message.documentinfo.v1_0.impl");
            Unmarshaller documentInfoUnmarshaller = documentInfoContext.createUnmarshaller();
            SchemaFactory documentinfochemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            URL documentinfochemaUrl = getClass().getClassLoader().getResource("xsd/pesc/DocumentInfo_v1.0.0.xsd");
            Schema documentInfoXsdSchema = documentinfochemaFactory.newSchema(documentinfochemaUrl);
            documentInfoUnmarshaller.setSchema(documentInfoXsdSchema);
            try {
                DocumentInfo DocumentInfo = (DocumentInfo)documentInfoUnmarshaller.unmarshal((Node) transcriptRequest.getTransmissionData().getUserDefinedExtensions().getAny());
                fail();
            } catch (JAXBException e) {
                logger.info("Expected Exception Caught! Message: "+e.getMessage());
            }

        } catch (Exception e) {
            logger.error("Error Unmarshalling TranscriptRequest", e);
            throw e;
        }

        Marshaller m = jc.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        m.marshal(transcriptRequest, byteArrayOutputStream);
        logger.info(byteArrayOutputStream.toString());
        assertTrue(true);
    }

    @Test
    public void testRequiredAndRecommendedXml() throws Exception {
        JAXBContext jc = JAXBContext.newInstance("org.pesc.sdk.message.transcriptrequest.v1_2.impl");
        Unmarshaller u = jc.createUnmarshaller();
        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        URL transcriptRequestSchemaUrl = getClass().getClassLoader().getResource("xsd/pesc/TranscriptRequest_v1.2.0.xsd");
        Schema schema = sf.newSchema(transcriptRequestSchemaUrl);
        u.setSchema(schema);
        String file = "org/pesc/sdk/message/transcriptrequest/v1_2/RequiredAndRecommended_001.xml";
        TranscriptRequest transcriptRequest = null;
        DocumentInfo DocumentInfo = null;
        try {
            transcriptRequest = (TranscriptRequest) u.unmarshal(getClass().getClassLoader().getResource(file));
            JAXBContext documentInfoContext = JAXBContext.newInstance("org.pesc.sdk.message.documentinfo.v1_0.impl");
            Unmarshaller documentInfoUnmarshaller = documentInfoContext.createUnmarshaller();
            SchemaFactory documentinfochemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            URL documentinfochemaUrl = getClass().getClassLoader().getResource("xsd/pesc/DocumentInfo_v1.0.0.xsd");
            Schema documentInfoXsdSchema = documentinfochemaFactory.newSchema(documentinfochemaUrl);
            documentInfoUnmarshaller.setSchema(documentInfoXsdSchema);
            DocumentInfo = (DocumentInfo)documentInfoUnmarshaller.unmarshal((Node) transcriptRequest.getTransmissionData().getUserDefinedExtensions().getAny());
        } catch (Exception e) {
            logger.error("Error Unmarshalling TranscriptRequest", e);
            throw e;
        }

        Marshaller m = jc.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        m.marshal(transcriptRequest, byteArrayOutputStream);
        logger.info(byteArrayOutputStream.toString());
        testTranscriptRequestContentRequired(transcriptRequest);
        testTranscriptRequestContentRecommended(transcriptRequest);
        testdocumentInfoContentRequired(DocumentInfo);
        assertTrue(true);
    }

    @Test
    public void testRequestXml() throws Exception {
        String[] files = new String[2];
        files[0] = "org/pesc/sdk/message/transcriptrequest/v1_2/modified_requiredAndRecommended_0fd6ad111a83487c9536ea71602ecd7d_request.xml";
        files[1] = "org/pesc/sdk/message/transcriptrequest/v1_2/modified_requiredOnly_0fd6ad111a83487c9536ea71602ecd7d_request.xml";
        for(String file: files){
            JAXBContext jc = JAXBContext.newInstance("org.pesc.sdk.message.transcriptrequest.v1_2.impl");
            Unmarshaller u = jc.createUnmarshaller();
            SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            URL transcriptRequestSchemaUrl = getClass().getClassLoader().getResource("xsd/pesc/TranscriptRequest_v1.2.0.xsd");
            Schema schema = sf.newSchema(transcriptRequestSchemaUrl);
            u.setSchema(schema);
            TranscriptRequest transcriptRequest = null;
            DocumentInfo DocumentInfo = null;
            try {
                transcriptRequest = (TranscriptRequest) u.unmarshal(getClass().getClassLoader().getResource(file));
                JAXBContext documentInfoContext = JAXBContext.newInstance("org.pesc.sdk.message.documentinfo.v1_0.impl");
                Unmarshaller documentInfoUnmarshaller = documentInfoContext.createUnmarshaller();
                SchemaFactory documentinfochemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
                URL documentinfochemaUrl = getClass().getClassLoader().getResource("xsd/pesc/DocumentInfo_v1.0.0.xsd");
                Schema documentInfoXsdSchema = documentinfochemaFactory.newSchema(documentinfochemaUrl);
                documentInfoUnmarshaller.setSchema(documentInfoXsdSchema);
                DocumentInfo = (DocumentInfo)documentInfoUnmarshaller.unmarshal((Node) transcriptRequest.getTransmissionData().getUserDefinedExtensions().getAny());
            } catch (Exception e) {
                logger.error("Error Unmarshalling TranscriptRequest", e);
                throw e;
            }

            Marshaller m = jc.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            m.marshal(transcriptRequest, byteArrayOutputStream);
            logger.info(byteArrayOutputStream.toString());
            testTranscriptRequestContentRequired(transcriptRequest);
//            testTranscriptRequestContentRecommended(transcriptRequest);
            testdocumentInfoContentRequired(DocumentInfo);
            assertTrue(true);
        }
    }

    @Test
    public void testInternationalAddress()throws Exception {
        JAXBContext jc = JAXBContext.newInstance("org.pesc.sdk.message.transcriptrequest.v1_2.impl");
        Unmarshaller u = jc.createUnmarshaller();
        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        URL transcriptRequestSchemaUrl = getClass().getClassLoader().getResource("xsd/pesc/TranscriptRequest_v1.2.0.xsd");
        Schema schema = sf.newSchema(transcriptRequestSchemaUrl);
        u.setSchema(schema);
        TranscriptRequest transcriptRequest = null;
        ValidationResponse vr = null;
        SeverityCodeType s = null;
        try {
            //Valid
            transcriptRequest = (TranscriptRequest) u.unmarshal(getClass().getClassLoader().getResource("org/pesc/sdk/message/transcriptrequest/v1_2/InternationalAddress_001.xml"));
            vr = TranscriptRequestValidator.validateTranscriptRequestRequiredContent(transcriptRequest);
            assertTrue(vr.getErrors().size()==0);
            vr = TranscriptRequestValidator.validateTranscriptRequestRecommendedContent(transcriptRequest);
            assertTrue(vr.getErrors().size() == 0);

            //Missing AddressLine- Fail
            try {
                transcriptRequest = (TranscriptRequest) u.unmarshal(getClass().getClassLoader().getResource("org/pesc/sdk/message/transcriptrequest/v1_2/InternationalAddress_002.xml"));
                fail();
            } catch (Exception e1){
                assert(true);
            }

            //Missing City- Fail
            try {
                transcriptRequest = (TranscriptRequest) u.unmarshal(getClass().getClassLoader().getResource("org/pesc/sdk/message/transcriptrequest/v1_2/InternationalAddress_003.xml"));
                fail();
            } catch (Exception e1){
                assert(true);
            }
            //Including StateProvince- Valid
            //Including PostalCode- Valid
            //Missing StateProvince- Valid
            //Missing PostalCode- Valid
            //Missing Country- Fail
            try {
                transcriptRequest = (TranscriptRequest) u.unmarshal(getClass().getClassLoader().getResource("org/pesc/sdk/message/transcriptrequest/v1_2/InternationalAddress_004.xml"));
                fail();
            } catch (Exception e1){
                assert(true);
            }
            //Missing destination address- Valid
            transcriptRequest = (TranscriptRequest) u.unmarshal(getClass().getClassLoader().getResource("org/pesc/sdk/message/transcriptrequest/v1_2/InternationalAddress_005.xml"));
            vr = TranscriptRequestValidator.validateTranscriptRequestRequiredContent(transcriptRequest);
            assertTrue(vr.getErrors().size()==0);
            vr = TranscriptRequestValidator.validateTranscriptRequestRecommendedContent(transcriptRequest);
            assertTrue(vr.getErrors().size() == 1);
            assertTrue(vr.getSeverity()==org.pesc.sdk.core.coremain.v1_9.SeverityCodeType.WARNING);
            //Missing source address- Fail
            transcriptRequest = (TranscriptRequest) u.unmarshal(getClass().getClassLoader().getResource("org/pesc/sdk/message/transcriptrequest/v1_2/InternationalAddress_006.xml"));
            vr = TranscriptRequestValidator.validateTranscriptRequestRequiredContent(transcriptRequest);
            assertTrue(vr.getErrors().size()==1);
            assertTrue(vr.getSeverity()==org.pesc.sdk.core.coremain.v1_9.SeverityCodeType.ERROR);
            vr = TranscriptRequestValidator.validateTranscriptRequestRecommendedContent(transcriptRequest);
            assertTrue(vr.getErrors().size() == 0);
            //Mix StateProvinceCode with non domestic CountryCode- Fail
            try {
                transcriptRequest = (TranscriptRequest) u.unmarshal(getClass().getClassLoader().getResource("org/pesc/sdk/message/transcriptrequest/v1_2/InternationalAddress_007.xml"));
            fail();
            } catch (Exception e1){
                assert(true);
            }

            //Valid
            transcriptRequest = (TranscriptRequest) u.unmarshal(getClass().getClassLoader().getResource("org/pesc/sdk/message/transcriptrequest/v1_2/InternationalAddress_008.xml"));
            vr = TranscriptRequestValidator.validateTranscriptRequestRequiredContent(transcriptRequest);
            assertTrue(vr.getErrors().size()==0);
            vr = TranscriptRequestValidator.validateTranscriptRequestRecommendedContent(transcriptRequest);
            assertTrue(vr.getErrors().size() == 0);
        } catch (Exception e) {
            logger.error("Error Unmarshalling TranscriptRequest", e);
            throw e;
        }
    }

    @Test
    public void testDomesticAddress()throws Exception {
        JAXBContext jc = JAXBContext.newInstance("org.pesc.sdk.message.transcriptrequest.v1_2.impl");
        Unmarshaller u = jc.createUnmarshaller();
        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        URL transcriptRequestSchemaUrl = getClass().getClassLoader().getResource("xsd/pesc/TranscriptRequest_v1.2.0.xsd");
        Schema schema = sf.newSchema(transcriptRequestSchemaUrl);
        u.setSchema(schema);
        TranscriptRequest transcriptRequest = null;
        try {
            //Valid
            transcriptRequest = (TranscriptRequest) u.unmarshal(getClass().getClassLoader().getResource("org/pesc/sdk/message/transcriptrequest/v1_2/DomesticAddress_001.xml"));
            ValidationResponse validationResponse1 = TranscriptRequestValidator.validateTranscriptRequestRequiredContent(transcriptRequest);
            assertTrue(validationResponse1.getSeverity()==null);
            assertTrue(validationResponse1.getErrors().size()==0);
            ValidationResponse validationResponse2 = TranscriptRequestValidator.validateTranscriptRequestRecommendedContent(transcriptRequest);
            assertTrue(validationResponse2.getSeverity() == null);
            assertTrue(validationResponse2.getErrors().size() == 0);

            //Include Country- Valid???
            //Missing AddressLine- Fail
            //Missing City- Fail
            //Missing PostalCode- Fail
            //Missing StateProvinceCode- Fail
            //Missing Destination Address- Valid
            //Missing Source Address- Fail
            //Mix StateProvince with domestic CountryCode- Valid
            //Mix StateProvince with no CountryCode- Fail


        } catch (Exception e) {
            logger.error("Error Unmarshalling TranscriptRequest", e);
            throw e;
        }
    }

    private void testTranscriptRequestContentRequired(TranscriptRequest transcriptRequest){
        String documentId = null;
        DocumentTypeCodeType documentTypeCode = null;
        String createDateTime = null;
        TransmissionTypeType transmissionType = null;
        String sendersCeeb = null;
        String sendersName = null;
        String sendersStreetAddress = null;
        String sendersCity = null;
        StateProvinceCodeType sendersState = null;
        String sendersPostalCode = null;
        String sendersCountry = null;
        String receiversCeeb = null;
        String receiversName = null;
        String requestedStudentFirstName = null;
        String requestedStudentLastName = null;
        String requestedStudentDateOfBirth = null;
        String requestedStudentEmail = null;
        String requestedStudentEnrollDate = null;
        String requestedStudentExitDate = null;
        String requestedStudentAcademicAwardDate = null;
        Boolean requestedStudentAuthorizedIndicator = null;
        ReleaseAuthorizedMethodType requestedStudentAuthorizedMethod = null;


        if(transcriptRequest!=null && transcriptRequest.getTransmissionData()!=null){
            TransmissionDataType transmissionDataType = transcriptRequest.getTransmissionData();
            documentId = transmissionDataType.getDocumentID();
            documentTypeCode = transmissionDataType.getDocumentTypeCode();
            createDateTime = transmissionDataType.getCreatedDateTime().toString();
            transmissionType = transmissionDataType.getTransmissionType();
            SourceDestinationType sourceDestinationType = transmissionDataType.getSource();
            if(sourceDestinationType!=null && sourceDestinationType.getOrganization()!=null){
                OrganizationType sendingOrganizationType = sourceDestinationType.getOrganization();
                sendersCeeb = sendingOrganizationType.getCEEBACT();
                if(sendingOrganizationType.getOrganizationNames()!=null && sendingOrganizationType.getOrganizationNames().size()>0){
                    sendersName = sendingOrganizationType.getOrganizationNames().get(0);
                }
                if(sendingOrganizationType.getContacts()!=null){
                    List<ContactsType> contacts = sendingOrganizationType.getContacts();
                    if(contacts!=null && contacts.size()>0){
                        ContactsType contact = contacts.get(0);
                        if(contact!=null){
                            if(contact.getAddresses()!=null && contact.getAddresses().size()>0){
                                AddressType address = contact.getAddresses().get(0);
                                if(address.getAddressLines()!=null && address.getAddressLines().size()>0){
                                    sendersStreetAddress = address.getAddressLines().get(0);
                                }
                                sendersCity = address.getCity();
                                sendersState = address.getStateProvinceCode();//Domestic
                                sendersPostalCode = address.getPostalCode();
                                sendersCountry = address.getCountryCode()!=null?address.getCountryCode().toString(): CountryCodeType.US.toString();
                            }
                        }
                    }
                }
            }
            SourceDestinationType destination = transmissionDataType.getDestination();
            if(destination!=null && destination.getOrganization()!=null){
                OrganizationType receiversOrganizationType = destination.getOrganization();
                receiversCeeb = receiversOrganizationType.getCEEBACT();
                if(receiversOrganizationType.getOrganizationNames()!=null && receiversOrganizationType.getOrganizationNames().size()>0){
                    receiversName = receiversOrganizationType.getOrganizationNames().get(0);
                }
            }
        }
        if(transcriptRequest!=null && transcriptRequest.getRequests()!=null && transcriptRequest.getRequests().size()>0) {
            RequestType requestType = transcriptRequest.getRequests().get(0);
            RequestedStudentType requestedStudentType = requestType.getRequestedStudent();
            if (requestedStudentType != null) {
                requestedStudentAuthorizedIndicator = requestedStudentType.isReleaseAuthorizedIndicator();
                if(requestedStudentType.getReleaseAuthorizedMethod()!=null){
                    requestedStudentAuthorizedMethod = requestedStudentType.getReleaseAuthorizedMethod();
                }
                if (requestedStudentType.getPerson() != null) {
                    PersonType personType = requestedStudentType.getPerson();
                    if (personType != null) {
                        if (personType.getName() != null){
                            requestedStudentFirstName = personType.getName().getFirstName();
                            requestedStudentLastName = personType.getName().getLastName();
                        }
                        if(personType.getBirth()!=null && personType.getBirth().getBirthDate()!=null){
                            requestedStudentDateOfBirth = personType.getBirth().getBirthDate().toString();
                        }
                        if(personType.getContacts()!=null && personType.getContacts().size()>0){
                            ContactsType contactsType = personType.getContacts().get(0);
                            if(contactsType.getEmails()!=null && contactsType.getEmails().size()>0){
                                EmailType emailType = contactsType.getEmails().get(0);
                                requestedStudentEmail = emailType.getEmailAddress();
                            }
                        }
                    }
                }
                if(requestedStudentType.getAttendances()!=null && requestedStudentType.getAttendances().size()>0){
                    AttendanceType attendanceType = requestedStudentType.getAttendances().get(0);
                    if(attendanceType.getEnrollDate()!=null) {
                        requestedStudentEnrollDate = attendanceType.getEnrollDate().toString();
                    }
                    if(attendanceType.getExitDate()!=null) {
                        requestedStudentExitDate = attendanceType.getExitDate().toString();
                    }
                    if(attendanceType!=null && attendanceType.getAcademicAwardsReporteds()!=null && attendanceType.getAcademicAwardsReporteds().size()>0){
                        AcademicAwardsReportedType academicAwardsReportedType = attendanceType.getAcademicAwardsReporteds().get(0);
                        if(academicAwardsReportedType!=null){
                            requestedStudentAcademicAwardDate = academicAwardsReportedType.getAcademicAwardDate().toString();
                        }
                    }
                }
            }
        }
        assertTrue(documentId!=null);
        assertTrue(documentId.length()>0);
        assertTrue(documentTypeCode!=null);
        assertTrue(createDateTime!=null);
        assertTrue(createDateTime.length()>0);
        assertTrue(transmissionType!=null);
        assertTrue(sendersCeeb!=null);
        assertTrue(sendersCeeb.length()>0);



        assertTrue(sendersName!=null);
        assertTrue(sendersName.length()>0);
        assertTrue(sendersStreetAddress!=null);
        assertTrue(sendersStreetAddress.length()>0);
        assertTrue(sendersCity!=null);
        assertTrue(sendersCity.length()>0);
        assertTrue(sendersState!=null);
        assertTrue(sendersPostalCode!=null);
        assertTrue(sendersPostalCode.length()>0);
        assertTrue("US".equals(sendersCountry));
        assertTrue(receiversCeeb!=null);
        assertTrue(receiversCeeb.length()>0);

        assertTrue(receiversName!=null);
        assertTrue(receiversName.length()>0);
        assertTrue(requestedStudentFirstName!=null);
        assertTrue(requestedStudentFirstName.length()>0);
        assertTrue(requestedStudentLastName!=null);
        assertTrue(requestedStudentLastName.length()>0);
        assertTrue(requestedStudentDateOfBirth!=null);
        assertTrue(requestedStudentDateOfBirth.length()>0);
        assertTrue(requestedStudentEmail!=null);
        assertTrue(requestedStudentEmail.length()>0);
        assertTrue(requestedStudentEnrollDate!=null);
        assertTrue(requestedStudentEnrollDate.length()>0);
        assertTrue(requestedStudentExitDate!=null);
        assertTrue(requestedStudentExitDate.length()>0);
        assertTrue(requestedStudentAcademicAwardDate!=null);
        assertTrue(requestedStudentAcademicAwardDate.length()>0);
        assertTrue(requestedStudentAuthorizedIndicator!=null);
        assertTrue(requestedStudentAuthorizedMethod!=null);

        ValidationResponse validationResponse = TranscriptRequestValidator.validateTranscriptRequestRequiredContent(transcriptRequest);
        assertFalse(SeverityCodeType.FATAL_ERROR.equals(validationResponse.getSeverity()) || SeverityCodeType.ERROR.equals(validationResponse.getSeverity()));
    }

    private void testTranscriptRequestContentRecommended(TranscriptRequest transcriptRequest){
        String requestTrackingId = null;
        String sendersPhone = null;
        String sendersEmail = null;
        String receiversStreetAddress = null;
        String receiversCity = null;
        String receiversState = null;
        String receiversPostalCode = null;
        String receiversCountry = null;
        String receiversPhone = null;
        String receiversEmail = null;
        String requestedStudentMiddleName = null;
        String requestedStudentPartialSsn = null;
        Boolean requestedStudentCurrentEnrollmentIndicator = null;
        String requestedStudentAcademicAwardTitle = null;
        if(transcriptRequest!=null && transcriptRequest.getTransmissionData()!=null){
            TransmissionDataType transmissionDataType = transcriptRequest.getTransmissionData();
            requestTrackingId = transmissionDataType.getRequestTrackingID();
            SourceDestinationType sourceDestinationType = transmissionDataType.getSource();
            if(sourceDestinationType!=null && sourceDestinationType.getOrganization()!=null && sourceDestinationType.getOrganization().getContacts()!=null){
                List<ContactsType> contacts = sourceDestinationType.getOrganization().getContacts();
                if(contacts!=null && contacts.size()>0){
                    ContactsType contact = contacts.get(0);
                    if(contact!=null && contact.getPhones()!=null && contact.getPhones().size()>0){
                        PhoneType phone = contact.getPhones().get(0);
                        if(phone!=null && phone.getCountryPrefixCode()!=null && phone.getAreaCityCode()!=null && phone.getPhoneNumber()!=null) {
                            StringBuilder phoneSb = new StringBuilder();
                            phoneSb.append(phone.getCountryPrefixCode()).append("-").append(phone.getAreaCityCode()).append("-").append(phone.getPhoneNumber());
                            sendersPhone = phoneSb.toString();
                        }
                        if(contact.getEmails()!=null){
                            EmailType email = contact.getEmails().get(0);
                            if(email!=null && email.getEmailAddress()!=null){
                                sendersEmail = email.getEmailAddress();
                            }
                        }
                    }
                }
            }
            SourceDestinationType destination = transmissionDataType.getDestination();
            if(destination!=null && destination.getOrganization()!=null && destination.getOrganization().getContacts()!=null){
                List<ContactsType> contacts = destination.getOrganization().getContacts();
                if(contacts!=null && contacts.size()>0){
                    ContactsType contact = contacts.get(0);
                    if(contact!=null){
                        if(contact.getPhones()!=null && contact.getPhones().size()>0) {
                            PhoneType phone = contact.getPhones().get(0);
                            if (phone != null && phone.getCountryPrefixCode() != null && phone.getAreaCityCode() != null && phone.getPhoneNumber() != null) {
                                StringBuilder phoneSb = new StringBuilder();
                                phoneSb.append(phone.getCountryPrefixCode()).append("-").append(phone.getAreaCityCode()).append("-").append(phone.getPhoneNumber());
                                receiversPhone = phoneSb.toString();
                            }
                            if (contact.getEmails() != null) {
                                EmailType email = contact.getEmails().get(0);
                                if (email != null && email.getEmailAddress() != null) {
                                    receiversEmail = email.getEmailAddress();
                                }
                            }
                        }
                        if(contact.getAddresses()!=null && contact.getAddresses().size()>0){
                            AddressType address = contact.getAddresses().get(0);
                            if(address.getAddressLines()!=null && address.getAddressLines().size()>0){
                                receiversStreetAddress = address.getAddressLines().get(0);
                            }
                            receiversCity = address.getCity();
                            StateProvinceCodeType stateProvinceCodeType = address.getStateProvinceCode();//Domestic
                            if(stateProvinceCodeType!=null){
                                receiversState = stateProvinceCodeType.toString();
                            }
                            receiversPostalCode = address.getPostalCode();
                            receiversCountry = address.getCountryCode()!=null?address.getCountryCode().toString(): CountryCodeType.US.toString();
                        }
                    }
                }
            }
        }
        if(transcriptRequest!=null && transcriptRequest.getRequests()!=null && transcriptRequest.getRequests().size()>0) {
            RequestType requestType = transcriptRequest.getRequests().get(0);
            RequestedStudentType requestedStudentType = requestType.getRequestedStudent();
            if (requestedStudentType != null) {
                if (requestedStudentType.getPerson() != null) {
                    PersonType personType = requestedStudentType.getPerson();
                    if (personType != null) {
                        if (personType.getName() != null && personType.getName().getMiddleNames() != null && personType.getName().getMiddleNames().size() > 0) {
                            requestedStudentMiddleName = personType.getName().getMiddleNames().get(0);
                        }
                        requestedStudentPartialSsn = personType.getPartialSSN();
                    }
                }
                if(requestedStudentType.getAttendances()!=null && requestedStudentType.getAttendances().size()>0){
                    AttendanceType attendanceType = requestedStudentType.getAttendances().get(0);
                    requestedStudentCurrentEnrollmentIndicator = attendanceType.isCurrentEnrollmentIndicator();
                    if(attendanceType!=null && attendanceType.getAcademicAwardsReporteds()!=null && attendanceType.getAcademicAwardsReporteds().size()>0){
                        AcademicAwardsReportedType academicAwardsReportedType = attendanceType.getAcademicAwardsReporteds().get(0);
                        if(academicAwardsReportedType!=null){
                            requestedStudentAcademicAwardTitle = academicAwardsReportedType.getAcademicAwardTitle();
                        }
                    }
                }
            }
        }
        assertTrue("123456789".equals(requestTrackingId));
        assertTrue("1-987-123-4567".equals(sendersPhone));
        assertTrue("a".equals(sendersEmail));
        assertTrue("a".equals(receiversStreetAddress));
        assertTrue("aa".equals(receiversCity));
        assertTrue("CA".equals(receiversState));
        assertTrue("95825".equals(receiversPostalCode));
        assertTrue("US".equals(receiversCountry));
        assertTrue("1-987-123-4567".equals(receiversPhone));
        assertTrue("a".equals(receiversEmail));
        assertTrue("b".equals(requestedStudentMiddleName));
        assertTrue("1234".equals(requestedStudentPartialSsn));
        assertTrue(requestedStudentCurrentEnrollmentIndicator);
        assertTrue("Degree".equals(requestedStudentAcademicAwardTitle));

        ValidationResponse validationResponse1 = TranscriptRequestValidator.validateTranscriptRequestRequiredContent(transcriptRequest);
        assertTrue(validationResponse1.getSeverity()==null);
        assertTrue(validationResponse1.getErrors().size()==0);
        ValidationResponse validationResponse2 = TranscriptRequestValidator.validateTranscriptRequestRecommendedContent(transcriptRequest);
        assertTrue(validationResponse2.getSeverity()==null);
        assertTrue(validationResponse2.getErrors().size()==0);
    }

    private void testdocumentInfoContentRequired(DocumentInfo DocumentInfo){
        String fileName = null;
        DocumentTypeCode documentType = null;
        if(DocumentInfo!=null){
            fileName = DocumentInfo.getFileName();
            documentType = DocumentInfo.getDocumentType();
        }
        assertTrue("0fd6ad111a83487c9536ea71602ecd7d_document.pdf".equals(fileName));
        assertTrue(DocumentTypeCode.TRANSCRIPT==documentType);
    }

    @Test
    public void testRequestPescXmlTranscript() throws Exception {
        JAXBContext jc = JAXBContext.newInstance("org.pesc.sdk.message.transcriptrequest.v1_2.impl");
        Unmarshaller u = jc.createUnmarshaller();
        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        URL transcriptRequestSchemaUrl = getClass().getClassLoader().getResource("xsd/pesc/TranscriptRequest_v1.2.0.xsd");
        Schema schema = sf.newSchema(transcriptRequestSchemaUrl);
        u.setSchema(schema);
        TranscriptRequest transcriptRequest = null;
        DocumentInfo documentInfo = null;
        try {
            transcriptRequest = (TranscriptRequest) u.unmarshal(getClass().getClassLoader().getResource("org/pesc/sdk/message/transcriptrequest/v1_2/xmlTranscript_request.xml"));
            JAXBContext documentInfoContext = JAXBContext.newInstance("org.pesc.sdk.message.documentinfo.v1_0.impl");
            Unmarshaller documentInfoUnmarshaller = documentInfoContext.createUnmarshaller();
            SchemaFactory documentinfochemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            URL documentinfochemaUrl = getClass().getClassLoader().getResource("xsd/pesc/DocumentInfo_v1.0.0.xsd");
            Schema documentInfoXsdSchema = documentinfochemaFactory.newSchema(documentinfochemaUrl);
            documentInfoUnmarshaller.setSchema(documentInfoXsdSchema);
            documentInfo = (DocumentInfo)documentInfoUnmarshaller.unmarshal((Node) transcriptRequest.getTransmissionData().getUserDefinedExtensions().getAny());
        } catch (Exception e) {
            logger.error("Error Unmarshalling TranscriptRequest", e);
            throw e;
        }

        Marshaller m = jc.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        m.marshal(transcriptRequest, byteArrayOutputStream);
        logger.info(byteArrayOutputStream.toString());
        testTranscriptRequestContentRequired(transcriptRequest);

        DocumentInfoValidator.validateDocumentInfoRequiredContent(documentInfo);
        String fileName = documentInfo.getFileName();
        assertTrue(fileName.endsWith("_document.xml") || fileName.endsWith("_document.pdf"));
        assertTrue(DocumentTypeCode.TRANSCRIPT==documentInfo.getDocumentType());

    }

    @Test
    public void testRequestLetterOfRecommendation() throws Exception {
        String[] files = new String[2];
        files[0] = "org/pesc/sdk/message/transcriptrequest/v1_2/letterOfRecommendationCounselor_request.xml";
        files[1] = "org/pesc/sdk/message/transcriptrequest/v1_2/letterOfRecommendationTeacher_request.xml";
        for(String file: files){
            JAXBContext jc = JAXBContext.newInstance("org.pesc.sdk.message.transcriptrequest.v1_2.impl");
            Unmarshaller u = jc.createUnmarshaller();
            SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            URL transcriptRequestSchemaUrl = getClass().getClassLoader().getResource("xsd/pesc/TranscriptRequest_v1.2.0.xsd");
            Schema schema = sf.newSchema(transcriptRequestSchemaUrl);
            u.setSchema(schema);
            TranscriptRequest transcriptRequest = null;
            DocumentInfo DocumentInfo = null;
            try {
                transcriptRequest = (TranscriptRequest) u.unmarshal(getClass().getClassLoader().getResource(file));
                JAXBContext documentInfoContext = JAXBContext.newInstance("org.pesc.sdk.message.documentinfo.v1_0.impl");
                Unmarshaller documentInfoUnmarshaller = documentInfoContext.createUnmarshaller();
                SchemaFactory documentinfochemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
                URL documentinfochemaUrl = getClass().getClassLoader().getResource("xsd/pesc/DocumentInfo_v1.0.0.xsd");
                Schema documentInfoXsdSchema = documentinfochemaFactory.newSchema(documentinfochemaUrl);
                documentInfoUnmarshaller.setSchema(documentInfoXsdSchema);
                DocumentInfo = (DocumentInfo)documentInfoUnmarshaller.unmarshal((Node) transcriptRequest.getTransmissionData().getUserDefinedExtensions().getAny());
            } catch (Exception e) {
                logger.error("Error Unmarshalling TranscriptRequest", e);
                throw e;
            }

            Marshaller m = jc.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            m.marshal(transcriptRequest, byteArrayOutputStream);
            logger.info(byteArrayOutputStream.toString());
            testTranscriptRequestContentRequired(transcriptRequest);

            DocumentInfoValidator.validateDocumentInfoRequiredContent(DocumentInfo);
            String fileName = DocumentInfo.getFileName();
            assertTrue(fileName.endsWith("_document.pdf"));
            assertTrue(DocumentTypeCode.LETTER_OF_RECOMMENDATION==DocumentInfo.getDocumentType()
                    || DocumentTypeCode.COUNSELOR_RECOMMENDATION==DocumentInfo.getDocumentType());

        }
    }

}
