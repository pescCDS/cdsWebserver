package org.pesc.edexchange.v1_0.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.pesc.cds.webservice.service.HibernateUtil;
import org.pesc.edexchange.v1_0.Organization;

public class OrganizationsDao implements DBDataSourceDao<Organization> {
	private static final Log log = LogFactory.getLog(OrganizationsDao.class);
	
	
	/**
	 * Default no-arg constructor
	 */
	public OrganizationsDao() { }
	
	
	/**
	 * The <code>all()</code> method required by the <code>DBDataSourceDao</code> interface
	 * @return <code>List&lt;Organization&gt;</code>
	 */
	public List<Organization> all() {
		List<Organization> retList = new ArrayList<Organization>();
		try {
			if(HibernateUtil.getSessionFactory().isClosed()) {
				HibernateUtil.getSessionFactory().openSession();
			}
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			
			retList = session.createCriteria(Organization.class).list();
			
			session.getTransaction().commit();
			
		} catch(Exception ex) {
			log.error(ex.getMessage());
			ex.printStackTrace();
			HibernateUtil.getSessionFactory().getCurrentSession().getTransaction().rollback();
		}
		return retList;
	}
	
	
	/**
	 * The <code>byId()</code> method required by the <code>DBDataSourceDao</code> interface
	 * @param id <code>Integer</code> identifier value for the <code>organization_directory</code> record
	 * @return <code>Organization</code>
	 */
	public Organization byId(Integer id) {
		Organization retOrg = null;
		try {
			if(HibernateUtil.getSessionFactory().isClosed()) {
				HibernateUtil.getSessionFactory().openSession();
			}
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			
			retOrg = (Organization)session.get(Organization.class, id);
			
			session.getTransaction().commit();
			
		} catch(Exception ex) {
			log.error(ex.getMessage());
			ex.printStackTrace();
			HibernateUtil.getSessionFactory().getCurrentSession().getTransaction().rollback();
		}
		return retOrg;
	}
	
	// save Organization
	public Organization save(Organization org) {
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
	public Organization remove(Organization org) {
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
		return org;
	}
}
