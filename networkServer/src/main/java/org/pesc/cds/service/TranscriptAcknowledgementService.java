package org.pesc.cds.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pesc.sdk.core.coremain.v1_14.DocumentTypeCodeType;
import org.pesc.sdk.core.coremain.v1_14.TransmissionTypeType;
import org.pesc.sdk.message.collegetranscript.v1_6.CollegeTranscript;
import org.pesc.sdk.message.transcriptacknowledgement.v1_3.Acknowledgment;
import org.pesc.sdk.sector.academicrecord.v1_9.*;
import org.pesc.sdk.util.ValidationUtils;
import org.pesc.sdk.util.XmlFileType;
import org.pesc.sdk.util.XmlSchemaVersion;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import javax.naming.OperationNotSupportedException;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.validation.Schema;
import java.io.StringWriter;
import java.util.Calendar;

/**
 * Created by James Whetstone on 1/5/17.
 */
@Service
public class TranscriptAcknowledgementService {

    private static final Log log = LogFactory.getLog(TranscriptAcknowledgementService.class);

    private static final org.pesc.sdk.message.transcriptacknowledgement.v1_3.ObjectFactory transcriptAcknoweledgementFactory = new org.pesc.sdk.message.transcriptacknowledgement.v1_3.ObjectFactory();
    private static final org.pesc.sdk.sector.academicrecord.v1_9.ObjectFactory academicRecordObjectFactory = new org.pesc.sdk.sector.academicrecord.v1_9.ObjectFactory();


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

        if (!collegeTranscript.getStudent().getAcademicRecords().isEmpty() &&
            !collegeTranscript.getStudent().getAcademicRecords().get(0).getAcademicSummaries().isEmpty()) {
                ack.setAcademicSummary(collegeTranscript.getStudent().getAcademicRecords().get(0).getAcademicSummaries().get(0));
        }

        int totalCourses = 0;
        int totalAcademicAwards = 0;
        for(AcademicRecordType ar : collegeTranscript.getStudent().getAcademicRecords()) {
            totalCourses += ar.getCourses().size();
            totalAcademicAwards += ar.getAcademicAwards().size();

            for(AcademicSessionType session: ar.getAcademicSessions()) {
                totalCourses += session.getCourses().size();
                totalAcademicAwards += session.getAcademicAwards().size();
            }
        }
        ack.setCourseTotal(totalCourses);
        ack.setAcademicAwardTotal(totalAcademicAwards);
        ack.setPerson(person);

        return ack;
    }

    public String toXml(Acknowledgment acknowledgment) {
        try {

            Marshaller marshaller = ValidationUtils.createMarshaller("org.pesc.sdk.message.transcriptacknowledgement.v1_3.impl");
            Schema schema = ValidationUtils.getSchema(XmlFileType.TRANSCRIPT_ACKNOWLEDGEMENT, XmlSchemaVersion.V1_3_0);

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

}
