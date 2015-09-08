package org.pesc.cds.webservice.service;

import java.io.InputStream;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.apache.cxf.rs.security.cors.CrossOriginResourceSharing;
import org.codehaus.jackson.annotate.JsonProperty;
import org.pesc.cds.datatables.FiltersToHQLUtil;
import org.pesc.cds.webservice.service.request.DeliveryMethodSearch;
import org.pesc.cds.webservice.service.request.DeliveryOptionSearch;
import org.pesc.cds.webservice.service.request.DocumentFormatSearch;
import org.pesc.cds.webservice.service.request.EntityCodeSearch;
import org.pesc.cds.webservice.service.request.OrganizationContactSearch;
import org.pesc.cds.webservice.service.request.OrganizationSearch;
import org.pesc.cds.xml.Validator;
import org.pesc.cds.xml.XmlFileType;
import org.pesc.cds.xml.XmlSchemaVersion;
import org.pesc.edexchange.v1_0.ContentCodeType;
import org.pesc.edexchange.v1_0.DeliveryMethod;
import org.pesc.edexchange.v1_0.DeliveryOption;
import org.pesc.edexchange.v1_0.DocumentFormat;
import org.pesc.edexchange.v1_0.EntityCode;
import org.pesc.edexchange.v1_0.Organization;
import org.pesc.edexchange.v1_0.OrganizationContact;
import org.pesc.edexchange.v1_0.dao.DocumentFormatsDao;
import org.pesc.edexchange.v1_0.dao.EntityCodesDao;
import org.pesc.edexchange.v1_0.dao.OrganizationsDao;
import org.pesc.edexchange.v1_0.dao.ContactsDao;
import org.pesc.edexchange.v1_0.dao.DeliveryMethodsDao;
import org.pesc.edexchange.v1_0.dao.DeliveryOptionsDao;

/**
 * REST web service class
 * This endpoint is at /{webapp}/services/rest
 * @author owenwe
 * 
 */
@CrossOriginResourceSharing(
		allowAllOrigins = true,
		allowCredentials = true,
		allowOrigins = {"http://pesc.cccnext.net:8080", "http://local.pesc.dev:8080"}, 
		maxAge = 1
)
public class RestWebServiceImpl {
	
	private static final Log log = LogFactory.getLog(RestWebServiceImpl.class);
	
	@Context
	private HttpHeaders headers;
	
	
	/***********************************************************************************
	 * These are for AJAX web services
	 * The only data served out should be auxilary tables where the total row count
	 * is under 300 or so. Bigger tables will need to go through the SOAP or REST
	 * web services agreed upon by the PESC EdExchange group
	 * 
	 ***********************************************************************************/
	
	//////////////////////////////////////////////
	// OrganizationContact
	//////////////////////////////////////////////
	
	/**
	 * This is the GET version of /contacts/search<p>
	 * Each parameter has a <code>QueryParam</code> annotation
	 * @param city
	 * @param contactId
	 * @param contactName
	 * @param contactTitle
	 * @param contactType
	 * @param country
	 * @param createdTime
	 * @param directoryId
	 * @param email
	 * @param modifiedTime
	 * @param phone1
	 * @param phone2
	 * @param state
	 * @param streetAddress1
	 * @param streetAddress2
	 * @param streetAddress3
	 * @param streetAddress4
	 * @param zip
	 * @return
	 */
	@CrossOriginResourceSharing(allowAllOrigins = true, allowCredentials = true, maxAge = 1)
	@Path("/contacts")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<OrganizationContact> searchContactsGet(
			@QueryParam("city") String city,
			@QueryParam("contactId") Integer contactId,
			@QueryParam("contactName") String contactName,
			@QueryParam("contactTitle") String contactTitle,
			@QueryParam("contactType") String contactType,
			@QueryParam("country") String country,
			@QueryParam("createdTime") Long createdTime,
			@QueryParam("directoryId") Integer directoryId,
			@QueryParam("email") String email,
			@QueryParam("modifiedTime") Long modifiedTime,
			@QueryParam("phone1") String phone1,
			@QueryParam("phone2") String phone2,
			@QueryParam("state") String state,
			@QueryParam("streetAddress1") String streetAddress1,
			@QueryParam("streetAddress2") String streetAddress2,
			@QueryParam("streetAddress3") String streetAddress3,
			@QueryParam("streetAddress4") String streetAddress4,
			@QueryParam("zip") String zip
		) {
		if(city!=null || contactId!=null || contactName!=null || 
				contactTitle!=null || contactType!=null || country!=null || 
				createdTime!=null || directoryId!=null || email!=null ||
				modifiedTime!=null || phone1!=null || phone2!=null ||
				state!=null || streetAddress1!=null || streetAddress2!=null ||
				streetAddress3!=null || streetAddress4!=null || zip!=null) {
			return ((ContactsDao)DatasourceManagerUtil.getContacts()).search(
					city, 
					contactId, 
					contactName, 
					contactTitle, 
					contactType, 
					country, 
					createdTime, 
					directoryId, 
					email, 
					modifiedTime, 
					phone1, 
					phone2, 
					state, 
					streetAddress1, 
					streetAddress2, 
					streetAddress3, 
					streetAddress4, 
					zip
			);
		} else {
			return DatasourceManagerUtil.getContacts().all();
		}
	}
	
