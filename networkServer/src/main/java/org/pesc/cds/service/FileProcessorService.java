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

package org.pesc.cds.service;

import com.google.common.base.Preconditions;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pesc.cds.domain.Transaction;
import org.pesc.cds.model.DocumentFormat;
import org.pesc.cds.model.DocumentType;
import org.pesc.cds.model.EndpointMode;
import org.pesc.cds.model.TransactionStatus;
import org.pesc.cds.repository.StringUtils;
import org.pesc.cds.repository.TransactionService;
import org.pesc.cds.utils.ErrorUtils;
import org.pesc.sdk.core.coremain.v1_14.AcknowledgmentCodeType;
import org.pesc.sdk.core.coremain.v1_14.DocumentTypeCodeType;
import org.pesc.sdk.core.coremain.v1_14.SeverityCodeType;
import org.pesc.sdk.core.coremain.v1_14.TransmissionTypeType;
import org.pesc.sdk.message.collegetranscript.v1_6.CollegeTranscript;
import org.pesc.sdk.message.functionalacknowledgement.v1_2.Acknowledgment;
import org.pesc.sdk.message.functionalacknowledgement.v1_2.AcknowledgmentDataType;
import org.pesc.sdk.message.functionalacknowledgement.v1_2.SyntaxErrorLocatorType;
import org.pesc.sdk.message.functionalacknowledgement.v1_2.SyntaxErrorType;
import org.pesc.sdk.message.functionalacknowledgement.v1_2.impl.AcknowledgmentImpl;
import org.pesc.sdk.message.transcriptrequest.v1_4.TranscriptRequest;
import org.pesc.sdk.message.transcriptresponse.v1_4.TranscriptResponse;
import org.pesc.sdk.sector.academicrecord.v1_9.*;
import org.pesc.sdk.util.ValidationUtils;
import org.pesc.sdk.util.XmlFileType;
import org.pesc.sdk.util.XmlSchemaVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.xml.sax.SAXException;

