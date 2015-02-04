package org.pesc.edexchange.v1_0.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.pesc.cds.webservice.service.HibernateUtil;
import org.pesc.edexchange.v1_0.EntityCode;

public class EntityCodesDao implements DBDataSourceDao<EntityCode> {
	private static final Log log = LogFactory.getLog(EntityCodesDao.class);
	
	/**
	 * Default no-arg constructor
	 */
	public EntityCodesDao() { }
	
	/**
	 * The <code>all()</code> method implemented from the <code>DBDataSourceDao</code> interface.
	 * @return <code>List&lt;EntityCode&gt;</code> All records from the <code>organization_entity_code</code> table.
	 */
	public List<EntityCode> all() {
		List<EntityCode> retList = new ArrayList<EntityCode>();
		try {
			if(HibernateUtil.getSessionFactory().isClosed()) {
				HibernateUtil.getSessionFactory().openSession();
			}
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			
			Criteria ct = session.createCriteria(EntityCode.class);
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
	 * The <code>byId()</code> method implemented from the <code>DBDataSourceDao</code> interface.
	 * 
	 * @param id <code>Integer</code> The identifier value for the record.
	 * @return <code>EntityCode</code>
	 */
	public EntityCode byId(Integer id) {
		EntityCode retEC = null;
		try {
			if(HibernateUtil.getSessionFactory().isClosed()) {
				HibernateUtil.getSessionFactory().openSession();
			}
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			
			retEC = (EntityCode)session.get(EntityCode.class, id);
			
			session.getTransaction().commit();
			
		} catch(Exception ex) {
			log.error(ex.getMessage());
			ex.printStackTrace();
			HibernateUtil.getSessionFactory().getCurrentSession().getTransaction().rollback();
		}
		return retEC;
	}
	
	// save Entity Code
	public EntityCode save(EntityCode entityCode) {
		EntityCode retEc = null;
		try {
			if(HibernateUtil.getSessionFactory().isClosed()) {
				HibernateUtil.getSessionFactory().openSession();
			}
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			
			// save EntityCode to persistence layer
			session.saveOrUpdate(entityCode);
			log.debug(String.format("Saved %s",entityCode.toString()));
			
			// get the saved EntityCode and load it into the return variable
			retEc = (EntityCode)session.get(EntityCode.class, entityCode.getId());
			
			session.getTransaction().commit();
			
		} catch(Exception ex) {
			log.debug(ex.getMessage());
			ex.printStackTrace();
			HibernateUtil.getSessionFactory().getCurrentSession().getTransaction().rollback();
		}
		return retEc;
	}
	
	// remove Entity Code
	public EntityCode remove(EntityCode entityCode) {
		try {
			if(HibernateUtil.getSessionFactory().isClosed()) {
				HibernateUtil.getSessionFactory().openSession();
			}
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			
			// load the EntityCode from the persistence layer and then delete it
			EntityCode ec = (EntityCode)session.get(EntityCode.class, entityCode.getId());
			session.delete(ec);
			
			session.getTransaction().commit();
			
		} catch(Exception ex) {
			log.debug(ex.getMessage());
			ex.printStackTrace();
			HibernateUtil.getSessionFactory().getCurrentSession().getTransaction().rollback();
		}
		return entityCode;
	}
}