	@CrossOriginResourceSharing(allowAllOrigins = true, allowCredentials = true, maxAge = 1)
	@Path("/contacts/search")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public List<OrganizationContact> searchContactsGet(@JsonProperty OrganizationContactSearch contactSearch) {
		return ((ContactsDao)DatasourceManagerUtil.getContacts()).search(
				contactSearch.getCity(),
				contactSearch.getContactId(),
				contactSearch.getContactName(),
				contactSearch.getContactTitle(),
				contactSearch.getContactType(),
				contactSearch.getCountry(),
				contactSearch.getCreatedTime(),
				contactSearch.getDirectoryId(),
				contactSearch.getEmail(),
				contactSearch.getModifiedTime(),
				contactSearch.getPhone1(),
				contactSearch.getPhone2(),
				contactSearch.getState(),
				contactSearch.getStreetAddress1(),
				contactSearch.getStreetAddress2(),
				contactSearch.getStreetAddress3(),
				contactSearch.getStreetAddress4(),
				contactSearch.getZip()
		);
	}
	
	@CrossOriginResourceSharing(allowAllOrigins = true, allowCredentials = true, maxAge = 1)
	@Path("/contacts/{contactId}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public OrganizationContact getContact(@PathParam("contactId") Integer id) {
		return DatasourceManagerUtil.getContacts().byId(id);
	}
	
	@CrossOriginResourceSharing(allowAllOrigins = true, allowCredentials = true, maxAge = 1)
	@Path("/contacts")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public OrganizationContact createContact(@JsonProperty OrganizationContact contact) {
		return DatasourceManagerUtil.getContacts().save(contact);
	}
	
	@CrossOriginResourceSharing(allowAllOrigins = true, allowCredentials = true, maxAge = 1)
	@Path("/contacts/{contactId}")
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public OrganizationContact saveContact(@PathParam("contactId") Integer contactId, 
			@JsonProperty OrganizationContact contact) {
		return DatasourceManagerUtil.getContacts().save(contact);
	}
	
	@CrossOriginResourceSharing(allowAllOrigins = true, allowCredentials = true, maxAge = 1)
	@Path("/contacts/{contactId}")
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public void removeContact(@PathParam("contactId") Integer contactId) {
		OrganizationContact contact = DatasourceManagerUtil.getContacts().byId(contactId);
		if(contact!=null) {
			DatasourceManagerUtil.getContacts().remove(contact);
		}
	}
	
	
	//////////////////////////////////////////////
	// Delivery Methods
	//////////////////////////////////////////////
	
	@CrossOriginResourceSharing(allowAllOrigins = true, allowCredentials = true, maxAge = 1)
	@Path("/deliveryMethods")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<DeliveryMethod> searchDeliveryMethods(
			@QueryParam("id") Integer id, 
			@QueryParam("method") String method) {
		if(id!=null || method!=null) {
			return ((DeliveryMethodsDao)DatasourceManagerUtil
					.getDeliveryMethods()).search(id, method);
		} else {
			return DatasourceManagerUtil.getDeliveryMethods().all();
		}
	}
	
