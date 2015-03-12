package org.pesc.edexchange.v1_0.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.pesc.cds.webservice.service.HibernateUtil;
import org.pesc.edexchange.v1_0.OrganizationContact;

public class ContactsDao implements DBDataSourceDao<OrganizationContact> {
	private static final Log log = LogFactory.getLog(ContactsDao.class);
	
	public ContactsDao() { }
	
	// 
	public List<OrganizationContact> filterByName(String query) {
		List<OrganizationContact> retList = new ArrayList<OrganizationContact>();
		try {
			if(HibernateUtil.getSessionFactory().isClosed()) {
				HibernateUtil.getSessionFactory().openSession();
			}
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			
			Criteria ct = session.createCriteria(OrganizationContact.class);
			
			// tokenize string by whitespace and use each token as
			// a LIKE clause for the contactName column
			StringTokenizer tokens = new StringTokenizer(query);
			while(tokens.hasMoreElements()) {
				String token = tokens.nextToken();
				ct.add( Restrictions.like("contactName", token, MatchMode.ANYWHERE) );
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
	
	public OrganizationContact byId(Integer id) {
		OrganizationContact retContact = null;
		try {
			if(HibernateUtil.getSessionFactory().isClosed()) {
				HibernateUtil.getSessionFactory().openSession();
			}
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			
			retContact = (OrganizationContact)session.get(OrganizationContact.class, id);
			
			session.getTransaction().commit();
		} catch(Exception ex) {
			log.error(ex.getMessage());
			ex.printStackTrace();
			HibernateUtil.getSessionFactory().getCurrentSession().getTransaction().rollback();
		}
		return retContact;
	}

	public List<OrganizationContact> all() {
		List<OrganizationContact> retList = new ArrayList<OrganizationContact>();
		try {
			if(HibernateUtil.getSessionFactory().isClosed()) {
				HibernateUtil.getSessionFactory().openSession();
			}
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			
			retList = session.createCriteria(OrganizationContact.class).list();
			
			session.getTransaction().commit();
		} catch(Exception ex) {
			log.error(ex.getMessage());
			ex.printStackTrace();
			HibernateUtil.getSessionFactory().getCurrentSession().getTransaction().rollback();
		}
		return retList;
	}
	
	public OrganizationContact save(OrganizationContact contact) {
		OrganizationContact retContact = null;
		try {
			if(HibernateUtil.getSessionFactory().isClosed()) {
				HibernateUtil.getSessionFactory().openSession();
			}
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			
			session.saveOrUpdate(contact);
			// using .get now instead of .load
			retContact = (OrganizationContact)session.get(OrganizationContact.class, contact.getContactId());
			
			session.getTransaction().commit();
		} catch(Exception ex) {
			log.error(ex.getMessage());
			ex.printStackTrace();
			HibernateUtil.getSessionFactory().getCurrentSession().getTransaction().rollback();
		}
		return retContact;
	}
	
	public OrganizationContact remove(OrganizationContact contact) {
		try {
			if(HibernateUtil.getSessionFactory().isClosed()) {
				HibernateUtil.getSessionFactory().openSession();
			}
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			
			// using .get instead of .load
			OrganizationContact remContact = (OrganizationContact)session.get(OrganizationContact.class, contact.getContactId());
			session.delete(remContact);
			
			session.getTransaction().commit();
		} catch(Exception ex) {
			log.error(ex.getMessage());
			ex.printStackTrace();
			HibernateUtil.getSessionFactory().getCurrentSession().getTransaction().rollback();
		}
		return contact;
	}
}
