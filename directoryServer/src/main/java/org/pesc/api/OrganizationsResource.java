package org.pesc.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ResponseHeader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.rs.security.cors.CrossOriginResourceSharing;
import org.pesc.api.exception.ApiException;
import org.pesc.api.model.CertificateInfo;
import org.pesc.api.model.MessageTopic;
import org.pesc.api.model.Organization;
import org.pesc.api.model.SchoolCode;
import org.pesc.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.jws.WebService;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by James Whetstone (jwhetstone@ccctechcenter.org) on 3/22/16.
 */
@Component
@WebService
@Path("/organizations")
@Api("/organizations")
@CrossOriginResourceSharing(allowAllOrigins = true, allowCredentials = true, maxAge = 1)
public class OrganizationsResource {

    private static final Log log = LogFactory.getLog(OrganizationsResource.class);

    public static final String NETWORK_CERTFICATE_CHANGED_MESSAGE = "<div>The network certificate for <a href='%s'>%d</a> changed and requires approval.</div>";
    public static final String SIGNING_CERTIFICATION_CHANGED_MESSAGE = "<div>The signing certificate for <a href='%s'>%d</a> changed and requires approval.</div>";


    @Value("${api.base.uri}")
    private String baseURI;


    @Autowired
    private EmailService emailService;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private SchoolCodesService schoolCodesService;

    @Context
    private HttpServletResponse servletResponse;

    @Autowired
    private MessageService messageService;


    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @ApiOperation("Search organizations based on the optional search parameters.  All organizations are returned" +
            " when no search criteria are provided.")
    @ResponseHeader(name="X-Total-Count", description = "The total number of available records. Useful for pagination.")
    public List<Organization> findOrganization(
            @QueryParam("id") @ApiParam("The directory identifier for the organization.") Integer id,
            @QueryParam("organizationCode") @ApiParam("A code such as ATP code that identifies the organization.") String organizationCode,
            @QueryParam("organizationCodeType") @ApiParam("Indicates the type of organization code and should be one of the following: ACT, ATP, FICE, IPEDS.") String organizationCodeType,
            @QueryParam("name") @ApiParam("The case insensitive organization name or partial name.") String name,
            @QueryParam("subcode") @ApiParam("A proprietary code used to identify an organization.") String subcode,
            @QueryParam("type") @ApiParam("The type of organization (Institution, Service Provider).") String type,
            @QueryParam("ein") @ApiParam("The federal tax identification number (Employer Identification Number).") String ein,
            @QueryParam("createdTime") Long createdTime,
            @QueryParam("modifiedTime") Long modifiedTime,
            @QueryParam("active") @ApiParam("Indicates whether the organization and all it's endpoints are active.") Boolean active,
            @QueryParam("enabled") @ApiParam("Indicates whether the organization is in good standing and included in the directory.") Boolean enabled,
            @QueryParam("serviceprovider") @ApiParam("Indicates whether the organization is a service provider.") Boolean isServiceProvider,
            @QueryParam("institution") @ApiParam("Indicates whether the organization is an institution.") Boolean isInstitution,
            @QueryParam("limit") @DefaultValue("5") Integer limit,
            @QueryParam("offset") @DefaultValue("0") Integer offset


    ) {

        if (limit == null || offset == null) {
            limit = 5;
            offset = 0;
        }

        try {
            schoolCodesService.validateCode(organizationCode, organizationCodeType);
        }
        catch (IllegalArgumentException e) {
            throw new ApiException(e, Response.Status.BAD_REQUEST, "/organizations");
        }

        PagedData<Organization> pagedData = new PagedData<Organization>(limit,offset);

        organizationService.search(
                id,
                organizationCode,
                organizationCodeType,
                name,
                subcode,
                type,
                ein,
                createdTime,
                modifiedTime,
                active,
                enabled,
                isServiceProvider,
                isInstitution,
                pagedData);

        servletResponse.addHeader("X-Total-Count", String.valueOf(pagedData.getTotal()) );
        return pagedData.getData();
    }

