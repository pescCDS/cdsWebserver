package org.pesc.edexchange.v1_0.dao;

import java.sql.Timestamp;
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
	
	
	public List<OrganizationContact> search(
			String city,
			Integer contactId,
			String contactName,
			String contactTitle,
			String contactType,
			String country,
			Long createdTime,
			Integer directoryId,
			String email,
			Long modifiedTime,
			String phone1,
			String phone2,
			String state,
			String streetAddress1,
			String streetAddress2,
			String streetAddress3,
			String streetAddress4,
			String zip
		) {
		List<OrganizationContact> retList = new ArrayList<OrganizationContact>();
		try {
			if(HibernateUtil.getSessionFactory().isClosed()) {
				HibernateUtil.getSessionFactory().openSession();
			}
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			
			Criteria ct = session.createCriteria(OrganizationContact.class);
			boolean hasCriteria = false;
			
			// City
			if(city!=null && city.length()>1) {
				ct.add(Restrictions.ilike("city", city, MatchMode.ANYWHERE));
				hasCriteria = true;
			}
			
			// Contact Id
			if(contactId!=null) {
				ct.add(Restrictions.idEq(contactId));
				hasCriteria = true;
			}
			
			// Contact Name
			if(contactName!=null) {
				ct.add(Restrictions.ilike("contactName", contactName, MatchMode.ANYWHERE));
				hasCriteria = true;
			}
			
			// Contact Title
			if(contactTitle!=null) {
				ct.add(Restrictions.ilike("contactTitle", contactTitle, MatchMode.ANYWHERE));
				hasCriteria = true;
			}
			
			// Contact Type
			if(contactType!=null) {
				ct.add(Restrictions.ilike("contactType", contactType, MatchMode.START));
				hasCriteria = true;
			}
			
			// Country
			if(country!=null) {
				ct.add(Restrictions.ilike("country", country, MatchMode.START));
				hasCriteria = true;
			}
			
			// Created Time
			if(createdTime!=null) {
				ct.add(Restrictions.eq("createdTime", new Timestamp(createdTime)));
				hasCriteria = true;
			}
			
			// Directory Id
			if(directoryId!=null) {
				ct.createAlias("directory", "dir");
				ct.add(Restrictions.eq("dir.id", directoryId));
				hasCriteria = true;
			}
			
			// Email
			if(email!=null) {
				ct.add(Restrictions.ilike("email", email, MatchMode.ANYWHERE));
				hasCriteria = true;
			}
			
			// Modified Time
			if(modifiedTime!=null) {
				ct.add(Restrictions.eq("modifiedTime", new Timestamp(modifiedTime)));
				hasCriteria = true;
			}
			
			// Phone1
			if(phone1!=null) {
				ct.add(Restrictions.ilike("phone1", phone1, MatchMode.ANYWHERE));
				hasCriteria = true;
			}
			
			// Phone2
			if(phone2!=null) {
				ct.add(Restrictions.ilike("phone2", phone2, MatchMode.ANYWHERE));
				hasCriteria = true;
			}
			
			// State
			if(state!=null) {
				ct.add(Restrictions.ilike("state", state, MatchMode.START));
				hasCriteria = true;
			}
			
			// Street Address 1
			if(streetAddress1!=null) {
				ct.add(Restrictions.ilike("streetAddress1", streetAddress1, MatchMode.ANYWHERE));
				hasCriteria = true;
			}
			// Street Address 2
			if(streetAddress2!=null) {
				ct.add(Restrictions.ilike("streetAddress2", streetAddress2, MatchMode.ANYWHERE));
				hasCriteria = true;
			}
			// Street Address 3
			if(streetAddress3!=null) {
				ct.add(Restrictions.ilike("streetAddress3", streetAddress3, MatchMode.ANYWHERE));
				hasCriteria = true;
			}
			// Street Address 4
			if(streetAddress4!=null) {
				ct.add(Restrictions.ilike("streetAddress4", streetAddress4, MatchMode.ANYWHERE));
				hasCriteria = true;
			}
			
			// Zip
			if(zip!=null) {
				ct.add(Restrictions.ilike("zip", zip, MatchMode.START));
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
	
	
	// search for contact using a first and last name token
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
