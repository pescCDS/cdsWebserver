package org.pesc.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.rs.security.cors.CrossOriginResourceSharing;
import org.pesc.api.model.DirectoryUser;
import org.pesc.api.model.DocumentFormat;
import org.pesc.api.repository.DocumentFormatService;
import org.pesc.api.repository.UserService;
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
@Path("/document-formats")
@Api("/document-formats")
public class DocumentFormatResource {

    private static final Log log = LogFactory.getLog(DocumentFormatResource.class);

    @Autowired
    private DocumentFormatService documentFormatService;

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @ApiOperation("Get a list of valid document formats supported by Ed Exchange endpoints.")
    public List<DocumentFormat> getDocumentFormats(){

        return (List<DocumentFormat>)documentFormatService.getDocumentFormats();
    }
}