package org.pesc.cds.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.pesc.sdk.core.coremain.v1_12.BirthType;
import org.pesc.sdk.core.coremain.v1_12.CountryCodeType;
import org.pesc.sdk.core.coremain.v1_12.DocumentTypeCodeType;
import org.pesc.sdk.core.coremain.v1_12.NameType;
import org.pesc.sdk.core.coremain.v1_12.StateProvinceCodeType;
import org.pesc.sdk.core.coremain.v1_12.TransmissionTypeType;
import org.pesc.sdk.core.coremain.v1_12.UserDefinedExtensionsType;
import org.pesc.sdk.core.coremain.v1_9.SeverityCodeType;
import org.pesc.sdk.message.documentinfo.v1_0.DocumentInfo;
import org.pesc.sdk.message.documentinfo.v1_0.DocumentTypeCode;
import org.pesc.sdk.message.functionalacknowledgment.v1_0.SyntaxErrorType;
import org.pesc.sdk.message.functionalacknowledgment.v1_0.ValidationResponse;
import org.pesc.sdk.message.transcriptrequest.v1_2.TranscriptRequest;
import org.pesc.sdk.message.transcriptrequest.v1_2.TranscriptRequestValidator;
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
import org.pesc.sdk.sector.academicrecord.v1_7.SchoolType;
import org.pesc.sdk.sector.academicrecord.v1_7.SourceDestinationType;
import org.pesc.sdk.sector.academicrecord.v1_7.TransmissionDataType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.oxm.Marshaller;
import org.springframework.util.CollectionUtils;
import org.w3c.dom.Document;

import javax.annotation.Resource;
import javax.xml.transform.dom.DOMResult;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by sallen on 8/1/16.
 */
public class TranscriptRequestBuilder {
    private static final Log log = LogFactory.getLog(TranscriptRequestBuilder.class);
    private static final org.pesc.sdk.message.transcriptrequest.v1_2.ObjectFactory transcriptrequestObjectFactory = new org.pesc.sdk.message.transcriptrequest.v1_2.ObjectFactory();
    private static final org.pesc.sdk.sector.academicrecord.v1_7.ObjectFactory academicRecordObjectFactory = new org.pesc.sdk.sector.academicrecord.v1_7.ObjectFactory();
    private static final org.pesc.sdk.core.coremain.v1_12.ObjectFactory coreMainObjectFactory = new org.pesc.sdk.core.coremain.v1_12.ObjectFactory();
    private static final org.pesc.sdk.message.documentinfo.v1_0.ObjectFactory DocumentInfoObjectFactory = new org.pesc.sdk.message.documentinfo.v1_0.ObjectFactory();
    private static final org.pesc.sdk.message.functionalacknowledgment.v1_0.ObjectFactory functionalAcknowledgmentObjectFactory = new org.pesc.sdk.message.functionalacknowledgment.v1_0.ObjectFactory();
    private Marshaller DocumentInfoMarshaller;

    private String documentID;
    private DocumentTypeCodeType documentTypeCode;
    private TransmissionTypeType transmissionType;
    private String requestTrackingID;
    //source
    private Map<SchoolCodeType, String> sourceSchoolCodes = Maps.newHashMap();
    private List<String> sourceOrganizationNames = Lists.newArrayList();
    private List<String> sourceOrganizationAddressLines = Lists.newArrayList();
    private String sourceOrganizationCity;
    private StateProvinceCodeType sourceOrganizationStateProvinceCode;
    private String sourceOrganizationPostalCode;
      //optional
    private PhoneType sendersPhone;
    private String sendersEmail;
    //destination
    private Map<SchoolCodeType, String> destinationSchoolCodes = Maps.newHashMap();
    private List<String> destinationOrganizationNames = Lists.newArrayList();
      //optional
    private List<String> destinationOrganizationAddressLines = Lists.newArrayList();
    private String destinationCity;
    private StateProvinceCodeType destinationOrganizationStateProvinceCode;
    private String destinationOrganizationPostalCode;
    private PhoneType receiversPhone;
    private String receiversEmail;
    //document
    private DocumentTypeCode parchmentDocumentTypeCode;
    private String fileName;
    //student
    private Boolean studentRelease;
    private ReleaseAuthorizedMethodType studentReleasedMethod;
    private Date studentBirthDate;
    private String studentFirstName;
    private String studentLastName;
    private String studentSchoolName;
    private Map<SchoolCodeType, String> studentSchoolCodes = Maps.newHashMap();
      //optional
    private List<String> studentMiddleNames = Lists.newArrayList();
    private String studentEmail;
    private String studentPartialSsn;
    private Boolean studentCurrentlyEnrolled;

