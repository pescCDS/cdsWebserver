package org.pesc.config;

import javax.xml.ws.Endpoint;
import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.cxf.transport.servlet.CXFServlet;

import org.pesc.cds.webservice.WebServicesInterface;
import org.pesc.service.WebServiceImpl;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.config.annotation.WsConfigurerAdapter;

/**
 * Created by james on 2/25/16.
 */
@EnableWs
@Configuration
public class SOAPConfig extends WsConfigurerAdapter {


    @Bean(name = "cxf")
    public SpringBus springBus() {
        return new SpringBus();
    }

    @Bean
    public WebServicesInterface myService() {
        return new WebServiceImpl();
    }

    @Bean
    public Endpoint endpoint() {
        EndpointImpl endpoint = new EndpointImpl(springBus(), myService());
        endpoint.publish("/soap");
        return endpoint;
    }
}