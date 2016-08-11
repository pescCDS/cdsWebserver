package org.pesc.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.rs.security.cors.CrossOriginResourceSharing;
import org.pesc.api.exception.ApiException;
import org.pesc.api.model.DeliveryMethod;
import org.pesc.service.DeliveryMethodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jws.WebService;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
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

    @CrossOriginResourceSharing(allowAllOrigins = true, allowCredentials = true, maxAge = 1)
    @POST
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @ApiOperation("Create a delivery method.")
    public DeliveryMethod create(DeliveryMethod deliveryMethod) {

        return deliveryMethodsService.create(deliveryMethod);
    }

    @CrossOriginResourceSharing(allowAllOrigins = true, allowCredentials = true, maxAge = 1)
    @PUT
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @ApiOperation("Update a delivery method.")
    @Path("/{id}")
    public DeliveryMethod update(@PathParam("id") @ApiParam("The resource identifier.") Integer id, DeliveryMethod deliveryMethod) {

        return deliveryMethodsService.create(deliveryMethod);
    }


    @CrossOriginResourceSharing(allowAllOrigins = true, allowCredentials = true, maxAge = 1)
    @Path("/{id}")
    @DELETE
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @ApiOperation("Delete the contact with the given ID.")
    public void removeContact(@PathParam("id") @ApiParam("The resource identifier.") Integer id) {
        try {
            deliveryMethodsService.delete(id);
        }
        catch (Exception e) {
            throw new ApiException(new IllegalArgumentException("The resource couldn't be deleted.  Is it in use by an endpoint?"), Response.Status.BAD_REQUEST, "/delivery-methods/" + id);
        }

    }


}
