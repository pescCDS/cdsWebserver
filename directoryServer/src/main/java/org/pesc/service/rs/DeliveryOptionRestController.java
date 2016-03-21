package org.pesc.service.rs;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.rs.security.cors.CrossOriginResourceSharing;
import org.pesc.edexchange.v1_0.DeliveryOption;
import org.pesc.service.dao.DeliveryOptionsDao;
import org.pesc.service.rs.request.DeliveryOptionSearch;
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
@Path("/deliveryOptions")
@Api("/deliveryOptions")
@Component
@WebService
public class DeliveryOptionRestController {
    private static final Log log = LogFactory.getLog(DeliveryOptionRestController.class);

    @Autowired
    DeliveryOptionsDao deliveryOptionsDao;

    /***********************************************************************************
     * These are for AJAX web services
     * The only data served out should be auxilary tables where the total row count
     * is under 300 or so. Bigger tables will need to go through the SOAP or REST
     * web services agreed upon by the PESC EdExchange group
     *
     ***********************************************************************************/

    //////////////////////////////////////////////
    // Delivery Options
    //////////////////////////////////////////////

    @CrossOriginResourceSharing(allowAllOrigins = true, allowCredentials = true, maxAge = 1)
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation("GET operation to search DeliveryOption with query parameters passed in URL.")
    public List<DeliveryOption> searchDeliveryOptions(
            @QueryParam("id") Integer id,
            @QueryParam("memberId") Integer memberId,
            @QueryParam("formatId") Integer formatId,
            @QueryParam("webserviceUrl") String webserviceUrl,
            @QueryParam("deliveryMethodId") Integer deliveryMethodId,
            @QueryParam("deliveryConfirm") Boolean deliveryConfirm,
            @QueryParam("error") Boolean error,
            @QueryParam("operationalStatus") String operationalStatus
    ) {
        if(id!=null || memberId!=null || formatId!=null ||
                webserviceUrl!=null || deliveryMethodId!=null ||
                deliveryConfirm!=null || error!=null ||
                operationalStatus!=null) {
            return deliveryOptionsDao.search(
                    id,
                    memberId,
                    formatId,
                    webserviceUrl,
                    deliveryMethodId,
                    deliveryConfirm,
                    error,
                    operationalStatus);
        } else {
            return deliveryOptionsDao.all();
        }

    }

    @CrossOriginResourceSharing(allowAllOrigins = true, allowCredentials = true, maxAge = 1)
    @Path("/search")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation("POST operation to search DeliveryOption using JSON object.  Empty fields will be ignored.")
    public List<DeliveryOption> searchDeliveryOptionsPost(DeliveryOptionSearch deliveryOptionSearch) {
        return deliveryOptionsDao.search(
                deliveryOptionSearch.getId(),
                deliveryOptionSearch.getMemberId(),
                deliveryOptionSearch.getFormatId(),
                deliveryOptionSearch.getWebserviceUrl(),
                deliveryOptionSearch.getDeliveryMethodId(),
                deliveryOptionSearch.getDeliveryConfirm(),
                deliveryOptionSearch.getError(),
                deliveryOptionSearch.getOperationalStatus()
        );
    }

    @CrossOriginResourceSharing(allowAllOrigins = true, allowCredentials = true, maxAge = 1)
    @Path("/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation("The read (single) method to the DeliveryOptions REST API Returning a single DeliveryOption that has an " +
            "identifier matching the value in the request path or nothing if not found.")
    public DeliveryOption getDeliveryOptions(@PathParam("id") @ApiParam("An integer used as the DeliveryOption identifier") Integer id) {
        return deliveryOptionsDao.byId(id);
    }

    @CrossOriginResourceSharing(allowAllOrigins = true, allowCredentials = true, maxAge = 1)
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation("The create (single) method for the DeliveryOptions REST API.")
    public DeliveryOption createDeliveryOptions(DeliveryOption opt) {
        return deliveryOptionsDao.save(opt);
    }

    @CrossOriginResourceSharing(allowAllOrigins = true, allowCredentials = true, maxAge = 1)
    @Path("/{id}")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation("The update (single) method for the DeliveryOptions REST API.")
    public DeliveryOption saveDeliveryOption(@PathParam("id") Integer id,
                                             DeliveryOption opt) {
        return deliveryOptionsDao.save(opt);
    }

    @CrossOriginResourceSharing(allowAllOrigins = true, allowCredentials = true, maxAge = 1)
    @Path("/{id}")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation("The delete (single) method for the DeliveryOptions REST API.")
    public void removeDeliveryOptions(@PathParam("id") Integer id) {
        DeliveryOption opt = deliveryOptionsDao.byId(id);
        if(opt!=null) {
            deliveryOptionsDao.remove(opt);
        }
    }



}
