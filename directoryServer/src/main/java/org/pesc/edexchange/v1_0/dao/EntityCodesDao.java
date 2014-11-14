package org.pesc.edexchange.v1_0.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.pesc.cds.directoryserver.view.EntityCodeJson;
import org.pesc.cds.webservice.service.HibernateUtil;
import org.pesc.edexchange.v1_0.EntityCode;

public class EntityCodesDao {
	private static final Log log = LogFactory.getLog(EntityCodesDao.class);
	private static List<EntityCode> entityCodes;
	
	// Constructor
	public EntityCodesDao() {
		entityCodes = new ArrayList<EntityCode>();
		try {
			if(HibernateUtil.getSessionFactory().isClosed()) {
				HibernateUtil.getSessionFactory().openSession();
			}
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			
			this.entityCodes = session.createQuery("FROM EntityCode").list();
			
			session.getTransaction().commit();
		} catch(Exception ex) {
			HibernateUtil.getSessionFactory().getCurrentSession().getTransaction().rollback();
		}
	}
	
	// save Entity Code
	public static EntityCode save(EntityCode entityCode) {
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
			retEc = (EntityCode)session.load(EntityCode.class, entityCode.getId());
			
			// TODO re-populate the local List
			
			session.getTransaction().commit();
			
		} catch(Exception ex) {
			log.debug(ex.getMessage());
			HibernateUtil.getSessionFactory().getCurrentSession().getTransaction().rollback();
		}
		return retEc;
	}
	
	// remove Entity Code
	public static void remove(EntityCode entityCode) {
		try {
			if(HibernateUtil.getSessionFactory().isClosed()) {
				HibernateUtil.getSessionFactory().openSession();
			}
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			
			// load the EntityCode from the persistence layer and then delete it
			EntityCode ec = (EntityCode)session.load(EntityCode.class, entityCode.getId());
			session.delete(ec);
			
			// TODO re-populate local List
			
			session.getTransaction().commit();
			
		} catch(Exception ex) {
			log.debug(ex.getMessage());
			HibernateUtil.getSessionFactory().getCurrentSession().getTransaction().rollback();
		}
	}
	
	public static List<EntityCode> getEntityCodes() { return entityCodes; }
}
