package org.pesc.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pesc.api.model.Institution;
import org.pesc.api.model.Organization;
import org.pesc.api.model.ServiceProvider;
import org.pesc.api.repository.InstitutionRepository;
import org.pesc.api.repository.ServiceProviderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jws.WebService;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by james on 3/22/16.
 */
@Component
@WebService
@Path("/service-providers")
@Api("/service-providers")
public class ServiceProviderResource {

    private static final Log log = LogFactory.getLog(ServiceProviderResource.class);

    @Autowired
    private ServiceProviderRepository serviceProviderRepository;

    @Autowired
    private InstitutionRepository institutionRepository;



    @GET
    @ApiOperation("Return the service providers for the institution.")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Organization> getServiceProvidersForInstitution(@QueryParam("institution_id") @ApiParam("The directory identifier for the institution.") Integer id) {

        Institution organization = institutionRepository.findOne(id);

        if (organization == null) {
            return new ArrayList<Organization>();
        }

        return organization.getServiceProviders();
    }

    @PUT
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @ApiOperation("Update the organization with the given ID.")
    public Institution updateServiceProvidersForInstitution(@QueryParam("institution_id") @ApiParam("The directory identifier for the organization.") Integer id, List<Organization> serviceProviders) {

        Institution institution = new Institution();
        institution.setId(id);
        institution.setServiceProviders(serviceProviders);

        return institutionRepository.save(institution);
    }



}
