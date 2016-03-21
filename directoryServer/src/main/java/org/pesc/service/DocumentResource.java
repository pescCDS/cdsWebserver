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
@Path("/document")
@WebService
public class DocumentResource {

    // The Java method will process HTTP GET requests
    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Organization sayHello() {

        Organization org = new Organization();

        org.setName("Butte College");
        org.setId("1");

        return org;
    }


}

