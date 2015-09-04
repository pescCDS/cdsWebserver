package org.pesc.cds.webservice.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.pesc.cds.webservice.service.HibernateUtil;
import org.pesc.cds.webservice.WebServicesInterface;
import org.pesc.cds.webservice.ServletException;
import org.pesc.core.coremain.v1_13.UserDefinedExtensionsType;
import org.pesc.edexchange.v1_0.ContentTypeType;
import org.pesc.edexchange.v1_0.DeliveryOptionType;
import org.pesc.edexchange.v1_0.Organization;
import org.pesc.sector.academicrecord.v1_8.OrganizationType;
import org.pesc.sector.academicrecord.v1_8.SourceDestinationType;


public class WebServiceImpl implements WebServicesInterface {
	
	private static final Log log = LogFactory.getLog(WebServiceImpl.class);
	
	
	/****************************************************************
	 * JSON AND SOAP METHOD?
	 ****************************************************************/
	/**/
	public List<Organization> organizationListRequest(String organizationNameSearchString) throws ServletException {
		try {
			return DatasourceManagerUtil.getOrganizations().all();
			
		} catch(Exception ex) {
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
	public String sendCDSBatch(String psisCode, String username, String password, String cdsBatchXML) {
		return "TODO: need content for the sendCDSBatch method";
	}

	
	public String getCDSBatch(String psisCode, String username, String password) {
		return "TODO: need content for the getCDSBatch method";
	}
	
	
	public List<DeliveryOptionType> deliveryOptionRequest(
			List<SourceDestinationType> destinationOrganization,
			ContentTypeType contentType, List<String> noteMessageText,
			UserDefinedExtensionsType userDefinedExtensionsText)
			throws ServletException {
		
		List<DeliveryOptionType> retList = new ArrayList<DeliveryOptionType>();
		return retList;
	}
	
	
	public String getDeliveryLocation() throws ServletException {
		return "TODO: need content for the getDeliveryLocation method";
	}

	
	public String updateLocalOptions() throws ServletException {
		return "TODO: need content for the updateLocalOptions method";
	}

	
	public String updateOrganization(OrganizationType organization) throws ServletException {
		return "TODO: need content for the updateOrganization method";
	}
	
}