	@CrossOriginResourceSharing(allowAllOrigins = true, allowCredentials = true, maxAge = 1)
	@Path("/deliveryMethods/search")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public List<DeliveryMethod> searchDeliveryMethods(
			@JsonProperty DeliveryMethodSearch deliveryMethodSearch) {
		return ((DeliveryMethodsDao)DatasourceManagerUtil
				.getDeliveryMethods()).search(
						deliveryMethodSearch.getId(),
						deliveryMethodSearch.getMethod());
	}
	
	@CrossOriginResourceSharing(allowAllOrigins = true, allowCredentials = true, maxAge = 1)
	@Path("/deliveryMethods/{id}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public DeliveryMethod getDeliveryMethod(@PathParam("id") Integer id) {
		return DatasourceManagerUtil.getDeliveryMethods().byId(id);
	}
	
	@CrossOriginResourceSharing(allowAllOrigins = true, allowCredentials = true, maxAge = 1)
	@Path("/deliveryMethods")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public DeliveryMethod createDeliveryMethod(@JsonProperty DeliveryMethod method) {
		return DatasourceManagerUtil.getDeliveryMethods().save(method);
	}
	
	@CrossOriginResourceSharing(allowAllOrigins = true, allowCredentials = true, maxAge = 1)
	@Path("/deliveryMethods/{id}")
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public DeliveryMethod saveDeliveryMethod(@PathParam("id") Integer id, @JsonProperty DeliveryMethod method) {
		return DatasourceManagerUtil.getDeliveryMethods().save(method);
	}
	
	@CrossOriginResourceSharing(allowAllOrigins = true, allowCredentials = true, maxAge = 1)
	@Path("/deliveryMethods/{id}")
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public void removeDeliveryMethod(@PathParam("id") Integer id) {
		DeliveryMethod method = DatasourceManagerUtil.getDeliveryMethods().byId(id);
		if(method!=null) {
			DatasourceManagerUtil.getDeliveryMethods().remove(method);
		}
	}
	
	
	//////////////////////////////////////////////
	// Delivery Options
	//////////////////////////////////////////////
	
	@CrossOriginResourceSharing(allowAllOrigins = true, allowCredentials = true, maxAge = 1)
	@Path("/deliveryOptions")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<DeliveryOption> searchDeliveryOptions(
			@QueryParam("id") Integer id, 
			@QueryParam("memberId") Integer memberId,
			@QueryParam("formatId") Integer formatId,
			@QueryParam("webserviceUrl") String webserviceUrl,
			@QueryParam("deliveryMethodId") Integer deliveryMethodId,
			@QueryParam("deliveryConfirm") Boolean deliveryConfirm,
			@QueryParam("error") Boolean error,
			@QueryParam("operationalStatus") String operationalStatus
		) {
		if(id!=null || memberId!=null || formatId!=null || 
				webserviceUrl!=null || deliveryMethodId!=null || 
				deliveryConfirm!=null || error!=null || 
				operationalStatus!=null) {
			return ((DeliveryOptionsDao)DatasourceManagerUtil
					.getDeliveryOptions()).search(
							id, 
							memberId, 
							formatId, 
							webserviceUrl, 
							deliveryMethodId, 
							deliveryConfirm, 
							error, 
							operationalStatus);
		} else {
			return DatasourceManagerUtil.getDeliveryOptions().all();
		}
		
	}
	
	@CrossOriginResourceSharing(allowAllOrigins = true, allowCredentials = true, maxAge = 1)
	@Path("/deliveryOptions/search")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public List<DeliveryOption> searchDeliveryOptionsPost(@JsonProperty DeliveryOptionSearch deliveryOptionSearch) {
		return ((DeliveryOptionsDao)DatasourceManagerUtil.getDeliveryOptions()).search(
				deliveryOptionSearch.getId(), 
				deliveryOptionSearch.getMemberId(), 
				deliveryOptionSearch.getFormatId(), 
				deliveryOptionSearch.getWebserviceUrl(), 
				deliveryOptionSearch.getDeliveryMethodId(), 
				deliveryOptionSearch.getDeliveryConfirm(), 
				deliveryOptionSearch.getError(), 
				deliveryOptionSearch.getOperationalStatus()
		);
	}
	
