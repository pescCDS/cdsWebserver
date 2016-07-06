package org.pesc.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pesc.api.exception.ApiException;
import org.pesc.api.model.Institution;
import org.pesc.api.model.InstitutionsUpload;
import org.pesc.api.model.InstitutionsUploadResult;
import org.pesc.api.model.Organization;
import org.pesc.api.repository.InstitutionRepository;
import org.pesc.service.InstitutionService;
import org.pesc.service.InstitutionUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jws.WebService;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by James Whetstone (jwhetstone@ccctechcenter.org) on 3/22/16.
 */
@Component
@WebService
@Path("/service-providers")
@Api("/service-providers")
public class ServiceProviderResource {

    private static final Log log = LogFactory.getLog(ServiceProviderResource.class);

    @Autowired
    private InstitutionService institutionService;

    @Autowired
    private InstitutionUploadService uploadService;

    @GET
    @Path("/{id}/uploads")
    @ApiOperation("Return the uploads for this service provider.")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<InstitutionsUpload> getUploads(@PathParam("id") @ApiParam("The directory identifier for the organization.") Integer id) {
        List<InstitutionsUpload> results = uploadService.getUploadsByOrganization(id);

        if (results == null) {
            results = new ArrayList<InstitutionsUpload>();
        }

        return results;

    }

    @GET
    @Path("/{id}/upload-results")
    @ApiOperation("Return the uploads for this service provider.")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<InstitutionsUploadResult> getUploadResults(@PathParam("id") @ApiParam("The directory identifier for the organization.") Integer id,
                                         @QueryParam("upload_id") Integer uploadID) {


        List<InstitutionsUploadResult> results = uploadService.getUploadResultsByUploadId(uploadID);

        if (results == null) {
            results = new ArrayList<InstitutionsUploadResult>();
        }

        return results;
    }




    @GET
    @ApiOperation("Return the service providers for the institution.")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Set<Organization> getServiceProvidersForInstitution(@QueryParam("institution_id")
                                                                   @ApiParam(value="The directory identifier for the institution.", required = true) Integer id) {

        if (id == null) {
            throw new ApiException(new IllegalArgumentException("The institution's id must be provided."), Response.Status.BAD_REQUEST, "/service-providers");
        }

        Institution organization = institutionService.findById(id);

        if (organization == null) {
            return new HashSet<Organization>();
        }

        return organization.getServiceProviders();
    }


    @PUT
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @ApiOperation("Update the organization with the given ID.")
    public Institution updateServiceProvidersForInstitution(@QueryParam("institution_id") @ApiParam(value="The directory identifier for the organization.", required = true) Integer id,
                                                            Set<Organization> serviceProviders) {

        if (id == null) {
            throw new ApiException(new IllegalArgumentException("The institution's id must be provided."), Response.Status.BAD_REQUEST, "/service-providers");
        }
        Institution institution = new Institution();
        institution.setId(id);
        institution.setServiceProviders(serviceProviders);

        //TODO: add security contraints...

        return institutionService.save(institution);
    }



}
