package org.pesc.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.rs.security.cors.CrossOriginResourceSharing;
import org.pesc.api.model.Contact;
import org.pesc.api.model.DirectoryUser;
import org.pesc.service.ContactService;
import org.pesc.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jws.WebService;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by James Whetstone (jwhetstone@ccctechcenter.org) on 3/22/16.
 */
@Component
@WebService
@Path("/contacts")
@Api("/contacts")
public class ContactResource {

    private static final Log log = LogFactory.getLog(ContactResource.class);


    //Security is enforced using method level annotations on the service.
    @Autowired
    private ContactService contactService;


    private void checkOrganizationParameter(Integer organizationId) {
        if (organizationId == null) {
            throw new IllegalArgumentException("The org_id parameter is mandatory.");
        }
    }


    @GET
    @ApiOperation("Return the user with the given id.")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Contact> getContacts(@QueryParam("org_id") @ApiParam("The organization id for the contacts.") Integer id) {

        checkOrganizationParameter(id);
        return contactService.search(id);
    }

    @CrossOriginResourceSharing(allowAllOrigins = true, allowCredentials = true, maxAge = 1)
    @POST
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @ApiOperation("Create a contact.")
    public Contact createContact(Contact contact) {

        return contactService.create(contact);
    }

    @Path("/{id}")
    @PUT
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @ApiOperation("Update the contact with the given ID.")
    public Contact saveContact(@PathParam("id") @ApiParam("The identifier for the contact.") Integer id, Contact contact) {
        return contactService.update(contact);
    }

    @CrossOriginResourceSharing(allowAllOrigins = true, allowCredentials = true, maxAge = 1)
    @Path("/{id}")
    @DELETE
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @ApiOperation("Delete the contact with the given ID.")
    public void removeContact(@PathParam("id") @ApiParam("The unique identifier for the contact.") Integer id) {
        Contact contact = contactService.findById(id);

        contactService.delete(contact);

    }


}
