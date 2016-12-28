package org.pesc.cds.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pesc.cds.domain.Transaction;
import org.pesc.cds.model.TransactionStatus;
import org.pesc.cds.repository.StringUtils;
import org.pesc.sdk.core.coremain.v1_14.AcknowledgmentCodeType;
import org.pesc.sdk.core.coremain.v1_14.DocumentTypeCodeType;
import org.pesc.sdk.core.coremain.v1_14.TransmissionTypeType;
import org.pesc.sdk.message.collegetranscript.v1_6.CollegeTranscript;
import org.pesc.sdk.message.functionalacknowledgement.v1_2.Acknowledgment;
import org.pesc.sdk.message.functionalacknowledgement.v1_2.AcknowledgmentDataType;
import org.pesc.sdk.message.functionalacknowledgement.v1_2.SyntaxErrorType;
import org.pesc.sdk.message.transcriptrequest.v1_4.TranscriptRequest;
import org.pesc.sdk.sector.academicrecord.v1_9.SourceDestinationType;
import org.pesc.sdk.sector.academicrecord.v1_9.TransmissionDataType;
import org.pesc.sdk.util.ValidationUtils;
import org.pesc.sdk.util.XmlFileType;
import org.pesc.sdk.util.XmlSchemaVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.ResourceAccessException;
import org.xml.sax.SAXException;

import javax.naming.OperationNotSupportedException;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by James Whetstone (jwhetstone@ccctechcenter.org) on 7/19/16.
 */
@Service
public class FileProcessorService {

    private static final Log log = LogFactory.getLog(FileProcessorService.class);

    @Value("${networkServer.ssl.trust-certificates}")
    private Boolean trustCertificates;

    @Value("${networkServer.outbox.path}")
    private String localServerOutboxPath;

    @Value("${networkServer.inbox.path}")
    private String localServerInboxPath;


    @Autowired
    @Qualifier("myRestTemplate")
    private OAuth2RestOperations restTemplate;

    private static final org.pesc.sdk.message.functionalacknowledgement.v1_2.ObjectFactory functionalacknowledgementObjectFactory = new org.pesc.sdk.message.functionalacknowledgement.v1_2.ObjectFactory();
    private static final org.pesc.sdk.sector.academicrecord.v1_9.ObjectFactory academicRecordObjectFactory = new org.pesc.sdk.sector.academicrecord.v1_9.ObjectFactory();

    public static final String DEFAULT_DELIVERY_MESSAGE = "Successfully delivered document.";
    public static final String DEFAULT_RECEIVE_MESSAGE = "Received document.";

    public void sendAck(String ackURL, Acknowledgment acknowledgment) {

        if (ackURL != null && !ackURL.isEmpty()) {

            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            //headers.setContentType(MediaType.TEXT_XML);
            headers.add("Content-Type", "text/xml; charset=utf8");


            try {
                ResponseEntity<String> response = restTemplate.exchange
                        (ackURL, HttpMethod.POST, new org.springframework.http.HttpEntity<String>(toXml(acknowledgment), headers), String.class);

                log.debug(response.getStatusCode());
                if (response.getStatusCode() != HttpStatus.OK) {
                    throw new RuntimeException(response.getStatusCode().getReasonPhrase());
                }

            }
            catch(ResourceAccessException e) {

                //Force the OAuth client to retrieve the token again whenever it is used again.
                restTemplate.getOAuth2ClientContext().setAccessToken(null);

                log.error(e);
                throw e;
            }

        }
    }


    public void sendAck(String ackURL, Integer transactionId, String message, TransactionStatus status) {
        // send response back to sending network server

        if (ackURL != null && !ackURL.isEmpty() && transactionId != null) {

            LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
            map.add("transactionId", transactionId.toString());
            map.add("status", status.name());
            map.add("message", message);


            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.setContentType(org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED);


            try {
                ResponseEntity<String> response = restTemplate.exchange
                        (ackURL, HttpMethod.POST, new org.springframework.http.HttpEntity<Object>(map, headers), String.class);

                log.debug(response.getStatusCode());
                if (response.getStatusCode() != HttpStatus.OK) {
                    throw new RuntimeException(response.getStatusCode().getReasonPhrase());
                }

            }
            catch(ResourceAccessException e) {

                //Force the OAuth client to retrieve the token again whenever it is used again.
                restTemplate.getOAuth2ClientContext().setAccessToken(null);

                log.error(e);
                throw e;
            }

        }

    }