	@CrossOriginResourceSharing(allowAllOrigins = true, allowCredentials = true, maxAge = 1)
	@Path("/deliveryOptions/{id}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public DeliveryOption getDeliveryOptions(@PathParam("id") Integer id) {
		return DatasourceManagerUtil.getDeliveryOptions().byId(id);
	}
	
	@CrossOriginResourceSharing(allowAllOrigins = true, allowCredentials = true, maxAge = 1)
	@Path("/deliveryOptions")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public DeliveryOption createDeliveryOptions(@JsonProperty DeliveryOption opt) {
		return DatasourceManagerUtil.getDeliveryOptions().save(opt);
	}
	
	@CrossOriginResourceSharing(allowAllOrigins = true, allowCredentials = true, maxAge = 1)
	@Path("/deliveryOptions/{id}")
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public DeliveryOption saveDeliveryOption(@PathParam("id") Integer id, 
			@JsonProperty DeliveryOption opt) {
		return DatasourceManagerUtil.getDeliveryOptions().save(opt);
	}
	
	@CrossOriginResourceSharing(allowAllOrigins = true, allowCredentials = true, maxAge = 1)
	@Path("/deliveryOptions/{id}")
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public void removeDeliveryOptions(@PathParam("id") Integer id) {
		DeliveryOption opt = DatasourceManagerUtil.getDeliveryOptions().byId(id);
		if(opt!=null) {
			DatasourceManagerUtil.getDeliveryOptions().remove(opt);
		}
	}
	
	
	//////////////////////////////////////////////
	// Document Formats
	//////////////////////////////////////////////
	
