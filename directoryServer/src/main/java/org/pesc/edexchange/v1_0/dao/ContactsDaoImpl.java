package org.pesc.edexchange.v1_0.dao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.pesc.cds.webservice.service.HibernateUtil;
import org.pesc.edexchange.v1_0.OrganizationContact;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

@Repository
public class ContactsDaoImpl extends AbstractDaoImpl<OrganizationContact> implements ContactsDao {
	private static final Log log = LogFactory.getLog(ContactsDaoImpl.class);
	
	@Override
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
			if(city!=null && city.trim().length()>1) {
				StringTokenizer cityTokens = new StringTokenizer(city.trim());
				while(cityTokens.hasMoreElements()) {
					String cityToken = cityTokens.nextToken();
					ct.add(Restrictions.ilike("city", cityToken, MatchMode.ANYWHERE));
				}
				hasCriteria = true;
			}
			
			// Contact Id
			if(contactId!=null) {
				ct.add(Restrictions.idEq(contactId));
				hasCriteria = true;
			}
			
			// Contact Name
			if(contactName!=null && contactName.trim().length()>0) {
				StringTokenizer nameTokens = new StringTokenizer(contactName.trim());
				while(nameTokens.hasMoreElements()) {
					String nameToken = nameTokens.nextToken();
					ct.add(Restrictions.ilike("contactName", nameToken, MatchMode.ANYWHERE));
				}
				hasCriteria = true;
			}
			
			// Contact Title
			if(contactTitle!=null && contactTitle.trim().length()>0) {
				StringTokenizer titleTokens = new StringTokenizer(contactTitle.trim());
				while(titleTokens.hasMoreElements()) {
					String titleToken = titleTokens.nextToken();
					ct.add(Restrictions.ilike("contactTitle", titleToken, MatchMode.ANYWHERE));
				}
				hasCriteria = true;
			}
			
			// Contact Type
			if(contactType!=null && contactType.trim().length()>0) {
				StringTokenizer typeTokens = new StringTokenizer(contactType.trim());
				while(typeTokens.hasMoreElements()) {
					String typeToken = typeTokens.nextToken();
					ct.add(Restrictions.ilike("contactType", typeToken, MatchMode.ANYWHERE));
				}
				hasCriteria = true;
			}
			
			// Country
			if(country!=null && country.trim().length()>0) {
				StringTokenizer countryTokens = new StringTokenizer(country.trim());
				while(countryTokens.hasMoreElements()) {
					String countryToken = countryTokens.nextToken();
					ct.add(Restrictions.ilike("country", countryToken, MatchMode.START));
				}
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
			if(email!=null && email.trim().length()>0) {
				ct.add(Restrictions.ilike("email", email.trim(), MatchMode.ANYWHERE));
				hasCriteria = true;
			}
			
			// Modified Time
			if(modifiedTime!=null) {
				ct.add(Restrictions.eq("modifiedTime", new Timestamp(modifiedTime)));
				hasCriteria = true;
			}
			
			// Phone1
			if(phone1!=null && phone1.trim().length()>0) {
				ct.add(Restrictions.ilike("phone1", phone1.trim(), MatchMode.ANYWHERE));
				hasCriteria = true;
			}
			
			// Phone2
			if(phone2!=null && phone2.trim().length()>0) {
				ct.add(Restrictions.ilike("phone2", phone2.trim(), MatchMode.ANYWHERE));
				hasCriteria = true;
			}
			
			// State
			if(state!=null && state.trim().length()>0) {
				ct.add(Restrictions.ilike("state", state.trim(), MatchMode.START));
				hasCriteria = true;
			}
			
			// Street Address 1
			if(streetAddress1!=null && streetAddress1.trim().length()>0) {
				StringTokenizer addr1Tokens = new StringTokenizer(streetAddress1.trim());
				while(addr1Tokens.hasMoreElements()) {
					String addr1Token = addr1Tokens.nextToken();
					ct.add(Restrictions.ilike("streetAddress1", addr1Token, MatchMode.ANYWHERE));
				}
				hasCriteria = true;
			}
			// Street Address 2
			if(streetAddress2!=null && streetAddress2.trim().length()>0) {
				StringTokenizer addr2Tokens = new StringTokenizer(streetAddress2.trim());
				while(addr2Tokens.hasMoreElements()) {
					String addr2Token = addr2Tokens.nextToken();
					ct.add(Restrictions.ilike("streetAddress2", addr2Token, MatchMode.ANYWHERE));
				}
				hasCriteria = true;
			}
			// Street Address 3
			if(streetAddress3!=null && streetAddress3.trim().length()>0) {
				StringTokenizer addr3Tokens = new StringTokenizer(streetAddress3.trim());
				while(addr3Tokens.hasMoreElements()) {
					String addr3Token = addr3Tokens.nextToken();
					ct.add(Restrictions.ilike("streetAddress3", addr3Token, MatchMode.ANYWHERE));
				}
				hasCriteria = true;
			}
			// Street Address 4
			if(streetAddress4!=null && streetAddress4.trim().length()>0) {
				StringTokenizer addr4Tokens = new StringTokenizer(streetAddress4.trim());
				while(addr4Tokens.hasMoreElements()) {
					String addr4Token = addr4Tokens.nextToken();
					ct.add(Restrictions.ilike("streetAddress4", addr4Token, MatchMode.ANYWHERE));
				}
				hasCriteria = true;
			}
			
			// Zip
			if(zip!=null && zip.trim().length()>0) {
				ct.add(Restrictions.ilike("zip", zip.trim(), MatchMode.START));
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

	@Override
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

	@Override
	public OrganizationContact save(OrganizationContact contact) {
		OrganizationContact retContact = null;
		try {
			if(HibernateUtil.getSessionFactory().isClosed()) {
				HibernateUtil.getSessionFactory().openSession();
			}
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();

			// database handles create/modified values
			contact.setCreatedTime(null);
			contact.setModifiedTime(null);

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

	@Override
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
