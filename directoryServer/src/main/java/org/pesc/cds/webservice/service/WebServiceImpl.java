package org.pesc.cds.webservice.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.annotate.JsonProperty;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.pesc.cds.datatables.Column_Type;
import org.pesc.cds.datatables.DataTablesRequest;
import org.pesc.cds.datatables.Filter_Type;
import org.pesc.cds.webservice.service.HibernateUtil;
import org.pesc.cds.webservice.WebServicesInterface;
import org.pesc.cds.webservice.ServletException;
import org.pesc.core.coremain.v1_12.UserDefinedExtensionsType;
import org.pesc.edexchange.v1_0.ContentTypeType;
import org.pesc.edexchange.v1_0.DeliveryOptionType;
import org.pesc.sector.academicrecord.v1_7.OrganizationType;
import org.pesc.sector.academicrecord.v1_7.SourceDestinationType;


public class WebServiceImpl implements WebServicesInterface {
	
	private static final Log log = LogFactory.getLog(WebServiceImpl.class);
	
	
	/****************************************************************
	 * JSON AND SOAP METHOD?
	 ****************************************************************/
	/**/
	@Override
	public List<SourceDestinationType> organizationListRequest(String organizationNameSearchString) throws ServletException {
		List<SourceDestinationType> retList = new ArrayList<SourceDestinationType>();
		log.debug("trying to load organizations");
		try {
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			
			log.debug("grabbing organizations from session");
			List<OrganizationType> orgList = session.createQuery("FROM Organizations").list();
			log.debug("looping through results");
			for(Iterator<OrganizationType> iter = orgList.iterator(); iter.hasNext();) {
				OrganizationType org = iter.next();
				SourceDestinationType sdt = new SourceDestinationType();
				sdt.setOrganization(org);
				retList.add(sdt);
			}
			
			session.getTransaction().commit();
			log.debug("returning list of organizations");
			return retList;
		} catch(Exception ex) {
			HibernateUtil.getSessionFactory().getCurrentSession().getTransaction().rollback();
			log.debug("exception getting organizations");
			log.debug(ex.getMessage());
			if ( ServletException.class.isInstance( ex ) ) {
                throw ( ServletException ) ex;
            }
            else {
                throw new ServletException( ex.getMessage() );
            }
		}
	}
	
	
	/**/
	@Override
	public String sendCDSBatch(String psisCode, String username, String password, String cdsBatchXML) {
		return "TODO: need content for the sendCDSBatch method";
	}

	@Override
	public String getCDSBatch(String psisCode, String username, String password) {
		return "TODO: need content for the getCDSBatch method";
	}
	
	@Override
	public List<DeliveryOptionType> deliveryOptionRequest(
			List<SourceDestinationType> destinationOrganization,
			ContentTypeType contentType, List<String> noteMessageText,
			UserDefinedExtensionsType userDefinedExtensionsText)
			throws ServletException {
		
		List<DeliveryOptionType> retList = new ArrayList<DeliveryOptionType>();
		return retList;
	}
	
	@Override
	public String getDeliveryLocation() throws ServletException {
		return "TODO: need content for the getDeliveryLocation method";
	}

	@Override
	public String updateLocalOptions() throws ServletException {
		return "TODO: need content for the updateLocalOptions method";
	}

	@Override
	public String updateOrganization(OrganizationType organization) throws ServletException {
		return "TODO: need content for the updateOrganization method";
	}
	
}