	@CrossOriginResourceSharing(allowAllOrigins = true, allowCredentials = true, maxAge = 1)
	@Path("/documentFormats")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
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
			return ((DocumentFormatsDao)DatasourceManagerUtil
					.getDocumentFormats()).search(
					    id, 
					    formatName, 
					    formatDescription, 
					    formatInuseCount, 
					    createdTime, 
					    modifiedTime
					);
		} else {
			return DatasourceManagerUtil.getDocumentFormats().all();
		}
	}
	
	@CrossOriginResourceSharing(allowAllOrigins = true, allowCredentials = true, maxAge = 1)
	@Path("/documentFormats/search")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public List<DocumentFormat> searchDocumentFormat(
			@JsonProperty DocumentFormatSearch documentFormatSearch) {
		return ((DocumentFormatsDao)DatasourceManagerUtil
				.getDocumentFormats()).search(
			documentFormatSearch.getId(), 
			documentFormatSearch.getFormatName(), 
			documentFormatSearch.getFormatDescription(), 
			documentFormatSearch.getFormatInuseCount(), 
			documentFormatSearch.getCreatedTime(),
			documentFormatSearch.getModifiedTime()
		);
	}
	
	@CrossOriginResourceSharing(allowAllOrigins = true, allowCredentials = true, maxAge = 1)
	@Path("/documentFormats/{id}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public DocumentFormat getDocumentFormat(@PathParam("id") Integer id) {
		return DatasourceManagerUtil.getDocumentFormats().byId(id);
	}
	
	@CrossOriginResourceSharing(allowAllOrigins = true, allowCredentials = true, maxAge = 1)
	@Path("/documentFormats")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public DocumentFormat createDocumentFormat(@JsonProperty DocumentFormat docFormat) {
		// TODO validate document format object
		//save document format
		return DatasourceManagerUtil.getDocumentFormats().save(docFormat);
	}
	
	@CrossOriginResourceSharing(allowAllOrigins = true, allowCredentials = true, maxAge = 1)
	@Path("/documentFormats/{id}")
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public DocumentFormat saveDocumentFormat(
			@PathParam("id") Integer id, 
			@JsonProperty DocumentFormat docFormat) {
		return DatasourceManagerUtil.getDocumentFormats().save(docFormat);
	}
	
	@CrossOriginResourceSharing(allowAllOrigins = true, allowCredentials = true, maxAge = 1)
	@Path("/documentFormats/{id}")
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public void removeDocumentFormat(@PathParam("id") Integer id) {
		DocumentFormat docFormat = DatasourceManagerUtil.getDocumentFormats().byId(id);
		if(docFormat!=null) {
			DatasourceManagerUtil.getDocumentFormats().remove(docFormat);
		}
	}
	
	
	//////////////////////////////////////////////
	// Entity Codes
	//////////////////////////////////////////////
	
	/**
	 * This  is the <code>GET</code> version of the <code>entityCodes/search</code> 
	 * @param id           <code>Integer></code>
	 * @param code         <code>Integer</code>
	 * @param description  <code>String</code>
	 * @param createdTime  <code>Long</code>
	 * @param modifiedTime <code>Long</code>
	 * @return
	 */
	@CrossOriginResourceSharing(allowAllOrigins = true, allowCredentials = true, maxAge = 1)
	@Path("/entityCodes")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<EntityCode> getEntityCodes(
			@QueryParam("id") Integer id, 
			@QueryParam("code") Integer code,
			@QueryParam("description") String description, 
			@QueryParam("createdTime") Long createdTime, 
			@QueryParam("modifiedTime") Long modifiedTime
		) {
		if(id!=null || code!=null || description !=null || createdTime!=null || modifiedTime!=null) {
			return ((EntityCodesDao)DatasourceManagerUtil.getEntityCodes()).search(id, code, description, createdTime, modifiedTime);
		} else {
			return DatasourceManagerUtil.getEntityCodes().all();
		}
	}
	
	@CrossOriginResourceSharing(allowAllOrigins = true, allowCredentials = true, maxAge = 1)
	@Path("/entityCodes/search/")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public List<EntityCode> getEntityCodes(@JsonProperty EntityCodeSearch entityCodeSearch) {
		return ((EntityCodesDao)DatasourceManagerUtil.getEntityCodes()).search(
				entityCodeSearch.getId(), 
				entityCodeSearch.getCode(), 
				entityCodeSearch.getDescription(), 
				entityCodeSearch.getCreatedTime(), 
				entityCodeSearch.getModifiedTime()
		);
	}
	
	/**
	 * The read (single) method to the EntityCodes REST API
	 * Returning a single EntityCode that has an identifier matching the value in
	 * the request path.
	 * @param id An integer used as the EntityCode identifier
	 * @return An EntityCode or nothing if not found.
	 */
	@CrossOriginResourceSharing(allowAllOrigins = true, allowCredentials = true, maxAge = 1)
	@Path("/entityCodes/{id}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public EntityCode getEntityCode(@PathParam("id") Integer id) {
		return DatasourceManagerUtil.getEntityCodes().byId(id);
	}
	
	/**
	 * The create (single) method for the EntityCodes REST API
	 * 
	 * @param entityCode An <code>EntityCode</code> JSON object
	 * @return The created <code>EntityCode</code>
	 */
	@CrossOriginResourceSharing(allowAllOrigins = true, allowCredentials = true, maxAge = 1)
	@Path("/entityCodes")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public EntityCode createEntityCode(@JsonProperty EntityCode entityCode) {
		// TODO validate document format object
		return DatasourceManagerUtil.getEntityCodes().save(entityCode);
	}
	
	@Path("/entityCodes/{id}")
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public EntityCode saveEntityCode(@PathParam("id") Integer id, @JsonProperty EntityCode ec) {
		// TODO server-side validation
		return DatasourceManagerUtil.getEntityCodes().save(ec);
	}
	
	@CrossOriginResourceSharing(allowAllOrigins = true, allowCredentials = true, maxAge = 1)
	@Path("/entityCodes/{id}")
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	public void removeEntityCode(@PathParam("id") Integer id) {
		EntityCode ec = DatasourceManagerUtil.getEntityCodes().byId(id);
		if(ec!=null) {
			DatasourceManagerUtil.getEntityCodes().remove(ec);
		}
	}
	
	
	//////////////////////////////////////////////
	// Organizations
	//////////////////////////////////////////////
	
	@CrossOriginResourceSharing(allowAllOrigins = true, allowCredentials = true, maxAge = 1)
	@Path("/organizations")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Organization> getOrganizations(
			@QueryParam("directoryId") Integer directoryId, 
			@QueryParam("organizationId") String organizationId,
			@QueryParam("organizationIdType") String organizationIdType,
			@QueryParam("organizationName") String organizationName,
			@QueryParam("organizationSubcode") String organizationSubcode,
			@QueryParam("entityId") Integer entityId,
			@QueryParam("organizationEin") String organizationEin,
			@QueryParam("createdTime") Long createdTime,
			@QueryParam("modifiedTime") Long modifiedTime
		) {
		if(directoryId!=null || organizationId!=null || 
				organizationIdType!=null || organizationName!=null || 
				organizationSubcode!=null || entityId!=null || 
				organizationEin!=null || createdTime!=null || 
				modifiedTime!=null) {
			return ((OrganizationsDao)DatasourceManagerUtil.getOrganizations()).search(
					directoryId, 
					organizationId, 
					organizationIdType, 
					organizationName, 
					organizationSubcode, 
					entityId, 
					organizationEin, 
					createdTime, 
					modifiedTime
			);
		} else {
			return DatasourceManagerUtil.getOrganizations().all();
		}
	}
	
	@CrossOriginResourceSharing(allowAllOrigins = true, allowCredentials = true, maxAge = 1)
	@Path("/organizations/search")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public List<Organization> searchOrganizations(
			@JsonProperty OrganizationSearch organizationSearch) {
		return ((OrganizationsDao)DatasourceManagerUtil.getOrganizations()).search(
				organizationSearch.getDirectoryId(),
				organizationSearch.getOrganizationId(),
				organizationSearch.getOrganizationIdType(),
				organizationSearch.getOrganizationName(),
				organizationSearch.getOrganizationSubcode(),
				organizationSearch.getEntityId(),
				organizationSearch.getOrganizationEin(),
				organizationSearch.getCreatedTime(),
				organizationSearch.getModifiedTime()
		);
	}

	@CrossOriginResourceSharing(allowAllOrigins = true, allowCredentials = true, maxAge = 1)
	@Path("/organizations/{id}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Organization getOrganization(@PathParam("id") Integer id) {
		return DatasourceManagerUtil.getOrganizations().byId(id);
	}

	@CrossOriginResourceSharing(allowAllOrigins = true, allowCredentials = true, maxAge = 1)
	@Path("/organizations")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Organization createOrganization(@JsonProperty Organization org) {
		// TODO validate organization object
		//save organization object to persistence layer
		return DatasourceManagerUtil.getOrganizations().save(org);
	}
	
	@Path("/organizations/{directoryId}")
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Organization saveOrganization(@PathParam("directoryId") Integer id, 
			@JsonProperty Organization org) {
		return DatasourceManagerUtil.getOrganizations().save(org);
	}
	
	@CrossOriginResourceSharing(allowAllOrigins = true, allowCredentials = true, maxAge = 1)
	@Path("/organizations/{directoryId}")
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	public void removeOrganization(@PathParam("directoryId") Integer directoryId) {
		Organization org = DatasourceManagerUtil.getOrganizations().byId(directoryId);
		if(org!=null) {
			DatasourceManagerUtil.getOrganizations().remove(org);
		}
	}

    /**
     * Validates a file specified by fileFormat
     * @param uploadedInputStream the InputStream containing the file
     * @param fileFormatStr Cast to a ContentCodeType
     * @param xmlFileTypeStr Optional parameter, used for PESCXML validation, specifies whether HighSchool or College PESC Transcript.  Default: COLLEGE_TRANSCRIPT
     * @param versionStr Optional parameter, used for PESCXML validation, specifies the version of the HS/College transcript schema to validate with.  Default: V1_4_0.
     * @return Response containing a error message string if a validation error is encountered.
     */
    @CrossOriginResourceSharing(allowAllOrigins = true, allowCredentials = true, maxAge = 1)
	@POST
	@Path("/validateFile")  //Your Path or URL to call this service
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response validateFile(
            @Multipart("file") InputStream uploadedInputStream,
            @Multipart("fileFormat") String fileFormatStr,
			@Multipart(value="xmlType", required=false) String xmlFileTypeStr,
			@Multipart(value="schemaVersion", required=false) String versionStr)

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