    /**
     * This asynchronous method is intended to be starting point for modeling and automating the receiver's delivery process.
     * When this method is invoked, the document has already been received and stored in the file system.
     * transaction.getFilePath() returns the location of the file.
     */
    @Async
    public void deliverFile(Transaction transaction){

        //If appropriate add logic here to model your custom file processing logic.

        //Only documents that are either PESCXML transcripts or a document accompanied by a transcript request
        //will utilize the functional acknowledgement.  This is because the PESC functional ack requires information
        //contained in the PESCXML transcript or transcript request.
        Acknowledgment functionalacknowledgement = null;
        String message = DEFAULT_DELIVERY_MESSAGE;
        TransactionStatus status = TransactionStatus.SUCCESS;

        try {
            if (transaction.getFileFormat().equalsIgnoreCase("PESCXML")){
                CollegeTranscript collegeTranscript = getCollegeTranscript(transaction.getRequestFilePath());

                functionalacknowledgement = buildAcceptedAcknowledgement(collegeTranscript) ;
            }
            else if (!StringUtils.isEmpty(transaction.getRequestFilePath()) ) {
                TranscriptRequest transcriptRequest = getTranscriptRequest(transaction.getRequestFilePath());

                functionalacknowledgement = buildAcceptedAcknowledgement(transcriptRequest) ;
            }

        }
        catch (Exception e){
            log.error(e);
            message = e.getMessage();
            status = TransactionStatus.FAILURE;
        }



        sendAck(transaction.getAckURL(), functionalacknowledgement);


    }

    public Acknowledgment buildAcceptedAcknowledgement(TranscriptRequest transcriptRequest) {
        Acknowledgment ack = buildBaseAcknowledgement(transcriptRequest.getTransmissionData().getDestination(),
                transcriptRequest.getTransmissionData().getSource(), transcriptRequest.getTransmissionData().getRequestTrackingID(),
                transcriptRequest.getTransmissionData().getDocumentID(), AcknowledgmentCodeType.ACCEPTED);

        return ack;
    }

    public Acknowledgment buildAcceptedAcknowledgement(SourceDestinationType source, SourceDestinationType destination,
                                                       String requestTrackingID, String documentID) {
        Acknowledgment ack = buildBaseAcknowledgement(source,destination,requestTrackingID,documentID,AcknowledgmentCodeType.ACCEPTED);
        return ack;
    }

    public Acknowledgment buildRejectedAcknowledgement(SourceDestinationType source, SourceDestinationType destination,
                                                        String requestTrackingID, String documentID, List<SyntaxErrorType> errors) {
        Acknowledgment ack = buildBaseAcknowledgement(source, destination, requestTrackingID, documentID,AcknowledgmentCodeType.REJECTED);
        ack.getAcknowledgmentData().getSyntaxErrors().addAll(errors);
        return ack;
    }

    public Acknowledgment buildBaseAcknowledgement(SourceDestinationType source, SourceDestinationType destination,
                                                    String requestTrackingID, String documentID, AcknowledgmentCodeType ackCode) {
        Acknowledgment ack = functionalacknowledgementObjectFactory.createAcknowledgment();

        TransmissionDataType transmissionData = academicRecordObjectFactory.createTransmissionDataType();

        transmissionData.setSource(source);
        transmissionData.setDestination(destination);
        transmissionData.setCreatedDateTime(Calendar.getInstance().getTime());
        transmissionData.setDocumentID(documentID);
        transmissionData.setDocumentTypeCode(DocumentTypeCodeType.ACKNOWLEDGMENT);
        transmissionData.setTransmissionType(TransmissionTypeType.ORIGINAL);
        //Request tracking ID here is the transaction key as defined by the sender's network server.
        transmissionData.setRequestTrackingID(requestTrackingID);

        AcknowledgmentDataType ackData = functionalacknowledgementObjectFactory.createAcknowledgmentDataType();
        ackData.setDocumentID(documentID);
        ackData.setAcknowledgmentCode(ackCode);


        ack.setTransmissionData(transmissionData);
        ack.setAcknowledgmentData(ackData);

        ack.getTransmissionData();

        return ack;
    }

