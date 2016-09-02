package org.pesc.sdk.message.transcriptrequest.v1_2;

import com.google.common.base.Preconditions;
import org.pesc.sdk.core.coremain.v1_12.DocumentTypeCodeType;
import org.pesc.sdk.core.coremain.v1_12.TransmissionTypeType;
import org.pesc.sdk.core.coremain.v1_9.SeverityCodeType;
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
import org.pesc.sdk.util.PescAddress;
import org.pesc.sdk.util.PescAddressUtils;
import org.pesc.sdk.util.ValidationUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: sallen
 * Date: 5/27/2014
 * Time: 9:54 AM
 * To change this template use File | Settings | File Templates.
 */
public class TranscriptRequestValidator {
    private static final org.pesc.sdk.sector.academicrecord.v1_7.ObjectFactory academicRecordObjectFactory = new org.pesc.sdk.sector.academicrecord.v1_7.ObjectFactory();

    public static ValidationResponse validateTranscriptRequestRequiredContent(TranscriptRequest transcriptRequest){
        ValidationResponse validationResponse = new ValidationResponse();
        String documentId = null;
        DocumentTypeCodeType documentTypeCode = null;
        String createDateTime = null;
        TransmissionTypeType transmissionType = null;
        boolean senderSchoolCodeProvided = false;
        String sendersName = null;
        AddressType sendersAddress = null;
        String sendersStreetAddress = null;
        String sendersCity = null;
        String sendersState = null;
        String sendersPostalCode = null;
        String sendersCountry = null;
        boolean addressValid = true;
        boolean addressDomestic = true;
        boolean receiverSchoolCodeProvided = false;
        String receiversName = null;
        String requestedStudentFirstName = null;
        String requestedStudentLastName = null;
        String requestedStudentDateOfBirth = null;
        String requestedStudentEmail = null;

        String requestedStudentMiddleName = null;
        String requestedStudentPartialSsn = null;
        Boolean requestedStudentCurrentEnrollmentIndicator = null;

        Boolean requestedStudentAuthorizedIndicator = null;
        ReleaseAuthorizedMethodType requestedStudentAuthorizedMethod = null;

        Preconditions.checkNotNull(transcriptRequest);
        if(transcriptRequest!=null && transcriptRequest.getTransmissionData()!=null){
            TransmissionDataType transmissionDataType = transcriptRequest.getTransmissionData();
            documentId = transmissionDataType.getDocumentID();
            documentTypeCode = transmissionDataType.getDocumentTypeCode();
            createDateTime = transmissionDataType.getCreatedDateTime().toString();
            transmissionType = transmissionDataType.getTransmissionType();
            SourceDestinationType sourceDestinationType = transmissionDataType.getSource();
            if(sourceDestinationType!=null && sourceDestinationType.getOrganization()!=null){
                OrganizationType sendingOrganizationType = sourceDestinationType.getOrganization();
                senderSchoolCodeProvided = isSchoolCodeProvided(sendingOrganizationType);
                if(sendingOrganizationType.getOrganizationNames()!=null && sendingOrganizationType.getOrganizationNames().size()>0){
                    sendersName = sendingOrganizationType.getOrganizationNames().get(0);
                }
                if(sendingOrganizationType.getContacts()!=null){
                    List<ContactsType> contacts = sendingOrganizationType.getContacts();
                    if(contacts!=null && contacts.size()>0){
                        ContactsType contact = contacts.get(0);
                        if(contact!=null){
                            if(contact.getAddresses()!=null && contact.getAddresses().size()>0){//destination is optional, but if provided must be valid domestic or international
                                sendersAddress = contact.getAddresses().get(0);
                                if(sendersAddress!=null) {
                                    PescAddress pescAddress = PescAddressUtils.getPescAddress(sendersAddress);
                                    sendersStreetAddress = pescAddress.getStreetAddress1();
                                    sendersCity = pescAddress.getCity();
                                    sendersState = pescAddress.getState();
                                    sendersPostalCode = pescAddress.getPostalCode();
                                    sendersCountry = pescAddress.getCountry();
                                    addressValid = pescAddress.isAddressValid();
                                    addressDomestic = pescAddress.isAddressDomestic();
                                }
                            }
                        }
                    }
                }
            }
            SourceDestinationType destination = transmissionDataType.getDestination();
            if(destination!=null && destination.getOrganization()!=null){
                OrganizationType receiversOrganizationType = destination.getOrganization();
                receiverSchoolCodeProvided = isSchoolCodeProvided(receiversOrganizationType);
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
                            if (personType.getName().getMiddleNames() != null && personType.getName().getMiddleNames().size() > 0) {
                                requestedStudentMiddleName = personType.getName().getMiddleNames().get(0);
                            }
                            requestedStudentPartialSsn = personType.getPartialSSN();
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
                    requestedStudentCurrentEnrollmentIndicator = attendanceType.isCurrentEnrollmentIndicator();
                }
            }
        }
        String missingRequiredText = " is missing, this field is required.";
        String missingOneRequiredText = " is missing, one of these fields is required.";
        String missingRecommendedText = " is missing, this field is recommended.";
        ValidationUtils.checkArgument(StringUtils.isNotBlank(documentId), "TranscriptRequest.TransmissionData.DocumentID" + missingRequiredText, validationResponse, SeverityCodeType.ERROR);
        ValidationUtils.checkNotNull(documentTypeCode, "TranscriptRequest.TransmissionData.DocumentTypeCode"+missingRequiredText, validationResponse, SeverityCodeType.ERROR);
        ValidationUtils.checkArgument(StringUtils.isNotBlank(createDateTime), "TranscriptRequest.TransmissionData.CreatedDateTime"+missingRequiredText, validationResponse, SeverityCodeType.ERROR);
        ValidationUtils.checkNotNull(transmissionType, "TranscriptRequest.TransmissionData.TransmissionType"+missingRequiredText, validationResponse, SeverityCodeType.ERROR);
        ValidationUtils.checkArgument(senderSchoolCodeProvided, "TranscriptRequest.TransmissionData.Source.Organization OPEID, NCHELPID, IPEDS, ATP, FICE, ACT, CCD, PSS, CEEBACT, CSIS, USIS, ESIS, PSIS, DUNS, MutuallyDefined"+missingOneRequiredText, validationResponse, SeverityCodeType.ERROR);
        ValidationUtils.checkArgument(StringUtils.isNotBlank(sendersName), "TranscriptRequest.TransmissionData.Source.Organization.OrganizationName"+missingRequiredText, validationResponse, SeverityCodeType.ERROR);
        if(sendersAddress!=null) {
            if(!addressValid){
                String format = addressDomestic?"domestic":"international";
                ValidationUtils.checkArgument(addressValid, "TranscriptRequest.TransmissionData.Source.Organization.Contacts.Address contains errors.  " +
                        "Source address is required and must be valid domestic or international address.  This appears to be "+format+" address format but contains errors.", validationResponse, SeverityCodeType.ERROR);
                if(addressDomestic){
                    ValidationUtils.checkArgument(StringUtils.isNotBlank(sendersStreetAddress), "TranscriptRequest.TransmissionData.Source.Organization.Contacts.Address.AddressLine" + missingRequiredText, validationResponse, SeverityCodeType.ERROR);
                    ValidationUtils.checkArgument(StringUtils.isNotBlank(sendersCity), "TranscriptRequest.TransmissionData.Source.Organization.Contacts.Address.City" + missingRequiredText, validationResponse, SeverityCodeType.ERROR);
                    ValidationUtils.checkArgument(StringUtils.isNotBlank(sendersState), "TranscriptRequest.TransmissionData.Source.Organization.Contacts.Address.StateProvinceCode" + missingRequiredText, validationResponse, SeverityCodeType.ERROR);
                    ValidationUtils.checkArgument(StringUtils.isNotBlank(sendersPostalCode), "TranscriptRequest.TransmissionData.Source.Organization.Contacts.Address.PostalCode" + missingRequiredText, validationResponse, SeverityCodeType.ERROR);
                }else{
                    ValidationUtils.checkArgument(StringUtils.isNotBlank(sendersStreetAddress), "TranscriptRequest.TransmissionData.Source.Organization.Contacts.Address.AddressLine" + missingRequiredText, validationResponse, SeverityCodeType.ERROR);
                    ValidationUtils.checkArgument(StringUtils.isNotBlank(sendersCity), "TranscriptRequest.TransmissionData.Source.Organization.Contacts.Address.City" + missingRequiredText, validationResponse, SeverityCodeType.ERROR);
                    ValidationUtils.checkArgument(StringUtils.isNotBlank(sendersCountry), "TranscriptRequest.TransmissionData.Source.Organization.Contacts.Address.CountryCode" + missingRequiredText, validationResponse, SeverityCodeType.ERROR);
                }
            }
        }else{
            ValidationUtils.checkNotNull(sendersAddress, "TranscriptRequest.TransmissionData.Source.Organization.Contacts.Address" + missingRequiredText, validationResponse, SeverityCodeType.ERROR);
        }
        ValidationUtils.checkArgument(receiverSchoolCodeProvided, "TranscriptRequest.TransmissionData.Destination.Organization OPEID, NCHELPID, IPEDS, ATP, FICE, ACT, CCD, PSS, CEEBACT, CSIS, USIS, ESIS, PSIS, DUNS, MutuallyDefined"+missingOneRequiredText, validationResponse, SeverityCodeType.ERROR);
        ValidationUtils.checkArgument(StringUtils.isNotBlank(receiversName), "TranscriptRequest.TransmissionData.Destination.Organization.OrganizationName"+missingRequiredText, validationResponse, SeverityCodeType.ERROR);
        ValidationUtils.checkArgument(StringUtils.isNotBlank(requestedStudentFirstName), "TranscriptRequest.Request.RequestedStudent.Person.Name.FirstName"+missingRequiredText, validationResponse, SeverityCodeType.ERROR);
        ValidationUtils.checkArgument(StringUtils.isNotBlank(requestedStudentLastName), "TranscriptRequest.Request.RequestedStudent.Person.Name.LastName"+missingRequiredText, validationResponse, SeverityCodeType.ERROR);
        ValidationUtils.checkArgument(StringUtils.isNotBlank(requestedStudentDateOfBirth), "TranscriptRequest.Request.RequestedStudent.Person.Birth.BirthDate"+missingRequiredText, validationResponse, SeverityCodeType.ERROR);
        ValidationUtils.checkArgument(StringUtils.isNotBlank(requestedStudentEmail), "TranscriptRequest.Request.RequestedStudent.Person.Contacts.Email.EmailAddress"+missingRecommendedText, validationResponse, SeverityCodeType.WARNING);

