package org.pesc.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pesc.api.model.Organization;
import org.pesc.api.repository.OrganizationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseBody;

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
@Path("/organizations")
@Api("/organizations")
public class OrganizationsResource {

    private static final Log log = LogFactory.getLog(OrganizationsResource.class);

    @Autowired
    private OrganizationRepository organizationRepository;


    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @ApiOperation("Get a list of organizations based on the optional search parameters.  All organizations are returned" +
            " when no search criteria are provided.")
    public  @ResponseBody
    List<Organization> findOrganization(@QueryParam("name") String name) {

        if (name != null) {
            return organizationRepository.findByName(name);
        }

        return (List<Organization>)organizationRepository.findAll();

    }

    @GET
    @Path("/{id}")
    @ApiOperation("Return the organization with the given id.")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Organization> getOrganization(@PathParam("id") @ApiParam("An integer used as the EntityCode identifier") Integer id) {
        ArrayList<Organization> results = new ArrayList<Organization>();
        Organization organization = organizationRepository.findOne(id);

        if (organization != null) {
            results.add(organization);
        }

        return results;
    }

}
