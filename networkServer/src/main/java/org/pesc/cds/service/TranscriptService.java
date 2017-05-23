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
 *
 * Created by James Whetstone (jwhetstone@ccctechcenter.org) on 6/1/16.
 */

package org.pesc.cds.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pesc.sdk.message.collegetranscript.v1_6.CollegeTranscript;
import org.pesc.sdk.util.ValidationUtils;
import org.pesc.sdk.util.XmlFileType;
import org.pesc.sdk.util.XmlSchemaVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import javax.naming.OperationNotSupportedException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import java.io.StringWriter;
import java.net.URL;


@Service
public class TranscriptService {
    private static final Log log = LogFactory.getLog(TranscriptService.class);

    @Autowired
    private SerializationService serializationService;

    public CollegeTranscript fromURL(URL url, boolean isJSON) throws JAXBException, SAXException, OperationNotSupportedException {

        Unmarshaller u = serializationService.createTranscriptUnmarshaller(true, isJSON);

        CollegeTranscript collegeTranscript = null;
        try {
            collegeTranscript = (CollegeTranscript) u.unmarshal(url);
        } catch (Exception e) {
            log.error("Error Unmarshalling TranscriptRequest", e);
            throw e;
        }

        return collegeTranscript;
    }

    private String serialize(CollegeTranscript transcript, Marshaller marshaller) throws JAXBException {
        StringWriter writer = new StringWriter();
        marshaller.marshal(transcript, writer);
        return writer.toString();
    }

    public String toXml(CollegeTranscript transcript) {
        try {

            Marshaller marshaller = serializationService.createTranscriptMarshaller(false);

            return serialize(transcript, marshaller);

        } catch (JAXBException e) {
            log.error(e);
        }

        return null;
    }

    public String toJson(CollegeTranscript transcript) {
        try {

            Marshaller marshaller = serializationService.createTranscriptMarshaller(true);
            return serialize(transcript, marshaller);

        } catch (JAXBException e) {
            log.error(e);
        }

        return null;
    }


}