        ValidationUtils.checkArgument(StringUtils.isNotBlank(requestedStudentMiddleName), "TranscriptRequest.Request.RequestedStudent.Person.Name.MiddleName"+missingRecommendedText, validationResponse, SeverityCodeType.WARNING);
        ValidationUtils.checkArgument(StringUtils.isNotBlank(requestedStudentPartialSsn), "TranscriptRequest.Request.RequestedStudent.Person.PartialSSN"+missingRecommendedText, validationResponse, SeverityCodeType.WARNING);
        ValidationUtils.checkNotNull(requestedStudentCurrentEnrollmentIndicator, "TranscriptRequest.Request.RequestedStudent.Attendance.Current EnrollmentIndicator"+missingRecommendedText, validationResponse, SeverityCodeType.WARNING);

        ValidationUtils.checkNotNull(requestedStudentAuthorizedIndicator, "TranscriptRequest.Request.RequestedStudent.ReleaseAuthorizedIndicator"+missingRequiredText, validationResponse, SeverityCodeType.ERROR);
        ValidationUtils.checkNotNull(requestedStudentAuthorizedMethod, "TranscriptRequest.Request.RequestedStudent.ReleaseAuthorizedMethod"+missingRequiredText, validationResponse, SeverityCodeType.ERROR);

