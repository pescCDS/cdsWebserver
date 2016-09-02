package org.pesc.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.rs.security.cors.CrossOriginResourceSharing;
import org.pesc.api.exception.ApiException;
import org.pesc.api.model.Department;
import org.pesc.service.DepartmentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jws.WebService;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by James Whetstone (jwhetstone@ccctechcenter.org) on 3/22/16.
 */
@Component
@WebService
@Path("/departments")
@Api("/departments")
public class DepartmentsResource {

    private static final Log log = LogFactory.getLog(DepartmentsResource.class);

    @Autowired
    private DepartmentsService departmentsService;

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @ApiOperation("Get a list of valid document formats supported by Ed Exchange endpoints.")
    public List<Department> getDepartments(){

        return (List<Department>) departmentsService.getDepartments();
    }


    @CrossOriginResourceSharing(allowAllOrigins = true, allowCredentials = true, maxAge = 1)
    @POST
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @ApiOperation("Create a delivery method.")
    public Department create(Department department) {

        return departmentsService.create(department);
    }

    @CrossOriginResourceSharing(allowAllOrigins = true, allowCredentials = true, maxAge = 1)
    @PUT
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @ApiOperation("Update a department.")
    @Path("/{id}")
    public Department update(@PathParam("id") @ApiParam("The resource identifier.") Integer id, Department department) {

        return departmentsService.create(department);
    }


    @CrossOriginResourceSharing(allowAllOrigins = true, allowCredentials = true, maxAge = 1)
    @Path("/{id}")
    @DELETE
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @ApiOperation("Delete the contact with the given ID.")
    public void removeContact(@PathParam("id") @ApiParam("The resource identifier.") Integer id) {
        try {
            departmentsService.delete(id);
        }
        catch (Exception e) {
            throw new ApiException(new IllegalArgumentException("The resource couldn't be deleted.  Is it in use by an endpoint?"), Response.Status.BAD_REQUEST, "/delivery-methods/" + id);
        }

    }
}
