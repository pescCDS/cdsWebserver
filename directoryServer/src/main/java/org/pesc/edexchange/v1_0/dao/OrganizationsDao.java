package org.pesc.edexchange.v1_0.dao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.pesc.cds.webservice.service.HibernateUtil;
import org.pesc.edexchange.v1_0.Organization;

public class OrganizationsDao {
	private static final Log log = LogFactory.getLog(OrganizationsDao.class);
	
	// save Organization
	// TODO check if we can use an Organization object instead of an in between json class
	public static Organization save(Organization org) {
		Organization retOrg = null;
		try {
			if(HibernateUtil.getSessionFactory().isClosed()) {
				HibernateUtil.getSessionFactory().openSession();
			}
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			
			// save organization to persistence layer
			session.saveOrUpdate(org);
			
			// load the saved organization into the return variable
			retOrg = (Organization)session.load(Organization.class, org.getDirectoryId());
			
			session.getTransaction().commit();
			
		} catch(Exception ex) {
			log.debug(ex.getMessage());
			HibernateUtil.getSessionFactory().getCurrentSession().getTransaction().rollback();
		}
		return retOrg;
	}
	
	// remove Organization
	public static void remove(Organization org) {
		try {
			if(HibernateUtil.getSessionFactory().isClosed()) {
				HibernateUtil.getSessionFactory().openSession();
			}
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			
			// load the Organization from the persistence layer and then delete it
			Organization remOrg = (Organization)session.load(Organization.class, org.getDirectoryId());
			session.delete(remOrg);
			
			session.getTransaction().commit();
			
		} catch(Exception ex) {
			log.debug(ex.getMessage());
			HibernateUtil.getSessionFactory().getCurrentSession().getTransaction().rollback();
		}
	}
}
