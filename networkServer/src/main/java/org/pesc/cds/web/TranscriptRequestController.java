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

package org.pesc.cds.web;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.swagger.annotations.Api;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import org.pesc.cds.domain.Transaction;
import org.pesc.cds.model.*;
import org.pesc.cds.repository.TransactionService;
import org.pesc.cds.service.OrganizationService;
import org.pesc.cds.service.PKIService;
import org.pesc.cds.utils.ErrorUtils;
import org.pesc.sdk.core.coremain.v1_14.DocumentTypeCodeType;
import org.pesc.sdk.core.coremain.v1_14.StateProvinceCodeType;
import org.pesc.sdk.core.coremain.v1_14.TransmissionTypeType;
import org.pesc.sdk.message.documentinfo.v1_0.DocumentTypeCode;
import org.pesc.sdk.message.transcriptrequest.v1_4.TranscriptRequest;
import org.pesc.sdk.sector.academicrecord.v1_9.PhoneType;
import org.pesc.sdk.sector.academicrecord.v1_9.ReleaseAuthorizedMethodType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.oxm.Marshaller;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.ResourceAccessException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by james on 9/12/16.
 */
@RestController
@RequestMapping(value="api/v1/transcript-requests")
@Api(tags = "Transcript Requests", description = "Manage transcript requests.")
public class TranscriptRequestController {

    private static final Log log = LogFactory.getLog(TranscriptRequestController.class);

    @Autowired
    private PKIService pkiService;

    @Resource(name="transcriptRequestMarshaller")
    private Marshaller transcriptRequestMarshaller;

    @Resource(name="documentInfoMarshaller")
    private Marshaller documentInfoMarshaller;

    @Value("${networkServer.outbox.path}")
    private String localServerOutboxPath;

    @Value("${networkServer.id}")
    private String localServerId;

    @Value("${networkServer.webServiceURL}")
    private String localServerWebServiceURL;

    @Autowired
    private TransactionService transactionService;


    @Autowired
    private OrganizationService organizationService;

    @Autowired
    @Qualifier("myRestTemplate")
    private OAuth2RestTemplate restTemplate;

    private String getEmailAddress(JSONObject edexOrganization) {
        JSONArray contacts = edexOrganization.getJSONArray("contacts");
        if(contacts.length()>0){
            JSONObject contact = contacts.getJSONObject(0);
            return contact.getString("email");
        }

        return null;
    }
    /**
     * The source school indicates the school that is requesting the transcript.
     * @param builder
     * @param schoolDTO
     */
    private void constructTranscriptRequestSource(TranscriptRequestBuilder builder, RequestingSchoolDTO schoolDTO, JSONObject edexOrganization) {

        Map<SchoolCodeType, String> schoolCodes = Maps.newHashMap();

        SchoolCodeType srcSchoolCodeType = SchoolCodeType.valueOf(schoolDTO.getSchoolCodeType());
        schoolCodes.put(srcSchoolCodeType, schoolDTO.getSchoolCode());


        List<String> organizationNames = Arrays.asList(edexOrganization.getString("name"));
        List<String> addresses = Arrays.asList(edexOrganization.getString("street"));
        String city = edexOrganization.getString("city");
        StateProvinceCodeType stateProvinceCode = StateProvinceCodeType.valueOf(edexOrganization.getString("state"));
        String postalCode = edexOrganization.getString("zip");
        org.pesc.sdk.sector.academicrecord.v1_9.ObjectFactory academicRecordObjectFactory = new org.pesc.sdk.sector.academicrecord.v1_9.ObjectFactory();
        PhoneType phone = createPhone(edexOrganization.optString("telephone", ""));

        builder.sourceSchoolCodes(schoolCodes)
                .sendersEmail(getEmailAddress(edexOrganization))
                .sendersPhone(phone)
                .sourceOrganizationNames(organizationNames)
                .sourceOrganizationAddressLines(addresses)
                .sourceOrganizationCity(city)
                .sourceOrganizationStateProvinceCode(stateProvinceCode)
                .sourceOrganizationPostalCode(postalCode);


    }


