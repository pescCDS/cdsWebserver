package org.pesc.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.apache.cxf.rs.security.cors.CrossOriginResourceSharing;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.pesc.api.model.*;
import org.pesc.service.InstitutionUploadService;
import org.pesc.service.OrganizationService;
import org.pesc.service.PagedData;
import org.pesc.web.AppController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.activation.DataHandler;
import javax.jws.WebService;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;


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
    private OrganizationService organizationService;

    @Autowired
    private InstitutionUploadService uploadService;

    @Context
    private HttpServletResponse servletResponse;

    @Value("${directory.uploaded.csv}")
    private String csvDir;

    private InstitutionsUpload persistUploadRecord(String inputFilepath, Integer providerID) {

        if (SecurityContextHolder.getContext().getAuthentication() != null &&
                SecurityContextHolder.getContext().getAuthentication().isAuthenticated() &&
                //when Anonymous Authentication is enabled
                !(SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken)) {

            AuthUser auth = (AuthUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            //Ideally, this should be done with an annotation, but for some reason, the annotation was conflicting
            //with the Context annotation, making the HttpServletResponse null.
            if (AppController.hasRole(auth.getAuthorities(), "ROLE_SYSTEM_ADMIN") ||
                    (AppController.hasRole(auth.getAuthorities(), "ROLE_ORG_ADMIN") && auth.getOrganizationId().equals(providerID))) {

                InstitutionsUpload institutionsUpload = new InstitutionsUpload();
                institutionsUpload.setCreatedTime(Calendar.getInstance().getTime());
                institutionsUpload.setUserId(auth.getId());
                institutionsUpload.setOrganizationId(auth.getOrganizationId());
                institutionsUpload.setInputPath(inputFilepath);

                return uploadService.create(institutionsUpload);
            }

        }

        return null;
    }


    @CrossOriginResourceSharing(allowAllOrigins = true, allowCredentials = true, maxAge = 1)
    @POST
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @ApiOperation("Create an institution. This API is intended to be used by service providers.")
    public Organization createInstitution(Organization org) {

        return organizationService.createInstitution(org);
    }


    @CrossOriginResourceSharing(allowAllOrigins = true, allowCredentials = true, maxAge = 1)
    @POST
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @ApiOperation("Add an institution to the service provider's group of serviceable institutions.")
    @Path("/relation")
    public InstitutionServiceProviderRelation createRelation(InstitutionServiceProviderRelation relation) {

        organizationService.secureLinkInstitutionWithServiceProvider(relation.getInstitutionID(), relation.getServiceProviderID());

        return relation;
    }

    /**
     * Accepts a CSV file where each row represents and institution that should be associated with the service
     * provider.  If the institution already exists in the system, the service provider is granted permissions to
     * handle endpoints for the institution.
     * @param providerID The organication/directory ID of the provider.
     * @param attachment The CSV file of institutions where each row must have "state,name,city,ipeds,fice,act,atp,opeid"
     */
    @POST
    @Path("/csv")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @ApiOperation("Accepts a CSV file where each row represents and institution that should be associated with the service " +
            "provider.  If the institution already exists in the system, the service provider is granted permissions to " +
            "handle endpoints for the institution. The file header and the columns for the data are state,name,city,ipeds,fice,act,atp,opeid")
    public InstitutionsUpload associateInstitutions(@Multipart("org_id") @ApiParam("The service provider's directory ID.") Integer providerID,
                                      @Multipart("file") @ApiParam("The csv file.") Attachment attachment) {




        DataHandler handler = attachment.getDataHandler();
        try {
            InputStream stream = handler.getInputStream();
            //MultivaluedMap<String, String> map = attachment.getHeaders();
            //System.out.println("fileName Here" + getFileName(map));


            File outfile = new File(csvDir + File.separator + UUID.randomUUID().toString() + ".csv");
            Files.copy(stream, outfile.toPath());

            InstitutionsUpload institutionsUpload = persistUploadRecord(outfile.getAbsolutePath(), providerID);

            uploadService.processCSVFile(institutionsUpload, outfile.getPath(), providerID);

            return institutionsUpload;

        } catch (Exception e) {
            log.error("Failed to save uploaded CSV file", e);

            throw new RuntimeException("Failed to save uploaded CSV file");

        }

    }

    @GET
    @Path("/csv")
    public void getFile(
            @QueryParam("upload_id") Integer uploadID) {
        try {

            InstitutionsUpload institutionsUpload = uploadService.getUploadById(uploadID);

            if (institutionsUpload == null) {
                throw new RuntimeException("Invalid institutions upload id.");
            }


            InputStream is = new FileInputStream(new File(institutionsUpload.getInputPath()));

            servletResponse.setContentType("text/csv");
            IOUtils.copy(is, servletResponse.getOutputStream());
            servletResponse.flushBuffer();
        } catch (IOException e) {
            log.error("Error writing file to output stream. Institution upload id " + uploadID, e);
            throw new RuntimeException("IOError writing file to output stream");
        }
    }

    @GET
    @Path("/csv-status")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<CSVStatusDTO> getCSVStatus(
            @QueryParam("upload_id") Integer uploadID) {

        return uploadService.getCSVStatus(uploadID);
    }


    @GET
    @ApiOperation("Return the institutions that are serviced by this service provider.")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<OrganizationDTO> getServiceProvidersForInstitution(
            @QueryParam("service_provider_id") @ApiParam(value="The directory identifier for the service provider.", required = true) Integer id,
            @QueryParam("limit") @DefaultValue("5") Integer limit,
            @QueryParam("offset") @DefaultValue("0") Integer offset) {

        if (limit == null || offset == null) {
            limit = 5;
            offset = 0;
        }
        PagedData<OrganizationDTO> pagedData = new PagedData<OrganizationDTO>(limit,offset);

        if (id == null) {
            throw new IllegalArgumentException("The service provider's id must be provided.");
        }
        organizationService.getInstitutionsByServiceProviderId(id, pagedData);
        servletResponse.addHeader("X-Total-Count", String.valueOf(pagedData.getTotal()));
        return pagedData.getData();
    }

}
