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
        transcriptRequestMarshaller.setContextPath("org.pesc.sdk.message.transcriptrequest.v1_2.impl");
        transcriptRequestMarshaller.setSchema(new ClassPathResource("xsd/pesc/TranscriptRequest_v1.2.0.xsd"));
        return transcriptRequestMarshaller;
    }

    @Bean(name="DocumentInfoMarshaller")
    public Jaxb2Marshaller DocumentInfoMarshaller(){
        Jaxb2Marshaller DocumentInfoMarshaller = new Jaxb2Marshaller();
        DocumentInfoMarshaller.setContextPath("org.pesc.sdk.message.documentinfo.v1_0.impl");
        DocumentInfoMarshaller.setSchema(new ClassPathResource("xsd/pesc/DocumentInfo_v1.0.0.xsd"));
        return DocumentInfoMarshaller;
    }

    @Bean(name="functionalAcknowledgmentMarshaller")
    public Jaxb2Marshaller functionalAcknowledgmentMarshaller(){
        Jaxb2Marshaller functionalAcknowledgmentMarshaller = new Jaxb2Marshaller();
        functionalAcknowledgmentMarshaller.setContextPath("org.pesc.sdk.message.functionalacknowledgment.v1_0.impl");
        functionalAcknowledgmentMarshaller.setSchema(new ClassPathResource("xsd/pesc/FunctionalAcknowledgment_v1.0.0.xsd"));
        return functionalAcknowledgmentMarshaller;
    }

    @Bean(name="transcriptResponseMarshaller")
    public Jaxb2Marshaller transcriptResponseMarshaller(){
        Jaxb2Marshaller transcriptResponseMarshaller = new Jaxb2Marshaller();
        transcriptResponseMarshaller.setContextPath("org.pesc.sdk.message.transcriptresponse.v1_2.impl");
        transcriptResponseMarshaller.setSchema(new ClassPathResource("xsd/pesc/TranscriptResponse_v1.2.0.xsd"));
        return transcriptResponseMarshaller;
    }
}