    private PhoneType createPhone(String phoneNumber){
        org.pesc.sdk.sector.academicrecord.v1_9.ObjectFactory academicRecordObjectFactory = new org.pesc.sdk.sector.academicrecord.v1_9.ObjectFactory();
        PhoneType phone = academicRecordObjectFactory.createPhoneType();//Provided by Source Institution - optional
        if(StringUtils.isNotBlank(phoneNumber)) {
            int extensionIndex = StringUtils.indexOfIgnoreCase(phoneNumber, "x");//has extension?
            if(extensionIndex!=-1) {
                if((phoneNumber.length()-1)>extensionIndex) {
                    String extension = phoneNumber.substring(extensionIndex + 1);
                    phone.setPhoneNumberExtension(extension);
                }
                phoneNumber = phoneNumber.substring(0, extensionIndex - 1);
            }
            phoneNumber = phoneNumber.replaceAll("\\D", "");
            if(phoneNumber.length()>7){
                String basePhoneNumber = phoneNumber.substring(phoneNumber.length()-7);
                phone.setPhoneNumber(basePhoneNumber);
                String areaCode = phoneNumber.length()>10?phoneNumber.substring(phoneNumber.length()-10, phoneNumber.length()-7):phoneNumber.substring(0, phoneNumber.length()-7);
                phone.setAreaCityCode(areaCode);
                if(phoneNumber.length()>10){
                    String countryCode = phoneNumber.substring(0, phoneNumber.length()-10);
                    phone.setCountryPrefixCode(countryCode);
                }
            }
        }
        return phone;
    }

    private TranscriptRequestBuilder constructStudent(TranscriptRequestBuilder builder, TranscriptRequestDTO transcriptRequestDTO, RecordHolderDTO schoolDTO, JSONObject edexOrganization) {
        Map<SchoolCodeType, String> currentlyAttendedSchoolCodes = Maps.newHashMap();


        Boolean trStudentRelease = transcriptRequestDTO.isStudentRelease();
        String trStudentReleasedMethod = transcriptRequestDTO.getStudentReleasedMethod();
        String studentBirthDate = transcriptRequestDTO.getStudentBirthDate();
        String trStudentFirstName = transcriptRequestDTO.getStudentFirstName();
        String trStudentLastName = transcriptRequestDTO.getStudentLastName() ;
        String trStudentMiddleName = transcriptRequestDTO.getStudentMiddleName();
        String trStudentEmail = transcriptRequestDTO.getStudentEmail();
        String trStudentPartialSsn = transcriptRequestDTO.getStudentPartialSSN();

        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);
        Date trStudentDOB = null;
        try {
            if (studentBirthDate != null) {
                trStudentDOB = dateFormat.parse(studentBirthDate);
            }
        } catch (Exception e) {
            log.error(e);

        }

        SchoolCodeType srcSchoolCodeType = SchoolCodeType.valueOf(schoolDTO.getSchoolCodeType());
        if (schoolDTO.isStudentCurrentlyEnrolled() == true) {
            currentlyAttendedSchoolCodes.put(srcSchoolCodeType, schoolDTO.getSchoolCode());
        }

        builder.studentRelease(trStudentRelease)
                .studentReleasedMethod(ReleaseAuthorizedMethodType.valueOf(trStudentReleasedMethod))
                .studentBirthDate(trStudentDOB)
                .studentFirstName(trStudentFirstName)
                .studentLastName(trStudentLastName)
                .studentEmail(trStudentEmail)
                .studentPartialSsn(trStudentPartialSsn)
                .studentSchoolCodes(currentlyAttendedSchoolCodes)
                .studentSchoolName(edexOrganization.optString("name", ""))
                .studentCurrentlyEnrolled(schoolDTO.isStudentCurrentlyEnrolled());

        if (StringUtils.isNotEmpty(trStudentMiddleName)) {
            builder.studentMiddleNames(Arrays.asList(trStudentMiddleName));
        }

