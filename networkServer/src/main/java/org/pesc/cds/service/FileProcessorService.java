package org.pesc.cds.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.pesc.cds.domain.Transaction;
import org.pesc.cds.model.TransactionStatus;
import org.pesc.cds.repository.StringUtils;
import org.pesc.message.academicrecordbatch.v2_1.AcademicRecordBatch;
import org.pesc.sdk.message.collegetranscript.v1_6.CollegeTranscript;
import org.pesc.sdk.message.documentinfo.v1_0.DocumentInfo;
import org.pesc.sdk.message.functionalacknowledgment.v1_2.Acknowledgment;
import org.pesc.sdk.message.functionalacknowledgment.v1_2.AcknowledgmentDataType;
import org.pesc.sdk.message.transcriptrequest.v1_4.TranscriptRequest;
import org.pesc.sdk.sector.academicrecord.v1_9.TransmissionDataType;
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
import org.w3c.dom.Node;
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

    private static final org.pesc.sdk.message.functionalacknowledgment.v1_2.ObjectFactory functionalAcknowledgmentObjectFactory = new org.pesc.sdk.message.functionalacknowledgment.v1_2.ObjectFactory();
    private static final org.pesc.sdk.sector.academicrecord.v1_9.ObjectFactory academicRecordObjectFactory = new org.pesc.sdk.sector.academicrecord.v1_9.ObjectFactory();

    public static final String DEFAULT_DELIVERY_MESSAGE = "Successfully delivered document.";
    public static final String DEFAULT_RECEIVE_MESSAGE = "Received document.";

    public void sendPESCFunctionalAcknowledgement(Acknowledgment acknowledgment) {


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
        Acknowledgment functionalAcknowledgment = null;
        String message = DEFAULT_DELIVERY_MESSAGE;
        TransactionStatus status = TransactionStatus.SUCCESS;

        try {
            if (transaction.getFileFormat().equalsIgnoreCase("PESCXML")){

            }
            else if (!StringUtils.isEmpty(transaction.getRequestFilePath()) ) {
                TranscriptRequest transcriptRequest = getTranscriptRequest(transaction.getRequestFilePath());

                //functionalAcknowledgment = buildAcknowledgement(transcriptRequest) ;
            }



        }
        catch (Exception e){
            log.error(e);
            message = e.getMessage();
            status = TransactionStatus.FAILURE;
        }


        if (functionalAcknowledgment == null) {
            sendAck(transaction.getAckURL(), transaction.getSenderTransactionId(), message, status);
        }
        else {
            sendPESCFunctionalAcknowledgement(functionalAcknowledgment);
        }

    }

    public Acknowledgment buildAcknowledgement(TranscriptRequest transcriptRequest) {
        Acknowledgment ack = functionalAcknowledgmentObjectFactory.createAcknowledgment();

        TransmissionDataType transmissionData = academicRecordObjectFactory.createTransmissionDataType();

        transmissionData.setSource(transcriptRequest.getTransmissionData().getDestination());
        transmissionData.setDestination(transcriptRequest.getTransmissionData().getSource());


        AcknowledgmentDataType ackData = functionalAcknowledgmentObjectFactory.createAcknowledgmentDataType();
        ackData.setDocumentID(transcriptRequest.getTransmissionData().getDocumentID());

        //TODO: set remaining ack data elements here.

        ack.setTransmissionData(transmissionData);
        ack.setAcknowledgmentData(ackData);

        ack.getTransmissionData();

        return ack;
    }

    public Acknowledgment buildAcknowledgement(CollegeTranscript transcript) {
        Acknowledgment ack = functionalAcknowledgmentObjectFactory.createAcknowledgment();

        TransmissionDataType transmissionData = academicRecordObjectFactory.createTransmissionDataType();

        transmissionData.setSource(transcript.getTransmissionData().getDestination());
        transmissionData.setDestination(transcript.getTransmissionData().getSource());


        AcknowledgmentDataType ackData = functionalAcknowledgmentObjectFactory.createAcknowledgmentDataType();
        ackData.setDocumentID(transcript.getTransmissionData().getDocumentID());


        //TODO: set remaining ack data elements here.

        ack.setTransmissionData(transmissionData);
        ack.setAcknowledgmentData(ackData);

        ack.getTransmissionData();

        return ack;
    }

    public Acknowledgment buildAcknowledgement(AcademicRecordBatch batch) {
        Acknowledgment ack = functionalAcknowledgmentObjectFactory.createAcknowledgment();

        TransmissionDataType transmissionData = academicRecordObjectFactory.createTransmissionDataType();

        transmissionData.setSource(batch.getBatchEnvelope().getDestinationAgency());
        transmissionData.setDestination(batch.getBatchEnvelope().getSourceAgency());


        AcknowledgmentDataType ackData = functionalAcknowledgmentObjectFactory.createAcknowledgmentDataType();
        ackData.setBatchID(batch.getBatchEnvelope().getBatchID());


        //TODO: set remaining ack data elements here.

        ack.setTransmissionData(transmissionData);
        ack.setAcknowledgmentData(ackData);

        ack.getTransmissionData();

        return ack;
    }



    private DocumentInfo getDocumentInfo(TranscriptRequest transcriptRequest) throws JAXBException, SAXException {

        JAXBContext documentInfoContext = JAXBContext.newInstance("org.pesc.sdk.message.documentinfo.v1_0.impl");
        Unmarshaller documentInfoUnmarshaller = documentInfoContext.createUnmarshaller();
        SchemaFactory documentinfochemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        URL documentinfochemaUrl = getClass().getClassLoader().getResource("xsd/pesc/DocumentInfo_v1.0.0.xsd");
        Schema documentInfoXsdSchema = documentinfochemaFactory.newSchema(documentinfochemaUrl);
        documentInfoUnmarshaller.setSchema(documentInfoXsdSchema);
        return (DocumentInfo) documentInfoUnmarshaller.unmarshal((Node) transcriptRequest.getTransmissionData().getUserDefinedExtensions().getAny());

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
