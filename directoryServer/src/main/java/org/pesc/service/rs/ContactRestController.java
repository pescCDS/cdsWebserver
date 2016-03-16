package org.pesc.service.rs;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.rs.security.cors.CrossOriginResourceSharing;
import org.pesc.service.rs.request.OrganizationContactSearch;
import org.pesc.edexchange.v1_0.OrganizationContact;
import org.pesc.service.dao.ContactsDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by rgehbauer on 9/11/15.
 */
@CrossOriginResourceSharing(
        allowAllOrigins = true,
        allowCredentials = true,
        allowOrigins = {"http://pesc.cccnext.net:8080", "http://local.pesc.dev:8080"},
        maxAge = 1
)
@Api("/contacts")
@Path("/contacts")
@Component
public class ContactRestController {

    private static final Log log = LogFactory.getLog(ContactRestController.class);

    @Autowired
    ContactsDao contactsDao;

    /***********************************************************************************
     * These are for AJAX web services
     * The only data served out should be auxilary tables where the total row count
     * is under 300 or so. Bigger tables will need to go through the SOAP or REST
     * web services agreed upon by the PESC EdExchange group
     *
     ***********************************************************************************/

    //////////////////////////////////////////////
    // OrganizationContact
    //////////////////////////////////////////////

    @CrossOriginResourceSharing(allowAllOrigins = true, allowCredentials = true, maxAge = 1)
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation("This is the GET version of /contacts/search, taking parameters as query parameters in the URL.")
    public List<OrganizationContact> searchContactsGet(
            @QueryParam("city") String city,
            @QueryParam("contactId") Integer contactId,
            @QueryParam("contactName") String contactName,
            @QueryParam("contactTitle") String contactTitle,
            @QueryParam("contactType") String contactType,
            @QueryParam("country") String country,
            @QueryParam("createdTime") Long createdTime,
            @QueryParam("directoryId") Integer directoryId,
            @QueryParam("email") String email,
            @QueryParam("modifiedTime") Long modifiedTime,
            @QueryParam("phone1") String phone1,
            @QueryParam("phone2") String phone2,
            @QueryParam("state") String state,
            @QueryParam("streetAddress1") String streetAddress1,
            @QueryParam("streetAddress2") String streetAddress2,
            @QueryParam("streetAddress3") String streetAddress3,
            @QueryParam("streetAddress4") String streetAddress4,
            @QueryParam("zip") String zip
    ) {
        if(city!=null || contactId!=null || contactName!=null ||
                contactTitle!=null || contactType!=null || country!=null ||
                createdTime!=null || directoryId!=null || email!=null ||
                modifiedTime!=null || phone1!=null || phone2!=null ||
                state!=null || streetAddress1!=null || streetAddress2!=null ||
                streetAddress3!=null || streetAddress4!=null || zip!=null) {
            return contactsDao.search(
                    city,
                    contactId,
                    contactName,
                    contactTitle,
                    contactType,
                    country,
                    createdTime,
                    directoryId,
                    email,
                    modifiedTime,
                    phone1,
                    phone2,
                    state,
                    streetAddress1,
                    streetAddress2,
                    streetAddress3,
                    streetAddress4,
                    zip
            );
        } else {
            return contactsDao.all();
        }
    }

    @CrossOriginResourceSharing(allowAllOrigins = true, allowCredentials = true, maxAge = 1)
    @Path("/search")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation("Search OrganizationContact based on JSON object.  Empty fields will be ignored.")
    public List<OrganizationContact> searchContactsGet(OrganizationContactSearch contactSearch) {
        return contactsDao.search(
                contactSearch.getCity(),
                contactSearch.getContactId(),
                contactSearch.getContactName(),
                contactSearch.getContactTitle(),
                contactSearch.getContactType(),
                contactSearch.getCountry(),
                contactSearch.getCreatedTime(),
                contactSearch.getDirectoryId(),
                contactSearch.getEmail(),
                contactSearch.getModifiedTime(),
                contactSearch.getPhone1(),
                contactSearch.getPhone2(),
                contactSearch.getState(),
                contactSearch.getStreetAddress1(),
                contactSearch.getStreetAddress2(),
                contactSearch.getStreetAddress3(),
                contactSearch.getStreetAddress4(),
                contactSearch.getZip()
        );
    }

    @CrossOriginResourceSharing(allowAllOrigins = true, allowCredentials = true, maxAge = 1)
    @Path("/{contactId}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation("The read (single) method to the Contacts REST API Returning a single OrganizationContact that has an " +
            "identifier matching the value in the request path or nothing if not found.")
    public OrganizationContact getContact(@PathParam("contactId") @ApiParam("An integer used as the OrganizationContact identifier") Integer id) {
        return contactsDao.byId(id);
    }

    @CrossOriginResourceSharing(allowAllOrigins = true, allowCredentials = true, maxAge = 1)
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation("The create (single) method for the Contacts REST API.")
    public OrganizationContact createContact(OrganizationContact contact) {
        return contactsDao.save(contact);
    }

    @CrossOriginResourceSharing(allowAllOrigins = true, allowCredentials = true, maxAge = 1)
    @Path("/{contactId}")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation("The update (single) method for the Contacts REST API.")
    public OrganizationContact saveContact(@PathParam("contactId") Integer contactId,
                                           OrganizationContact contact) {
        return contactsDao.save(contact);
    }

    @CrossOriginResourceSharing(allowAllOrigins = true, allowCredentials = true, maxAge = 1)
    @Path("/{contactId}")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation("The delete (single) method for the Contacts REST API.")
    public void removeContact(@PathParam("contactId") Integer contactId) {
        OrganizationContact contact = contactsDao.byId(contactId);
        if(contact!=null) {
            contactsDao.remove(contact);
        }
    }



}