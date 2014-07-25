package org.pesc.ns.webservice.service;

import java.util.List;

//import org.hibernate.Session;
//import org.pesc.ns.webservice.service.HibernateUtil;
import org.pesc.ns.webservice.ServletException;
import org.pesc.ns.webservice.domain.Organization;
import org.pesc.ns.webservice.WebServicesInterface;



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
}