import javax.naming.OperationNotSupportedException;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import java.io.*;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.*;

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

    @Value("${networkServer.id}")
    private Integer localServerId;

    @Value("${networkServer.webServiceURL}")
    private String localServerWebServiceURL;

    @Autowired
    private TranscriptAcknowledgementService transcriptAcknowledgementService;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private PKIService pkiService;

    @Autowired
    @Qualifier("myRestTemplate")
    private OAuth2RestOperations restTemplate;

    private static final org.pesc.sdk.message.functionalacknowledgement.v1_2.ObjectFactory functionalacknowledgementObjectFactory = new org.pesc.sdk.message.functionalacknowledgement.v1_2.ObjectFactory();
    private static final org.pesc.sdk.sector.academicrecord.v1_9.ObjectFactory academicRecordObjectFactory = new org.pesc.sdk.sector.academicrecord.v1_9.ObjectFactory();
    private static final org.pesc.sdk.message.transcriptresponse.v1_4.ObjectFactory transcriptResponseObjectFactory = new org.pesc.sdk.message.transcriptresponse.v1_4.ObjectFactory();

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
            catch (Exception e) {
                log.error("Failed to send PESC functional acknowledgement.", e);
            }

        }
    }

    /**
     * Implementors should develop logic here to hand the transcript request---obtain the requested student's
     * transcript and send it to the source organization using the source's EDExchange endpoint for the
     * transcript.
     * @param transaction
     * @param ackDocID
     * @return
     */
    private Acknowledgment handleTranscriptRequest(Transaction transaction, String ackDocID) {


        return null;
    }

    private void sendTranscriptAcknowledgment(CollegeTranscript transcript, Integer recipientID, Integer senderID) {

        Transaction tx = new Transaction();

        tx.setRecipientId(recipientID);
        tx.setSenderId(senderID);
        tx.setSignerId(senderID);
        tx.setFileFormat(DocumentFormat.PESCXML.getFormatName());
        tx.setDocumentType(DocumentType.TRANSCRIPT_ACKNOWLEDGEMENT.getDocumentName());
        //tx.setDepartment("");
        tx.setAckURL(localServerWebServiceURL);
        tx.setOperation("SEND");
        tx.setOccurredAt(new Timestamp(Calendar.getInstance().getTimeInMillis()));

        tx = transactionService.create(tx);


        File outboxDirectory = new File(localServerOutboxPath);
        outboxDirectory.mkdirs();
        UUID uuid = UUID.randomUUID();
        String tranAckFilePath = "transcript-ack-" + uuid.toString()+".xml";

        File ackFile = new File(outboxDirectory, tranAckFilePath);

        tx.setFilePath(ackFile.getAbsolutePath());


        try {
            BufferedOutputStream stream2 = new BufferedOutputStream(new FileOutputStream(ackFile));

            org.pesc.sdk.message.transcriptacknowledgement.v1_3.Acknowledgment acknowledgment =
                    transcriptAcknowledgementService.buildBaseTranscriptAcknowledgement(transcript.getTransmissionData().getDestination(),
                            transcript.getTransmissionData().getSource(), transcript, String.valueOf(tx.getId()), transcript.getTransmissionData().getDocumentID());
            String xml = transcriptAcknowledgementService.toXml(acknowledgment);
            stream2.write(xml.getBytes("UTF-8"));
            stream2.close();
            tx.setFileSize(Long.valueOf(xml.getBytes().length));

            String endpointURI = organizationService.getEndpointForOrg(
                    tx.getRecipientId(), tx.getFileFormat(), tx.getDocumentType(), tx.getDepartment(), EndpointMode.LIVE);


            if (endpointURI == null) {
                String error = ErrorUtils.getNoEndpointFoundMessage(tx.getRecipientId(), tx.getFileFormat(),
                        tx.getDocumentType(), tx.getDepartment()) ;
                throw new IllegalArgumentException(error);
            }

            byte[] fileSignature = pkiService.createDigitalSignature(new FileInputStream(ackFile), pkiService.getSigningKeys().getPrivate());


            LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
            map.add("recipient_id", tx.getRecipientId());
            map.add("sender_id", tx.getSenderId());
            map.add("signer_id", tx.getSignerId());
            map.add("file_format", DocumentFormat.PESCXML.getFormatName());
            map.add("document_type", DocumentType.TRANSCRIPT_ACKNOWLEDGEMENT.getDocumentName());
            map.add("department", tx.getDepartment());
            map.add("transaction_id", tx.getId());
            map.add("ack_url", localServerWebServiceURL);
            map.add("file", new FileSystemResource(ackFile));
            map.add("signature", new ByteArrayResource(fileSignature){
                @Override
                public String getFilename(){
                    return "signature.dat";
                }
            });



            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.setContentType(org.springframework.http.MediaType.MULTIPART_FORM_DATA);


            ResponseEntity<String> response = restTemplate.exchange
                    (endpointURI, HttpMethod.POST, new org.springframework.http.HttpEntity<Object>(map, headers), String.class);


            log.info(response.getStatusCode().getReasonPhrase());

            if (response.getStatusCode() == HttpStatus.OK) {
                tx.setStatus(TransactionStatus.SUCCESS);
            }
            else {
                tx.setStatus(TransactionStatus.FAILURE);
                tx.setError("Failed to transmit PESC transcript acknowledgement document. HTTP Status  " + response.getStatusCode().getReasonPhrase());
            }


        }
        catch (Exception e) {
            log.error(e);
            tx.setError(e.getLocalizedMessage());
            tx.setStatus(TransactionStatus.FAILURE);
        }
        finally {
            transactionService.create(tx);
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
        Acknowledgment ack = null;

        Date currentTime = Calendar.getInstance().getTime();
        String ackDocID = String.format("%d-%d", currentTime.getTime(), transaction.getId());

        try {

            if (transaction.getStatus() == TransactionStatus.SUCCESS) {
                if (DocumentType.FUNCTIONAL_ACKNOWLEDGEMENT.getDocumentName().equalsIgnoreCase(transaction.getDocumentType())) {

                }
                else if (DocumentType.COLLEGE_TRANSCRIPT.getDocumentName().equalsIgnoreCase(transaction.getDocumentType())) {
                    if (DocumentFormat.PESCXML.getFormatName().equalsIgnoreCase(transaction.getFileFormat())){

                        CollegeTranscript collegeTranscript = getCollegeTranscript(transaction.getFilePath());
                        ack = buildAcceptedAcknowledgement(collegeTranscript, ackDocID) ;

                        sendTranscriptAcknowledgment(collegeTranscript, transaction.getSenderId(), transaction.getRecipientId());
                    }
                }
                else if (DocumentType.TRANSCRIPT_RESPONSE.getDocumentName().equalsIgnoreCase(transaction.getDocumentType())){

                }
                else if (DocumentType.TRANSCRIPT_REQUEST.getDocumentName().equalsIgnoreCase(transaction.getDocumentType())){
                    ack = handleTranscriptRequest(transaction,ackDocID);
                }
                else if (DocumentType.TRANSCRIPT_ACKNOWLEDGEMENT.getDocumentName().equalsIgnoreCase(transaction.getDocumentType())) {

                    org.pesc.sdk.message.transcriptacknowledgement.v1_3.Acknowledgment transcriptAck =
                            transcriptAcknowledgementService.getTranscriptAcknowledgement(transaction.getFilePath());

                    String requestTrackingID = transcriptAck.getTransmissionData().getRequestTrackingID();

                    Preconditions.checkArgument(org.apache.commons.lang3.StringUtils.isNotBlank(requestTrackingID) && org.apache.commons.lang3.StringUtils.isNumeric(requestTrackingID), requestTrackingID+" invalid, must be a nonBlank Integer");
                    Transaction transcriptTransaction = transactionService.findById(Integer.parseInt(requestTrackingID));
                    Preconditions.checkNotNull(transcriptTransaction, "Cannot find transcript for ID:" + requestTrackingID);
                    Preconditions.checkArgument(org.apache.commons.lang3.StringUtils.isNotBlank(transcriptTransaction.getFilePath()), "filePath is missing for transcriptTransaction for ID:"+requestTrackingID);
                    CollegeTranscript collegeTranscript = getCollegeTranscript(transcriptTransaction.getFilePath());

                    transcriptAcknowledgementService.verifyTranscript(transcriptAck, collegeTranscript);
                }
            }


            //If the functional acknowledgement is null here, we need to construct one based on information in
            //the transaction entity.
            if (ack == null) {

                if (!StringUtils.isEmpty(transaction.getRequestFilePath()) ) {
                    TranscriptRequest transcriptRequest = getTranscriptRequest(transaction.getRequestFilePath());

                    ack = buildAcceptedAcknowledgement(transcriptRequest, ackDocID) ;
                }
                else {
                    ack = createFunctionalAcknowledgement(transaction, ackDocID);
                }


            }

            sendAck(transaction.getAckURL(), ack);

        }
        catch (Exception e){
            transaction.setAcknowledged(false);
            transaction.setError(e.getLocalizedMessage());
            //transaction.setStatus(TransactionStatus.FAILURE);
            transactionService.update(transaction);
            log.error(e);
        }



    }


    public Acknowledgment createFunctionalAcknowledgement(Transaction transaction, String ackDocID) {

        SourceDestinationType source = academicRecordObjectFactory.createSourceDestinationType();
        OrganizationType org = academicRecordObjectFactory.createOrganizationType();
        org.setMutuallyDefined(String.valueOf(transaction.getRecipientId()));
        source.setOrganization(org);

        SourceDestinationType destination = academicRecordObjectFactory.createSourceDestinationType();
        org = academicRecordObjectFactory.createOrganizationType();
        org.setMutuallyDefined(String.valueOf(transaction.getSenderId()));
        destination.setOrganization(org);

        Acknowledgment ack = null;

        if (StringUtils.isEmpty(transaction.getError())) {
            ack = buildAcceptedAcknowledgement(source,
                    destination,
                    String.valueOf(transaction.getSenderTransactionId()),
                    String.valueOf(transaction.getSenderTransactionId()),
                    ackDocID);
        }
        else {

            ack = buildRejectedAcknowledgement(source,
                    destination,
                    String.valueOf(transaction.getSenderTransactionId()),
                    String.valueOf(transaction.getSenderTransactionId()),
                    ackDocID,
                    transaction.getError());
        }


        return ack;

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
        Acknowledgment ack = buildBaseAcknowledgement(source, destination, requestTrackingID, documentID, AcknowledgmentCodeType.ACCEPTED, ackDocID);
        return ack;
    }
    public Acknowledgment buildRejectedAcknowledgement(SourceDestinationType source, SourceDestinationType destination,
                                                       String requestTrackingID, String documentID, String ackDocID,  String noteMessage) {

        Acknowledgment ack = buildBaseAcknowledgement(source, destination, requestTrackingID, documentID,AcknowledgmentCodeType.REJECTED,ackDocID);


        ack.getAcknowledgmentData().getNoteMessages().addAll(StringUtils.splitEqually(noteMessage, 80));
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
                transcript.getTransmissionData().getDocumentID(), ackDocID);

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


    private void addLocator(SyntaxErrorType error,  BigInteger lineNumber,BigInteger columnNumber) {
        SyntaxErrorLocatorType locator = functionalacknowledgementObjectFactory.createSyntaxErrorLocatorType();
        locator.setLineNumber(lineNumber);
        locator.setColumnNumber(columnNumber);

        error.setLocator(locator);
    }

    private SyntaxErrorType createSyntaxError(String message, SeverityCodeType severityCode ) {
        SyntaxErrorType error = functionalacknowledgementObjectFactory.createSyntaxErrorType();
        error.setErrorMessage(message);
        error.setSeverityCode(severityCode);

        return error;
    }

    private SyntaxErrorType createSyntaxError(String message, SeverityCodeType severityCode, BigInteger lineNumber,BigInteger columnNumber ) {
        SyntaxErrorType error = functionalacknowledgementObjectFactory.createSyntaxErrorType();
        error.setErrorMessage(message);
        error.setSeverityCode(severityCode);
        addLocator(error, lineNumber, columnNumber);
        return error;
    }



    private TranscriptRequest getTranscriptRequest(String filePath) throws JAXBException, SAXException, OperationNotSupportedException {

        Unmarshaller u = ValidationUtils.createUnmarshaller("org.pesc.sdk.message.transcriptrequest.v1_4.impl");
        Schema schema = ValidationUtils.getSchema(XmlFileType.TRANSCRIPT_REQUEST, XmlSchemaVersion.V1_4_0);
        u.setSchema(schema);
        return (TranscriptRequest) u.unmarshal(new File(filePath));
    }

    private CollegeTranscript getCollegeTranscript(String filePath) throws JAXBException, SAXException, OperationNotSupportedException {

        Unmarshaller u = ValidationUtils.createUnmarshaller("org.pesc.sdk.message.collegetranscript.v1_6.impl");
        Schema schema = ValidationUtils.getSchema(XmlFileType.COLLEGE_TRANSCRIPT, XmlSchemaVersion.V1_6_0);
        u.setSchema(schema);
        return (CollegeTranscript) u.unmarshal(new File(filePath));
    }


    @PreAuthorize("hasRole('ROLE_ORG_ADMIN')")
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

    @PreAuthorize("hasRole('ROLE_ORG_ADMIN')")
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