    @GET
    @Path("/{id}")
    @ApiOperation("Return the organization with the given id.")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Organization> getOrganization(@PathParam("id") @ApiParam("The directory identifier for the organization.") Integer id) {
        ArrayList<Organization> results = new ArrayList<Organization>();

        Organization organization = organizationService.findById(id);

        if (organization != null) {
            results.add(organization);
        }

        return results;
    }


    @CrossOriginResourceSharing(allowAllOrigins = true, allowCredentials = true, maxAge = 1)
    @POST
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @ApiOperation("Create an organization.")
    public Organization createOrganization(Organization org) {

        return organizationService.create(org);
    }


    @Path("/{id}")
    @PUT
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @ApiOperation("Update the organization with the given ID.")
    public Organization saveOrganization(@PathParam("id") @ApiParam("The directory identifier for the organization.") Integer id, Organization org) {
        return organizationService.update(org);
    }

    @CrossOriginResourceSharing(allowAllOrigins = true, allowCredentials = true, maxAge = 1)
    @Path("/{id}")
    @DELETE
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @ApiOperation("Delete the organization with the given ID.")
    public void removeOrganization(@PathParam("id") @ApiParam("The directory identifier for the organization.") Integer id) {

        organizationService.delete(id);

    }

    @Path("/{id}")
    @POST
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @ApiOperation("Add the endpoint with the given ID to the organization's list of endpoints.")
    public void updateEndpoints(@PathParam("id") @ApiParam("The identifier for the organization.") Integer id,
                                @QueryParam("endpoint_id") @ApiParam(value="The identifier for the endpoint.", required = true) Integer endpointID,
                                @QueryParam("operation") @ApiParam(value="The operation to perform. Must by case insensitive 'add' or 'remove'.", required = true) String operation) {

        checkParameter(endpointID, "endpoint_id", baseURI + "/organizations/" + id);
        checkParameter(operation, "operation", baseURI + "/organizations/" + id);

        if ("add".equalsIgnoreCase(operation)) {
            organizationService.addEndpointToOrganization(id, endpointID);
        }

        else if ("remove".equalsIgnoreCase(operation)) {
            organizationService.removeEndpointToOrganization(id, endpointID);
        }
        else {
            throw new ApiException(
                    new IllegalArgumentException("The opration parameter must be either 'add' or 'remove'."),
                    Response.Status.BAD_REQUEST, baseURI + "/organizations/" + id );

        }

    }


    @Path("/{id}/school-code")
    @PUT
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @ApiOperation("Add or update a school code for the institution. Code types include 'FICE', 'ATP', 'ACT', 'IPEDS' and 'OPEID'")
    public SchoolCode putSchoolCode(@PathParam("id") @ApiParam("The identifier for the organization.") Integer id,
                              @FormParam("code-type") @ApiParam("'FICE', 'ATP', 'ACT', 'IPEDS' or 'OPEID'") String codeType,
                              @FormParam("code") String code) {

        SchoolCode schoolCode = new SchoolCode();
        schoolCode.setOrganizationId(id);
        schoolCode.setCode(code);
        schoolCode.setCodeType(codeType);


        try {
            schoolCode = schoolCodesService.create(schoolCode);
        }
        catch (IllegalArgumentException e) {
            throw new ApiException(e, Response.Status.BAD_REQUEST, "/organizations/" + id.toString() + "/school-code");
        }

        return schoolCode;
    }





    @Path("/{id}/enabled")
    @PUT
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @ApiOperation("Update the enabled property of the organization.")
    public void updateEnabledProperty(@PathParam("id") @ApiParam("The identifier for the organization.") Integer id, String enabled) {

        try {
            organizationService.setProperty(id, "enabled", Boolean.valueOf(enabled));

        }
        catch (Exception e) {
            throw new ApiException(e, Response.Status.BAD_REQUEST, "/organizations/" + id.toString() + "/enabled");
        }

    }


