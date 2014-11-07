package org.pesc.edexchange.v1_0.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.pesc.cds.directoryserver.view.DocumentFormatJson;
import org.pesc.cds.webservice.service.HibernateUtil;
import org.pesc.edexchange.v1_0.DocumentFormat;

public class DocumentFormatsDao {
	private static final Log log = LogFactory.getLog(DocumentFormatsDao.class);
	private static List<DocumentFormat> documentFormats;
	
	// Constructor
	public DocumentFormatsDao() {
		documentFormats = new ArrayList<DocumentFormat>();
		
		// load the local List from the persistence layer 
		try {
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			
			this.documentFormats = session.createQuery("FROM DocumentFormat").list();
			
			session.getTransaction().commit();
		} catch(Exception ex) {
			HibernateUtil.getSessionFactory().getCurrentSession().getTransaction().rollback();
		} 
	}
	
	// returns a single DocumentFormat object that matches the given format id (get document format by id)
	public static DocumentFormat get(Integer formatId) {
		DocumentFormat df = null;
		// search through the local List
		for(ListIterator<DocumentFormat> iter = documentFormats.listIterator(); iter.hasNext();) {
			DocumentFormat d = iter.next();
			if(formatId.equals(d.getId())) {
				df = d;
				break;
			}
		}
		
		// if a DocumentFormat object wasn't found in the local List, then search the persistence layer
		if(df == null) {
			log.debug("didn't find document format in class list, checking persistence ...");
			try {
				Session session = HibernateUtil.getSessionFactory().getCurrentSession();
				session.beginTransaction();
				
				df = (DocumentFormat)session.load(DocumentFormat.class.getName(), formatId);
				
				session.getTransaction().commit();
				
			} catch(Exception ex) {
				HibernateUtil.getSessionFactory().getCurrentSession().getTransaction().rollback();
			}
		}
		return df;
	}
	
	// save DocumentFormat
	public static DocumentFormat save(DocumentFormatJson jsDocFormat) {
		DocumentFormat retDf = null;
		try {
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			
			// create a new DocumentFormat object to save
			DocumentFormat df = new DocumentFormat();
			if(jsDocFormat.getId()!=null) {
				df.setId(jsDocFormat.getId());
			}
			df.setFormatName(jsDocFormat.getFormat_name());
			df.setFormatDescription(jsDocFormat.getFormat_description());
			
			// save to persistence layer
			session.saveOrUpdate(df);
			log.debug(String.format("Saved %s",df.toString()));
			
			// get the saved DocumentFormat object and put it into the return variable
			retDf = (DocumentFormat)session.load(DocumentFormat.class.getName(), df.getId());
			
			// TODO re-populate the local List
			
			session.getTransaction().commit();
			
		} catch(Exception ex) {
			log.debug(ex.getMessage());
			HibernateUtil.getSessionFactory().getCurrentSession().getTransaction().rollback();
		}
		return retDf;
	}
	// remove DocumentFormat
	public static void remove(DocumentFormatJson jsDocFormat) {
		try {
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			
			// get the Entity Code object from the persistence layer and delete it
			DocumentFormat df = (DocumentFormat)session.load(DocumentFormat.class, jsDocFormat.getId());
			session.delete(df);
			
			// TODO re-populate the local List
			
			session.getTransaction().commit();
			
		} catch(Exception ex) {
			log.debug(ex.getMessage());
			HibernateUtil.getSessionFactory().getCurrentSession().getTransaction().rollback();
		}
	}
	
	public List<DocumentFormat> getDocumentFormats() { return documentFormats; }
}
