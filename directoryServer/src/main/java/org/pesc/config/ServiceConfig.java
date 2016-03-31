package org.pesc.config;

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.jersey.listing.ApiListingResourceJSON;
import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxws.JaxWsServerFactoryBean;
import org.pesc.api.OrganizationsResource;
import org.pesc.api.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by james on 2/25/16.
 */
@Configuration
public class ServiceConfig {

    /*
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

    */

    @Autowired
    private OrganizationsResource organizationsResource;
    @Autowired
    private UserResource userResource;


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
    public Server organizationsEndpoint() {
        JaxWsServerFactoryBean sf = new JaxWsServerFactoryBean();
        sf.setServiceClass(OrganizationsResource.class);
        sf.setAddress("/soap/v1/organizations");
        return sf.create();
    }


    /**
     * SOAP endpoint for organizations
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
     * Create the REST endpoint
     * @return

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

     */


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
        beanConfig.setSchemes(new String[]{"http"});
        beanConfig.setHost(restApiHost);
        beanConfig.setBasePath("/services/rest/v1");   //TODO: change path after completion
        beanConfig.setResourcePackage(restApiPackageToScan);
        beanConfig.setPrettyPrint(true);
        beanConfig.setScan(true);

        beans.add(beanConfig);
        beans.add(apiListingResourceJSON());
        //beans.add(wadlResource());

        beans.add(organizationsResource);
        beans.add(userResource);

        endpoint.setProviders(Arrays.<Object>asList(jacksonJaxbJsonProvider()));

        endpoint.setServiceBeans(beans);

        endpoint.setAddress("/rest/v1");  //TODO: change path after completion

        return endpoint.create();
    }

}
