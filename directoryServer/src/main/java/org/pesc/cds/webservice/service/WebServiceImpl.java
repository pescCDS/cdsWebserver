package org.pesc.cds.webservice.service;

import java.util.List;

import org.hibernate.Session;
import org.pesc.cds.webservice.service.HibernateUtil;
import org.pesc.cds.webservice.WebServicesInterface;
import org.pesc.cds.webservice.domain.Organization;
import org.pesc.cds.webservice.ServletException;

public class WebServiceImpl implements WebServicesInterface {
	
	@Override
	public List<Organization> getOrganizations() throws ServletException {
		try {
			/*Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			
			List<Organization> list = session.createQuery("FROM Organization").list();
			
			session.getTransaction().commit();
			return list;*/
			return null;
		}
		catch (Exception ex) {
			//HibernateUtil.getSessionFactory().getCurrentSession().getTransaction().rollback();
			if ( ServletException.class.isInstance( ex ) ) {
                throw ( ServletException ) ex;
            }
            else {
                throw new ServletException( ex.getMessage() );
            }
		}
	}
	/*
	@Override
	public String getCDSBatch(String psisCode, String username, String password) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String sendCDSBatch(String psisCode, String username, String password, String cdsBatchXML) {
		// TODO Auto-generated method stub
		return null;
	}
	*/
}