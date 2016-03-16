package org.pesc.service.rs;

import io.swagger.annotations.*;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.apache.cxf.rs.security.cors.CrossOriginResourceSharing;
import org.pesc.service.xml.Validator;
import org.pesc.service.xml.XmlFileType;
import org.pesc.service.xml.XmlSchemaVersion;
import org.pesc.edexchange.v1_0.ContentCodeType;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;

/**
 * Created by rgehbauer on 9/11/15.
 */
@CrossOriginResourceSharing(
        allowAllOrigins = true,
        allowCredentials = true,
        allowOrigins = {"http://pesc.cccnext.net:8080", "http://local.pesc.dev:8080"},
        maxAge = 1
)
@Api("/utility")
@Path("/utility")
@Component
public class UtilityRestController {

    private static final Log log = LogFactory.getLog(OrganizationRestController.class);

    @CrossOriginResourceSharing(allowAllOrigins = true, allowCredentials = true, maxAge = 1)
    @POST
    @Path("/validateFile")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @ApiOperation("Validates a file specified by fileFormat, returns a Response containing a error message string if a validation error is encountered")
    @ApiResponses( {
            @ApiResponse(code=400, message = "File validation only supported for PESC XML and EDI")
    })
    public Response validateFile(
            @Multipart("file") @ApiParam("The InputStream containing the file") InputStream uploadedInputStream,
            @Multipart("fileFormat") @ApiParam("Cast to a ContentCodeType") String fileFormatStr,
            @Multipart(value="xmlType", required=false) @ApiParam("Optional parameter, used for PESCXML validation, specifies whether HighSchool or College PESC Transcript.  Default: COLLEGE_TRANSCRIPT") String xmlFileTypeStr,
            @Multipart(value="schemaVersion", required=false) @ApiParam("Optional parameter, used for PESCXML validation, specifies the version of the HS/College transcript schema to validate with.  Default: V1_4_0") String versionStr)

    {
        Response.Status status = Response.Status.OK;
        ContentCodeType fileFormat = ContentCodeType.fromValue(fileFormatStr);
        String errorMsg = "";
        switch(fileFormat) {
            case PESCXML:
                try {
                    XmlFileType xmlFileType = getXmlFileTypeEnum(xmlFileTypeStr);
                    XmlSchemaVersion version = getXmlSchemaVersionEnum(versionStr);
                    Validator.validatePESCXMLTranscript(uploadedInputStream, xmlFileType, version);
                }
                catch (Exception e) {
                    errorMsg = e.getLocalizedMessage();
                }
                break;
            case EDI:
                // TODO
                break;
            case TEXT:
            case XML:
            case PDF:
            case IMAGE:
            case BINARY:
            case MUTUALLY_DEFINED:
                // Return BAD_REQUEST and tell them why...
                status = Response.Status.BAD_REQUEST;
                errorMsg = ("File validation is currently only supported for PESC XML and EDI validation.");
                break;
        }
        return Response.status(status.getStatusCode()).entity(errorMsg).build();
    }

    /**
     * Null-/blank string-safe string converter to XmlSchemaVersion enum.  Will default to V1_4_0 if otherwise invalid.
     * @param versionStr
     * @return
     */
    private XmlSchemaVersion getXmlSchemaVersionEnum(String versionStr) {
        XmlSchemaVersion version = XmlSchemaVersion.V1_4_0;
        if(StringUtils.isNotBlank(versionStr)) {
            try {
                version = Enum.valueOf(XmlSchemaVersion.class, versionStr);
            } catch(Throwable t) { /* eat it, and use the default v1.4.0 */ }
        }
        return version;
    }

    /**
     * Null-/blank string-safe string converter to XmlFileType enum.  Will default to COLLEGE_TRANSCRIPT if otherwise invalid.
     * @param xmlFileTypeStr
     * @return
     */
    private XmlFileType getXmlFileTypeEnum(String xmlFileTypeStr) {
        XmlFileType xmlFileType = XmlFileType.COLLEGE_TRANSCRIPT;
        if(StringUtils.isNotBlank(xmlFileTypeStr)) {
            try {
                xmlFileType = Enum.valueOf(XmlFileType.class, xmlFileTypeStr);
            } catch(Throwable t) { /* eat it, and use the default COLLEGE_TRANSCRIPT */ }
        }
        return xmlFileType;
    }
}
