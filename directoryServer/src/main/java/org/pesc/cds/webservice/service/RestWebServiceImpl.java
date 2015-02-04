package org.pesc.cds.webservice.service;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.annotate.JsonProperty;
import org.pesc.edexchange.v1_0.DocumentFormat;
import org.pesc.edexchange.v1_0.EntityCode;
import org.pesc.edexchange.v1_0.Organization;
import org.pesc.edexchange.v1_0.dao.DocumentFormatsDao;
import org.pesc.edexchange.v1_0.dao.EntityCodesDao;
import org.pesc.edexchange.v1_0.dao.OrganizationsDao;

/**
 * REST web service class
 * @author owenwe
 *
 */
public class RestWebServiceImpl {
	
	private static final Log log = LogFactory.getLog(RestWebServiceImpl.class);
	
	
	/***********************************************************************************
	 * These are for AJAX web services
	 * The only data served out should be auxilary tables where the total row count
	 * is under 300 or so. Bigger tables will need to go through the SOAP or REST
	 * web services agreed upon by the PESC EdExchange group
	 ***********************************************************************************/
	
	// Document Formats
	
	/**
	 * Returns all DocumentFormat objects in the persistence layer
	 * @return <code>List&lt;DocumentFormat&gt;</code>
	 */
	@Path("/documentFormats")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<DocumentFormat> getDocumentFormats() {
		return DatasourceManagerUtil.getDocumentFormats().all();
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
	
	
	// Entity Codes
	
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
	
	
	// Organizations
	
	@Path("/organizations")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Organization> getOrganizations() {
		return DatasourceManagerUtil.getOrganizations().all();
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
		log.debug(org);
		log.debug(String.format(
			"Organization {%n  directoryId:%s,%n  Id:%s,%n  Name:%s,%n  IdType:%s,%n  SubCode:%s,%n  EIN:%s,%n  EntityCode:%s,%n  SiteUrl:%s,%n  Description:%s,%n  termsOfUser:%s,%n  privacyPolicy:%s",
			org.getDirectoryId(),
			org.getOrganizationId(),
			org.getOrganizationName(),
			org.getOrganizationIdType(),
			org.getOrganizationSubcode(),
			org.getOrganizationEin(),
			org.getOrganizationEntityCode(),
			org.getOrganizationSiteUrl(),
			org.getDescription(),
			org.getTermsOfUse(),
			org.getPrivacyPolicy()
		));
		
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
