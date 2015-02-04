package org.pesc.edexchange.v1_0.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.pesc.cds.datatables.FilterSet;
import org.pesc.cds.webservice.service.HibernateUtil;
import org.springframework.security.core.context.SecurityContextHolder;

public class FilterSetsDao implements DBDataSourceDao<FilterSet> {
	private static final Log log = LogFactory.getLog(FilterSetsDao.class);
	
	public List<FilterSet> all() {
		List<FilterSet> retList = new ArrayList<FilterSet>();
		try {
			if(HibernateUtil.getSessionFactory().isClosed()) {
				HibernateUtil.getSessionFactory().openSession();
			}
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			
			// TODO create a user class and database table(s) for logging in
			//Integer userId = ((<Webapp User>)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserId();
			Criteria ct = session.createCriteria(FilterSet.class);
			//ct.add(Restrictions.eq("userId", userId));
			retList = ct.list();
			
			for(ListIterator<FilterSet> li = retList.listIterator(); li.hasNext();) {
				FilterSet fset = li.next();
				fset.parseJsonFilters();
			}
			
			session.getTransaction().commit();
		} catch(Exception ex) {
			log.error(ex.getMessage());
			HibernateUtil.getSessionFactory().getCurrentSession().getTransaction().rollback();
		}
		return retList;
	}
	
	public FilterSet byId(Integer id) {
		FilterSet retFilterSet = null;
		try {
			if(HibernateUtil.getSessionFactory().isClosed()) {
				HibernateUtil.getSessionFactory().openSession();
			}
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			
			retFilterSet = (FilterSet)session.get(FilterSet.class, id);
			retFilterSet.parseJsonFilters();
			
			session.getTransaction().commit();
		} catch(Exception ex) {
			log.error(ex.getMessage());
			HibernateUtil.getSessionFactory().getCurrentSession().getTransaction().rollback();
		}
		return retFilterSet;
	}
	
	public List<FilterSet> byUserId(Integer userId) {
		List<FilterSet> retList = new ArrayList<FilterSet>();
		try {
			if(HibernateUtil.getSessionFactory().isClosed()) {
				HibernateUtil.getSessionFactory().openSession();
			}
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			
			Criteria ct = session.createCriteria(FilterSet.class);
			ct.add(Restrictions.eq("userId", userId));
			retList = ct.list();
			
			for(ListIterator<FilterSet> li = retList.listIterator(); li.hasNext();) {
				FilterSet fset = li.next();
				fset.parseJsonFilters();
			}
			
			session.getTransaction().commit();
		} catch(Exception ex) {
			log.error(ex.getMessage());
			HibernateUtil.getSessionFactory().getCurrentSession().getTransaction().rollback();
		}
		return retList;
	}
	
	public List<FilterSet> byTable(String table) {
		List<FilterSet> retList = new ArrayList<FilterSet>();
		try {
			if(HibernateUtil.getSessionFactory().isClosed()) {
				HibernateUtil.getSessionFactory().openSession();
			}
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			
			// TODO need to create User class and database tables
			//Integer userId = ((<Webapp User Class>)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserId();
			Criteria ct = session.createCriteria(FilterSet.class);
			//log.debug(String.format("adding user id[%s] and table[%s] to filter set criteria",userId,table));
			//ct.add(Restrictions.eq("userId", userId));
			ct.add(Restrictions.eq("table", table));
			retList = ct.list();
			
			for(ListIterator<FilterSet> li = retList.listIterator(); li.hasNext();) {
				FilterSet fset = li.next();
				fset.parseJsonFilters();
			}
			
			session.getTransaction().commit();
		} catch(Exception ex) {
			log.error(ex.getMessage());
			HibernateUtil.getSessionFactory().getCurrentSession().getTransaction().rollback();
		}
		return retList;
	}
	
	public List<FilterSet> byUserIdAndTable() {
		List<FilterSet> retList = new ArrayList<FilterSet>();
		
		return retList;
	}
	
	
	public FilterSet save(FilterSet fs) {
		log.debug(fs);
		FilterSet retFilterSet = null;
		try {
			if(HibernateUtil.getSessionFactory().isClosed()) {
				HibernateUtil.getSessionFactory().openSession();
			}
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			
			//Integer userId = ((PipUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserId();
			//fs.setUserId(userId);
			fs.stringifyFilters();
			
			session.saveOrUpdate(fs);
			retFilterSet = (FilterSet)session.get(FilterSet.class, fs.getId());
			
			session.getTransaction().commit();
		} catch(Exception ex) {
			log.error(ex.getMessage());
			HibernateUtil.getSessionFactory().getCurrentSession().getTransaction().rollback();
		}
		return retFilterSet;
	}
	
	public FilterSet remove(FilterSet fs) {
		FilterSet retFilterSet = null;
		try {
			if(HibernateUtil.getSessionFactory().isClosed()) {
				HibernateUtil.getSessionFactory().openSession();
			}
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			
			retFilterSet = (FilterSet)session.get(FilterSet.class, fs.getId());
			session.delete(retFilterSet);
			
			session.getTransaction().commit();
		} catch(Exception ex) {
			log.error(ex.getMessage());
			HibernateUtil.getSessionFactory().getCurrentSession().getTransaction().rollback();
		}
		return retFilterSet;	
	}
}
