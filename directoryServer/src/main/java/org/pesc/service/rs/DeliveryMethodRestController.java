package org.pesc.service.rs;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.rs.security.cors.CrossOriginResourceSharing;
import org.pesc.edexchange.v1_0.DeliveryMethod;
import org.pesc.service.dao.DeliveryMethodsDao;
import org.pesc.service.rs.request.DeliveryMethodSearch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jws.WebService;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by rgehbauer on 9/11/15.
 */
@CrossOriginResourceSharing(
        allowAllOrigins = true,
        allowCredentials = true,
        allowOrigins = {"http://pesc.cccnext.net:8080", "http://local.pesc.dev:8080"},
        maxAge = 1
)
@Api("/deliveryMethods")
@Path("/deliveryMethods")
@Component
@WebService
public class DeliveryMethodRestController {
    private static final Log log = LogFactory.getLog(DeliveryMethodRestController.class);

    @Autowired
    DeliveryMethodsDao deliveryMethodsDao;

    /***********************************************************************************
     * These are for AJAX web services
     * The only data served out should be auxilary tables where the total row count
     * is under 300 or so. Bigger tables will need to go through the SOAP or REST
     * web services agreed upon by the PESC EdExchange group
     *
     ***********************************************************************************/

    //////////////////////////////////////////////
    // Delivery Methods
    //////////////////////////////////////////////

    @CrossOriginResourceSharing(allowAllOrigins = true, allowCredentials = true, maxAge = 1)
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation("GET operation to search DeliveryMethod with query parameters passed in URL.")
    public List<DeliveryMethod> searchDeliveryMethods(
            @QueryParam("id") Integer id,
            @QueryParam("method") String method) {
        if(id!=null || method!=null) {
            return deliveryMethodsDao.search(id, method);
        } else {
            return deliveryMethodsDao.all();
        }
    }

    @CrossOriginResourceSharing(allowAllOrigins = true, allowCredentials = true, maxAge = 1)
    @Path("/search")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation("POST operation to search DeliveryMethod using JSON object.  Empty fields will be ignored.")
    public List<DeliveryMethod> searchDeliveryMethodsByObject(
            DeliveryMethodSearch deliveryMethodSearch) {
        return deliveryMethodsDao.search(deliveryMethodSearch.getId(), deliveryMethodSearch.getMethod());
    }

    @CrossOriginResourceSharing(allowAllOrigins = true, allowCredentials = true, maxAge = 1)
    @Path("/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation("The read (single) method to the DeliveryMethods REST API Returning a single DeliveryMethod that has an " +
            "identifier matching the value in the request path or nothing if not found.")
    public DeliveryMethod getDeliveryMethod(@PathParam("id") @ApiParam("An integer used as the DeliveryMethod identifier") Integer id) {
        return deliveryMethodsDao.byId(id);
    }

    @CrossOriginResourceSharing(allowAllOrigins = true, allowCredentials = true, maxAge = 1)
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation("The create (single) method for the DeliveryMethods REST API.")
    public DeliveryMethod createDeliveryMethod(DeliveryMethod method) {
        return deliveryMethodsDao.save(method);
    }

    @CrossOriginResourceSharing(allowAllOrigins = true, allowCredentials = true, maxAge = 1)
    @Path("/{id}")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation("The update (single) method for the DeliveryMethods REST API.")
    public DeliveryMethod saveDeliveryMethod(@PathParam("id") Integer id, DeliveryMethod method) {
        return deliveryMethodsDao.save(method);
    }

    @CrossOriginResourceSharing(allowAllOrigins = true, allowCredentials = true, maxAge = 1)
    @Path("/{id}")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation("The delete (single) method for the DeliveryMethods REST API.")
    public void removeDeliveryMethod(@PathParam("id") Integer id) {
        DeliveryMethod method = deliveryMethodsDao.byId(id);
        if(method!=null) {
            deliveryMethodsDao.remove(method);
        }
    }


}
