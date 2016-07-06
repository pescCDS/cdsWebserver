package org.pesc.config;

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.jersey.listing.ApiListingResourceJSON;
import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxws.JaxWsServerFactoryBean;
import org.pesc.api.*;
import org.pesc.api.exception.ExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.ws.rs.ext.ExceptionMapper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by James Whetstone (jwhetstone@ccctechcenter.org) on 2/25/16.
 */
@Configuration
public class ServiceConfig {


    @Autowired
    private OrganizationsResource organizationsResource;
    @Autowired
    private UserResource userResource;
    @Autowired
    private DocumentFormatResource documentFormatResource;
    @Autowired
    private DeliveryMethodsResource deliveryMethodsResource;
    @Autowired
    private EndpointResource endpointResource;
    @Autowired
    private SchoolCodesResource schoolCodesResource;

    @Autowired
    private ServiceProviderResource serviceProviderResource;

    @Autowired
    private InstitutionResource institutionResource;

    @Autowired
    private MessageResource messageResource;

    @Autowired
    private DepartmentsResource departmentsResource;

    @Autowired
    private DocumentTypesResource documentTypesResource;

    @Autowired ContactResource contactResource;

    @Bean
    public JacksonJaxbJsonProvider jacksonJaxbJsonProvider() {
        return new JacksonJaxbJsonProvider();
    }

    @Bean
    public ExceptionMapper apiExceptionMapper() {
        return new ExceptionHandler();
    }

    @Bean
    public CORSFilter corsFilter() {
        return new CORSFilter();
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
    public Server organizationsEndpoint() {
        JaxWsServerFactoryBean sf = new JaxWsServerFactoryBean();
        sf.setServiceClass(OrganizationsResource.class);
        sf.setAddress("/soap/v1/organizations");
        return sf.create();
    }


    /**
     * SOAP endpoint for users
     * @return
     */
    @Bean
    public Server usersEndpoint() {
        JaxWsServerFactoryBean sf = new JaxWsServerFactoryBean();
        sf.setServiceClass(UserResource.class);
        sf.setAddress("/soap/v1/users");
        return sf.create();
    }


    /**
     * SOAP endpoint for document formats
     * @return
     */
    @Bean
    public Server documentFormats() {
        JaxWsServerFactoryBean sf = new JaxWsServerFactoryBean();
        sf.setServiceClass(DocumentFormatResource.class);
        sf.setAddress("/soap/v1/document-formats");
        return sf.create();
    }

    /**
     * SOAP endpoint for document formats
     * @return
     */
    @Bean
    public Server contacts() {
        JaxWsServerFactoryBean sf = new JaxWsServerFactoryBean();
        sf.setServiceClass(ContactResource.class);
        sf.setAddress("/soap/v1/contacts");
        return sf.create();
    }

    /**
     * SOAP endpoint for delivery methods
     * @return
     */
    @Bean
    public Server deliveryMethods() {
        JaxWsServerFactoryBean sf = new JaxWsServerFactoryBean();
        sf.setServiceClass(DeliveryMethodsResource.class);
        sf.setAddress("/soap/v1/delivery-methods");
        return sf.create();
    }

    /**
     * SOAP endpoint for delivery methods
     * @return
     */
    @Bean
    public Server endpoints() {
        JaxWsServerFactoryBean sf = new JaxWsServerFactoryBean();
        sf.setServiceClass(EndpointResource.class);
        sf.setAddress("/soap/v1/endpoints");
        return sf.create();
    }

    /**
     * SOAP endpoint for delivery methods
     * @return
     */
    @Bean
    public Server schoolCodes() {
        JaxWsServerFactoryBean sf = new JaxWsServerFactoryBean();
        sf.setServiceClass(SchoolCodesResource.class);
        sf.setAddress("/soap/v1/school-codes");
        return sf.create();
    }

    /**
     * SOAP endpoint for delivery methods
     * @return
     */
    @Bean
    public Server institutions() {
        JaxWsServerFactoryBean sf = new JaxWsServerFactoryBean();
        sf.setServiceClass(InstitutionResource.class);
        sf.setAddress("/soap/v1/institutions");
        return sf.create();
    }


    /**
     * SOAP endpoint for service providers
     * @return
     */
    @Bean
    public Server serviceProviders() {
        JaxWsServerFactoryBean sf = new JaxWsServerFactoryBean();
        sf.setServiceClass(ServiceProviderResource.class);
        sf.setAddress("/soap/v1/service-providers");
        return sf.create();
    }


    /**
     * Create the REST endpoint
     * @return
     */
    @Bean
    public Server restServer() {
        JAXRSServerFactoryBean endpoint = new JAXRSServerFactoryBean();

        List<Object> beans  = new ArrayList<Object>();

        BeanConfig beanConfig = new BeanConfig();
        beanConfig.setVersion(restAPIVersion);
        beanConfig.setTitle("PESC CDS REST Interface");
        beanConfig.setDescription("Swagger UI to document and explore the REST interface provided by the PESC CDS.");
        beanConfig.setSchemes(new String[]{"https"});
        beanConfig.setHost(restApiHost);
        beanConfig.setBasePath("/services/rest/v1");
        beanConfig.setResourcePackage(restApiPackageToScan);
        beanConfig.setPrettyPrint(true);
        beanConfig.setScan(true);

        beans.add(beanConfig);
        beans.add(apiListingResourceJSON());

        beans.add(documentFormatResource);
        beans.add(deliveryMethodsResource) ;
        beans.add(endpointResource);
        beans.add(schoolCodesResource);
        beans.add(serviceProviderResource);
        beans.add(institutionResource);
        beans.add(messageResource);
        beans.add(departmentsResource);
        beans.add(documentTypesResource);
        beans.add(organizationsResource);
        beans.add(userResource);
        beans.add(contactResource);

        endpoint.setProviders(Arrays.<Object>asList(jacksonJaxbJsonProvider(), apiExceptionMapper(), corsFilter()));

        endpoint.setServiceBeans(beans);

        endpoint.setAddress("/rest/v1");

        return endpoint.create();
    }

}
