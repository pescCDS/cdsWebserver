package org.pesc.cds.web;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import liquibase.util.file.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.pesc.cds.domain.Transaction;
import org.pesc.cds.model.SchoolCodeType;
import org.pesc.cds.model.TransactionStatus;
import org.pesc.cds.model.TranscriptRequestBuilder;
import org.pesc.cds.repository.TransactionService;
import org.pesc.cds.service.FileProcessorService;
import org.pesc.cds.service.OrganizationService;
import org.pesc.cds.service.PKIService;
import org.pesc.sdk.core.coremain.v1_12.DocumentTypeCodeType;
import org.pesc.sdk.core.coremain.v1_12.StateProvinceCodeType;
import org.pesc.sdk.core.coremain.v1_12.TransmissionTypeType;
import org.pesc.sdk.message.documentinfo.v1_0.DocumentTypeCode;
import org.pesc.sdk.message.transcriptrequest.v1_2.TranscriptRequest;
import org.pesc.sdk.sector.academicrecord.v1_7.PhoneType;
import org.pesc.sdk.sector.academicrecord.v1_7.ReleaseAuthorizedMethodType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.oxm.Marshaller;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.security.PublicKey;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

;

@RestController
@RequestMapping(value="api/v1/documents")
public class DocumentController {

    private static final Log log = LogFactory.getLog(DocumentController.class);

    @Value("${directory.server.base.url}")
    private String directoryServer;

    @Value("${networkServer.id}")
    private String localServerId;

    @Value("${networkServer.name}")
    private String localServerName;

    @Value("${networkServer.subcode}")
    private String localServerSubcode;

    @Value("${networkServer.ein}")
    private String localServerEIN;

    @Value("${networkServer.webServiceURL}")
    private String localServerWebServiceURL;

    @Value("${networkServer.outbox.path}")
    private String localServerOutboxPath;

    @Value("${networkServer.inbox.path}")
    private String localServerInboxPath;

    @Value("${api.organization}")
    private String organizationApiPath;


    @Value("${api.endpoints}")
    private String endpointsApiPath;


    @Value("${api.public_key}")
    private String publicKeyApiPath;

    @Value("${authentication.oauth.accessTokenUri}")
    private String accessTokenUri;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    PKIService pkiService;

    @Autowired
    FileProcessorService fileProcessorService;

    @Resource(name="transcriptRequestMarshaller")
    private Marshaller transcriptRequestMarshaller;

    @Resource(name="documentInfoMarshaller")
    private Marshaller DocumentInfoMarshaller;

    @Autowired
    private OrganizationService organizationService;


    @Autowired
    @Qualifier("myRestTemplate")
    private OAuth2RestOperations restTemplate;

