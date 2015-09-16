package org.pesc.cds.webservice.service;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.rs.security.cors.CrossOriginResourceSharing;
import org.codehaus.jackson.annotate.JsonProperty;
import org.pesc.cds.webservice.service.request.EntityCodeSearch;
import org.pesc.edexchange.v1_0.EntityCode;
import org.pesc.edexchange.v1_0.dao.EntityCodesDao;
import org.springframework.beans.factory.annotation.Autowired;
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
@Api("/entityCodes")
@Path("/entityCodes")
@Component
public class EntityCodeRestController {
    private static final Log log = LogFactory.getLog(EntityCodeRestController.class);
    
    @Autowired
    EntityCodesDao entityCodesDao;

    /***********************************************************************************
     * These are for AJAX web services
     * The only data served out should be auxilary tables where the total row count
     * is under 300 or so. Bigger tables will need to go through the SOAP or REST
     * web services agreed upon by the PESC EdExchange group
     *
     ***********************************************************************************/

    //////////////////////////////////////////////
    // Entity Codes
    //////////////////////////////////////////////

    @CrossOriginResourceSharing(allowAllOrigins = true, allowCredentials = true, maxAge = 1)
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation("GET operation to search EntityCode with query parameters passed in URL.")
    public List<EntityCode> getEntityCodes(
            @QueryParam("id") Integer id,
            @QueryParam("code") Integer code,
            @QueryParam("description") String description,
            @QueryParam("createdTime") Long createdTime,
            @QueryParam("modifiedTime") Long modifiedTime
    ) {
        if(id!=null || code!=null || description !=null || createdTime!=null || modifiedTime!=null) {
            return entityCodesDao.search(id, code, description, createdTime, modifiedTime);
        } else {
            return entityCodesDao.all();
        }
    }

    @CrossOriginResourceSharing(allowAllOrigins = true, allowCredentials = true, maxAge = 1)
    @Path("/search/")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation("POST operation to search EntityCode using JSON object.  Empty fields will be ignored.")
    public List<EntityCode> getEntityCodes(@JsonProperty EntityCodeSearch entityCodeSearch) {
        return entityCodesDao.search(
                entityCodeSearch.getId(),
                entityCodeSearch.getCode(),
                entityCodeSearch.getDescription(),
                entityCodeSearch.getCreatedTime(),
                entityCodeSearch.getModifiedTime()
        );
    }

    @CrossOriginResourceSharing(allowAllOrigins = true, allowCredentials = true, maxAge = 1)
    @Path("/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation("The read (single) method to the EntityCodes REST API Returning a single EntityCode that has an " +
            "identifier matching the value in the request path or nothing if not found.")
    public EntityCode getEntityCode(@PathParam("id") @ApiParam("An integer used as the EntityCode identifier") Integer id) {
        return entityCodesDao.byId(id);
    }

    @CrossOriginResourceSharing(allowAllOrigins = true, allowCredentials = true, maxAge = 1)
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation("The create (single) method for the EntityCodes REST API")
    public EntityCode createEntityCode(@JsonProperty EntityCode entityCode) {
        // TODO validate document format object
        return entityCodesDao.save(entityCode);
    }

    @Path("/{id}")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation("The update (single) method for the EntityCodes REST API")
    public EntityCode saveEntityCode(@PathParam("id") Integer id, @JsonProperty EntityCode ec) {
        // TODO server-side validation
        return entityCodesDao.save(ec);
    }

    @CrossOriginResourceSharing(allowAllOrigins = true, allowCredentials = true, maxAge = 1)
    @Path("/{id}")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation("The delete (single) method for the EntityCodes REST API")
    public void removeEntityCode(@PathParam("id") Integer id) {
        EntityCode ec = entityCodesDao.byId(id);
        if(ec!=null) {
            entityCodesDao.remove(ec);
        }
    }



}
