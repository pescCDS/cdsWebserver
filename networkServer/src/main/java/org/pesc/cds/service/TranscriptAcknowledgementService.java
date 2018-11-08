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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pesc.cds.exception.TranscriptException;
import org.pesc.sdk.core.coremain.v1_14.DocumentTypeCodeType;
import org.pesc.sdk.core.coremain.v1_14.GPAType;
import org.pesc.sdk.core.coremain.v1_14.NameType;
import org.pesc.sdk.core.coremain.v1_14.TransmissionTypeType;
import org.pesc.sdk.message.collegetranscript.v1_6.CollegeTranscript;
import org.pesc.sdk.message.transcriptacknowledgement.v1_3.Acknowledgment;
import org.pesc.sdk.sector.academicrecord.v1_9.*;
import org.pesc.sdk.util.ValidationUtils;
import org.pesc.sdk.util.XmlFileType;
import org.pesc.sdk.util.XmlSchemaVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import javax.naming.OperationNotSupportedException;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import java.io.File;
import java.io.StringWriter;
import java.util.Calendar;
import java.util.List;

/**
 * Created by James Whetstone on 1/5/17.
 */
@Service
public class TranscriptAcknowledgementService {

    public static class AckTotals {
        public Integer totalCourses;
        public Integer totalAcademicAwards;
    }

    @Autowired
    private SerializationService serializationService;


    private static final Log log = LogFactory.getLog(TranscriptAcknowledgementService.class);

    private static final org.pesc.sdk.message.transcriptacknowledgement.v1_3.ObjectFactory transcriptAcknoweledgementFactory = new org.pesc.sdk.message.transcriptacknowledgement.v1_3.ObjectFactory();
    private static final org.pesc.sdk.sector.academicrecord.v1_9.ObjectFactory academicRecordObjectFactory = new org.pesc.sdk.sector.academicrecord.v1_9.ObjectFactory();

    public Acknowledgment getTranscriptAcknowledgement(String filePath) throws JAXBException, SAXException, OperationNotSupportedException {

        Unmarshaller u = serializationService.createTranscriptAckUnmarshaller(true);

        return (Acknowledgment) u.unmarshal(new File(filePath));
    }

    private boolean verifyName(NameType ackName, NameType studentName) {

        boolean match = true;

        if (studentName.getNameCode() != null) {
            match &= studentName.getNameCode().equals( ackName.getNameCode());
        }
        if (studentName.getNamePrefix() != null) {
            match &= studentName.getNamePrefix().equals( ackName.getNamePrefix());
        }
        if (studentName.getFirstName() != null) {
            match &= studentName.getFirstName().equals( ackName.getFirstName());
        }
        if (studentName.getLastName() != null) {
            match &= studentName.getLastName().equals( ackName.getLastName());
        }
        if (studentName.getMiddleNames() != null) {
            match &= ( studentName.getMiddleNames().size() == ackName.getMiddleNames().size() );

            for(int i=0; i < studentName.getMiddleNames().size(); i++) {
                match &= studentName.getMiddleNames().get(i).equals(ackName.getMiddleNames().get(i));
            }
        }
        if (studentName.getNameTitle() != null) {
            match &= studentName.getNameTitle().equals( ackName.getNameTitle());
        }
        if (studentName.getNameSuffix() != null) {
            match &= studentName.getNameSuffix().equals( ackName.getNameSuffix());
        }

        return match;
    }

    private boolean verifyGPA(GPAType ackGpa, GPAType transcriptGPA) {

        boolean match = true;

        if (transcriptGPA.getCreditHoursAttempted() != null) {
            match &= transcriptGPA.getCreditHoursAttempted().equals(ackGpa.getCreditHoursAttempted());
        }
        if (transcriptGPA.getCreditHoursEarned() != null) {
            match &= transcriptGPA.getCreditHoursEarned().equals(ackGpa.getCreditHoursEarned());
        }
        if (transcriptGPA.getCreditHoursforGPA() != null) {
            match &= transcriptGPA.getCreditHoursforGPA().equals(ackGpa.getCreditHoursforGPA());
        }
        if (transcriptGPA.getCreditHoursRequired() != null) {
            match &= transcriptGPA.getCreditHoursRequired().equals(ackGpa.getCreditHoursRequired());
        }
        if (transcriptGPA.getCreditUnit() != null) {
            match &= transcriptGPA.getCreditUnit().equals(ackGpa.getCreditUnit());
        }
        if (transcriptGPA.getGPARangeMaximum() != null) {
            match &= transcriptGPA.getGPARangeMaximum().equals(ackGpa.getGPARangeMaximum());
        }
        if (transcriptGPA.getGPARangeMinimum() != null) {
            match &= transcriptGPA.getGPARangeMinimum().equals(ackGpa.getGPARangeMinimum());
        }
        if (transcriptGPA.getGradePointAverage() != null){
            match &= transcriptGPA.getGradePointAverage().equals(ackGpa.getGradePointAverage());
        }
        if (transcriptGPA.getTotalQualityPoints() != null) {
            match &= transcriptGPA.getTotalQualityPoints().equals(ackGpa.getTotalQualityPoints());
        }
        return match;

    }


