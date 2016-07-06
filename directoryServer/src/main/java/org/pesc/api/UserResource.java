package org.pesc.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.rs.security.cors.CrossOriginResourceSharing;
import org.pesc.api.model.DirectoryUser;
import org.pesc.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jws.WebService;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by James Whetstone (jwhetstone@ccctechcenter.org) on 3/22/16.
 */
@Component
@WebService
@Path("/users")
@Api("/users")
public class UserResource {

    private static final Log log = LogFactory.getLog(UserResource.class);


    //Security is enforced using method level annotations on the service.
    @Autowired
    private UserService userService;


    private void checkOrganizationParameter(Integer organizationId) {
        if (organizationId == null) {
            throw new IllegalArgumentException("The organizationID parameter is mandatory.");
        }
    }

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @ApiOperation("Search users based on the search parameters.")
    public List<DirectoryUser> findUser(
            @QueryParam("id") @ApiParam("The identifier for the user.") Integer id,
            @QueryParam("name") @ApiParam("Case insensitive search for name and parts of name.") String name,
            @QueryParam("organizationId") @ApiParam(value = "The user's organization ID.", required = true) Integer organizationId
    ) {

        checkOrganizationParameter(organizationId);

        return userService.search(
                id,
                organizationId,
                name);
    }

    @GET
    @Path("/{id}")
    @ApiOperation("Return the user with the given id.")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<DirectoryUser> getUser(@PathParam("id") @ApiParam("The unique identifier for the user.") Integer id) {
        ArrayList<DirectoryUser> results = new ArrayList<DirectoryUser>();

        DirectoryUser user = userService.findById(id);

        if (user != null) {
            results.add(user);
        }

        return results;
    }

    @CrossOriginResourceSharing(allowAllOrigins = true, allowCredentials = true, maxAge = 1)
    @POST
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @ApiOperation("Create a user.")
    public DirectoryUser createUser(DirectoryUser user) {

        return userService.create(user);
    }

    @Path("/{id}")
    @PUT
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @ApiOperation("Update the user with the given ID.")
    public DirectoryUser saveUser(@PathParam("id") @ApiParam("The identifier for the user.") Integer id, DirectoryUser user) {
        return userService.update(user);
    }

    @Path("/{id}/password")
    @PUT
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @ApiOperation("Update the user with the given ID.")
    public Response updatePassword(@PathParam("id") @ApiParam("The identifier for the user.") Integer id, String password) {
        userService.updatePassword(id, password);
        return Response.ok().build();
    }

    @CrossOriginResourceSharing(allowAllOrigins = true, allowCredentials = true, maxAge = 1)
    @Path("/{id}")
    @DELETE
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @ApiOperation("Delete the user with the given ID.")
    public void removeUser(@PathParam("id") @ApiParam("The directory identifier for the user.") Integer id) {
        userService.delete(id);

    }


}
