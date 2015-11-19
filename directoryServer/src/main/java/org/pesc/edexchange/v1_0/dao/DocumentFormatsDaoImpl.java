package org.pesc.edexchange.v1_0.dao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.pesc.cds.webservice.service.HibernateUtil;
import org.pesc.edexchange.v1_0.DocumentFormat;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

@Repository
public class DocumentFormatsDaoImpl extends AbstractDaoImpl<DocumentFormat> implements DocumentFormatsDao {
	private static final Log log = LogFactory.getLog(DocumentFormatsDaoImpl.class);
	
	@Override
	public List<DocumentFormat> search(
			Integer id,
			String formatName,
			String formatDescription,
			Integer formatInuseCount,
			Long createdTime,
			Long modifiedTime) {
		List<DocumentFormat> retList = new ArrayList<DocumentFormat>();
		try {
			if(HibernateUtil.getSessionFactory().isClosed()) {
				HibernateUtil.getSessionFactory().openSession();
			}
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			
			Criteria ct = session.createCriteria(DocumentFormat.class);
			boolean hasCriteria = false;
			
			if(id!=null) {
				ct.add(Restrictions.idEq(id));
				hasCriteria = true;
			}
			
			if(formatName!=null && formatName.trim().length()>0) {
				StringTokenizer tokens = new StringTokenizer(formatName.trim());
				while(tokens.hasMoreElements()) {
					String token = tokens.nextToken();
					ct.add(Restrictions.ilike("formatName", token, MatchMode.ANYWHERE));
				}
				hasCriteria = true;
			}
			
			if(formatDescription!=null && formatDescription.trim().length()>0) {
				StringTokenizer tokens = new StringTokenizer(formatDescription.trim());
				while(tokens.hasMoreElements()) {
					String token = tokens.nextToken();
					ct.add(Restrictions.ilike("formatDescription", token, MatchMode.ANYWHERE));
				}
				hasCriteria = true;
			}
			
			if(formatInuseCount!=null) {
				ct.add(Restrictions.eq("formatInuseCount", formatInuseCount));
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
	 * Thymeleaf 2.1.4 will output Date/Calendar/Timestamp values as ISO8601 
	 * date string literal from a script inline output
	 * Thymeleaf < 2.1.4 would output the Date/Calendar/Timestamp value as a 
	 * JSON object of that class
	 * @return List&lt;HashMap&lt;String, Object&gt;&gt; format of DocumentFormats
	 */
	@Override
	public List<HashMap<String, Object>> forJson() {
		List<HashMap<String, Object>> retList = new ArrayList<HashMap<String, Object>>();
		List<DocumentFormat> docFormats = all();
		for(DocumentFormat df : docFormats) {
			HashMap<String, Object> dfObj = new HashMap<String, Object>();
			dfObj.put("id", df.getId());
			dfObj.put("formatName", df.getFormatName());
			dfObj.put("formatDescription", df.getFormatDescription());
			dfObj.put("formatInuseCount", df.getFormatInuseCount());
			dfObj.put("createdTime", df.getCreatedTime().getTimeInMillis());
			dfObj.put("modifiedTime", df.getModifiedTime().getTimeInMillis());
			retList.add(dfObj);
		}
		return retList;
	}
	
	
	/**
	 * The <code>byId()</code> method as defined in the <code>DBDataSourceDao</code> interface.
	 * @param id <code>Integer</code> identifier value for the document_format record
	 * @return <code>DocumentFormat</code>
	 */
	public DocumentFormat byId(Integer id) {
		DocumentFormat retDF = null;
		try {
			if(HibernateUtil.getSessionFactory().isClosed()) {
				HibernateUtil.getSessionFactory().openSession();
			}
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			
			retDF = (DocumentFormat)session.get(DocumentFormat.class, id);
			
			session.getTransaction().commit();
			
		} catch(Exception ex) {
			log.error(ex.getMessage());
			ex.printStackTrace();
			HibernateUtil.getSessionFactory().getCurrentSession().getTransaction().rollback();
		}
		return retDF;
	}
	
	
	// save DocumentFormat
	public DocumentFormat save(DocumentFormat docFormat) {
		DocumentFormat retDf = null;
		try {
			if(HibernateUtil.getSessionFactory().isClosed()) {
				HibernateUtil.getSessionFactory().openSession();
			}
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			
			// database handles create/modified values
			docFormat.setCreatedTime(null);
			docFormat.setModifiedTime(null);
			
			// save to persistence layer
			session.saveOrUpdate(docFormat);
			log.debug(String.format("Saved %s",docFormat.toString()));
			
			// get the saved DocumentFormat object and put it into the return variable
			retDf = (DocumentFormat)session.get(DocumentFormat.class, docFormat.getId());
			
			session.getTransaction().commit();
			
		} catch(Exception ex) {
			log.debug(ex.getMessage());
			ex.printStackTrace();
			HibernateUtil.getSessionFactory().getCurrentSession().getTransaction().rollback();
		}
		return retDf;
	}
	
	
	// remove DocumentFormat
	public DocumentFormat remove(DocumentFormat docFormat) {
		try {
			if(HibernateUtil.getSessionFactory().isClosed()) {
				HibernateUtil.getSessionFactory().openSession();
			}
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			
			// get the Entity Code object from the persistence layer and delete it
			DocumentFormat df = (DocumentFormat)session.get(DocumentFormat.class, docFormat.getId());
			session.delete(df);
			
			// TODO re-populate the local List
			
			session.getTransaction().commit();
			
		} catch(Exception ex) {
			log.debug(ex.getMessage());
			ex.printStackTrace();
			HibernateUtil.getSessionFactory().getCurrentSession().getTransaction().rollback();
		}
		return docFormat;
	}
}
