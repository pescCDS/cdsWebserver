package org.pesc.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pesc.api.model.Organization;
import org.pesc.api.model.ServiceProvider;
import org.pesc.api.repository.ServiceProviderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jws.WebService;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by james on 3/22/16.
 */
@Component
@WebService
@Path("/institutions")
@Api("/institutions")
public class InstitutionResource {

    private static final Log log = LogFactory.getLog(InstitutionResource.class);

    @Autowired
    private ServiceProviderRepository serviceProviderRepository;


    @GET
    @ApiOperation("Return the institutions that are serviced by this service provider.")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Set<Organization> getServiceProvidersForInstitution(@QueryParam("service_provider_id") @ApiParam("The directory identifier for the service provider.") Integer id) {

        ServiceProvider serviceProvider = serviceProviderRepository.findOne(id);

        if (serviceProvider == null) {
            return new HashSet<>();
        }

        return serviceProvider.getInstitutions();
    }

}
