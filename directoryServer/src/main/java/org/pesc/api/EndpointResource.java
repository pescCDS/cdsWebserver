package org.pesc.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.rs.security.cors.CrossOriginResourceSharing;
import org.pesc.api.model.Endpoint;
import org.pesc.service.EndpointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jws.WebService;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by james on 3/22/16.
 */
@Component
@WebService
@Path("/endpoints")
@Api("/endpoints")
public class EndpointResource {

    private static final Log log = LogFactory.getLog(EndpointResource.class);


    //Security is enforced using method level annotations on the service.
    @Autowired
    private EndpointService endpointService;


    private void validateParameters(List<Integer> organizationIdList) {
        if ((organizationIdList == null || organizationIdList.size() == 0 )) {
            throw new IllegalArgumentException("At least one organization ID or school code parameter is mandatory.");
        }

    }

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @ApiOperation("Search endpoints based on the search parameters.")
    public List<Endpoint> findEndpoint(
            @QueryParam("documentFormat") @ApiParam(value = "Must be one of the supported documents types (case insensitive), e.g 'text', 'pdf', 'xml', 'pescxml'. See the document-formats API for more.", required = true) String documentFormat,
            @QueryParam("id") @ApiParam("The identifier for the endpoint.") Integer id,
            @QueryParam("hostingOrganizationId") @ApiParam("The organization ID of the member that hosts the endpoint.") Integer hostingOrganizationId,
            @QueryParam("organizationId") @ApiParam(value = "A list of organization ID that use the endpoint.") List<Integer> organizationIdList
    ) {

        validateParameters(organizationIdList);

        return endpointService.search(
                documentFormat,
                id,
                hostingOrganizationId,
                organizationIdList);
    }

    @GET
    @Path("/{id}")
    @ApiOperation("Return the endpoint with the given id.")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Endpoint> getEndpoint(@PathParam("id") @ApiParam("The unique identifier for the endpoint.") Integer id) {
        ArrayList<Endpoint> results = new ArrayList<Endpoint>();

        Endpoint endpoint = endpointService.findById(id);

        if (endpoint != null) {
            results.add(endpoint);
        }

        return results;
    }

    @CrossOriginResourceSharing(allowAllOrigins = true, allowCredentials = true, maxAge = 1)
    @POST
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @ApiOperation("Create a endpoint.")
    public Endpoint createEndpoint(Endpoint endpoint) {

        return endpointService.create(endpoint);
    }

    @Path("/{id}")
    @PUT
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @ApiOperation("Update the endpoint with the given ID.")
    public Endpoint saveEndpoint(@PathParam("id") @ApiParam("The identifier for the endpoint.") Integer id, Endpoint endpoint) {
        return endpointService.update(endpoint);
    }

    @CrossOriginResourceSharing(allowAllOrigins = true, allowCredentials = true, maxAge = 1)
    @Path("/{id}")
    @DELETE
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @ApiOperation("Delete the endpoint with the given ID.")
    public void removeEndpoint(@PathParam("id") @ApiParam("The identifier for the endpoint.") Integer id) {
        endpointService.delete(id);

    }

}
