package org.pesc.service;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.Path;

/**
 * Created by james on 2/25/16.
 */
@Path("/document")
public class DocumentService {

    // The Java method will process HTTP GET requests
    @GET
    // The Java method will produce content identified by the MIME Media
    // type "text/plain"
    @Produces("text/plain")
    public String sayHello() {
        // Return some cliched textual content
        return "Hello Document";
    }


}
