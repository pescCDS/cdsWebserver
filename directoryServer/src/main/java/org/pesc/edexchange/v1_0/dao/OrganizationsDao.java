package org.pesc.edexchange.v1_0.dao;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.pesc.cds.webservice.service.HibernateUtil;
import org.pesc.edexchange.v1_0.Organization;

public class OrganizationsDao implements DBDataSourceDao<Organization> {
	private static final Log log = LogFactory.getLog(OrganizationsDao.class);
	
	
	/**
	 * Default no-arg constructor
	 */
	public OrganizationsDao() { }
	
	
	
	public List<Organization> search(
			Integer directoryId, 
			String organizationId, 
			String organizationIdType, 
			String organizationName, 
			String organizationSubcode, 
			Integer entityId,
			String organizationEin,
			Long createdTime,
			Long modifiedTime
		) {
		List<Organization> retList = new ArrayList<Organization>();
		try {
			if(HibernateUtil.getSessionFactory().isClosed()) {
				HibernateUtil.getSessionFactory().openSession();
			}
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			
			Criteria ct = session.createCriteria(Organization.class);
			boolean hasCriteria = false;
			
			if(directoryId!=null) {
				ct.add(Restrictions.idEq(directoryId));
				hasCriteria = true;
			}
			
			if(organizationId!=null) {
				ct.add(Restrictions.ilike("organizationId", organizationId, MatchMode.START));
				hasCriteria = true;
			}
			
			if(organizationIdType!=null) {
				ct.add(Restrictions.ilike("organizationIdType", organizationIdType, MatchMode.START));
				hasCriteria = true;
			}
			
			if(organizationName!=null) {
				ct.add(Restrictions.ilike("organizationName", organizationName, MatchMode.ANYWHERE));
				hasCriteria = true;
			}
			
			if(organizationSubcode!=null) {
				ct.add(Restrictions.ilike("organizationSubcode", organizationSubcode, MatchMode.ANYWHERE));
				hasCriteria = true;
			}
			
			if(entityId!=null) {
				ct.createAlias("ent", "entity");
				ct.add(Restrictions.eq("ent.id", entityId));
				hasCriteria = true;
			}
			
			if(organizationEin!=null) {
				ct.add(Restrictions.ilike("organizationEin", organizationEin, MatchMode.START));
				hasCriteria = true;
			}
			
			if(createdTime!=null) {
				ct.add(Restrictions.eq("createdTime", new Timestamp(createdTime)));
				hasCriteria = true;
			}
			
			if(modifiedTime!=null) {
				ct.add(Restrictions.eq("modifiedTime", new Timestamp(modifiedTime)));
				hasCriteria = true;
			}
			
			
			if(hasCriteria) {
				retList = ct.list();
			}
			
			session.getTransaction().commit();
			
		} catch(Exception ex) {
			log.error(ex.getMessage());
			ex.printStackTrace();
			HibernateUtil.getSessionFactory().getCurrentSession().getTransaction().rollback();
		}
		return retList;
	}
	
	
	// 
	public List<Organization> filterByName(String query) {
		List<Organization> retList = new ArrayList<Organization>();
		try {
			if(HibernateUtil.getSessionFactory().isClosed()) {
				HibernateUtil.getSessionFactory().openSession();
			}
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			
			Criteria ct = session.createCriteria(Organization.class);
			
			// tokenize string by whitespace and use each token as
			// a LIKE clause for the organizationName column 
			StringTokenizer tokens = new StringTokenizer(query);
			while(tokens.hasMoreElements()) {
				String token = tokens.nextToken();
				ct.add( Restrictions.like("organizationName", token, MatchMode.ANYWHERE) );
			}
			
			retList = ct.list();
			
			session.getTransaction().commit();
		} catch(Exception ex) {
			log.error(ex.getMessage());
			ex.printStackTrace();
			HibernateUtil.getSessionFactory().getCurrentSession().getTransaction().rollback();
		}
		return retList;
	}
	
	
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
			retOrg = (Organization)session.get(Organization.class, org.getDirectoryId());
			
			session.getTransaction().commit();
			
		} catch(Exception ex) {
			log.debug(ex.getMessage());
			ex.printStackTrace();
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
			Organization remOrg = (Organization)session.get(Organization.class, org.getDirectoryId());
			session.delete(remOrg);
			
			session.getTransaction().commit();
			
		} catch(Exception ex) {
			log.debug(ex.getMessage());
			ex.printStackTrace();
			HibernateUtil.getSessionFactory().getCurrentSession().getTransaction().rollback();
		}
		return org;
	}
}