        return builder;
    }

    /**
     * The destination is the record holder's school.
     * @param builder
     * @param schoolDTO
     */
    private void constructTranscriptRequestDestination(TranscriptRequestBuilder builder, RecordHolderDTO schoolDTO, JSONObject edexOrganization) {

        Map<SchoolCodeType, String> schoolCodes = Maps.newHashMap();

        SchoolCodeType srcSchoolCodeType = SchoolCodeType.valueOf(schoolDTO.getSchoolCodeType());
        schoolCodes.put(srcSchoolCodeType, schoolDTO.getSchoolCode());


        List<String> organizationNames = Arrays.asList(edexOrganization.getString("name"));
        List<String> addresses = Arrays.asList(edexOrganization.getString("street"));
        String city = edexOrganization.getString("city");
        StateProvinceCodeType stateProvinceCode = StateProvinceCodeType.valueOf(edexOrganization.getString("state"));
        String postalCode = edexOrganization.getString("zip");
        PhoneType phone = createPhone(edexOrganization.optString("telephone", ""));

        builder.destinationSchoolCodes(schoolCodes)
                .receiversEmail(getEmailAddress(edexOrganization))
                .receiversPhone(phone)
                .destinationOrganizationNames(organizationNames)
                .destinationOrganizationAddressLines(addresses)
                .destinationCity(city)
                .destinationOrganizationStateProvinceCode(stateProvinceCode)
                .destinationOrganizationPostalCode(postalCode);

    }

    private JSONObject getEDExOrg(SchoolDTO schoolDTO){
        //get the EDExchange organization object from the directory server...
        Preconditions.checkArgument(StringUtils.isNotBlank(schoolDTO.getSchoolCode()), "School Code is required");
        Preconditions.checkArgument(StringUtils.isNotBlank(schoolDTO.getSchoolCodeType()), "School Code Type is required");

        if (schoolDTO.getSchoolCodeType().equals(SchoolCodeType.EDEXCHANGE.name())) {
            return organizationService.getOrganization(Integer.valueOf(schoolDTO.getSchoolCode()));
        }
        return organizationService.getOrganization(schoolDTO.getSchoolCode(), schoolDTO.getSchoolCodeType());

    }

    @RequestMapping(method= RequestMethod.POST)
    @ResponseBody
    public List<Transaction> transcriptRequest( @RequestBody TranscriptRequestDTO transcriptRequestDTO
    ) {

        Preconditions.checkArgument(transcriptRequestDTO.getDestinationInstitutions().size() == 1) ;
        Preconditions.checkArgument(transcriptRequestDTO.getSourceInstitutions().size() == 1);

        String fileFormat = DocumentFormat.PESCXML.getFormatName();
        String documentType = DocumentType.TRANSCRIPT_REQUEST.getDocumentName();
        String department = "Administration";

        List<Transaction> transactions = new ArrayList<Transaction>();

        //The receiving institution information in terms of the transcript.  For the transcript request,
        //this will be the opposite---it will be the sending/originating institution.
        RequestingSchoolDTO transcriptRequestor = transcriptRequestDTO.getDestinationInstitutions().get(0);
        RecordHolderDTO recordHolderInstitution = transcriptRequestDTO.getSourceInstitutions().get(0);

        List<String> transcriptRequestRecordHolderNames = Lists.newArrayList();//Provided by Source Institution

        //Create a transaction to track the transcript request.

        Transaction tx = new Transaction();

        //Get the endpoint for the record holder.  This is where the transcript request will be sent.
        String endpointURI = organizationService.getEndpointURIForSchool(recordHolderInstitution.getSchoolCode(), recordHolderInstitution.getSchoolCodeType(), fileFormat,
                    documentType, department, tx, transcriptRequestRecordHolderNames, transcriptRequestDTO.getMode());


        if (endpointURI == null) {
            String error = ErrorUtils.getNoEndpointFoundMessage(tx.getRecipientId(),
                    fileFormat,
                    documentType,
                    department);
            log.warn(error);
            throw new IllegalArgumentException(error);
        }

        JSONObject requestorOrg = getEDExOrg(transcriptRequestor);
        JSONObject recordHolderOrg = getEDExOrg(recordHolderInstitution);
        //Create
        File outboxDirectory = new File(localServerOutboxPath);
        outboxDirectory.mkdirs();
        UUID uuid = UUID.randomUUID();
        String ext = "xml";
        String trFileName = uuid.toString()+"_document."+ext;
        File requestFile = new File(outboxDirectory, trFileName);


        tx.setSenderId(requestorOrg.getInt("id"));
        tx.setRecipientId(recordHolderOrg.getInt("id"));
        tx.setFileFormat(fileFormat);
        tx.setFilePath(requestFile.getAbsolutePath());
        tx.setDocumentType(documentType);
        tx.setDepartment(department);
        tx.setAckURL(localServerWebServiceURL);
        tx.setSignerId(Integer.valueOf(localServerId));
        tx.setOperation("SEND");
        tx.setOccurredAt(new Timestamp(Calendar.getInstance().getTimeInMillis()));

        tx = transactionService.create(tx);
        String trDocumentID = tx.getId() + "";
        DocumentTypeCodeType trDocumentTypeCode = DocumentTypeCodeType.STUDENT_REQUEST;
        TransmissionTypeType trTransmissionType = TransmissionTypeType.ORIGINAL;
        String trRequestTrackingID = tx.getId() + "";


        //destination

        //trDestinationSchoolCodes.put(SchoolCodeType.MutuallyDefined, String.valueOf(tx.getSenderId()));
        //document
        DocumentTypeCode trDocumentType = DocumentTypeCode.TRANSCRIPT;

        //student

        TranscriptRequestBuilder builder = new TranscriptRequestBuilder()
                .documentInfoMarshaller(documentInfoMarshaller)
                .documentID(trDocumentID)
                .documentTypeCode(trDocumentTypeCode)
                .documentFormat(transcriptRequestDTO.getFileFormat())
                .transmissionType(trTransmissionType)
                .requestTrackingID(trRequestTrackingID)
                .parchmentDocumentTypeCode(trDocumentType)
                .fileName(trFileName);


        constructStudent(builder,transcriptRequestDTO, recordHolderInstitution,recordHolderOrg);

        constructTranscriptRequestDestination(builder, recordHolderInstitution, recordHolderOrg);

        constructTranscriptRequestSource(builder,transcriptRequestor, requestorOrg);

        TranscriptRequest transcriptRequest = builder.build();

        try {
            if(!requestFile.createNewFile()){
                String message = tx.getError()!=null?tx.getError():"";
                tx.setError(message + ". " + String.format("file %s already exists", trFileName));
            }else {
                //File gets saved to file system here.
                transcriptRequestMarshaller.marshal(transcriptRequest, new StreamResult(requestFile));
            }
        }
        catch (Exception e) {
            log.error("Failed to save transcript request document.", e);

            tx.setError(e.getLocalizedMessage());
            tx.setStatus(TransactionStatus.FAILURE);
        }

        tx.setFileSize(requestFile.length());

        transactionService.update(tx);

        transactions.add(tx);

        try {
            if (StringUtils.isEmpty(tx.getError())) {
                sendDocument(requestFile, endpointURI, tx, fileFormat,documentType, department);
            }
        } catch (Exception e) {

            tx.setError(e.getMessage());
            transactionService.update(tx);

            log.error("Failed to send transcript request.", e);

            throw new IllegalArgumentException(e.getMessage());
        }

        //Now send the transcript request document to the destination school




        return transactions;
    }


    private void sendDocument(File outboxFile, String endpointURI, Transaction tx, String fileFormat, String documentType, String department) throws IOException {

        byte[] fileSignature = pkiService.createDigitalSignature(new FileInputStream(outboxFile), pkiService.getSigningKeys().getPrivate());
        try {

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


            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.setContentType(org.springframework.http.MediaType.MULTIPART_FORM_DATA);


            ResponseEntity<String> response = restTemplate.exchange
                    (endpointURI, HttpMethod.POST, new org.springframework.http.HttpEntity<Object>(map, headers), String.class);


            if (response.getStatusCode() != HttpStatus.OK) {
                throw new IllegalArgumentException("Failed to send document.  Reason: " + response.getStatusCode().getReasonPhrase());
            }

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

    }

    @ExceptionHandler
    void handleIllegalArgumentException(IllegalArgumentException e, HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.BAD_REQUEST.value());
    }


}
