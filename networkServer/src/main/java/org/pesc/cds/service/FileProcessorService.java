package org.pesc.cds.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pesc.cds.domain.Transaction;
import org.pesc.cds.repository.StringUtils;
import org.pesc.sdk.core.coremain.v1_14.AcknowledgmentCodeType;
import org.pesc.sdk.core.coremain.v1_14.DocumentTypeCodeType;
import org.pesc.sdk.core.coremain.v1_14.TransmissionTypeType;
import org.pesc.sdk.message.collegetranscript.v1_6.CollegeTranscript;
import org.pesc.sdk.message.functionalacknowledgement.v1_2.Acknowledgment;
import org.pesc.sdk.message.functionalacknowledgement.v1_2.AcknowledgmentDataType;
import org.pesc.sdk.message.functionalacknowledgement.v1_2.SyntaxErrorType;
import org.pesc.sdk.message.functionalacknowledgement.v1_2.impl.AcknowledgmentImpl;
import org.pesc.sdk.message.transcriptrequest.v1_4.TranscriptRequest;
import org.pesc.sdk.message.transcriptresponse.v1_4.TranscriptResponse;
import org.pesc.sdk.sector.academicrecord.v1_9.*;
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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
    private static final org.pesc.sdk.message.transcriptresponse.v1_4.ObjectFactory transcriptResponseObjectFactory = new org.pesc.sdk.message.transcriptresponse.v1_4.ObjectFactory();

    public static final String DEFAULT_DELIVERY_MESSAGE = "Successfully delivered document.";
    public static final String DEFAULT_RECEIVE_MESSAGE = "Received document.";

    public void sendAck(String ackURL, Acknowledgment acknowledgment) {

        if (ackURL != null && !ackURL.isEmpty()) {


            try {
                ResponseEntity<String> response = restTemplate.exchange
                        (ackURL, HttpMethod.POST, new org.springframework.http.HttpEntity<Acknowledgment>((AcknowledgmentImpl)acknowledgment), String.class);

                log.debug(response.getStatusCode());
                if (response.getStatusCode() != HttpStatus.OK) {
                    throw new RuntimeException(response.getStatusCode().getReasonPhrase());
                }

            }
            catch(ResourceAccessException e) {

                //Force the OAuth client to retrieve the token again whenever it is used again.
                restTemplate.getOAuth2ClientContext().setAccessToken(null);

                log.error(e);
            }
            catch (HttpClientErrorException e) {
                log.error("Failed to send PESC functional acknowledgement: ." + e.getResponseBodyAsString(), e);
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

        Date currentTime = Calendar.getInstance().getTime();
        String ackDocID = String.format("%d-%d", currentTime.getTime(), transaction.getId());

        try {
            if (transaction.getFileFormat().equalsIgnoreCase("PESCXML")){
                CollegeTranscript collegeTranscript = getCollegeTranscript(transaction.getRequestFilePath());

                functionalacknowledgement = buildAcceptedAcknowledgement(collegeTranscript, ackDocID) ;
            }
            else if (!StringUtils.isEmpty(transaction.getRequestFilePath()) ) {
                TranscriptRequest transcriptRequest = getTranscriptRequest(transaction.getRequestFilePath());

                functionalacknowledgement = buildAcceptedAcknowledgement(transcriptRequest, ackDocID) ;
            }
            else {


            }

        }
        catch (Exception e){
            log.error(e);
        }



        sendAck(transaction.getAckURL(), functionalacknowledgement);


    }

    public ResponseHoldType createResponseHold(HoldReasonType reason, Date plannedReleaseDate) {
        ResponseHoldType hold = academicRecordObjectFactory.createResponseHoldType();

        hold.setHoldReason(reason);
        hold.setPlannedReleaseDate(plannedReleaseDate);

        return hold;
    }

    public TranscriptResponse buildBaseTranscriptResponse(SourceDestinationType source,
                                                          SourceDestinationType destination,
                                                          String requestTrackingID,
                                                          String transcriptRequestTrackingID,
                                                          String transcriptResponseDocID,
                                                          ResponseStatusType responseStatus,
                                                          List<ResponseHoldType> holds,
                                                          RequestedStudentType requestedStudent) {
        TranscriptResponse transcriptResponse = transcriptResponseObjectFactory.createTranscriptResponse();

        TransmissionDataType transmissionData = academicRecordObjectFactory.createTransmissionDataType();

        Date createdDateTime = Calendar.getInstance().getTime();

        transmissionData.setSource(source);
        transmissionData.setDestination(destination);
        transmissionData.setCreatedDateTime(createdDateTime);
        transmissionData.setDocumentID(transcriptResponseDocID);
        transmissionData.setDocumentTypeCode(DocumentTypeCodeType.RESPONSE);
        transmissionData.setTransmissionType(TransmissionTypeType.ORIGINAL);
        transmissionData.setRequestTrackingID(requestTrackingID);


        ResponseType response = academicRecordObjectFactory.createResponseType();
        response.setCreatedDateTime( createdDateTime );
        response.setRequestTrackingID(transcriptRequestTrackingID);
        response.setRecipientTrackingID(requestTrackingID);
        response.setResponseStatus(responseStatus);
        response.getResponseHolds().addAll(holds);
        response.setRequestedStudent(requestedStudent);

        transcriptResponse.setTransmissionData(transmissionData);
        transcriptResponse.getResponses().add(response);


        return transcriptResponse;
    }

    public Acknowledgment buildAcceptedAcknowledgement(TranscriptRequest transcriptRequest, String ackDocID) {
        Acknowledgment ack = buildBaseAcknowledgement(transcriptRequest.getTransmissionData().getDestination(),
                transcriptRequest.getTransmissionData().getSource(), transcriptRequest.getTransmissionData().getRequestTrackingID(),
                transcriptRequest.getTransmissionData().getDocumentID(), AcknowledgmentCodeType.ACCEPTED, ackDocID);

        return ack;
    }

    public Acknowledgment buildAcceptedAcknowledgement(SourceDestinationType source, SourceDestinationType destination,
                                                       String requestTrackingID, String documentID, String ackDocID) {
        Acknowledgment ack = buildBaseAcknowledgement(source,destination,requestTrackingID,documentID,AcknowledgmentCodeType.ACCEPTED, ackDocID);
        return ack;
    }

    public Acknowledgment buildRejectedAcknowledgement(SourceDestinationType source, SourceDestinationType destination,
                                                        String requestTrackingID, String documentID, List<SyntaxErrorType> errors, String ackDocID) {
        Acknowledgment ack = buildBaseAcknowledgement(source, destination, requestTrackingID, documentID,AcknowledgmentCodeType.REJECTED,ackDocID);
        ack.getAcknowledgmentData().getSyntaxErrors().addAll(errors);
        return ack;
    }

    public Acknowledgment buildBaseAcknowledgement(SourceDestinationType source,
                                                   SourceDestinationType destination,
                                                   String requestTrackingID,
                                                   String documentID,
                                                   AcknowledgmentCodeType ackCode,
                                                   String ackDocID) {
        Acknowledgment ack = functionalacknowledgementObjectFactory.createAcknowledgment();

        TransmissionDataType transmissionData = academicRecordObjectFactory.createTransmissionDataType();

        transmissionData.setSource(source);
        transmissionData.setDestination(destination);
        transmissionData.setCreatedDateTime(Calendar.getInstance().getTime());
        transmissionData.setDocumentID(ackDocID);
        transmissionData.setDocumentTypeCode(DocumentTypeCodeType.ACKNOWLEDGMENT);
        transmissionData.setTransmissionType(TransmissionTypeType.ORIGINAL);
        //Request tracking ID here is the transaction key as defined by the sender's network server.
        transmissionData.setRequestTrackingID(requestTrackingID);

        AcknowledgmentDataType ackData = functionalacknowledgementObjectFactory.createAcknowledgmentDataType();
        ackData.setDocumentID(documentID);
        ackData.setAcknowledgmentCode(ackCode);

        ack.setTransmissionData(transmissionData);
        ack.setAcknowledgmentData(ackData);

        return ack;
    }

    public Acknowledgment buildAcceptedAcknowledgement(CollegeTranscript transcript, String ackDocID) {

        Acknowledgment ack = buildAcceptedAcknowledgement(transcript.getTransmissionData().getDestination(),
                transcript.getTransmissionData().getSource(), transcript.getTransmissionData().getRequestTrackingID(),
                transcript.getTransmissionData().getDocumentID(),ackDocID);

        return ack;
    }

    public Acknowledgment buildRejectedAcknowledgement(CollegeTranscript transcript, List<SyntaxErrorType> errors, String ackDocID) {

        Acknowledgment ack = buildRejectedAcknowledgement(transcript.getTransmissionData().getDestination(),
                transcript.getTransmissionData().getSource(), transcript.getTransmissionData().getRequestTrackingID(),
                transcript.getTransmissionData().getDocumentID(), errors, ackDocID);

        return ack;
    }

    public Acknowledgment buildAcceptedBatchAcknowledgement(CollegeTranscript transcript, String batchID, String ackDocID) {

        Acknowledgment ack = buildAcceptedAcknowledgement(transcript, ackDocID);
        ack.getAcknowledgmentData().setBatchID(batchID);
        return ack;
    }

    public Acknowledgment buildRejectedBatchAcknowledgement(CollegeTranscript transcript,
                                                            List<SyntaxErrorType> errors,
                                                            String batchID,
                                                            String ackDocID) {

        Acknowledgment ack = buildRejectedAcknowledgement(transcript, errors, ackDocID);
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