    @Path("/{id}/signing-certificate")
    @PUT
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @ApiOperation("Update the signing certificate of the organization.")
    public CertificateInfo udpateSendingCertificateProperty(@PathParam("id") @ApiParam("The identifier for the organization.") Integer id, String pemCert) {

        CertificateInfo info;
        try {

            info = organizationService.setSigningCertificate(id, pemCert);
           String message = String.format(SIGNING_CERTIFICATION_CHANGED_MESSAGE, emailService.getOrganizationURL() + id, id);

            messageService.createMessage(MessageTopic.SIGNING_CERTIFICATE_CHANGED.name(), message, true,1, null );

            emailService.sendEmailToSysAdmins(MessageTopic.SIGNING_CERTIFICATE_CHANGED.name(), message);

            return info;


        } catch (CertificateException e) {
            log.error("Failed to read signing certificate.", e);
            throw new WebApplicationException("Failed to save certificate", e);
        }
    }

    @Path("/{id}/network-certificate")
    @PUT
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @ApiOperation("Update the network certificate of the organization.")
    public CertificateInfo udpateNetworkCertificateProperty(@PathParam("id") @ApiParam("The identifier for the organization.") Integer id, String pemCert) {

        CertificateInfo info;
        try {
            info = organizationService.setNetworkCertificate(id, pemCert);
            String message = String.format(NETWORK_CERTFICATE_CHANGED_MESSAGE, emailService.getOrganizationURL() + id, id);

            messageService.createMessage(MessageTopic.NETWORK_CERTIFICATE_CHANGED.name(),
                    message, true,1, null );

            emailService.sendEmailToSysAdmins(MessageTopic.NETWORK_CERTIFICATE_CHANGED.name(),message);
            return info;

        } catch (CertificateException e) {
            log.error("Failed to set network certificate.", e);
            throw new ApiException(e, Response.Status.BAD_REQUEST, "/organizations/" + id.toString() + "/network-certificate");
        }
        catch (IOException e) {
            log.error("Failed to set network certificate.", e);
            throw new ApiException(e, Response.Status.BAD_REQUEST, "/organizations/" + id.toString() + "/network-certificate");
        }


    }


    @Path("/{id}/signing-certificate")
    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @ApiOperation("Get certificate info including the PEM formatted signing certificate for the given organization.")
    public CertificateInfo getCertificate(@PathParam("id") @ApiParam("The identifier for the organization.") Integer id) {

        try {

            return organizationService.getSigningCertificate(id);

        } catch (CertificateException e) {
            log.error("Failed to retrieve signing certificate.", e);
            throw new ApiException(e, Response.Status.BAD_REQUEST, "/organizations/" + id.toString() + "/signing-certificate");
        }
    }

    @Path("/{id}/network-certificate")
    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @ApiOperation("Get the certificate info including the PEM formatted network certificate for the given organization.")
    public CertificateInfo getNetworkCertificate(@PathParam("id") @ApiParam("The identifier for the organization.") Integer id) {

        try {

            return organizationService.getNetworkCertificate(id);

        } catch (CertificateException e) {
            log.error("Failed to retrieve network certificate.", e);
            throw new ApiException(e, Response.Status.BAD_REQUEST, "/organizations/" + id.toString() + "/network-certificate");
        }
    }

    @Path("/{id}/public-key")
    @GET
    @Produces({MediaType.TEXT_HTML})
    @ApiOperation("Get PEM formatted public key associated with the signing certificate used to digitally sign documents. If the organization that owns the key is disabled" +
            ", no key is returned.")
    public String getPublicKey(@PathParam("id") @ApiParam("The identifier for the organization.") Integer id) {

        try {

            return organizationService.getPEMPublicKey(id);

        } catch (CertificateException e) {
            log.error("Failed to retrieve public key.", e);
            throw new ApiException(e, Response.Status.BAD_REQUEST, "/organizations/" + id.toString() + "/network-certificate");
        }
        catch (Exception e) {
            log.error("Failed to retrieve public key.", e);
        }

        return null;
    }

    public static void checkParameter(Object param, String parameterName, String path) {
        if (param == null) {

            throw new ApiException(
                    new IllegalArgumentException(String.format("The %s parameter is required.", parameterName)),
                    Response.Status.BAD_REQUEST, path );
        }
    }

}
