package org.pesc.service;

import org.pesc.web.model.Organization;

import javax.jws.WebService;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

/**
 * Created by james on 2/25/16.
 */
@Path("/organization")
@WebService
public class OrganizationResource {


    // The Java method will process HTTP GET requests
    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Organization getOrganization() {

        Organization org = new Organization();

        org.setName("Sacramento City College");
        org.setId("2");

        return org;
    }


}