    @RequestMapping(value="/send", method= RequestMethod.GET)
    @ResponseBody
    @Produces(MediaType.APPLICATION_JSON)
    public Transaction sendDocument(@RequestParam("transaction_id")Integer tranID) {

        Transaction tx = transactionService.findById(tranID);

        Transaction tran = new Transaction();
        tran.setOperation(tx.getOperation());
        tran.setFileFormat(tx.getFileFormat());
        tran.setDepartment(tx.getDepartment());
        tran.setDocumentType(tx.getDocumentType());
        tran.setFilePath(tx.getFilePath());
        tran.setRecipientId(tx.getRecipientId());
        tran.setSenderId(tx.getSenderId());
        tran.setFileSize(tx.getFileSize());
        tran.setOccurredAt(new Timestamp(Calendar.getInstance().getTimeInMillis()));


        tran = transactionService.create(tran);

        String endpointURI = getEndpointForOrg(tran.getRecipientId(), tran.getFileFormat(), tran.getDocumentType(), tran.getDepartment());

        if (endpointURI == null) {
            tran.setError("No endpoint found for this organization and document type.");
            transactionService.update(tran);
            throw new RuntimeException("No endpoint found for this organization and document type.");
        }
        // send http post to network server
        CloseableHttpClient client = fileProcessorService.makeHttpClient();
        try {

            HttpPost post = new HttpPost(endpointURI);

            File outboxFile = new File(tran.getFilePath());
            byte[] fileSignature = pkiService.createDigitalSignature(new FileInputStream(tran.getFilePath()), pkiService.getSigningKeys().getPrivate());

            ContentBody signature = new ByteArrayBody(fileSignature, "signature.dat");

            HttpEntity reqEntity = MultipartEntityBuilder.create()
                    .addPart("recipient_id", new StringBody(tran.getRecipientId().toString()))
                    .addPart("sender_id", new StringBody(tran.getSenderId().toString()))
                    .addPart("signer_id", new StringBody(localServerId))
                    .addPart("file_format", new StringBody(tran.getFileFormat()))
                    .addPart("document_type", new StringBody(tran.getDocumentType()))
                    .addPart("department", new StringBody(tran.getDepartment()))
                    .addPart("transaction_id", new StringBody(tran.getId().toString()))
                    .addPart("ack_url", new StringBody(localServerWebServiceURL))
                    .addPart("file", new FileBody(outboxFile))
                    .addPart("signature", signature)
                    .build();
            post.setEntity(reqEntity);

            CloseableHttpResponse response = client.execute(post);

            try {
                log.debug(response.getStatusLine());
                if (response.getStatusLine().getStatusCode() != 200)  {
                    throw new RuntimeException(response.getStatusLine().toString());
                }
                else {
                    HttpEntity resEntity = response.getEntity();
                    if (resEntity != null) {
                        log.debug("Response content length: " + resEntity.getContentLength());
                    }
                    EntityUtils.consume(resEntity);

                    return tran;
                }


            }
            finally {
                response.close();
            }

        } catch (Exception e) {
            tran.setError(e.getMessage());
            transactionService.update(tran);
            log.error(e);
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                log.error(e);
            }
        }

