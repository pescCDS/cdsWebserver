package org.pesc.cds.webservice.service;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.rs.security.cors.CrossOriginResourceSharing;
import org.codehaus.jackson.annotate.JsonProperty;
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
 * @author owenwe
 *
 */
@CrossOriginResourceSharing(
		allowAllOrigins = false,
		allowCredentials = false,
		allowOrigins = {"http://local.pesc.dev:8080"}, 
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
	
	@CrossOriginResourceSharing(allowOrigins = {"http://local.pesc.dev:8080"}, maxAge = 1)
	@Path("/contacts")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<OrganizationContact> getContacts(@QueryParam("query") String query) {
		if(query!=null) {
			return ((ContactsDao)DatasourceManagerUtil.getContacts()).filterByName(query);
		} else {
			return DatasourceManagerUtil.getContacts().all();
		}
	}
	
	@Path("/contacts/{contactId}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public OrganizationContact getContact(@PathParam("contactId") Integer id) {
		return DatasourceManagerUtil.getContacts().byId(id);
	}
	
	@Path("/contacts/save/")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public OrganizationContact saveContact(@JsonProperty OrganizationContact contact) {
		log.debug("saving Contact {");
		log.debug(String.format(" id: %s", contact.getContactId()));
		log.debug(String.format(" name: %s", contact.getContactName()));
		log.debug(String.format(" title: %s", contact.getContactTitle()));
		log.debug(String.format(" type: %s", contact.getContactType()));
		log.debug(String.format(" email: %s", contact.getEmail()));
		log.debug(String.format(" phone1: %s", contact.getPhone1()));
		log.debug(String.format(" phone2: %s", contact.getPhone2()));
		log.debug(String.format(" streetAddress1: %s", contact.getStreetAddress1()));
		log.debug(String.format(" streetAddress2: %s", contact.getStreetAddress2()));
		log.debug(String.format(" streetAddress3: %s", contact.getStreetAddress3()));
		log.debug(String.format(" streetAddress4: %s", contact.getStreetAddress4()));
		log.debug(String.format(" city: %s", contact.getCity()));
		log.debug(String.format(" zip: %s", contact.getZip()));
		log.debug(String.format(" state: %s", contact.getState()));
		log.debug(String.format(" country: %s", contact.getCountry()));
		log.debug(String.format(" createdTime: %s", contact.getCreatedTime()));
		log.debug(String.format(" modifiedTime: %s", contact.getModifiedTime()));
		
		log.debug("}");
		
		return ((ContactsDao)DatasourceManagerUtil.getContacts()).save(contact);
	}
	
	@Path("/contacts/remove/")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public OrganizationContact removeContact(@JsonProperty OrganizationContact contact) {
		log.debug(contact);
		
		return ((ContactsDao)DatasourceManagerUtil.getContacts()).remove(contact);
	}
	
	
	//////////////////////////////////////////////
	// Delivery Methods
	//////////////////////////////////////////////
	
	@CrossOriginResourceSharing(allowOrigins = {"http://localhost:8080"}, maxAge = 1)
	@Path("/deliveryMethods")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<DeliveryMethod> getDeliveryMethods() {
		return DatasourceManagerUtil.getDeliveryMethods().all();
	}
	
	@Path("/deliveryMethods/{id}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public DeliveryMethod getDeliveryMethod(@PathParam("id") Integer id) {
		return DatasourceManagerUtil.getDeliveryMethods().byId(id);
	}
	
	@Path("/deliveryMethods/save/")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public DeliveryMethod saveDeliveryMethod(@JsonProperty DeliveryMethod method) {
		log.debug(String.format("saving DeliveryMethod { id: %s, method: %s }", method.getId(), method.getMethod()));
		
		return ((DeliveryMethodsDao)DatasourceManagerUtil.getDeliveryMethods()).save(method);
	}
	
	@Path("/deliveryMethods/remove/")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public DeliveryMethod removeDeliveryMethod(@JsonProperty DeliveryMethod method) {
		log.debug(method);
		
		return ((DeliveryMethodsDao)DatasourceManagerUtil.getDeliveryMethods()).remove(method);
	}
	
	
	//////////////////////////////////////////////
	// Delivery Options
	//////////////////////////////////////////////
	
	@CrossOriginResourceSharing(allowOrigins = {"http://localhost:8080"}, maxAge = 1)
	@Path("/deliveryOptions")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<DeliveryOption> getDeliveryOptions() {
		return DatasourceManagerUtil.getDeliveryOptions().all();
	}
	
	@Path("/deliveryOptions/{id}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public DeliveryOption getDeliveryOptions(@PathParam("id") Integer id) {
		return DatasourceManagerUtil.getDeliveryOptions().byId(id);
	}
	
	@Path("/deliveryOptions/save/")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public DeliveryOption saveDeliveryOptions(@JsonProperty DeliveryOption option) {
		log.debug(String.format(
			"saving DeliveryOption {%n id: %s,%n member: %s,%n format: %s,%n deliveryMethod: %s,%n deliveryConfirm: %s,%n error: %s,%n operationalStatus: %s%n}", 
			option.getId(),
			option.getMember().getOrganizationName(),
			option.getFormat().getFormatName(),
			option.getDeliveryMethod().getMethod(),
			option.isDeliveryConfirm(),
			option.isError(),
			option.getOperationalStatus()
		));
		
		return ((DeliveryOptionsDao)DatasourceManagerUtil.getDeliveryOptions()).save(option);
	}
	
	@Path("/deliveryOptions/remove/")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public DeliveryOption removeDeliveryOptions(@JsonProperty DeliveryOption option) {
		log.debug(option);
		
		return ((DeliveryOptionsDao)DatasourceManagerUtil.getDeliveryOptions()).remove(option);
	}
	
	
	//////////////////////////////////////////////
	// Document Formats
	//////////////////////////////////////////////
	
	/**
	 * Returns all DocumentFormat objects in the persistence layer
	 * @return <code>List&lt;DocumentFormat&gt;</code>
	 */
	@CrossOriginResourceSharing(allowOrigins = {"http://localhost:8080"}, maxAge = 1)
	@Path("/documentFormats")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<DocumentFormat> getDocumentFormats(@QueryParam("query") String query) {
		if(query!=null) {
			return ((DocumentFormatsDao)DatasourceManagerUtil.getDocumentFormats()).filterByName(query);
		} else {
			return DatasourceManagerUtil.getDocumentFormats().all();
		}
	}
	
	@Path("/documentFormats/{id}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public DocumentFormat getDocumentFormat(@PathParam("id") Integer id) {
		return DatasourceManagerUtil.getDocumentFormats().byId(id);
	}
	
	// Document Formats
	@Path("/documentFormats/save/")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public DocumentFormat saveDocumentFormat(@JsonProperty DocumentFormat docFormat) {
		// TODO validate document format object
		log.debug(docFormat);
		
		//save document format
		return ((DocumentFormatsDao)DatasourceManagerUtil.getDocumentFormats()).save(docFormat);
	}
	
	@Path("/documentFormats/remove/")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public DocumentFormat removeDocumentFormat(@JsonProperty DocumentFormat docFormat) {
		log.debug(docFormat);
		
		//remove document format
		return ((DocumentFormatsDao)DatasourceManagerUtil.getDocumentFormats()).remove(docFormat);
	}
	
	
	//////////////////////////////////////////////
	// Entity Codes
	//////////////////////////////////////////////
	
	@CrossOriginResourceSharing(allowOrigins = {"http://localhost:8080"}, maxAge = 1)
	@Path("/entityCodes")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<EntityCode> getEntityCodes() {
		return DatasourceManagerUtil.getEntityCodes().all();
	}
	
	@Path("/entityCodes/{id}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public EntityCode getEntityCode(@PathParam("id") Integer id) {
		return DatasourceManagerUtil.getEntityCodes().byId(id);
	}
	
	@Path("/entityCodes/save/")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public EntityCode saveEntityCode(@JsonProperty EntityCode entityCode) {
		// TODO validate document format object
		log.debug(entityCode);
		
		//save document format object to persistence layer
		EntityCode df = ((EntityCodesDao)DatasourceManagerUtil.getEntityCodes()).save(entityCode);
		log.debug(df);
		return df;
	}
	
	@Path("/entityCodes/remove/")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public EntityCode removeEntityCode(@JsonProperty EntityCode entityCode) {
		log.debug(entityCode);
		
		//remove document format object from persistence layer
		return ((EntityCodesDao)DatasourceManagerUtil.getEntityCodes()).remove(entityCode);
	}
	
	
	//////////////////////////////////////////////
	// Organizations
	//////////////////////////////////////////////
	
	@CrossOriginResourceSharing(allowOrigins = {"http://localhost:8080"}, maxAge = 1)
	@Path("/organizations")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Organization> getOrganizations(@QueryParam("query") String query) {
		if(query!=null) {
			return ((OrganizationsDao)DatasourceManagerUtil.getOrganizations()).filterByName(query);
		} else {
			return DatasourceManagerUtil.getOrganizations().all();
		}
	}
	
	@Path("/organizations/{id}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Organization getOrganization(@PathParam("id") Integer id) {
		return DatasourceManagerUtil.getOrganizations().byId(id);
	}
	
	@Path("/organizations/save/")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Organization saveOrganization(@JsonProperty Organization org) {
		// TODO validate organization object
		log.debug("Organization {");
		log.debug(String.format("  directoryId:%s,", org.getDirectoryId()));
		log.debug(String.format("  Id:%s,", org.getOrganizationId()));
		log.debug(String.format("  Name:%s,", org.getOrganizationName()));
		log.debug(String.format("  IdType:%s,", org.getOrganizationIdType()));
		log.debug(String.format("  SubCode:%s,", org.getOrganizationSubcode()));
		log.debug(String.format("  EIN:%s,", org.getOrganizationEin()));
		log.debug(String.format("  EntityCode:%s,", org.getEntity().getCode()));
		log.debug(String.format("  SiteUrl:%s,", org.getOrganizationSiteUrl()));
		log.debug(String.format("  Description:%s,", org.getDescription()));
		log.debug(String.format("  termsOfUser:%s,", org.getTermsOfUse()));
		log.debug(String.format("  privacyPolicy:%s,", org.getPrivacyPolicy()));
		log.debug(String.format("  createdTime:%s,", org.getCreatedTime()));
		log.debug(String.format("  modifiedTime:%s", org.getModifiedTime()));
		log.debug("}");
		
		//save organization object to persistence layer
		Organization retOrg = ((OrganizationsDao)DatasourceManagerUtil.getOrganizations()).save(org);
		log.debug(retOrg);
		return retOrg;
	}
	
	@Path("/organizations/remove/")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Organization removeOrganization(@JsonProperty Organization org) {
		log.debug(org);
		
		//remove organization object from persistence layer
		return ((OrganizationsDao)DatasourceManagerUtil.getOrganizations()).remove(org);
	}
	
}
