package org.pesc.service.rs;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.rs.security.cors.CrossOriginResourceSharing;
import org.pesc.edexchange.v1_0.DocumentFormat;
import org.pesc.service.dao.DocumentFormatsDao;
import org.pesc.service.rs.request.DocumentFormatSearch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jws.WebService;
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
@Path("/documentFormats")
@Api("/documentFormats")
@Component
@WebService
public class DocumentFormatRestController {
    private static final Log log = LogFactory.getLog(DocumentFormatRestController.class);
    
    @Autowired
    DocumentFormatsDao documentFormatsDao;

    /***********************************************************************************
     * These are for AJAX web services
     * The only data served out should be auxilary tables where the total row count
     * is under 300 or so. Bigger tables will need to go through the SOAP or REST
     * web services agreed upon by the PESC EdExchange group
     *
     ***********************************************************************************/

    //////////////////////////////////////////////
    // Document Formats
    //////////////////////////////////////////////

    @CrossOriginResourceSharing(allowAllOrigins = true, allowCredentials = true, maxAge = 1)
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation("GET operation to search DocumentFormat with query parameters passed in URL.")
    public List<DocumentFormat> searchDocumentFormat(
            @QueryParam("id") Integer id,
            @QueryParam("formatName") String formatName,
            @QueryParam("formatDescription") String formatDescription,
            @QueryParam("formatInuseCount") Integer formatInuseCount,
            @QueryParam("createdTime") Long createdTime,
            @QueryParam("modifiedTime") Long modifiedTime
    ) {
        if(id!=null || formatName!=null || formatDescription!=null ||
                formatInuseCount!=null || createdTime!=null ||
                modifiedTime!=null) {
            return documentFormatsDao.search(
                    id,
                    formatName,
                    formatDescription,
                    formatInuseCount,
                    createdTime,
                    modifiedTime
            );
        } else {
            return documentFormatsDao.all();
        }
    }

    @CrossOriginResourceSharing(allowAllOrigins = true, allowCredentials = true, maxAge = 1)
    @Path("/search")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation("POST operation to search DocumentFormat using JSON object.  Empty fields will be ignored.")
    public List<DocumentFormat> searchDocumentFormatByJSON(
            DocumentFormatSearch documentFormatSearch) {
        return documentFormatsDao.search(
                documentFormatSearch.getId(),
                documentFormatSearch.getFormatName(),
                documentFormatSearch.getFormatDescription(),
                documentFormatSearch.getFormatInuseCount(),
                documentFormatSearch.getCreatedTime(),
                documentFormatSearch.getModifiedTime()
        );
    }

    @CrossOriginResourceSharing(allowAllOrigins = true, allowCredentials = true, maxAge = 1)
    @Path("/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation("The read (single) method to the DocumentFormats REST API Returning a single DocumentFormat that has an \" +\n" +
            "            \"identifier matching the value in the request path or nothing if not found.")
    public DocumentFormat getDocumentFormat(@PathParam("id") @ApiParam("An integer used as the DocumentFormat identifier") Integer id) {
        return documentFormatsDao.byId(id);
    }

    @CrossOriginResourceSharing(allowAllOrigins = true, allowCredentials = true, maxAge = 1)
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation("The create (single) method for the DocumentFormats REST API.")
    public DocumentFormat createDocumentFormat(DocumentFormat docFormat) {
        // TODO validate document format object
        //save document format
        return documentFormatsDao.save(docFormat);
    }

    @CrossOriginResourceSharing(allowAllOrigins = true, allowCredentials = true, maxAge = 1)
    @Path("/{id}")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation("The update (single) method for the DocumentFormats REST API.")
    public DocumentFormat saveDocumentFormat(
            @PathParam("id") Integer id,
            DocumentFormat docFormat) {
        return documentFormatsDao.save(docFormat);
    }

    @CrossOriginResourceSharing(allowAllOrigins = true, allowCredentials = true, maxAge = 1)
    @Path("/{id}")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation("The delete (single) method for the DocumentFormats REST API.")
    public void removeDocumentFormat(@PathParam("id") Integer id) {
        DocumentFormat docFormat = documentFormatsDao.byId(id);
        if(docFormat!=null) {
            documentFormatsDao.remove(docFormat);
        }
    }



}
