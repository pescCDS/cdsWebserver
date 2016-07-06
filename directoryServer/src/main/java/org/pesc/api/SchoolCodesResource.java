package org.pesc.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.rs.security.cors.CrossOriginResourceSharing;
import org.pesc.api.exception.ApiException;
import org.pesc.api.model.SchoolCode;
import org.pesc.service.SchoolCodesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jws.WebService;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by James Whetstone (jwhetstone@ccctechcenter.org) on 3/22/16.
 */
@Component
@WebService
@Path("/school-codes")
@Api("/school-codes")
public class SchoolCodesResource {

    private static final Log log = LogFactory.getLog(SchoolCodesResource.class);

    @Autowired
    private SchoolCodesService schoolCodesService;


    @CrossOriginResourceSharing(allowAllOrigins = true, allowCredentials = true, maxAge = 1)
    @POST
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @ApiOperation("Create an school code for an organization.")
    public SchoolCode createSchoolCode(SchoolCode schoolCode) {


        try {
            schoolCode = schoolCodesService.create(schoolCode);
        }
        catch (Exception e) {
            throw new ApiException(e, Response.Status.BAD_REQUEST, "/school-codes");
        }

        return schoolCode;
    }

    @Path("/{id}")
    @PUT
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @ApiOperation("Update the school code with the given ID.")
    public SchoolCode updateSchoolCode(@PathParam("id") @ApiParam("The unique id for the school code.") Integer id, SchoolCode schoolCode) {

        try {
            schoolCode = schoolCodesService.update(schoolCode);
        }
        catch (Exception e) {
            throw new ApiException(e, Response.Status.BAD_REQUEST, "/school-codes/" + id.toString());
        }

        return schoolCode;

    }

    @CrossOriginResourceSharing(allowAllOrigins = true, allowCredentials = true, maxAge = 1)
    @Path("/{id}")
    @DELETE
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @ApiOperation("Delete the organization with the given ID.")
    public void removeSchoolCode(@PathParam("id") @ApiParam("The identifier for the school code.") Integer id) {

        SchoolCode schoolCode = schoolCodesService.findById(id);

        schoolCodesService.delete(schoolCode);

    }


}