    public void verifyTranscript(Acknowledgment ack, CollegeTranscript collegeTranscript) throws TranscriptException {

        AcknowledgmentPersonType ackPerson = ack.getPerson();
        PersonType studentPerson = collegeTranscript.getStudent().getPerson();

        StringBuilder errorBuffer = new StringBuilder();

        boolean matched = true;

        try {
            if (verifyName(ackPerson.getName(), studentPerson.getName()) == false) {
                matched = false;
                errorBuffer.append("The student name on the transcript does not match the student name in the transcript acknowledgement.\n");
            }

            if (ack.getAcademicSummary() != null) {
                if (verifyGPA(ack.getAcademicSummary().getGPA(), findFirstAcademicSummary(collegeTranscript).getGPA()) == false) {
                    matched = false;
                    errorBuffer.append("The GPA on the transcript does not match the GPA in the transcript acknoweledgement.\n");
                }
            }


            AckTotals ackTotals = getCourseAndAwardTotals(collegeTranscript);

            if (ack.getAcademicAwardTotal() != ackTotals.totalAcademicAwards) {
                matched = false;
                errorBuffer.append("The total number of academic awards on the transcript do not match the total awards on the transcript acknowledgement.\n");
            }

            if (ack.getCourseTotal() != ackTotals.totalCourses) {
                matched = false;
                errorBuffer.append("The total number of courses on the transcript do not match the total number of courses on the transcript acknowledgement.\n");
            }
        }
        catch (Exception e) {
            log.error("Failed to verify PESC college transcript with PESC transcript acknowledgement.", e);
            matched = false;
            errorBuffer.append("Failed to verify PESC college transcript with PESC transcript acknowledgement: " + e.getClass().getCanonicalName());
        }
        finally {
            if (matched == false) {
                throw new TranscriptException(errorBuffer.toString());
            }
        }
    }


    private AckTotals getCourseAndAwardTotals(CollegeTranscript collegeTranscript) {

        AckTotals ackTotals = new AckTotals();
        ackTotals.totalAcademicAwards = 0;
        ackTotals.totalCourses = 0;

        for(AcademicRecordType ar : collegeTranscript.getStudent().getAcademicRecords()) {
            ackTotals.totalCourses += ar.getCourses().size();
            ackTotals.totalAcademicAwards += ar.getAcademicAwards().size();

            for(AcademicSessionType session: ar.getAcademicSessions()) {
                ackTotals.totalCourses += session.getCourses().size();
                ackTotals.totalAcademicAwards += session.getAcademicAwards().size();
            }
        }

        return ackTotals;
    }

    private AcademicSummaryFType findFirstAcademicSummary(CollegeTranscript collegeTranscript) {

        for(AcademicRecordType ar: collegeTranscript.getStudent().getAcademicRecords()) {
            if (!ar.getAcademicSummaries().isEmpty()){
                return ar.getAcademicSummaries().get(0);
            }
        }

        return null;
    }

    public Acknowledgment buildBaseTranscriptAcknowledgement(SourceDestinationType source,
                                                                        SourceDestinationType destination,
                                                                        CollegeTranscript collegeTranscript,
                                                                        String ackDocID,
                                                             String requestTrackingID) {
        Acknowledgment ack = transcriptAcknoweledgementFactory.createAcknowledgment();

        TransmissionDataType transmissionData = academicRecordObjectFactory.createTransmissionDataType();

        transmissionData.setSource(source);
        transmissionData.setDestination(destination);
        transmissionData.setCreatedDateTime(Calendar.getInstance().getTime());
        transmissionData.setDocumentID(ackDocID);
        transmissionData.setDocumentTypeCode(DocumentTypeCodeType.ACKNOWLEDGMENT);
        transmissionData.setTransmissionType(TransmissionTypeType.ORIGINAL);
        //Request tracking ID here is the transaction key as defined by the sender's network server.
        transmissionData.setRequestTrackingID(requestTrackingID);

        ack.setTransmissionData(transmissionData);

        AcknowledgmentPersonType person = academicRecordObjectFactory.createAcknowledgmentPersonType();
        person.setName(collegeTranscript.getStudent().getPerson().getName());


        ack.setAcademicSummary(findFirstAcademicSummary(collegeTranscript));

        AckTotals ackTotals = getCourseAndAwardTotals(collegeTranscript);

        ack.setCourseTotal(ackTotals.totalCourses);
        ack.setAcademicAwardTotal(ackTotals.totalAcademicAwards);
        ack.setPerson(person);

        return ack;
    }

    public String toXml(Acknowledgment acknowledgment) {
        try {

            Marshaller marshaller = serializationService.createTranscriptAckMarshaller();
            Schema schema = ValidationUtils.getSchema(XmlFileType.TRANSCRIPT_ACKNOWLEDGEMENT, XmlSchemaVersion.V1_3_0);

            marshaller.setSchema(schema);

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

}
