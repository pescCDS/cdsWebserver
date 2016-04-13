package org.pesc.service.rs;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.rs.security.cors.CrossOriginResourceSharing;
import org.pesc.api.model.Organization;
import org.pesc.service.dao.OrganizationsDao;
import org.pesc.service.rs.request.OrganizationSearch;
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
@Api("/organizations")
@Path("/organizations")
@Component
@WebService
public class OrganizationRestController {
    private static final Log log = LogFactory.getLog(OrganizationRestController.class);
    
    @Autowired
    OrganizationsDao organizationsDao;

    /***********************************************************************************
     * These are for AJAX web services
     * The only data served out should be auxilary tables where the total row count
     * is under 300 or so. Bigger tables will need to go through the SOAP or REST
     * web services agreed upon by the PESC EdExchange group
     *
     ***********************************************************************************/

    //////////////////////////////////////////////
    // Organizations
    //////////////////////////////////////////////

    @CrossOriginResourceSharing(allowAllOrigins = true, allowCredentials = true, maxAge = 1)
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation("GET operation to search Organization with query parameters passed in URL.")
    public List<Organization> getOrganizations(
            @QueryParam("directoryId") Integer directoryId,
            @QueryParam("organizationId") String organizationId,
            @QueryParam("organizationIdType") String organizationIdType,
            @QueryParam("organizationName") String organizationName,
            @QueryParam("organizationSubcode") String organizationSubcode,
            @QueryParam("entityId") Integer entityId,
            @QueryParam("organizationEin") String organizationEin,
            @QueryParam("createdTime") Long createdTime,
            @QueryParam("modifiedTime") Long modifiedTime
    ) {
        if(directoryId!=null || organizationId!=null ||
                organizationIdType!=null || organizationName!=null ||
                organizationSubcode!=null || entityId!=null ||
                organizationEin!=null || createdTime!=null ||
                modifiedTime!=null) {
            return organizationsDao.search(
                    directoryId,
                    organizationId,
                    organizationIdType,
                    organizationName,
                    organizationSubcode,
                    entityId,
                    organizationEin,
                    createdTime,
                    modifiedTime
            );
        } else {
            return organizationsDao.all();
        }
    }

    @CrossOriginResourceSharing(allowAllOrigins = true, allowCredentials = true, maxAge = 1)
    @Path("/search")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation("POST operation to search Organization using JSON object.  Empty fields will be ignored.")
    public List<Organization> searchOrganizations(
            OrganizationSearch organizationSearch) {
        return ((OrganizationsDao)organizationsDao).search(
                organizationSearch.getDirectoryId(),
                organizationSearch.getOrganizationId(),
                organizationSearch.getOrganizationIdType(),
                organizationSearch.getOrganizationName(),
                organizationSearch.getOrganizationSubcode(),
                organizationSearch.getEntityId(),
                organizationSearch.getOrganizationEin(),
                organizationSearch.getCreatedTime(),
                organizationSearch.getModifiedTime()
        );
    }

    @CrossOriginResourceSharing(allowAllOrigins = true, allowCredentials = true, maxAge = 1)
    @Path("/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation("The read (single) method to the Organizations REST API Returning a single Organization that has an \" +\n" +
            "            \"identifier matching the value in the request path or nothing if not found.")
    public Organization getOrganization(@PathParam("id") @ApiParam("An integer used as the EntityCode identifier") Integer id) {
        return organizationsDao.byId(id);
    }

    @CrossOriginResourceSharing(allowAllOrigins = true, allowCredentials = true, maxAge = 1)
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation("The create (single) method for the Organizations REST API")
    public Organization createOrganization(Organization org) {
        // TODO validate organization object
        //save organization object to persistence layer
        return organizationsDao.save(org);
    }

    @Path("/{directoryId}")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation("The update (single) method for the Organizations REST API")
    public Organization saveOrganization(@PathParam("directoryId") Integer id,
                                         Organization org) {
        return organizationsDao.save(org);
    }

    @CrossOriginResourceSharing(allowAllOrigins = true, allowCredentials = true, maxAge = 1)
    @Path("/{directoryId}")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation("The delete (single) method for the Organizations REST API")
    public void removeOrganization(@PathParam("directoryId") Integer directoryId) {
        Organization org = organizationsDao.byId(directoryId);
        if(org!=null) {
            organizationsDao.remove(org);
        }
    }

}
