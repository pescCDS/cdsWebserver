package org.pesc.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pesc.api.model.DeliveryMethod;
import org.pesc.service.DeliveryMethodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jws.WebService;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by James Whetstone (jwhetstone@ccctechcenter.org) on 4/6/16.
 */
@Component
@WebService
@Path("/delivery-methods")
@Api("/delivery-methods")
public class DeliveryMethodsResource {


    private static final Log log = LogFactory.getLog(DeliveryMethodsResource.class);

    @Autowired
    private DeliveryMethodsService deliveryMethodsService;

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @ApiOperation("Get a list of valid document formats supported by Ed Exchange endpoints.")
    public List<DeliveryMethod> getDocumentFormats(){

        return (List<DeliveryMethod>)deliveryMethodsService.getDeliveryMethods();
    }


}
