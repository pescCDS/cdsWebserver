package org.pesc.cds.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

/**
 * Created by sallen on 7/27/16.
 */
@Configuration
public class JaxbConfig {

    @Bean(name="transcriptRequestMarshaller")
    public Jaxb2Marshaller transcriptRequestMarshaller(){
        Jaxb2Marshaller transcriptRequestMarshaller = new Jaxb2Marshaller();
        transcriptRequestMarshaller.setContextPath("org.pesc.sdk.message.transcriptrequest.v1_4.impl");
        transcriptRequestMarshaller.setSchema(new ClassPathResource("xsd/pesc/TranscriptRequest_v1.4.0.xsd"));
        return transcriptRequestMarshaller;
    }

    @Bean(name="documentInfoMarshaller")
    public Jaxb2Marshaller DocumentInfoMarshaller(){
        Jaxb2Marshaller DocumentInfoMarshaller = new Jaxb2Marshaller();
        DocumentInfoMarshaller.setContextPath("org.pesc.sdk.message.documentinfo.v1_0.impl");
        DocumentInfoMarshaller.setSchema(new ClassPathResource("xsd/pesc/DocumentInfo_v1.0.0.xsd"));
        return DocumentInfoMarshaller;
    }

    @Bean(name="functionalacknowledgementMarshaller")
    public Jaxb2Marshaller functionalacknowledgementMarshaller(){
        Jaxb2Marshaller functionalacknowledgementMarshaller = new Jaxb2Marshaller();
        functionalacknowledgementMarshaller.setContextPath("org.pesc.sdk.message.functionalacknowledgement.v1_2.impl");
        functionalacknowledgementMarshaller.setSchema(new ClassPathResource("xsd/pesc/FunctionalAcknowledgement_v1.2.0.xsd"));
        return functionalacknowledgementMarshaller;
    }

    @Bean(name="transcriptResponseMarshaller")
    public Jaxb2Marshaller transcriptResponseMarshaller(){
        Jaxb2Marshaller transcriptResponseMarshaller = new Jaxb2Marshaller();
        transcriptResponseMarshaller.setContextPath("org.pesc.sdk.message.transcriptresponse.v1_4.impl");
        transcriptResponseMarshaller.setSchema(new ClassPathResource("xsd/pesc/TranscriptResponse_v1.4.0.xsd"));
        return transcriptResponseMarshaller;
    }
}
