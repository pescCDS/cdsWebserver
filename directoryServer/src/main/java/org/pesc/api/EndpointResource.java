package org.pesc.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.rs.security.cors.CrossOriginResourceSharing;
import org.pesc.api.model.Endpoint;
import org.pesc.api.repository.EndpointService;
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


    private void checkOrganizationParameter(Integer organizationId) {
        if (organizationId == null) {
            throw new IllegalArgumentException("The organizationID parameter is mandatory.");
        }
    }

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @ApiOperation("Search endpoints based on the search parameters.")
    public List<Endpoint> findEndpoint(
            @QueryParam("id") @ApiParam("The identifier for the endpoint.") Integer id,
            @QueryParam("organizationId") @ApiParam(value = "The endpoint's organization ID.", required = true) Integer organizationId
    ) {

        checkOrganizationParameter(organizationId);

        return endpointService.search(
                id,
                organizationId);
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
