package org.pesc.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.rs.security.cors.CrossOriginResourceSharing;
import org.pesc.api.model.User;
import org.pesc.api.repository.UserRepository;
import org.pesc.api.repository.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jws.WebService;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by james on 3/22/16.
 */
@Component
@WebService
@Path("/users")
@Api("/users")
public class UserResource {

    private static final Log log = LogFactory.getLog(UserResource.class);

    @Autowired
    private UserRepository userRepository;

    //The service wraps a UserRepository, but not all of the repository methods are exposed,
    //so both the repository and the service are wired here for convenience.
    //TODO: expose all repository methods using the service or refactory the respository implementation
    //to allow for custom query logic.
    @Autowired
    private UserService userService;


    private void checkOrganizationParameter(Integer organizationId) {
        if (organizationId == null) {
            throw new WebApplicationException(
                    Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
                            .entity("The organizationID parameter is mandatory.")
                            .build()
            );
        }
    }

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @ApiOperation("Search users based on the search parameters.")
    public List<User> findUser(
            @QueryParam("id") @ApiParam("The identifier for the user.") Integer id,
            @QueryParam("name") @ApiParam("A code such as CEEB code that identifies the user.") String name,
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
    public List<User> getUser(@PathParam("id") @ApiParam("The directory identifier for the user.") Integer id) {
        ArrayList<User> results = new ArrayList<User>();

        User user = userRepository.findOne(id);

        if (user != null) {
            //TODO: verify the calling user has access rights to view this user.
            results.add(user);
        }

        return results;
    }

    @CrossOriginResourceSharing(allowAllOrigins = true, allowCredentials = true, maxAge = 1)
    @POST
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @ApiOperation("Create a user.")
    public User createUser(User user) {
        // TODO validate user object and that the calling user has access rights to create a user account
        // for the organization identified by user.organizationId.
        return userRepository.save(user);
    }

    @Path("/{id}")
    @PUT
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @ApiOperation("Update the user with the given ID.")
    public User saveUser(@PathParam("id") @ApiParam("The identifier for the user.") Integer id, User user) {
        // TODO verify that the calling user has access rights to update this user account
        return userRepository.save(user);
    }

    @CrossOriginResourceSharing(allowAllOrigins = true, allowCredentials = true, maxAge = 1)
    @Path("/{id}")
    @DELETE
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @ApiOperation("Delete the user with the given ID.")
    public void removeUser(@PathParam("id") @ApiParam("The directory identifier for the user.") Integer id) {
        // TODO validate user object and that the calling user has access rights to update this user account
        userService.delete(id);

    }


}
