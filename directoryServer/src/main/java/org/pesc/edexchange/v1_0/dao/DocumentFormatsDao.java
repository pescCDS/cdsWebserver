package org.pesc.edexchange.v1_0.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.pesc.cds.webservice.service.HibernateUtil;
import org.pesc.edexchange.v1_0.DocumentFormat;

public class DocumentFormatsDao implements DBDataSourceDao<DocumentFormat> {
	private static final Log log = LogFactory.getLog(DocumentFormatsDao.class);
	
	/**
	 * Default no-arg constructor
	 */
	public DocumentFormatsDao() { }
	
	
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
			retDf = (DocumentFormat)session.load(DocumentFormat.class, docFormat.getId());
			
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
			DocumentFormat df = (DocumentFormat)session.load(DocumentFormat.class, docFormat.getId());
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
