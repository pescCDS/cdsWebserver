package org.pesc.config;

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.jersey.listing.ApiListingResourceJSON;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.pesc.service.rs.*;
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
public class RESTConfig {

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
