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
import org.apache.cxf.rs.security.cors.CrossOriginResourceSharing;
import org.pesc.api.exception.ApiException;
import org.pesc.api.model.DocumentFormat;
import org.pesc.api.model.DocumentFormat;
import org.pesc.service.DocumentFormatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jws.WebService;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by James Whetstone (jwhetstone@ccctechcenter.org) on 3/22/16.
 */
@Component
@WebService
@Path("/document-formats")
@Api("/document-formats")
@CrossOriginResourceSharing(allowAllOrigins = true, allowCredentials = true, maxAge = 1)
public class DocumentFormatResource {

    private static final Log log = LogFactory.getLog(DocumentFormatResource.class);

    @Autowired
    private DocumentFormatService documentFormatService;

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @ApiOperation("Get a list of valid document formats supported by EDExchange endpoints.")
    public List<DocumentFormat> getDocumentFormats(){

        return (List<DocumentFormat>)documentFormatService.getDocumentFormats();
    }



    @CrossOriginResourceSharing(allowAllOrigins = true, allowCredentials = true, maxAge = 1)
    @POST
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @ApiOperation("Create a delivery method.")
    public DocumentFormat create(DocumentFormat documentFormat) {

        return documentFormatService.create(documentFormat);
    }

    @CrossOriginResourceSharing(allowAllOrigins = true, allowCredentials = true, maxAge = 1)
    @PUT
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @ApiOperation("Update a document format.")
    @Path("/{id}")
    public DocumentFormat update(@PathParam("id") @ApiParam("The resource identifier.") Integer id, DocumentFormat documentFormat) {

        return documentFormatService.create(documentFormat);
    }


    @CrossOriginResourceSharing(allowAllOrigins = true, allowCredentials = true, maxAge = 1)
    @Path("/{id}")
    @DELETE
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @ApiOperation("Delete the contact with the given ID.")
    public void removeContact(@PathParam("id") @ApiParam("The resource identifier.") Integer id) {
        try {
            documentFormatService.delete(id);
        }
        catch (Exception e) {
            throw new ApiException(new IllegalArgumentException("The resource couldn't be deleted.  Is it in use by an endpoint?"), Response.Status.BAD_REQUEST, "/delivery-methods/" + id);
        }

    }
}
