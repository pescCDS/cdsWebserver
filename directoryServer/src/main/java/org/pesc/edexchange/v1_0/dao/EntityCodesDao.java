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
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			
			this.entityCodes = session.createQuery("FROM EntityCode").list();
			
			session.getTransaction().commit();
		} catch(Exception ex) {
			HibernateUtil.getSessionFactory().getCurrentSession().getTransaction().rollback();
		}
	}
	
	// save Entity Code
	public static EntityCode save(EntityCodeJson jsEntityCode) {
		EntityCode retEc = null;
		try {
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			
			// create new EntityCode to save
			EntityCode ec = new EntityCode();
			if(jsEntityCode.getId()!=null) {
				ec.setId(jsEntityCode.getId());
			}
			ec.setEntityCode(jsEntityCode.getEntity_code());
			ec.setDescription(jsEntityCode.getDescription());
			
			// save EntityCode to persistence layer
			session.saveOrUpdate(ec);;
			log.debug(String.format("Saved %s",jsEntityCode.toString()));
			
			// get the saved EntityCode and load it into the return variable
			retEc = (EntityCode)session.load(EntityCode.class.getName(), ec.getId());
			
			// TODO re-populate the local List
			
			session.getTransaction().commit();
			
		} catch(Exception ex) {
			log.debug(ex.getMessage());
			HibernateUtil.getSessionFactory().getCurrentSession().getTransaction().rollback();
		}
		return retEc;
	}
	
	// remove Entity Code
	public static void remove(EntityCodeJson jsEntityCode) {
		try {
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			
			// load the EntityCode from the persistence layer and then delete it
			EntityCode ec = (EntityCode)session.load(EntityCode.class, jsEntityCode.getId());
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