    public Acknowledgment buildAcceptedAcknowledgement(CollegeTranscript transcript) {

        Acknowledgment ack = buildAcceptedAcknowledgement(transcript.getTransmissionData().getDestination(),
                transcript.getTransmissionData().getSource(), transcript.getTransmissionData().getRequestTrackingID(),
                transcript.getTransmissionData().getDocumentID());

        return ack;
    }

    public Acknowledgment buildRejectedAcknowledgement(CollegeTranscript transcript, List<SyntaxErrorType> errors) {

        Acknowledgment ack = buildRejectedAcknowledgement(transcript.getTransmissionData().getDestination(),
                transcript.getTransmissionData().getSource(), transcript.getTransmissionData().getRequestTrackingID(),
                transcript.getTransmissionData().getDocumentID(), errors);

        return ack;
    }

    public Acknowledgment buildAcceptedBatchAcknowledgement(CollegeTranscript transcript, String batchID) {

        Acknowledgment ack = buildAcceptedAcknowledgement(transcript);
        ack.getAcknowledgmentData().setBatchID(batchID);
        return ack;
    }

    public Acknowledgment buildRejectedBatchAcknowledgement(CollegeTranscript transcript, List<SyntaxErrorType> errors,
                                                            String batchID) {

        Acknowledgment ack = buildRejectedAcknowledgement(transcript, errors);
        ack.getAcknowledgmentData().setBatchID(batchID);
        return ack;
    }


    private TranscriptRequest getTranscriptRequest(String filePath) throws JAXBException, SAXException {

        JAXBContext jc = JAXBContext.newInstance("org.pesc.sdk.message.transcriptrequest.v1_4.impl");
        Unmarshaller u = jc.createUnmarshaller();
        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        URL transcriptRequestSchemaUrl = getClass().getClassLoader().getResource("xsd/pesc/TranscriptRequest_v1.4.0.xsd");
        Schema schema = sf.newSchema(transcriptRequestSchemaUrl);
        u.setSchema(schema);
        return (TranscriptRequest) u.unmarshal(new File(filePath));
    }

    private CollegeTranscript getCollegeTranscript(String filePath) throws JAXBException, SAXException {

        JAXBContext jc = JAXBContext.newInstance("org.pesc.sdk.message.collegetranscript.v1_6.impl");
        Unmarshaller u = jc.createUnmarshaller();
        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        URL transcriptRequestSchemaUrl = getClass().getClassLoader().getResource("xsd/pesc/CollegeTranscript_v1.6.0.xsd");
        Schema schema = sf.newSchema(transcriptRequestSchemaUrl);
        u.setSchema(schema);
        return (CollegeTranscript) u.unmarshal(new File(filePath));
    }

    public String toXml(Acknowledgment acknowledgment) {
        try {

            JAXBContext jc = JAXBContext.newInstance("org.pesc.sdk.message.functionalacknowledgement.v1_2.impl");
            Marshaller marshaller = jc.createMarshaller();

            Schema schema = ValidationUtils.getSchema(XmlFileType.FUNCTIONAL_ACKNOWLEDGEMENT, XmlSchemaVersion.V1_2_0);

            marshaller.setSchema(schema);
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );

            StringWriter writer = new StringWriter();
            marshaller.marshal(acknowledgment, writer);

            return writer.toString();

        } catch (JAXBException e) {
            log.error(e);
        } catch (SAXException e) {
            log.error(e);
        } catch (OperationNotSupportedException e) {
            log.error(e);
        }

        return null;
    }


    @PreAuthorize("(hasRole('ROLE_ADMIN')")
    public List<String> getInboxDocumentList() {
        List<String> retList = new ArrayList<String>();
        File directory = new File(localServerInboxPath);
        File[] fList = directory.listFiles();
        for (File file : fList) {
            if (file.isFile()){
                retList.add(file.getName());
            }
        }
        return retList;
    }

    @PreAuthorize("(hasRole('ROLE_ADMIN')")
    public List<String> getOutboxDocumentList() {
        List<String> retList = new ArrayList<String>();
        File directory = new File(localServerOutboxPath);
        File[] fList = directory.listFiles();
        for (File file : fList) {
            if (file.isFile()){
                retList.add(file.getName());
            }
        }
        return retList;
    }
}
