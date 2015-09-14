package org.pesc.cds.webservice.service;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.rs.security.cors.CrossOriginResourceSharing;
import org.codehaus.jackson.annotate.JsonProperty;
import org.pesc.cds.webservice.service.request.DeliveryMethodSearch;
import org.pesc.edexchange.v1_0.DeliveryMethod;
import org.pesc.edexchange.v1_0.dao.DeliveryMethodsDao;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
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
public class DeliveryMethodRestController {
    private static final Log log = LogFactory.getLog(DeliveryMethodRestController.class);

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
            return ((DeliveryMethodsDao)DatasourceManagerUtil
                    .getDeliveryMethods()).search(id, method);
        } else {
            return DatasourceManagerUtil.getDeliveryMethods().all();
        }
    }

    @CrossOriginResourceSharing(allowAllOrigins = true, allowCredentials = true, maxAge = 1)
    @Path("/search")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation("POST operation to search DeliveryMethod using JSON object.  Empty fields will be ignored.")
    public List<DeliveryMethod> searchDeliveryMethods(
            @JsonProperty DeliveryMethodSearch deliveryMethodSearch) {
        return ((DeliveryMethodsDao)DatasourceManagerUtil
                .getDeliveryMethods()).search(
                deliveryMethodSearch.getId(),
                deliveryMethodSearch.getMethod());
    }

    @CrossOriginResourceSharing(allowAllOrigins = true, allowCredentials = true, maxAge = 1)
    @Path("/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation("The read (single) method to the DeliveryMethods REST API Returning a single DeliveryMethod that has an " +
            "identifier matching the value in the request path or nothing if not found.")
    public DeliveryMethod getDeliveryMethod(@PathParam("id") @ApiParam("An integer used as the DeliveryMethod identifier") Integer id) {
        return DatasourceManagerUtil.getDeliveryMethods().byId(id);
    }

    @CrossOriginResourceSharing(allowAllOrigins = true, allowCredentials = true, maxAge = 1)
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation("The create (single) method for the DeliveryMethods REST API.")
    public DeliveryMethod createDeliveryMethod(@JsonProperty DeliveryMethod method) {
        return DatasourceManagerUtil.getDeliveryMethods().save(method);
    }

    @CrossOriginResourceSharing(allowAllOrigins = true, allowCredentials = true, maxAge = 1)
    @Path("/{id}")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation("The update (single) method for the DeliveryMethods REST API.")
    public DeliveryMethod saveDeliveryMethod(@PathParam("id") Integer id, @JsonProperty DeliveryMethod method) {
        return DatasourceManagerUtil.getDeliveryMethods().save(method);
    }

    @CrossOriginResourceSharing(allowAllOrigins = true, allowCredentials = true, maxAge = 1)
    @Path("/{id}")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation("The delete (single) method for the DeliveryMethods REST API.")
    public void removeDeliveryMethod(@PathParam("id") Integer id) {
        DeliveryMethod method = DatasourceManagerUtil.getDeliveryMethods().byId(id);
        if(method!=null) {
            DatasourceManagerUtil.getDeliveryMethods().remove(method);
        }
    }


}
