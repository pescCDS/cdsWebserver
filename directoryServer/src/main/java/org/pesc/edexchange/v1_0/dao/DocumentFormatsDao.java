package org.pesc.edexchange.v1_0.dao;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.pesc.cds.webservice.service.HibernateUtil;
import org.pesc.edexchange.v1_0.DocumentFormat;

public class DocumentFormatsDao implements DBDataSourceDao<DocumentFormat> {
	private static final Log log = LogFactory.getLog(DocumentFormatsDao.class);
	
	public DocumentFormatsDao() { }
	
	
	public List<DocumentFormat> search(Integer id, String formatName, String formatDescription, Integer formatInuseCount, Long createdTime, Long modifiedTime) {
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
			
			if(formatName!=null) {
				ct.add(Restrictions.ilike("formatName", formatName, MatchMode.ANYWHERE));
				hasCriteria = true;
			}
			
			if(formatDescription!=null) {
				ct.add(Restrictions.ilike("formatDescription", formatDescription, MatchMode.ANYWHERE));
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
	
	
	public List<DocumentFormat> filterByName(String query) {
		List<DocumentFormat> retList = new ArrayList<DocumentFormat>();
		try {
			if(HibernateUtil.getSessionFactory().isClosed()) {
				HibernateUtil.getSessionFactory().openSession();
			}
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			
			Criteria ct = session.createCriteria(DocumentFormat.class);
			
			StringTokenizer tokens = new StringTokenizer(query);
			while(tokens.hasMoreElements()) {
				String token = tokens.nextToken();
				ct.add( Restrictions.like("formatName", token, MatchMode.ANYWHERE) );
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
	 * This instances <code>all()</code> method as required from the <code>DBDataSourceDao</code> interface
	 * 
	 * @return <code>List&lt;DocumentFormat&gt;</code>
	 */
	public List<DocumentFormat> all() {
		List<DocumentFormat> retList = new ArrayList<DocumentFormat>();
		try{
			if(HibernateUtil.getSessionFactory().isClosed()) {
				HibernateUtil.getSessionFactory().openSession();
			}
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			
			retList = session.createCriteria(DocumentFormat.class).list();
			
			session.getTransaction().commit();
			
		} catch(Exception ex) {
			log.error(ex.getMessage());
			ex.printStackTrace();
			HibernateUtil.getSessionFactory().getCurrentSession().getTransaction().rollback();
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
			
			// save to persistence layer
			session.saveOrUpdate(docFormat);
			log.debug(String.format("Saved %s",docFormat.toString()));
			
			// get the saved DocumentFormat object and put it into the return variable
			retDf = (DocumentFormat)session.get(DocumentFormat.class, docFormat.getId());
			
			// TODO re-populate the local List
			
			session.getTransaction().commit();
			
		} catch(Exception ex) {
			log.debug(ex.getMessage());
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
			HibernateUtil.getSessionFactory().getCurrentSession().getTransaction().rollback();
		}
		return docFormat;
	}
}
