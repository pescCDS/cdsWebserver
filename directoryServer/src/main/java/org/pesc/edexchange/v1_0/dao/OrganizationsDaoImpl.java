package org.pesc.edexchange.v1_0.dao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.pesc.cds.webservice.service.HibernateUtil;
import org.pesc.edexchange.v1_0.Organization;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

@Repository
public class OrganizationsDaoImpl extends AbstractDaoImpl<Organization> implements OrganizationsDao {
	private static final Log log = LogFactory.getLog(OrganizationsDaoImpl.class);
	
	@Override
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
			
			if(organizationId!=null && organizationId.trim().length()>0) {
				ct.add(Restrictions.ilike("organizationId", organizationId.trim(), MatchMode.START));
				hasCriteria = true;
			}
			
			if(organizationIdType!=null && organizationIdType.trim().length()>0) {
				ct.add(Restrictions.ilike("organizationIdType", organizationIdType.trim(), MatchMode.START));
				hasCriteria = true;
			}
			
			if(organizationName!=null && organizationName.trim().length()>0) {
				// tokenize string by whitespace and use each token as
				// a LIKE clause for the organizationName column 
				StringTokenizer orgNametokens = new StringTokenizer(organizationName.trim());
				while(orgNametokens.hasMoreElements()) {
					String orgNametoken = orgNametokens.nextToken();
					ct.add( Restrictions.ilike("organizationName", orgNametoken, MatchMode.ANYWHERE));
				}
				hasCriteria = true;
			}
			
			if(organizationSubcode!=null && organizationSubcode.trim().length()>0) {
				StringTokenizer subCodetokens = new StringTokenizer(organizationSubcode.trim());
				while(subCodetokens.hasMoreElements()) {
					String subCodetoken = subCodetokens.nextToken();
					ct.add( Restrictions.ilike("organizationSubcode", subCodetoken, MatchMode.ANYWHERE));
				}
				hasCriteria = true;
			}
			
			if(entityId!=null) {
				ct.createAlias("ent", "entity");
				ct.add(Restrictions.eq("ent.id", entityId));
				hasCriteria = true;
			}
			
			if(organizationEin!=null && organizationEin.trim().length()>0) {
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
			
			// strip out created, modified because the database handles those values
			org.setCreatedTime(null);
			org.setModifiedTime(null);
			
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