        return validationResponse;
    }

    public static ValidationResponse validateTranscriptRequestRecommendedContent(TranscriptRequest transcriptRequest){
        ValidationResponse validationResponse = new ValidationResponse();
        String requestTrackingId = null;
        String sendersPhone = null;
        String sendersEmail = null;
        AddressType receiversAddress = null;
        String receiversStreetAddress = null;
        String receiversCity = null;
        String receiversState = null;
        String receiversPostalCode = null;
        String receiversCountry = null;
        String receiversPhone = null;
        String receiversEmail = null;
        String requestedStudentEnrollDate = null;
        String requestedStudentExitDate = null;
        String requestedStudentAcademicAwardDate = null;
        String requestedStudentAcademicAwardTitle = null;
        boolean addressValid = true;
        boolean addressDomestic = true;
        Preconditions.checkNotNull(transcriptRequest);
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
                        if(contact.getAddresses()!=null && contact.getAddresses().size()>0){//destination is optional, but if provided must be valid domestic or international
                            receiversAddress = contact.getAddresses().get(0);
                            if(receiversAddress!=null) {
                                PescAddress pescAddress = PescAddressUtils.getPescAddress(receiversAddress);
                                receiversStreetAddress = pescAddress.getStreetAddress1();
                                receiversCity = pescAddress.getCity();
                                receiversState = pescAddress.getState();
                                receiversPostalCode = pescAddress.getPostalCode();
                                receiversCountry = pescAddress.getCountry();
                                addressValid = pescAddress.isAddressValid();
                                addressDomestic = pescAddress.isAddressDomestic();
                            }
                        }
                    }
                }
            }
        }
        if(transcriptRequest!=null && transcriptRequest.getRequests()!=null && transcriptRequest.getRequests().size()>0) {
            RequestType requestType = transcriptRequest.getRequests().get(0);
            RequestedStudentType requestedStudentType = requestType.getRequestedStudent();
            if (requestedStudentType != null) {
                if(requestedStudentType.getAttendances()!=null && requestedStudentType.getAttendances().size()>0){
                    AttendanceType attendanceType = requestedStudentType.getAttendances().get(0);
                    if(attendanceType!=null) {
                        if (attendanceType.getEnrollDate() != null) {
                            requestedStudentEnrollDate = attendanceType.getEnrollDate().toString();
                        }
                        if (attendanceType.getExitDate() != null) {
                            requestedStudentExitDate = attendanceType.getExitDate().toString();
                        }
                        if (attendanceType != null && attendanceType.getAcademicAwardsReporteds() != null && attendanceType.getAcademicAwardsReporteds().size() > 0) {
                            AcademicAwardsReportedType academicAwardsReportedType = attendanceType.getAcademicAwardsReporteds().get(0);
                            if (academicAwardsReportedType != null) {
                                requestedStudentAcademicAwardDate = academicAwardsReportedType.getAcademicAwardDate().toString();
                            }
                        }
                        if (attendanceType.getAcademicAwardsReporteds() != null && attendanceType.getAcademicAwardsReporteds().size() > 0) {
                            AcademicAwardsReportedType academicAwardsReportedType = attendanceType.getAcademicAwardsReporteds().get(0);
                            if (academicAwardsReportedType != null) {
                                requestedStudentAcademicAwardTitle = academicAwardsReportedType.getAcademicAwardTitle();
                            }
                        }
                    }
                }
            }
        }
        String missingRecommendedText = " is missing, this field is recommended.";
        String missingRequiredText = " is missing, this field is required.";
        ValidationUtils.checkArgument(StringUtils.isNotBlank(requestTrackingId), "TranscriptRequest.TransmissionData.RequestTrackingID"+missingRecommendedText, validationResponse, SeverityCodeType.WARNING);
        ValidationUtils.checkArgument(StringUtils.isNotBlank(sendersPhone), "TranscriptRequest.TransmissionData.Source.Organization.Contacts.Phone"+missingRecommendedText, validationResponse, SeverityCodeType.WARNING);
        ValidationUtils.checkArgument(StringUtils.isNotBlank(sendersEmail), "TranscriptRequest.TransmissionData.Source.Organization.Contacts.Email.EmailAddress"+missingRecommendedText, validationResponse, SeverityCodeType.WARNING);
        if(receiversAddress!=null) {
            if(!addressValid){
                String format = addressDomestic?"domestic":"international";
                ValidationUtils.checkArgument(addressValid, "TranscriptRequest.TransmissionData.Destination.Organization.Contacts.Address contains errors.  " +
                        "Destination address is optional, but if provided must be valid domestic or international address.  This appears to be "+format+" address format but contains errors.", validationResponse, SeverityCodeType.ERROR);
                if(addressDomestic){
                    ValidationUtils.checkArgument(StringUtils.isNotBlank(receiversStreetAddress), "TranscriptRequest.TransmissionData.Destination.Organization.Contacts.Address.AddressLine" + missingRequiredText, validationResponse, SeverityCodeType.ERROR);
                    ValidationUtils.checkArgument(StringUtils.isNotBlank(receiversCity), "TranscriptRequest.TransmissionData.Destination.Organization.Contacts.Address.City" + missingRequiredText, validationResponse, SeverityCodeType.ERROR);
                    ValidationUtils.checkArgument(StringUtils.isNotBlank(receiversState), "TranscriptRequest.TransmissionData.Destination.Organization.Contacts.Address.StateProvinceCode" + missingRequiredText, validationResponse, SeverityCodeType.ERROR);
                    ValidationUtils.checkArgument(StringUtils.isNotBlank(receiversPostalCode), "TranscriptRequest.TransmissionData.Destination.Organization.Contacts.Address.PostalCode" + missingRequiredText, validationResponse, SeverityCodeType.ERROR);
                }else{
                    ValidationUtils.checkArgument(StringUtils.isNotBlank(receiversStreetAddress), "TranscriptRequest.TransmissionData.Destination.Organization.Contacts.Address.AddressLine" + missingRequiredText, validationResponse, SeverityCodeType.ERROR);
                    ValidationUtils.checkArgument(StringUtils.isNotBlank(receiversCity), "TranscriptRequest.TransmissionData.Destination.Organization.Contacts.Address.City" + missingRequiredText, validationResponse, SeverityCodeType.ERROR);
                    ValidationUtils.checkArgument(StringUtils.isNotBlank(receiversCountry), "TranscriptRequest.TransmissionData.Destination.Organization.Contacts.Address.CountryCode" + missingRequiredText, validationResponse, SeverityCodeType.ERROR);
                }
            }
        }else{
            ValidationUtils.checkNotNull(receiversAddress, "TranscriptRequest.TransmissionData.Destination.Organization.Contacts.Address" + missingRecommendedText, validationResponse, SeverityCodeType.WARNING);
        }
        ValidationUtils.checkArgument(StringUtils.isNotBlank(receiversPhone), "TranscriptRequest.TransmissionData.Destination.Organization.Contacts.Phone"+missingRecommendedText, validationResponse, SeverityCodeType.WARNING);
        ValidationUtils.checkArgument(StringUtils.isNotBlank(receiversEmail), "TranscriptRequest.TransmissionData.Destination.Organization.Contacts.Email.EmailAddress"+missingRecommendedText, validationResponse, SeverityCodeType.WARNING);

        ValidationUtils.checkArgument(StringUtils.isNotBlank(requestedStudentEnrollDate), "TranscriptRequest.Request.RequestedStudent.Attendance.EnrollDate"+missingRecommendedText, validationResponse, SeverityCodeType.WARNING);
        ValidationUtils.checkArgument(StringUtils.isNotBlank(requestedStudentExitDate), "TranscriptRequest.Request.RequestedStudent.Attendance.ExitDate"+missingRecommendedText, validationResponse, SeverityCodeType.WARNING);
        ValidationUtils.checkArgument(StringUtils.isNotBlank(requestedStudentAcademicAwardDate), "TranscriptRequest.Request.RequestedStudent.Attendance.AcademicAwardsReported.AcademicAwardDate"+missingRecommendedText, validationResponse, SeverityCodeType.WARNING);

        ValidationUtils.checkArgument(StringUtils.isNotBlank(requestedStudentAcademicAwardTitle), "TranscriptRequest.Request.RequestedStudent.Attendance.AcademicAwardsReported.AcademicAwardTitle"+missingRecommendedText, validationResponse, SeverityCodeType.WARNING);

        return validationResponse;
    }

    /**
     * Create strict SourceDestinationType from relaxed SourceDestinationType.  Strict means that it will pass xsd validation.  Relaxed means that it doesn't need to pass xsd validation.
     * If relaxedOrganizationName is longer than 60 it will be abbreviated to 57 characters + '...'.  If relaxedOrganizationName is null or empty string strictOrganizationName will be null.
     * If relaxedCeeb is less than 6 characters it will be left padded.  If relaxedCeeb is more then 6 characters strictCeeb will be null.
     * @param relaxedSourceDestination
     * @return
     */
    public static SourceDestinationType relaxedToStrictSourceDestination(SourceDestinationType relaxedSourceDestination){
        SourceDestinationType strictSourceDestination = academicRecordObjectFactory.createSourceDestinationType();
        String organizationName = null;//1-60 characters
        String ceeb = null;//6 characters

        if(relaxedSourceDestination!=null){
            OrganizationType relaxedOrganization = relaxedSourceDestination.getOrganization();
            if(relaxedOrganization!=null){
                List<String> relaxedOrganizationNames = relaxedOrganization.getOrganizationNames();
                if(relaxedOrganizationNames.size()>0){
                    String name = relaxedOrganizationNames.get(0);
                    if(name!=null && name.length()>1) {
                        organizationName = StringUtils.abbreviate(name, 60);//limit to 60 characters
                    }
                }
                String relaxedCeeb = relaxedOrganization.getCEEBACT();
                if(relaxedCeeb!=null && relaxedCeeb.length()<7) {
                    ceeb = StringUtils.leftPad(relaxedCeeb, 6, '0');
                }
            }
        }
        OrganizationType organization = academicRecordObjectFactory.createOrganizationType();
        organization.getOrganizationNames().add(organizationName);
        organization.setCEEBACT(ceeb);
        strictSourceDestination.setOrganization(organization);
        return strictSourceDestination;
    }

    /**
     * If relaxedRequestTrackingId is longer than 35 it will be abbreviated to 32 characters + '...'.
     * If relaxedRequestTrackingId is null or empty string strictRequestTrackingId will be null.
     * minLength 1
     * maxLength 35
     * @param relaxedRequestTrackingId
     * @return
     */
    public static String relaxedToStrictRequestTrackingId(String relaxedRequestTrackingId){
        String strictRequestTrackingId = null;
        if(relaxedRequestTrackingId!=null && relaxedRequestTrackingId.length()>1){
            strictRequestTrackingId = StringUtils.abbreviate(relaxedRequestTrackingId, 35);
        }
        return strictRequestTrackingId;
    }

    private static boolean isSchoolCodeProvided(OrganizationType organizationType){
        return StringUtils.isNotBlank(organizationType.getOPEID()) || StringUtils.isNotBlank(organizationType.getNCHELPID()) || StringUtils.isNotBlank(organizationType.getIPEDS()) || StringUtils.isNotBlank(organizationType.getATP()) || StringUtils.isNotBlank(organizationType.getFICE()) || StringUtils.isNotBlank(organizationType.getACT()) || StringUtils.isNotBlank(organizationType.getCCD()) || StringUtils.isNotBlank(organizationType.getPSS()) || StringUtils.isNotBlank(organizationType.getCEEBACT()) || StringUtils.isNotBlank(organizationType.getCSIS()) || StringUtils.isNotBlank(organizationType.getUSIS()) || StringUtils.isNotBlank(organizationType.getESIS()) || StringUtils.isNotBlank(organizationType.getPSIS()) || StringUtils.isNotBlank(organizationType.getDUNS()) || StringUtils.isNotBlank(organizationType.getMutuallyDefined());
    }

}
