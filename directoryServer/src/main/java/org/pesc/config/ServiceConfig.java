package org.pesc.config;

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.jersey.listing.ApiListingResourceJSON;
import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.cxf.jaxws.JaxWsServerFactoryBean;
import org.pesc.service.DocumentResource;
import org.pesc.service.OrganizationResource;
import org.pesc.service.rs.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ws.config.annotation.EnableWs;

import javax.xml.ws.Endpoint;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by james on 2/25/16.
 */
@EnableWs
@Configuration
public class ServiceConfig {

    @Autowired
    private ContactRestController contactRestController;

    @Autowired
    DeliveryMethodRestController deliveryMethodRestController;

    @Autowired
    DeliveryOptionRestController deliveryOptionRestController;

    @Autowired
    DocumentFormatRestController documentFormatRestController;

    @Autowired
    EntityCodeRestController entityCodeRestController;

    @Autowired
    OrganizationRestController organizationRestController;

    @Autowired
    UtilityRestController utilityRestController;


    @Bean
    public JacksonJaxbJsonProvider jacksonJaxbJsonProvider() {
        return new JacksonJaxbJsonProvider();
    }

    @Bean
    public ApiListingResourceJSON apiListingResourceJSON() {
        return new ApiListingResourceJSON();
    }

    //A WADL is provided by default by the CXF RS implementation---no need to add it to swagger.
    /*

    @Bean
    public WadlResource wadlResource() {
        return new WadlResource();
    }
    */


    @Value("${rest.api.version}")
    private String restAPIVersion;

    @Value("${rest.api.host}")
    private String restApiHost;

    @Value("${rest.api.package}")
    private String restApiPackageToScan;


    @Bean(name = "cxf")
    public SpringBus springBus() {
        return new SpringBus();
    }

    /**
     * The SOAP services are spread out accross several endpoints.  There doesn't seem to be a way to
     * combine them into a single endpoint without refactory the code to use a single class for all
     * resources.
     */


    /**
     * SOAP endpoint for organizations
     * @return
     */
    @Bean
    public Server organizationEndpoint() {
        JaxWsServerFactoryBean sf = new JaxWsServerFactoryBean();
        sf.setServiceClass(OrganizationRestController.class);
        sf.setAddress("/soap/organizations");
        return sf.create();
    }

    /**
     * SOAP endpoint for contacts
     * @return
     */
    @Bean
    public Server contactEndpoint() {
        JaxWsServerFactoryBean sf = new JaxWsServerFactoryBean();
        sf.setServiceClass(ContactRestController.class);
        sf.setAddress("/soap/contacts");
        return sf.create();
    }

    /**
     * SOAP endpoints for delivery methods
     * @return
     */
    @Bean
    public Server deliveryMethodEndpoint() {
        JaxWsServerFactoryBean sf = new JaxWsServerFactoryBean();
        sf.setServiceClass(DeliveryMethodRestController.class);
        sf.setAddress("/soap/deliveryMethods");
        return sf.create();
    }

    /**
     * SOAP endpoint for delivery options
     * @return
     */
    @Bean
    public Server deliveryOptionsEndpoint() {
        JaxWsServerFactoryBean sf = new JaxWsServerFactoryBean();
        sf.setServiceClass(DeliveryOptionRestController.class);
        sf.setAddress("/soap/deliveryOptions");
        return sf.create();
    }

    /**
     * SOAP endpoint for entity codes
     * @return
     */
    @Bean
    public Server entityCodesEndpoint() {
        JaxWsServerFactoryBean sf = new JaxWsServerFactoryBean();
        sf.setServiceClass(EntityCodeRestController.class);
        sf.setAddress("/soap/entityCodes");
        return sf.create();
    }

    /**
     * SOAP endpoint for document formats
     * @return
     */
    @Bean
    public Server documentFormatEndpoint() {
        JaxWsServerFactoryBean sf = new JaxWsServerFactoryBean();
        sf.setServiceClass(DocumentFormatRestController.class);
        sf.setAddress("/soap/documentFormats");
        return sf.create();
    }


    /**
     * Create the REST endpoint
     * @return
     */
    @Bean
    public Server rsServer() {
        JAXRSServerFactoryBean endpoint = new JAXRSServerFactoryBean();

        List<Object> beans  = new ArrayList<Object>();

        BeanConfig beanConfig = new BeanConfig();
        beanConfig.setVersion(restAPIVersion);
        beanConfig.setTitle("PESC CDS REST Interface");
        beanConfig.setDescription("Swagger UI to document and explore the REST interface provided by the PESC CDS.");
        beanConfig.setSchemes(new String[]{"http"});
        beanConfig.setHost(restApiHost);
        beanConfig.setBasePath("/services/rest");
        beanConfig.setResourcePackage(restApiPackageToScan);
        beanConfig.setPrettyPrint(true);
        beanConfig.setScan(true);

        beans.add(beanConfig);
        beans.add(apiListingResourceJSON());
        //beans.add(wadlResource());

        beans.add(contactRestController);
        beans.add(deliveryMethodRestController);
        beans.add(deliveryOptionRestController);
        beans.add(documentFormatRestController);
        beans.add(entityCodeRestController);
        beans.add(organizationRestController);
        beans.add(utilityRestController);



        endpoint.setProviders(Arrays.<Object>asList(jacksonJaxbJsonProvider()));

        endpoint.setServiceBeans(beans);

        endpoint.setAddress("/rest");

        return endpoint.create();
    }

}
