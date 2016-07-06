package org.pesc.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pesc.api.model.Department;
import org.pesc.service.DepartmentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jws.WebService;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
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
    private DepartmentsService documentFormatService;

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @ApiOperation("Get a list of valid document formats supported by Ed Exchange endpoints.")
    public List<Department> getDepartments(){

        return (List<Department>)documentFormatService.getDepartments();
    }
}