        return tran;
    }

    private String getEndpointForOrg(int orgID, String documentFormat, String documentType, String department) {
        StringBuilder uri = new StringBuilder(directoryServer + endpointsApiPath);
        uri.append("?organizationId=").append(orgID).append("&documentFormat=").append(documentFormat).append("&documentType=").append(documentType).append("&enabled=true").append("&mode=LIVE")
                .append("&department=").append(department);

        CloseableHttpClient client = HttpClients.custom().build();
        String endpointURI = null;
        try {
            HttpGet get = new HttpGet(uri.toString());
            get.setHeader(HttpHeaders.ACCEPT, "application/json");
            CloseableHttpResponse response = client.execute(get);
            try {

                HttpEntity resEntity = response.getEntity();
                if (response.getStatusLine().getStatusCode() == 200 && resEntity != null) {
                    JSONArray endpoints = new JSONArray(EntityUtils.toString(resEntity));
                    if (endpoints.length() > 0) {

                        if (endpoints.length() != 1) {
                            throw new RuntimeException("More than one endpoint was found that fits the given criteria.");
                        }
                        endpointURI = endpoints.getJSONObject(0).getString("address");
                        log.debug(endpoints.toString(3));
                    }
                }
                EntityUtils.consume(resEntity);
            }
            finally {
                response.close();
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                client.close();
            }
            catch (IOException e) {

            }
        }
        return endpointURI;
    }

    private String getPEMPublicKeyByOrgID(int orgID) {
        StringBuilder uri = new StringBuilder(directoryServer + "/services/rest/v1/organizations/" + orgID + "/public-key");

        CloseableHttpClient client = HttpClients.custom().build();
        String pemPublicKey = null;
        try {
            HttpGet get = new HttpGet(uri.toString());
            get.setHeader(HttpHeaders.ACCEPT, "text/html");
            CloseableHttpResponse response = client.execute(get);
            try {

                HttpEntity resEntity = response.getEntity();
                if (response.getStatusLine().getStatusCode() == 200 && resEntity != null) {
                    pemPublicKey = EntityUtils.toString(resEntity);
                }
                EntityUtils.consume(resEntity);
            }
            finally {
                response.close();
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                client.close();
            }
            catch (IOException e) {

            }
        }
        return pemPublicKey;
    }


    private String getEndpointURIForSchool(String destinationSchoolCode, String destinationSchoolCodeType, String documentFormat, String documentType, String department, Transaction tx, List<String> destinationOrganizationNames) {

        int orgID = getOrganizationId(destinationSchoolCode, destinationSchoolCodeType, destinationOrganizationNames);
        tx.setRecipientId(orgID);
        return getEndpointForOrg(orgID, documentFormat, documentType, department);
    }

    private int getOrganizationId(String destinationSchoolCode, String destinationSchoolCodeType, List<String> destinationOrganizationNames) {
        int orgID = 0;
        JSONObject organization = organizationService.getOrganization(destinationSchoolCode, destinationSchoolCodeType);
        if(organization!=null){
            orgID = organization.getInt("id");
            destinationOrganizationNames.add(organization.getString("name"));
            log.debug("Getting endpoint for org");
        }
        return orgID;
    }


    /**
     *
     * @param multipartFile
     * @param fileFormat
     * @param documentType
     * @param department
     * @param sourceSchoolCode
     * @param sourceSchoolCodeType
     * @param destinationSchoolCode
     * @param destinationSchoolCodeType
     * @param trStudentRelease
     * @param trStudentReleasedMethod
     * @param studentBirthDate
     * @param trStudentFirstName
     * @param trStudentMiddleName
     * @param trStudentLastName
     * @param trStudentEmail
     * @param trStudentPartialSsn
     * @param trStudentCurrentlyEnrolled
     * @return
     */
    @RequestMapping(value="/outbox", method= RequestMethod.POST)
    @ResponseBody
    public Transaction sendFile(
            @RequestParam(value="file") MultipartFile multipartFile,
            @RequestParam(value="file_format", required=true) String fileFormat,
            @RequestParam(value="document_type", required=false) String documentType,
            @RequestParam(value="department", required=false) String department,
            @RequestParam(value="source_school_code", required=false) String sourceSchoolCode,
            @RequestParam(value="source_school_code_type", required=false) String sourceSchoolCodeType,
            @RequestParam(value="destination_school_code", required=true) String destinationSchoolCode,
            @RequestParam(value="destination_school_code_type", required=true) String destinationSchoolCodeType,
            @RequestParam(value="student_release", required=false) Boolean trStudentRelease,
            @RequestParam(value="student_released_method", required=false) String trStudentReleasedMethod,
            @RequestParam(value="student_birth_date", required=false) String studentBirthDate,
            @RequestParam(value="student_first_name", required=false) String trStudentFirstName,
            @RequestParam(value="student_middle_name", required=false) String trStudentMiddleName,
            @RequestParam(value="student_last_name", required=false) String trStudentLastName,
            @RequestParam(value="student_email", required=false) String trStudentEmail,
            @RequestParam(value="student_partial_ssn", required=false) String trStudentPartialSsn,
            @RequestParam(value="student_currently_enrolled", required=false) Boolean trStudentCurrentlyEnrolled
    ) {

        Transaction tx = new Transaction();

        if (!multipartFile.isEmpty()) {

            int recordHolderDirectoryID = Integer.valueOf(localServerId);

            if (sourceSchoolCode != null && sourceSchoolCodeType != null) {
                JSONObject recordHolder = organizationService.getOrganization(sourceSchoolCode, sourceSchoolCodeType);

                if (recordHolder == null) {
                    throw new IllegalArgumentException(String.format("No organization exists with %s code %s.", sourceSchoolCodeType, sourceSchoolCode));
                }

                recordHolderDirectoryID = recordHolder.getInt("id");
            }



            List<String> trDestinationOrganizationNames = Lists.newArrayList();//Provided by Source Institution
            String endpointURI = getEndpointURIForSchool(destinationSchoolCode, destinationSchoolCodeType, fileFormat, documentType, department, tx, trDestinationOrganizationNames);

            if (endpointURI == null) {
                throw new IllegalArgumentException("No endpoint URI exists for the given school and document format.");
            }

            try {
                File outboxDirectory = new File(localServerOutboxPath);
                outboxDirectory.mkdirs();
                UUID uuid = UUID.randomUUID();
                String ext = FilenameUtils.getExtension(multipartFile.getOriginalFilename());
                String trFileName = uuid.toString()+"_document."+ext;
                File outboxFile = new File(outboxDirectory, trFileName);
                multipartFile.transferTo(outboxFile);
                File requestFile = null;
                tx.setSenderId(recordHolderDirectoryID);
                tx.setSignerId(Integer.valueOf(localServerId));
                tx.setFileFormat(fileFormat);
                tx.setFilePath(outboxFile.getAbsolutePath());
                tx.setFileSize(multipartFile.getSize());
                tx.setDocumentType(documentType);
                tx.setDepartment(department);
                tx.setAckURL(localServerWebServiceURL);
                tx.setOperation("SEND");
                tx.setOccurredAt(new Timestamp(Calendar.getInstance().getTimeInMillis()));

                tx = transactionService.create(tx);


                log.debug(String.format(
                        "saved Transaction: {%n  recipientId: %s,%n  senderId: %s,%n  fileFormat: %s%n}",
                        tx.getRecipientId(),
                        tx.getSenderId(),
                        tx.getFileFormat()
                ));

                //transcript request
                boolean createTranscriptRequest = !"PESCXML".equals(fileFormat);
                if(createTranscriptRequest) {
                    String requestFileName = uuid.toString() + "_request.xml";
                    org.pesc.sdk.sector.academicrecord.v1_7.ObjectFactory academicRecordObjectFactory = new org.pesc.sdk.sector.academicrecord.v1_7.ObjectFactory();
                    String trDocumentID = tx.getId() + "";
                    DocumentTypeCodeType trDocumentTypeCode = DocumentTypeCodeType.STUDENT_REQUEST;
                    TransmissionTypeType trTransmissionType = TransmissionTypeType.ORIGINAL;
                    String trRequestTrackingID = tx.getId() + "";
                    //source
                    Map<SchoolCodeType, String> trSourceSchoolCodes = Maps.newHashMap();
                    Map<SchoolCodeType, String> trStudentSchoolCodes = Maps.newHashMap();
                    JSONObject organization = organizationService.getOrganization(Integer.valueOf(localServerId));
                    boolean institution = organizationService.isInstitution(organization);
                    if(!institution) {
                        Preconditions.checkArgument(StringUtils.isNotBlank(sourceSchoolCode), "Source School Code is required");
                        Preconditions.checkArgument(StringUtils.isNotBlank(sourceSchoolCodeType), "Source School Code Type is required");
                        SchoolCodeType srcSchoolCodeType = SchoolCodeType.valueOf(sourceSchoolCodeType);
                        trSourceSchoolCodes.put(srcSchoolCodeType, sourceSchoolCode);
                        trStudentSchoolCodes.put(srcSchoolCodeType, sourceSchoolCode);
                        //lookup Sending Institution by schoolCode and schoolCodeType
                        organization = organizationService.getOrganization(sourceSchoolCode, sourceSchoolCodeType);
                    }else{
                        JSONArray schoolCodes = organization.getJSONArray("schoolCodes");
                        for(int i =0; i<schoolCodes.length(); i++){
                            JSONObject schoolCode = schoolCodes.getJSONObject(i);
                            trSourceSchoolCodes.put(SchoolCodeType.valueOf(schoolCode.getString("codeType")), schoolCode.getString("code"));
                            trStudentSchoolCodes.put(SchoolCodeType.valueOf(schoolCode.getString("codeType")), schoolCode.getString("code"));
                        }
                    }
                    List<String> trSourceOrganizationNames = Arrays.asList(organization.getString("name"));
                    List<String> trSourceOrganizationAddressLines = Arrays.asList(organization.getString("street"));
                    String trSourceOrganizationCity = organization.getString("city");
                    StateProvinceCodeType trSourceOrganizationStateProvinceCode = StateProvinceCodeType.valueOf(organization.getString("state"));
                    String trSourceOrganizationPostalCode = organization.getString("zip");
                    String phoneNumber = organization.optString("telephone", "");
                    PhoneType trSendersPhone = academicRecordObjectFactory.createPhoneType();//Provided by Source Institution - optional
                    if(StringUtils.isNotBlank(phoneNumber)) {
                        int extensionIndex = org.apache.commons.lang3.StringUtils.indexOfIgnoreCase(phoneNumber, "x");//has extension?
                        if(extensionIndex!=-1) {
                            if((phoneNumber.length()-1)>extensionIndex) {
                                String extension = phoneNumber.substring(extensionIndex + 1);
                                trSendersPhone.setPhoneNumberExtension(extension);
                            }
                            phoneNumber = phoneNumber.substring(0, extensionIndex - 1);
                        }
                        phoneNumber = phoneNumber.replaceAll("\\D", "");
                        if(phoneNumber.length()>7){
                            String basePhoneNumber = phoneNumber.substring(phoneNumber.length()-7);
                            trSendersPhone.setPhoneNumber(basePhoneNumber);
                            String areaCode = phoneNumber.length()>10?phoneNumber.substring(phoneNumber.length()-10, phoneNumber.length()-7):phoneNumber.substring(0, phoneNumber.length()-7);
                            trSendersPhone.setAreaCityCode(areaCode);
                            if(phoneNumber.length()>10){
                                String countryCode = phoneNumber.substring(0, phoneNumber.length()-10);
                                trSendersPhone.setCountryPrefixCode(countryCode);
                            }
                        }
                    }
                    String trSendersEmail = null;
                    JSONArray contacts = organization.getJSONArray("contacts");
                    if(contacts.length()>0){
                        JSONObject contact = contacts.getJSONObject(0);
                        trSendersEmail = contact.getString("email");//optional
                    }
                    //destination
                    Map<SchoolCodeType, String> trDestinationSchoolCodes = Maps.newHashMap();
                    trDestinationSchoolCodes.put(SchoolCodeType.valueOf(destinationSchoolCodeType), destinationSchoolCode);
                    //document
                    DocumentTypeCode trDocumentType = null;
                    try{
                        trDocumentType = DocumentTypeCode.fromValue(documentType);
                    }catch (IllegalArgumentException e){
                        trDocumentType = DocumentTypeCode.OTHER;
                    }
                    //student
                    DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);
                    Date trStudentDOB = null;
                    try {
                        if (studentBirthDate != null) {
                            trStudentDOB = dateFormat.parse(studentBirthDate);
                        }
                    } catch (Exception e) {
                    }
                    String trStudentSchoolName = trSourceOrganizationNames.get(0);//Provided by Source Institution
                    TranscriptRequest transcriptRequest = new TranscriptRequestBuilder()
                            .DocumentInfoMarshaller(DocumentInfoMarshaller)
                            .documentID(trDocumentID)
                            .documentTypeCode(trDocumentTypeCode)
                            .transmissionType(trTransmissionType)
                            .requestTrackingID(trRequestTrackingID)
                            .sourceSchoolCodes(trSourceSchoolCodes)
                            .sourceOrganizationNames(trSourceOrganizationNames)
                            .sourceOrganizationAddressLines(trSourceOrganizationAddressLines)
                            .sourceOrganizationCity(trSourceOrganizationCity)
                            .sourceOrganizationStateProvinceCode(trSourceOrganizationStateProvinceCode)
                            .sourceOrganizationPostalCode(trSourceOrganizationPostalCode)
                            .sendersPhone(trSendersPhone)
                            .sendersEmail(trSendersEmail)
                            .destinationSchoolCodes(trDestinationSchoolCodes)
                            .destinationOrganizationNames(trDestinationOrganizationNames)
                            .parchmentDocumentTypeCode(trDocumentType)
                            .fileName(trFileName)
                            .studentRelease(trStudentRelease)
                            .studentReleasedMethod(ReleaseAuthorizedMethodType.valueOf(trStudentReleasedMethod))
                            .studentBirthDate(trStudentDOB)
                            .studentFirstName(trStudentFirstName)
                            .studentLastName(trStudentLastName)
                            .studentSchoolName(trStudentSchoolName)
                            .studentSchoolCodes(trStudentSchoolCodes)
                            .studentMiddleNames(Arrays.asList(trStudentMiddleName))
                            .studentEmail(trStudentEmail)
                            .studentPartialSsn(trStudentPartialSsn)
                            .studentCurrentlyEnrolled(trStudentCurrentlyEnrolled)
                            .build();
                    requestFile = new File(outboxDirectory, requestFileName);
                    tx.setRequestFilePath(requestFile.getAbsolutePath());
                    if(!requestFile.createNewFile()){
                        String message = tx.getError()!=null?tx.getError():"";
                        tx.setError(message + ". " + String.format("file %s already exists", requestFileName));
                    }else {
                        transcriptRequestMarshaller.marshal(transcriptRequest, new StreamResult(requestFile));
                    }
                }
                //transcript request

                byte[] fileSignature = pkiService.createDigitalSignature(new FileInputStream(outboxFile), pkiService.getSigningKeys().getPrivate());


                LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
                map.add("recipient_id", tx.getRecipientId());
                map.add("sender_id", tx.getSenderId());
                map.add("signer_id", localServerId);
                map.add("file_format", fileFormat);
                map.add("document_type", documentType);
                map.add("department", department);
                map.add("transaction_id", tx.getId());
                map.add("ack_url", localServerWebServiceURL);
                map.add("file", new FileSystemResource(outboxFile));
                map.add("signature", new ByteArrayResource(fileSignature){
                    @Override
                    public String getFilename(){
                        return "signature.dat";
                    }
                });
                if(createTranscriptRequest && requestFile!=null){
                    map.add("transcript_request_file", new FileSystemResource(requestFile));
                }


                org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
                headers.setContentType(org.springframework.http.MediaType.MULTIPART_FORM_DATA);


                ResponseEntity<String> response = restTemplate.exchange
                        (endpointURI, HttpMethod.POST, new org.springframework.http.HttpEntity<Object>(map, headers), String.class);

                log.info(response.getStatusCode().getReasonPhrase());


            } catch(ResourceAccessException e) {

                //Force the OAuth client to retrieve the token again whenever it is used again.

                restTemplate.getOAuth2ClientContext().setAccessToken(null);

                tx.setError(e.getMessage());
                transactionService.update(tx);

                log.error(e);
                throw new IllegalArgumentException(e);

            } catch (Exception e) {

                tx.setError(e.getMessage());
                transactionService.update(tx);

                log.error(e);

                throw new IllegalArgumentException(e);

            }

        } else {
            throw new IllegalArgumentException("No file was present in the upload.") ;
        }

        return tx;
    }


    /**
     * When another network server sends a file
     *
     * @param recipientId    In this case this is the network server that we need to send the response to
     * @param multipartFile           The transferred file
     * @param fileFormat     The expected format of the file
     * @param transactionId  This is the identifier of the transaction record from the sending network server, we send it back
     * @param ackURL  This is the url to the network server that we will send the response back to
     */
    @RequestMapping(value="/inbox", method= RequestMethod.POST)
    public void receiveFile(
            @RequestParam(value="recipient_id", required=false) Integer recipientId,
            @RequestParam(value="sender_id", required=false) Integer senderId,
            @RequestParam(value="signer_id", required=false) Integer signerId,
            @RequestParam(value="file") MultipartFile multipartFile,
            @RequestParam(value="signature") MultipartFile signatureFile,
            @RequestParam(value="file_format", required=false) String fileFormat,
            @RequestParam(value="document_type", required=false) String documentType,
            @RequestParam(value="department", required=false) String department,
            @RequestParam(value="transaction_id", required=false) Integer transactionId,
            @RequestParam(value="ack_url", required=false) String ackURL,
            @RequestParam(value="transcript_request_file", required = false) MultipartFile transcriptRequestFile,
            HttpServletRequest request
    ) {

        log.info(request.getRequestURI().toString());

        log.debug(String.format("received file from network server " + recipientId));


        if (multipartFile == null || signatureFile == null){
            log.error("Incorrect number of file uploaded.  Is the digital signature file present?");
            throw new WebApplicationException("A file and it's digital signature are required.");
        }
        Transaction tx = new Transaction();
        // we need the directoryId for this organization in the organizations table
        tx.setRecipientId(recipientId);
        tx.setSenderId(senderId);
        tx.setSignerId(signerId);
        tx.setSenderTransactionId(transactionId);
        tx.setFileFormat(fileFormat);
        tx.setFileSize(multipartFile.getSize());
        tx.setDepartment(department);
        tx.setDocumentType(documentType);
        tx.setOperation("RECEIVE");
        Timestamp occurredAt = new Timestamp(Calendar.getInstance().getTimeInMillis());
        tx.setOccurredAt(occurredAt);
        tx.setAcknowledgedAt(occurredAt);
        tx.setAcknowledged(true);
        tx.setStatus(TransactionStatus.FAILURE);

        if (!StringUtils.isEmpty(ackURL)){
            tx.setAckURL(ackURL);
        }

        Transaction savedTx = transactionService.create(tx);

        File inboxDirectory = new File(localServerInboxPath);
        inboxDirectory.mkdirs();


        try {
            String fileName = multipartFile.getOriginalFilename();
            File f =  new File(inboxDirectory, fileName);
            byte[] bytes = multipartFile.getBytes();

            String requestFileName = null;
            File requestFile = null;
            byte[] requestFileBytes = null;
            if(transcriptRequestFile!=null) {
                requestFileName = transcriptRequestFile.getOriginalFilename();
                requestFile = new File(inboxDirectory, requestFileName);
                requestFileBytes = transcriptRequestFile.getBytes();
            }

            String pemPublicKey = getPEMPublicKeyByOrgID(signerId);


            if (pemPublicKey == null) {
                throw new IllegalArgumentException("The sender's signing certificate was invalid or non existent.  File discarded.");
            }


            PublicKey senderPublicKey = pkiService.convertPEMPublicKey(pemPublicKey);


            if ( true == pkiService.verifySignature(signatureFile.getInputStream(),signatureFile.getBytes(), senderPublicKey)) {
                throw new WebApplicationException("Invalid digital signature found.  File discarded.");
            }


            File fp = f.getParentFile();
            if(!fp.exists() && !fp.mkdirs()) {
                tx.setError("Could not create directory: " + fp);
            } else {
                try {
                    if (!f.createNewFile()) {
                        tx.setError(String.format("file %s already exists", multipartFile.getOriginalFilename()));
                    }else {
                        tx.setFilePath(f.getPath());
                        BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(f));
                        stream.write(bytes);
                        stream.close();
                        if(requestFile!=null) {
                            if (!requestFile.createNewFile()) {
                                String message = tx.getError() != null ? tx.getError() : "";
                                tx.setError(message + ". " + String.format("file %s already exists", requestFileName));
                            }else{
                                tx.setRequestFilePath(requestFile.getPath());
                                BufferedOutputStream stream2 = new BufferedOutputStream(new FileOutputStream(requestFile));
                                stream2.write(requestFileBytes);
                                stream2.close();
                                tx.setStatus(TransactionStatus.SUCCESS);
                            }
                        }else {
                            tx.setStatus(TransactionStatus.SUCCESS);
                        }
                    }


                } catch(IOException ioex) {
                    tx.setMessage(ioex.getMessage());
                    tx.setError(ioex.getMessage());
                }
            }
        } catch(Exception ex) {
            log.error(ex);
            tx.setMessage(ex.getMessage() == null ? ex.getClass().getSimpleName() : ex.getMessage());
            tx.setError(ex.getMessage() == null ? ex.getClass().getSimpleName() : ex.getMessage());
        }

        transactionService.update(tx);

        if (tx.getStatus() == TransactionStatus.SUCCESS) {
            fileProcessorService.deliverFile(tx);
        }


    }

    /**
     * List Files endpoint<p>
     * TODO finish this
     *
     * @return <code>List&lt;String&gt;</code> A list of paths to files uploaded to the server.
     */
    @RequestMapping(value="/outbox", method= RequestMethod.GET)
    @Produces(MediaType.APPLICATION_JSON)
    @ResponseBody
    public List<String> listFilesFromOutbox() {
       return fileProcessorService.getOutboxDocumentList();
    }


    /**
     * List Files endpoint<p>
     * TODO finish this
     *
     * @return <code>List&lt;String&gt;</code> A list of paths to files uploaded to the server.
     */
    @RequestMapping(value="/inbox", method= RequestMethod.GET)
    @Produces(MediaType.APPLICATION_JSON)
    @ResponseBody
    public List<String> listFilesFromInbox() {
       return fileProcessorService.getInboxDocumentList();
    }
}
