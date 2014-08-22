package org.pesc.cds.webservice.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.pesc.cds.webservice.service.HibernateUtil;
import org.pesc.cds.webservice.WebServicesInterface;
import org.pesc.cds.webservice.ServletException;
import org.pesc.core.coremain.v1_12.UserDefinedExtensionsType;
import org.pesc.edexchange.v1_0.ContentTypeType;
import org.pesc.edexchange.v1_0.DeliveryOptionType;
import org.pesc.sector.academicrecord.v1_7.OrganizationType;
import org.pesc.sector.academicrecord.v1_7.SourceDestinationType;


public class WebServiceImpl implements WebServicesInterface {
	
	@Override
	public List<SourceDestinationType> organizationListRequest(String organizationNameSearchString) throws ServletException {
		List<SourceDestinationType> retList = new ArrayList<SourceDestinationType>();
		try {
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			
			List<OrganizationType> orgList = session.createQuery("FROM Organizations").list();
			for(Iterator<OrganizationType> iter = orgList.iterator(); iter.hasNext();) {
				OrganizationType org = iter.next();
				SourceDestinationType sdt = new SourceDestinationType();
				sdt.setOrganization(org);
				retList.add(sdt);
			}
			
			session.getTransaction().commit();
			
			return retList;
		} catch(Exception ex) {
			HibernateUtil.getSessionFactory().getCurrentSession().getTransaction().rollback();
			if ( ServletException.class.isInstance( ex ) ) {
                throw ( ServletException ) ex;
            }
            else {
                throw new ServletException( ex.getMessage() );
            }
		}
	}

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