    public TranscriptRequestBuilder DocumentInfoMarshaller(Marshaller DocumentInfoMarshaller) {
        this.DocumentInfoMarshaller = DocumentInfoMarshaller;
        return this;
    }

    public TranscriptRequestBuilder documentID(String documentID) {
        this.documentID = documentID;
        return this;
    }

    public TranscriptRequestBuilder documentTypeCode(DocumentTypeCodeType documentTypeCode) {
        this.documentTypeCode = documentTypeCode;
        return this;
    }

    public TranscriptRequestBuilder transmissionType(TransmissionTypeType transmissionType) {
        this.transmissionType = transmissionType;
        return this;
    }

    public TranscriptRequestBuilder requestTrackingID(String requestTrackingID) {
        this.requestTrackingID = requestTrackingID;
        return this;
    }

    public TranscriptRequestBuilder sourceSchoolCodes(Map<SchoolCodeType, String> sourceSchoolCodes) {
        this.sourceSchoolCodes = sourceSchoolCodes;
        return this;
    }

    public TranscriptRequestBuilder sourceOrganizationNames(List<String> sourceOrganizationNames) {
        this.sourceOrganizationNames = sourceOrganizationNames;
        return this;
    }

    public TranscriptRequestBuilder sourceOrganizationAddressLines(List<String> sourceOrganizationAddressLines) {
        this.sourceOrganizationAddressLines = sourceOrganizationAddressLines;
        return this;
    }

    public TranscriptRequestBuilder sourceOrganizationCity(String sourceOrganizationCity) {
        this.sourceOrganizationCity = sourceOrganizationCity;
        return this;
    }

    public TranscriptRequestBuilder sourceOrganizationStateProvinceCode(StateProvinceCodeType sourceOrganizationStateProvinceCode) {
        this.sourceOrganizationStateProvinceCode = sourceOrganizationStateProvinceCode;
        return this;
    }

    public TranscriptRequestBuilder sourceOrganizationPostalCode(String sourceOrganizationPostalCode) {
        this.sourceOrganizationPostalCode = sourceOrganizationPostalCode;
        return this;
    }

    public TranscriptRequestBuilder sendersPhone(PhoneType sendersPhone) {
        this.sendersPhone = sendersPhone;
        return this;
    }

    public TranscriptRequestBuilder sendersEmail(String sendersEmail) {
        this.sendersEmail = sendersEmail;
        return this;
    }

    public TranscriptRequestBuilder destinationSchoolCodes(Map<SchoolCodeType, String> destinationSchoolCodes) {
        this.destinationSchoolCodes = destinationSchoolCodes;
        return this;
    }

    public TranscriptRequestBuilder destinationOrganizationNames(List<String> destinationOrganizationNames) {
        this.destinationOrganizationNames = destinationOrganizationNames;
        return this;
    }

    public TranscriptRequestBuilder destinationOrganizationAddressLines(List<String> destinationOrganizationAddressLines) {
        this.destinationOrganizationAddressLines = destinationOrganizationAddressLines;
        return this;
    }

    public TranscriptRequestBuilder destinationCity(String destinationCity) {
        this.destinationCity = destinationCity;
        return this;
    }

    public TranscriptRequestBuilder destinationOrganizationStateProvinceCode(StateProvinceCodeType destinationOrganizationStateProvinceCode) {
        this.destinationOrganizationStateProvinceCode = destinationOrganizationStateProvinceCode;
        return this;
    }

    public TranscriptRequestBuilder destinationOrganizationPostalCode(String destinationOrganizationPostalCode) {
        this.destinationOrganizationPostalCode = destinationOrganizationPostalCode;
        return this;
    }

    public TranscriptRequestBuilder receiversPhone(PhoneType receiversPhone) {
        this.receiversPhone = receiversPhone;
        return this;
    }

