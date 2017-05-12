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


    public CollegeTranscript fromURL(URL url) throws JAXBException, SAXException, OperationNotSupportedException {

        Unmarshaller u = ValidationUtils.createUnmarshaller("org.pesc.sdk.message.collegetranscript.v1_6.impl");

        Schema schema = ValidationUtils.getSchema(XmlFileType.COLLEGE_TRANSCRIPT, XmlSchemaVersion.V1_6_0);
        u.setSchema(schema);

        CollegeTranscript collegeTranscript = null;
        try {
            collegeTranscript = (CollegeTranscript) u.unmarshal(url);
        } catch (Exception e) {
            log.error("Error Unmarshalling TranscriptRequest", e);
            throw e;
        }

        return collegeTranscript;
    }

    public String toXml(CollegeTranscript transcript) {
        try {
            JAXBContext jc = JAXBContext.newInstance("org.pesc.sdk.message.collegetranscript.v1_6.impl");
            Marshaller marshaller = jc.createMarshaller();

            Schema schema = ValidationUtils.getSchema(XmlFileType.COLLEGE_TRANSCRIPT, XmlSchemaVersion.V1_6_0);

            marshaller.setSchema(schema);
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );

            StringWriter writer = new StringWriter();
            marshaller.marshal(transcript, writer);

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
