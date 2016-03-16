package org.pesc.service.dao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.pesc.edexchange.v1_0.EntityCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManagerFactory;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

@Repository
public class EntityCodesDaoImpl extends AbstractDaoImpl<EntityCode> implements EntityCodesDao {
	private static final Log log = LogFactory.getLog(EntityCodesDaoImpl.class);

	@Autowired
	public EntityCodesDaoImpl(EntityManagerFactory factory) {
		super(factory);
	}

	@Override
	public List<EntityCode> search(Integer id, Integer code, String description,
								   Long createdTime, Long modifiedTime) {
		List<EntityCode> retList = new ArrayList<EntityCode>();
		try {
			if(hibernateFactory.isClosed()) {
				hibernateFactory.openSession();
			}
			Session session = hibernateFactory.getCurrentSession();
			session.beginTransaction();
			
			Criteria ct = session.createCriteria(EntityCode.class);
			boolean hasCriteria = false;
			
			if(id!=null) {
				ct.add(Restrictions.idEq(id));
				hasCriteria = true;
			}
			
			if(code!=null) {
				ct.add(Restrictions.eq("code", code));
				hasCriteria = true;
			}
			
			if(description!=null && description.trim().length()>0) {
				StringTokenizer tokens = new StringTokenizer(description.trim());
				while(tokens.hasMoreElements()) {
					String token = tokens.nextToken();
					ct.add(Restrictions.ilike("description", token, MatchMode.ANYWHERE));
				}
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
			hibernateFactory.getCurrentSession().getTransaction().rollback();
		}
		return retList;
	}
	
	/**
	 * Thymeleaf 2.1.4 will output Date/Calendar/Timestamp values as ISO8601 
	 * date string literal from a script inline output
	 * Thymeleaf < 2.1.4 would output the Date/Calendar/Timestamp value as a 
	 * JSON object of that class
	 * @return List&lt;HashMap&lt;String, Object&gt;&gt; format of EntityCodes
	 */
	public List<HashMap<String, Object>> forJson() {
		List<HashMap<String, Object>> retList = new ArrayList<HashMap<String, Object>>();
		
		List<EntityCode> allECodes = all();
		
		for(EntityCode e : allECodes) {
			HashMap<String, Object> eobj = new HashMap<String, Object>();
			eobj.put("id", e.getId());
			eobj.put("code", e.getCode());
			eobj.put("description", e.getDescription());
			eobj.put("createdTime", e.getCreatedTime().getTimeInMillis());
			eobj.put("modifiedTime", e.getModifiedTime().getTimeInMillis());
			retList.add(eobj);
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
			if(hibernateFactory.isClosed()) {
				hibernateFactory.openSession();
			}
			Session session = hibernateFactory.getCurrentSession();
			session.beginTransaction();
			
			retEC = (EntityCode)session.get(EntityCode.class, id);
			
			session.getTransaction().commit();
			
		} catch(Exception ex) {
			log.error(ex.getMessage());
			ex.printStackTrace();
			hibernateFactory.getCurrentSession().getTransaction().rollback();
		}
		return retEC;
	}
	
	// save Entity Code
	public EntityCode save(EntityCode entityCode) {
		EntityCode retEc = null;
		try {
			if(hibernateFactory.isClosed()) {
				hibernateFactory.openSession();
			}
			Session session = hibernateFactory.getCurrentSession();
			session.beginTransaction();
			
			// strip out created, modified because the database handles those values
			entityCode.setCreatedTime(null);
			entityCode.setModifiedTime(null);
			
			// save EntityCode to persistence layer
			session.saveOrUpdate(entityCode);
			log.debug(String.format("Saved %s",entityCode.toString()));
			
			// get the saved EntityCode and load it into the return variable
			retEc = (EntityCode)session.get(EntityCode.class, entityCode.getId());
			
			session.getTransaction().commit();
			
		} catch(Exception ex) {
			log.debug(ex.getMessage());
			ex.printStackTrace();
			hibernateFactory.getCurrentSession().getTransaction().rollback();
		}
		return retEc;
	}
	
	// remove Entity Code
	public EntityCode remove(EntityCode entityCode) {
		try {
			if(hibernateFactory.isClosed()) {
				hibernateFactory.openSession();
			}
			Session session = hibernateFactory.getCurrentSession();
			session.beginTransaction();
			
			// load the EntityCode from the persistence layer and then delete it
			EntityCode ec = (EntityCode)session.get(EntityCode.class, entityCode.getId());
			session.delete(ec);
			
			session.getTransaction().commit();
			
		} catch(Exception ex) {
			log.debug(ex.getMessage());
			ex.printStackTrace();
			hibernateFactory.getCurrentSession().getTransaction().rollback();
		}
		return entityCode;
	}
}
