package org.pesc.cds.webservice.service;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.rs.security.cors.CrossOriginResourceSharing;
import org.codehaus.jackson.annotate.JsonProperty;
import org.pesc.cds.webservice.service.request.OrganizationSearch;
import org.pesc.edexchange.v1_0.Organization;
import org.pesc.edexchange.v1_0.dao.OrganizationsDao;
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
@Api("/organizations")
@Path("/organizations")
@Component
public class OrganizationRestController {

    private static final Log log = LogFactory.getLog(OrganizationRestController.class);

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
            return ((OrganizationsDao)DatasourceManagerUtil.getOrganizations()).search(
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
            return DatasourceManagerUtil.getOrganizations().all();
        }
    }

    @CrossOriginResourceSharing(allowAllOrigins = true, allowCredentials = true, maxAge = 1)
    @Path("/search")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation("POST operation to search Organization using JSON object.  Empty fields will be ignored.")
    public List<Organization> searchOrganizations(
            @JsonProperty OrganizationSearch organizationSearch) {
        return ((OrganizationsDao)DatasourceManagerUtil.getOrganizations()).search(
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
        return DatasourceManagerUtil.getOrganizations().byId(id);
    }

    @CrossOriginResourceSharing(allowAllOrigins = true, allowCredentials = true, maxAge = 1)
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation("The create (single) method for the Organizations REST API")
    public Organization createOrganization(@JsonProperty Organization org) {
        // TODO validate organization object
        //save organization object to persistence layer
        return DatasourceManagerUtil.getOrganizations().save(org);
    }

    @Path("/{directoryId}")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation("The update (single) method for the Organizations REST API")
    public Organization saveOrganization(@PathParam("directoryId") Integer id,
                                         @JsonProperty Organization org) {
        return DatasourceManagerUtil.getOrganizations().save(org);
    }

    @CrossOriginResourceSharing(allowAllOrigins = true, allowCredentials = true, maxAge = 1)
    @Path("/{directoryId}")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation("The delete (single) method for the Organizations REST API")
    public void removeOrganization(@PathParam("directoryId") Integer directoryId) {
        Organization org = DatasourceManagerUtil.getOrganizations().byId(directoryId);
        if(org!=null) {
            DatasourceManagerUtil.getOrganizations().remove(org);
        }
    }

}