    public TranscriptRequestBuilder receiversEmail(String receiversEmail) {
        this.receiversEmail = receiversEmail;
        return this;
    }

    public TranscriptRequestBuilder parchmentDocumentTypeCode(DocumentTypeCode parchmentDocumentTypeCode) {
        this.parchmentDocumentTypeCode = parchmentDocumentTypeCode;
        return this;
    }

    public TranscriptRequestBuilder fileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public TranscriptRequestBuilder studentRelease(Boolean studentRelease) {
        this.studentRelease = studentRelease;
        return this;
    }

    public TranscriptRequestBuilder studentReleasedMethod(ReleaseAuthorizedMethodType studentReleasedMethod) {
        this.studentReleasedMethod = studentReleasedMethod;
        return this;
    }

    public TranscriptRequestBuilder studentBirthDate(Date studentBirthDate) {
        this.studentBirthDate = studentBirthDate;
        return this;
    }

    public TranscriptRequestBuilder studentFirstName(String studentFirstName) {
        this.studentFirstName = studentFirstName;
        return this;
    }

    public TranscriptRequestBuilder studentLastName(String studentLastName) {
        this.studentLastName = studentLastName;
        return this;
    }

    public TranscriptRequestBuilder studentSchoolName(String studentSchoolName) {
        this.studentSchoolName = studentSchoolName;
        return this;
    }


    public TranscriptRequestBuilder studentSchoolCodes(Map<SchoolCodeType, String> studentSchoolCodes) {
        this.studentSchoolCodes = studentSchoolCodes;
        return this;
    }

    public TranscriptRequestBuilder studentMiddleNames(List<String> studentMiddleNames) {
        this.studentMiddleNames = studentMiddleNames;
        return this;
    }

    public TranscriptRequestBuilder studentEmail(String studentEmail) {
        this.studentEmail = studentEmail;
        return this;
    }

    public TranscriptRequestBuilder studentPartialSsn(String studentPartialSsn) {
        this.studentPartialSsn = studentPartialSsn;
        return this;
    }

    public TranscriptRequestBuilder studentCurrentlyEnrolled(Boolean studentCurrentlyEnrolled) {
        this.studentCurrentlyEnrolled = studentCurrentlyEnrolled;
        return this;
    }

