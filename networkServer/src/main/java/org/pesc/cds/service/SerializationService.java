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

import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.pesc.sdk.util.ValidationUtils;
import org.pesc.sdk.util.XmlFileType;
import org.pesc.sdk.util.XmlSchemaVersion;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import javax.annotation.Resource;
import javax.naming.OperationNotSupportedException;
import javax.xml.bind.*;

/**
 * Created by james on 5/22/17.
 */
@Service
public class SerializationService {

    @Resource(name="functionalAckJAXBContext")
    private JAXBContext functionalAckJAXBContext;

    @Resource(name="transcriptResponseJAXBContext")
    private JAXBContext transcriptResponseJAXBContext;

    @Resource(name="transcriptJAXBContext")
    private JAXBContext transcriptJAXBContext;

    @Resource(name="transcriptAcknowledgementJAXBContext")
    private JAXBContext transcriptAcknowledgementJAXBContext;

    @Resource(name="transcriptRequestJAXBContext")
    private JAXBContext transcriptRequestJAXBContext;

    @Resource(name="docInfoJAXBContext")
    private JAXBContext docInfoJAXBContext;

    public JAXBContext getFunctionalAckJAXBContext() {
        return functionalAckJAXBContext;
    }

    public JAXBContext getTranscriptResponseJAXBContext() {
        return transcriptResponseJAXBContext;
    }

    public JAXBContext getTranscriptJAXBContext() {
        return transcriptJAXBContext;
    }

    public JAXBContext getTranscriptAcknowledgementJAXBContext() {
        return transcriptAcknowledgementJAXBContext;
    }

    public JAXBContext getTranscriptRequestJAXBContext() {
        return transcriptRequestJAXBContext;
    }

    public JAXBContext getDocInfoJAXBContext() {
        return docInfoJAXBContext;
    }

    public Unmarshaller json(Unmarshaller unmarshaller) throws PropertyException {

        unmarshaller.setProperty(UnmarshallerProperties.MEDIA_TYPE, "application/json");
        unmarshaller.setProperty(UnmarshallerProperties.JSON_INCLUDE_ROOT, Boolean.TRUE);
        unmarshaller.setProperty(UnmarshallerProperties.JSON_USE_XSD_TYPES_WITH_PREFIX, Boolean.TRUE);
        
        return unmarshaller;
    }

    public Marshaller json(Marshaller marshaller) throws PropertyException {
 
        marshaller.setProperty(MarshallerProperties.MEDIA_TYPE, "application/json");
        marshaller.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, true);
        marshaller.setProperty(MarshallerProperties.JSON_USE_XSD_TYPES_WITH_PREFIX, Boolean.TRUE);
        
        return marshaller;
    }
    private Marshaller marshaller(JAXBContext context) throws JAXBException {
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        return marshaller;
    }

    private Unmarshaller unmarshaller(JAXBContext context) throws JAXBException {
        return context.createUnmarshaller();
    }

    public Marshaller createTranscriptMarshaller(boolean useJSON) throws JAXBException {
        Marshaller marshaller = marshaller(transcriptJAXBContext);
        if (useJSON == true) {
            json(marshaller);
        }
        return marshaller;
    }

    public Marshaller createTranscriptAckMarshaller() throws JAXBException {
        return marshaller(transcriptAcknowledgementJAXBContext);
    }

    public Marshaller createFunctionalAckMarshaller() throws JAXBException {
        return marshaller(functionalAckJAXBContext);
    }

    public Marshaller createTranscriptRequestMarshaller() throws JAXBException {
        return marshaller(transcriptRequestJAXBContext);
    }

    public Marshaller createTranscriptResponseMarshaller() throws JAXBException {
        return marshaller(transcriptResponseJAXBContext);
    }

    public Marshaller createDocInfoMarshaller() throws JAXBException {
        return marshaller(docInfoJAXBContext);
    }

    public Unmarshaller createTranscriptUnmarshaller(boolean validate, boolean useJSON) throws JAXBException, SAXException, OperationNotSupportedException {
        Unmarshaller unmarshaller =  unmarshaller(transcriptJAXBContext);
        if (validate == true) {
            unmarshaller.setSchema(ValidationUtils.getSchema(XmlFileType.COLLEGE_TRANSCRIPT, XmlSchemaVersion.V1_6_0));
        }
        if (useJSON == true) {
            json(unmarshaller);
        }
        return unmarshaller;
    }

    public Unmarshaller createTranscriptAckUnmarshaller(boolean validate) throws JAXBException, SAXException, OperationNotSupportedException {
        Unmarshaller unmarshaller = unmarshaller(transcriptAcknowledgementJAXBContext);
        if (validate == true) {
            unmarshaller.setSchema(ValidationUtils.getSchema(XmlFileType.TRANSCRIPT_ACKNOWLEDGEMENT, XmlSchemaVersion.V1_3_0));
        }
        return unmarshaller;
    }

    public Unmarshaller createFunctionalAckUnmarshaller(boolean validate) throws JAXBException, SAXException, OperationNotSupportedException {
        Unmarshaller unmarshaller = unmarshaller(functionalAckJAXBContext);
        if (validate == true) {
            unmarshaller.setSchema(ValidationUtils.getSchema(XmlFileType.FUNCTIONAL_ACKNOWLEDGEMENT, XmlSchemaVersion.V1_2_0));
        }
        
        return unmarshaller;
    }

    public Unmarshaller createTranscriptRequestUnmarshaller(boolean validate, boolean useJSON) throws JAXBException, SAXException, OperationNotSupportedException {

        Unmarshaller unmarshaller = unmarshaller(transcriptRequestJAXBContext);

        if (validate == true) {
            unmarshaller.setSchema(ValidationUtils.getSchema(XmlFileType.TRANSCRIPT_REQUEST, XmlSchemaVersion.V1_4_0));
        }
        if (useJSON == true) {
            json(unmarshaller);
        }
        return unmarshaller;
    }

    public Unmarshaller createTranscriptResponseUnmarshaller(boolean validate) throws JAXBException, SAXException, OperationNotSupportedException {
        Unmarshaller unmarshaller = unmarshaller(transcriptResponseJAXBContext);
        if (validate == true) {
            unmarshaller.setSchema(ValidationUtils.getSchema(XmlFileType.TRANSCRIPT_RESPONSE, XmlSchemaVersion.V1_4_0));
        }
        return unmarshaller;
    }

    public Unmarshaller createDocInfoUnmarshaller(boolean validate) throws JAXBException, SAXException, OperationNotSupportedException {
        Unmarshaller unmarshaller = unmarshaller(docInfoJAXBContext);
        if (validate == true) {
            unmarshaller.setSchema(ValidationUtils.getSchema(XmlFileType.DOCUMENT_INFO, XmlSchemaVersion.V1_0_0));
        }
        return unmarshaller;

    }



}
