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
		refreshFromPersistence(); 
	}
	
	// refreshes the data from the persistence layer
	private void refreshFromPersistence() {
		try {
			documentFormats.clear();
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			
			this.documentFormats = session.createQuery("FROM DocumentFormat").list();
			
			session.getTransaction().commit();
		} catch(Exception ex) {
			HibernateUtil.getSessionFactory().getCurrentSession().getTransaction().rollback();
		}
	}
	
	// returns a single DocumentFormat object that matches the given format name (get document format by name)
	public static DocumentFormat getDocumentFormat(String formatName) {
		DocumentFormat df = null;
		for(ListIterator<DocumentFormat> iter = documentFormats.listIterator(); iter.hasNext();) {
			DocumentFormat d = iter.next();
			if(formatName.equals(d.getFormatName())) {
				df = d;
				break;
			}
		}
		if(df == null) {
			log.debug("didn't find document format in class list, checking persistence ...");
			try {
				Session session = HibernateUtil.getSessionFactory().getCurrentSession();
				session.beginTransaction();
				
				df = (DocumentFormat)session.load(DocumentFormat.class.getName(), formatName);
				
				session.getTransaction().commit();
				
			} catch(Exception ex) {
				HibernateUtil.getSessionFactory().getCurrentSession().getTransaction().rollback();
			}
		}
		return df;
	}
	
	// save DocumentFormat
	public static DocumentFormat saveDocumentFormat(DocumentFormatJson jsDocFormat) {
		DocumentFormat retDf = null;
		try {
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			
			DocumentFormat df = new DocumentFormat();
			if(jsDocFormat.getId()!=null) {
				df.setId(jsDocFormat.getId());
			}
			df.setFormatName(jsDocFormat.getFormat_name());
			df.setFormatDescription(jsDocFormat.getFormat_description());
			
			session.saveOrUpdate(df);
			log.debug(String.format(
				"Saved DocumentFormat {%n  id:%s,%n  formatName:%s,%n formatDescription:%s%n}",
				df.getId(),
				df.getFormatName(),
				df.getFormatDescription()
			));
			
			retDf = (DocumentFormat)session.load(DocumentFormat.class.getName(), df.getId());
			
			session.getTransaction().commit();
			
		} catch(Exception ex) {
			log.debug(ex.getMessage());
			HibernateUtil.getSessionFactory().getCurrentSession().getTransaction().rollback();
		}
		return retDf;
	}
	// TODO remove DocumentFormat
	public static void removeDocumentFormat(DocumentFormatJson jsDocFormat) {
		try {
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			
			DocumentFormat df = (DocumentFormat)session.load(DocumentFormat.class, jsDocFormat.getId());
			session.delete(df);
			
			session.getTransaction().commit();
			
		} catch(Exception ex) {
			log.debug(ex.getMessage());
			HibernateUtil.getSessionFactory().getCurrentSession().getTransaction().rollback();
		}
	}
	
	public List<DocumentFormat> getDocumentFormats() { return documentFormats; }
}
