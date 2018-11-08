/*
 * Copyright (c) 2017. California Community Colleges Technology Center
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.pesc.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.jaxrs.ext.MessageContext;
import org.apache.cxf.rs.security.cors.CrossOriginResourceSharing;
import org.pesc.api.exception.ApiException;
import org.pesc.api.model.ApiRequest;
import org.pesc.api.model.ApiRequestParameter;
import org.pesc.api.model.Endpoint;
import org.pesc.service.ApiRequestService;
import org.pesc.service.EndpointService;
import org.pesc.service.OrganizationService;
import org.pesc.utils.TimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.jws.WebService;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by James Whetstone (jwhetstone@ccctechcenter.org) on 3/22/16.
 */
@Component
@WebService
@Path("/endpoints")
@Api("/endpoints")
public class EndpointResource {

    private static final Log log = LogFactory.getLog(EndpointResource.class);
    private static final String PATH = "/endpoints";

    @Value("${api.base.uri}")
    private String baseURI;

    @Context
    private MessageContext context;

    @Autowired
    private ApiRequestService apiRequestService;


    //Security is enforced using method level annotations on the service.
    @Autowired
    private EndpointService endpointService;

    @Autowired
    private OrganizationService organizationService;


    private void validateParameters(List<Integer> organizationIdList, String path) {
        if ((organizationIdList == null || organizationIdList.size() == 0 )) {
            throw new ApiException(new IllegalArgumentException("At least one organization ID or school code parameter is mandatory."), Response.Status.BAD_REQUEST, path);
        }

    }

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @ApiOperation("Search endpoints based on the search parameters.")
    public List<Endpoint> findEndpoint(
            @QueryParam("documentFormat") @ApiParam(value = "Must be one of the supported documents formats (case insensitive), e.g 'text', 'pdf', 'xml', 'pescxml'. See the document-formats API for more.") String documentFormat,
            @QueryParam("documentType") @ApiParam(value = "Must be one of the supported documents types (case insensitive), e.g 'college transcript', 'transcript request'.") String documentType,
            @QueryParam("department") @ApiParam(value = "Must be one of the supported department definitions.") String departmentName,
            @QueryParam("id") @ApiParam("The identifier for the endpoint.") Integer id,
            @QueryParam("hostingOrganizationId") @ApiParam("The organization ID of the member that hosts the endpoint.") Integer hostingOrganizationId,
            @QueryParam("organizationId") @ApiParam(value = "A list of organization ID that use the endpoint.") List<Integer> organizationIdList,
            @QueryParam("mode") @ApiParam(value = "Must be either 'TEST' or 'LIVE'.") String mode,
            @QueryParam("enabled") @ApiParam(value = "'true' or 'false'") String enabled

    ) {


        validateParameters(organizationIdList, baseURI + PATH);

        List<Endpoint> endpoints = endpointService.search(
                documentFormat,
                documentType,
                departmentName,
                id,
                hostingOrganizationId,
                organizationIdList,
                mode,
                enabled);

        apiRequestService.createAndSave(context.getHttpServletRequest().getParameterMap(), context.getHttpServletRequest().getRequestURI(), endpoints.size());

        return endpoints;
    }

    @GET
    @Path("/{id}")
    @ApiOperation("Return the endpoint with the given id.")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Endpoint> getEndpoint(@PathParam("id") @ApiParam("The unique identifier for the endpoint.") Integer id) {
        ArrayList<Endpoint> results = new ArrayList<Endpoint>();

        Endpoint endpoint = endpointService.findById(id);

        if (endpoint != null) {
            results.add(endpoint);
        }

        return results;
    }


    @CrossOriginResourceSharing(allowAllOrigins = true, allowCredentials = true, maxAge = 1)
    @POST
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @ApiOperation("Create a endpoint.")
    public Endpoint createEndpoint(Endpoint endpoint) {

        //validate the url.
        try {
            validateEndpointURL(endpoint.getAddress(), organizationService.getNetworkDomainName(endpoint.getOrganization().getId()));

        }
        catch (MalformedURLException e) {
            throw new ApiException(e, Response.Status.BAD_REQUEST, "Invalid URL");
        }


        return endpointService.create(endpoint);
    }

    @Path("/{id}")
    @PUT
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @ApiOperation("Update the endpoint with the given ID.")
    public Endpoint saveEndpoint(@PathParam("id") @ApiParam("The identifier for the endpoint.") Integer id, Endpoint endpoint) {

        //validate the url.
        try {
            validateEndpointURL(endpoint.getAddress(), organizationService.getNetworkDomainName(endpoint.getOrganization().getId()));

        }
        catch (MalformedURLException e) {
            throw new ApiException(e, Response.Status.BAD_REQUEST, "Invalid URL");
        }


        return endpointService.update(endpoint);
    }

    @CrossOriginResourceSharing(allowAllOrigins = true, allowCredentials = true, maxAge = 1)
    @Path("/{id}")
    @DELETE
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @ApiOperation("Delete the endpoint with the given ID.")
    public void removeEndpoint(@PathParam("id") @ApiParam("The identifier for the endpoint.") Integer id) {
        endpointService.delete(id);

    }

    public static boolean wildcardCheck(String networkHostname, String endpointHostname) {
        if (networkHostname.contains("*")){

            //remove the subdomain from the endpoint's URL

            String endpointDomain = endpointHostname.substring(endpointHostname.indexOf('.'));
            String networkDomain = networkHostname.substring(networkHostname.indexOf('.'));

            return endpointDomain.equalsIgnoreCase(networkDomain);

        }

        return false;
    }

    public static String validateEndpointURL(String url, String networkHostName) throws MalformedURLException {
        URL _url = new URL(url);
        if (!"https".equalsIgnoreCase(_url.getProtocol())) {
            throw new ApiException(
                    new IllegalArgumentException(String.format("HTTPS is required for endpoint URLs.", _url.getHost(), networkHostName)),
                    Response.Status.BAD_REQUEST, PATH);
        }
        //If the hostname's don't match, or if if the hostname is not contained in the certificate's comman name (wildcard might be used).
        if (!_url.getHost().equalsIgnoreCase(networkHostName) && !wildcardCheck(networkHostName, _url.getHost())) {


            throw new ApiException(
                    new IllegalArgumentException(
                            String.format("The endpoint hostname %s does not match the network certificate hostname %s.  Have you uploaded your network certificate?", _url.getHost(), networkHostName != null ? networkHostName : "")),
                    Response.Status.BAD_REQUEST, PATH);
        }
        return _url.getHost();

    }

}
