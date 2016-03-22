package org.pesc.api;

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

/**
 * Top level annotations : "Component" so that Spring can inject Autowired objects.  "WebService" to expose
 * the service using SOAP.
 * Created by james on 2/23/16.
 */
@Component
@WebService
public class OrganizationResource {

    private static final Log log = LogFactory.getLog(OrganizationResource.class);

    @Autowired
    private OrganizationRepository organizationRepository;


    @GET
    @Path(value="/organization")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public @ResponseBody
    Organization getOrganization(@QueryParam("name") String name) {
        return organizationRepository.findByName(name);
    }

    @GET
    @Path(value="/organization/{id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public @ResponseBody
    Organization getOrganization(@PathParam("id") Integer id) {
        return organizationRepository.findOne(id);
    }

    @GET
    @Path(value="/organizations")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public @ResponseBody
    Iterable<Organization> getOrganizations() {
        return organizationRepository.findAll();
    }
}