    public TranscriptRequest build(){
        /**
         <?xml version="1.0" encoding="UTF-8"?>
         <TranscriptRequest
               <TransmissionData>
                  <DocumentID>0ed95fd13f3e48d4a9c844392853cdc5</DocumentID>
                  <CreatedDateTime>2016-07-27T14:33:10-04:00</CreatedDateTime>
                  <DocumentTypeCode>Application</DocumentTypeCode>
                  <TransmissionType>Original</TransmissionType>
                  <RequestTrackingID>2f7c4e21337341918d8a69ddaa15e6db</RequestTrackingID>
         */
        TranscriptRequest transcriptRequest = transcriptrequestObjectFactory.createTranscriptRequest();
        TransmissionDataType transmissionData = academicRecordObjectFactory.createTransmissionDataType();
        transcriptRequest.setTransmissionData(transmissionData);
        transmissionData.setDocumentID(documentID);
        transmissionData.setCreatedDateTime(new Date());
        transmissionData.setDocumentTypeCode(documentTypeCode);
        transmissionData.setTransmissionType(transmissionType);
        transmissionData.setRequestTrackingID(requestTrackingID);
        /**
                  <Source>
                     <Organization>
                        <FICE>930405</FICE>
                        <OrganizationName>Sierra College</OrganizationName>
                        <Contacts>
                            <Address>
                                <AddressLine>5000 Rocklin Road</AddressLine>
                                <City>Rocklin</City>
                                <StateProvinceCode>CA</StateProvinceCode>
                                <PostalCode>95677</PostalCode>
                            </Address>
                        </Contacts>
                     </Organization>
                  </Source>
         */
        SourceDestinationType source = createDomesticSourceDestinationType(sourceOrganizationNames, sourceOrganizationAddressLines, sourceOrganizationCity, sourceOrganizationStateProvinceCode, sourceOrganizationPostalCode, sourceSchoolCodes, sendersPhone, sendersEmail);
        transmissionData.setSource(source);
        /**
                  <Destination>
                     <Organization>
                        <FICE>001081</FICE>
                        <OrganizationName>Arizona State University</OrganizationName>
                     </Organization>
                  </Destination>
         */
        SourceDestinationType destination = createSourceDestinationTypeNoAddress(destinationOrganizationNames, destinationSchoolCodes);
        transmissionData.setDestination(destination);
        /**
                  <UserDefinedExtensions>
                     <ns2:DocumentInfo>
                        <ns2:FileName>2f7c4e21337341918d8a69ddaa15e6db_document.pdf</ns2:FileName>
                        <ns2:DocumentType>Letter of Recommendation - Teacher</ns2:DocumentType>
                        <ns2:ExchangeType>PESC XML Document Request and PDF or XML</ns2:ExchangeType>
                     </ns2:DocumentInfo>
                  </UserDefinedExtensions>
               </TransmissionData>
         */
        UserDefinedExtensionsType userDefinedExtensions = coreMainObjectFactory.createUserDefinedExtensionsType();
        transmissionData.setUserDefinedExtensions(userDefinedExtensions);
        DocumentInfo documentInfo = DocumentInfoObjectFactory.createDocumentInfo();
        documentInfo.setFileName(fileName);
        documentInfo.setDocumentType(parchmentDocumentTypeCode);//e.g. transcript
        try {
            DOMResult domResult = new DOMResult();
            DocumentInfoMarshaller.marshal(documentInfo, domResult);
            userDefinedExtensions.setAny( ((Document) domResult.getNode()).getDocumentElement());
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        /**
               <Request>
                  <CreatedDateTime>2016-07-27T14:33:10-04:00</CreatedDateTime>
                  <RequestedStudent>
                     <ReleaseAuthorizedIndicator>true</ReleaseAuthorizedIndicator>
                     <ReleaseAuthorizedMethod>ElectronicSignature</ReleaseAuthorizedMethod>
                     <Person>
                        <Birth>
                           <BirthDate>1998-03-03</BirthDate>
                        </Birth>
                        <Name>
                           <FirstName>JEFFREY STEPHEN</FirstName>
                           <LastName>SHEEDY CORONEL</LastName>
                        </Name>
                     </Person>
                     <Attendance>
                        <CurrentEnrollmentIndicator>true</CurrentEnrollmentIndicator>
                        <School>
                           <OrganizationName>Academia Cotopaxi American International School</OrganizationName>
                           <CEEBACT>930405</CEEBACT>
                        </School>
                     </Attendance>
                  </RequestedStudent>
               </Request>
         </ns1:TranscriptRequest>
         */
        RequestType request = academicRecordObjectFactory.createRequestType();
        transcriptRequest.getRequests().add(request);
        request.setCreatedDateTime(new Date());
        RequestedStudentType requestedStudent = academicRecordObjectFactory.createRequestedStudentType();
        request.setRequestedStudent(requestedStudent);
        requestedStudent.setReleaseAuthorizedIndicator(studentRelease);
        requestedStudent.setReleaseAuthorizedMethod(studentReleasedMethod);
        PersonType person = academicRecordObjectFactory.createPersonType();
        requestedStudent.setPerson(person);
        if(StringUtils.isNotBlank(studentPartialSsn)) {
            person.setPartialSSN(studentPartialSsn);
        }
        if(studentBirthDate!=null) {
            BirthType birth = coreMainObjectFactory.createBirthType();
            person.setBirth(birth);
            birth.setBirthDate(studentBirthDate);
        }
        NameType name = coreMainObjectFactory.createNameType();
        person.setName(name);
        name.setFirstName(studentFirstName);
        name.getMiddleNames().addAll(studentMiddleNames);
        name.setLastName(studentLastName);
        if(studentEmail!=null) {
            ContactsType contact = academicRecordObjectFactory.createContactsType();
            person.getContacts().add(contact);
            EmailType email = academicRecordObjectFactory.createEmailType();
            contact.getEmails().add(email);
            email.setEmailAddress(studentEmail);
        }
        AttendanceType attendance = academicRecordObjectFactory.createAttendanceType();
        requestedStudent.getAttendances().add(attendance);
        attendance.setCurrentEnrollmentIndicator(studentCurrentlyEnrolled);
        SchoolType school = academicRecordObjectFactory.createSchoolType();
        attendance.setSchool(school);
        school.setOrganizationName(studentSchoolName);
        setSchoolCode(studentSchoolCodes, school);
        ValidationResponse validationResponse = TranscriptRequestValidator.validateTranscriptRequestRequiredContent(transcriptRequest);
        if (validationResponse.getSeverity() == SeverityCodeType.FATAL_ERROR || validationResponse.getSeverity() == SeverityCodeType.ERROR) {
            SyntaxErrorType error = functionalAcknowledgmentObjectFactory.createSyntaxErrorType();
            error.setErrorMessage("Required content missing for documentId: "+documentID);
            error.setSeverityCode(SeverityCodeType.ERROR);
            validationResponse.addErrorToTop(error);//put on top
            log.error(validationResponse.toString());
            throw new IllegalStateException(validationResponse.toString());
        } else if(validationResponse.getErrors().size()>0){
            SyntaxErrorType warnning = functionalAcknowledgmentObjectFactory.createSyntaxErrorType();
            warnning.setErrorMessage("Recommended content missing for documentId: "+documentID);
            warnning.setSeverityCode(SeverityCodeType.WARNING);
            validationResponse.addErrorToTop(warnning);//put on top
            log.warn(validationResponse.toString());
        }
        return transcriptRequest;
    }

    /**
     *
     * @param organizationNames - required
     * @param schoolCodes - required
     * @return
     */
    private SourceDestinationType createSourceDestinationTypeNoAddress(List<String> organizationNames, Map<SchoolCodeType, String> schoolCodes){
        return createSourceDestinationType(organizationNames, Collections.EMPTY_LIST, null, null, null, null, null, schoolCodes, null, null);
    }

    /**
     *
     * @param organizationNames - required
     * @param organizationAddressLines - required
     * @param organizationCity - required
     * @param organizationStateProvinceCode - required
     * @param organizationPostalCode - required
     * @param schoolCodes - required
     * @param phone - optional
     * @param email - optional
     * @return
     */
    private SourceDestinationType createDomesticSourceDestinationType(List<String> organizationNames, List<String> organizationAddressLines, String organizationCity, StateProvinceCodeType organizationStateProvinceCode, String organizationPostalCode, Map<SchoolCodeType, String> schoolCodes, PhoneType phone, String email){
        return createSourceDestinationType(organizationNames, organizationAddressLines, organizationCity, organizationStateProvinceCode, null, organizationPostalCode, null, schoolCodes, phone, email);
    }

    /**
     *
     * @param organizationNames - required
     * @param organizationAddressLines - required
     * @param organizationCity - required
     * @param organizationCountryCode - required
     * @param schoolCodes - required
     * @param phone - optional
     * @param email - optional
     * @return
     */
    private SourceDestinationType createInternationalSourceDestinationType(List<String> organizationNames, List<String> organizationAddressLines, String organizationCity, CountryCodeType organizationCountryCode, Map<SchoolCodeType, String> schoolCodes, PhoneType phone, String email){
        return createInternationalSourceDestinationType(organizationNames, organizationAddressLines, organizationCity, null, null, organizationCountryCode, schoolCodes, phone, email);
    }

    /**
     *
     * @param organizationNames - required
     * @param organizationAddressLines - required
     * @param organizationCity - required
     * @param organizationStateProvince - optional
     * @param organizationPostalCode - optional
     * @param organizationCountryCode - required
     * @param schoolCodes - required
     * @param phone - optional
     * @param email - optional
     * @return
     */
    private SourceDestinationType createInternationalSourceDestinationType(List<String> organizationNames, List<String> organizationAddressLines, String organizationCity, String organizationStateProvince, String organizationPostalCode, CountryCodeType organizationCountryCode, Map<SchoolCodeType, String> schoolCodes, PhoneType phone, String email){
        return createSourceDestinationType(organizationNames, organizationAddressLines, organizationCity, null, organizationStateProvince, organizationPostalCode, organizationCountryCode, schoolCodes, phone, email);
    }

    /**
     * Don't use this method directly, use convenience methods above
     * @return
     */
    private SourceDestinationType createSourceDestinationType(List<String> organizationNames, List<String> organizationAddressLines, String organizationCity, StateProvinceCodeType organizationStateProvinceCode, String organizationStateProvince, String organizationPostalCode, CountryCodeType organizationCountryCode, Map<SchoolCodeType, String> schoolCodes, PhoneType phone, String email){
        org.pesc.sdk.sector.academicrecord.v1_7.ObjectFactory academicRecordObjectFactory = new org.pesc.sdk.sector.academicrecord.v1_7.ObjectFactory();
        SourceDestinationType sourceDestination = academicRecordObjectFactory.createSourceDestinationType();
        OrganizationType organization = academicRecordObjectFactory.createOrganizationType();
        sourceDestination.setOrganization(organization);
        setSchoolCode(schoolCodes, organization);
        organization.getOrganizationNames().addAll(organizationNames);
        ContactsType contact = academicRecordObjectFactory.createContactsType();
        organization.getContacts().add(contact);
        if(!CollectionUtils.isEmpty(organizationAddressLines)) {
            AddressType address = academicRecordObjectFactory.createAddressType();
            contact.getAddresses().add(address);
            address.getAddressLines().addAll(organizationAddressLines);
            address.setCity(organizationCity);
            if (organizationStateProvinceCode != null) {
                address.setStateProvinceCode(organizationStateProvinceCode);//domestic only (required for domestic)
            } else {
                address.setStateProvince(organizationStateProvince);//international only (optional for international)
                address.setCountryCode(organizationCountryCode);//international only (required for international)
            }
            address.setPostalCode(organizationPostalCode);
        }
        if(phone!=null && StringUtils.isNotBlank(phone.getPhoneNumber())){
            contact.getPhones().add(phone);
        }
        if(StringUtils.isNotBlank(email)){
            EmailType emailType = academicRecordObjectFactory.createEmailType();
            contact.getEmails().add(emailType);
            emailType.setEmailAddress(email);
        }
        /**
         International Address
         Example 1
         <Address>
             <AddressLine>Altova Corporation</AddressLine>
             <AddressLine>Rudolfsplatz 13a/9</AddressLine>
             <City>Wien</City>
             <PostalCode>A-1010</PostalCode>
             <CountryCode>AT</CountryCode>
         </Address>

         Example 2:
         <Address>
             <AddressLine>De las Higuerillas y Alondras (Montes...</AddressLine>
             <City>Quito</City>
             <CountryCode>EC</CountryCode>
         </Address>

         Domestic Address
         Example 1
         <Address>
             <AddressLine>Street Address</AddressLine>
             <City>Roseville</City>
             <StateProvinceCode>CA</StateProvinceCode>
             <PostalCode>95678</PostalCode>
         </Address>
         */
        return sourceDestination;
    }

    private void setSchoolCode(Map<SchoolCodeType, String> schoolCodes, OrganizationType organizationType){
        for(SchoolCodeType schoolCodeType: SchoolCodeType.values()) {
            switch (schoolCodeType) {
                case ACT:
                    organizationType.setACT(schoolCodes.get(schoolCodeType));
                    break;
                case ATP:
                    organizationType.setATP(schoolCodes.get(schoolCodeType));
                    break;
                case FICE:
                    organizationType.setFICE(schoolCodes.get(schoolCodeType));
                    break;
                case IPEDS:
                    organizationType.setIPEDS(schoolCodes.get(schoolCodeType));
                    break;
                case OPEID:
                    organizationType.setOPEID(schoolCodes.get(schoolCodeType));
                    break;
                default:
                    throw new IllegalStateException(schoolCodeType + " is not supported");
            }
        }
    }

    private void setSchoolCode(Map<SchoolCodeType, String> schoolCodes, SchoolType school){
        for(SchoolCodeType schoolCodeType: SchoolCodeType.values()) {
            switch (schoolCodeType) {
                case ACT:
                    school.setACT(schoolCodes.get(schoolCodeType));
                    break;
                case ATP:
                    school.setATP(schoolCodes.get(schoolCodeType));
                    break;
                case FICE:
                    school.setFICE(schoolCodes.get(schoolCodeType));
                    break;
                case IPEDS:
                    school.setIPEDS(schoolCodes.get(schoolCodeType));
                    break;
                case OPEID:
                    school.setOPEID(schoolCodes.get(schoolCodeType));
                    break;
                default:
                    throw new IllegalStateException(schoolCodeType + " is not supported");
            }
        }
    }
}
