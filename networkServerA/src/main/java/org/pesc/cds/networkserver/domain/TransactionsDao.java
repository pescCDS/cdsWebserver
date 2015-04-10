package org.pesc.cds.networkserver.domain;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.pesc.cds.networkserver.service.HibernateUtil;

public class TransactionsDao {
	private static final Log log = LogFactory.getLog(TransactionsDao.class);
	
	public TransactionsDao() {}
	
	
	
	/**
	 * 
	 * 
	 * @param senderId <b>required</b>
	 * @param status
	 * @param from
	 * @param to
	 * @param fetchSize <b>required</b>
	 * @return <code>List&lt;Transaction&gt;</code>
	 */
	public List<Transaction> bySenderStatusDate(Integer senderId, Boolean status, Long from, Long to, Long fetchSize) {
		List<Transaction> retList = new ArrayList<Transaction>();
		try {
			if(HibernateUtil.getSessionFactory().isClosed()) {
				HibernateUtil.getSessionFactory().openSession();
			}
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			
			Criteria ct = session.createCriteria(Transaction.class);
			ct.add(Restrictions.eq("senderId", senderId));
			
			if(status!=null) {
				ct.add(Restrictions.eqOrIsNull("status", status));
			}
			
			// TODO we can get a little bit fancier with this
			// if only 1 of the from/to is given:
			//     from: sent >= {from}
			//     to:   sent <= {to}
			if(from!=null && to!=null) {
				
			} else if(from!=null) {
				ct.add(Restrictions.ge("sent", new Timestamp(from)));
			} else if(to!=null) {
				ct.add(Restrictions.le("sent", new Timestamp(to)));
			}
			
			ct.setMaxResults(fetchSize.intValue());
			
			log.debug(String.format("Criteria Query: %s", ct.toString()));
			
			retList = ct.list();
			
			session.getTransaction().commit();
			
		} catch(Exception ex) {
			log.error(ex.getMessage());
			ex.printStackTrace();
			HibernateUtil.getSessionFactory().getCurrentSession().getTransaction().rollback();
		}
		return retList;
	}
	
	
	
	public List<Transaction> all() {
		List<Transaction> retList = new ArrayList<Transaction>();
		try {
			if(HibernateUtil.getSessionFactory().isClosed()) {
				HibernateUtil.getSessionFactory().openSession();
			}
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			
			retList = session.createCriteria(Transaction.class).list();
			
			session.getTransaction().commit();
			
		} catch(Exception ex) {
			log.error(ex.getMessage());
			ex.printStackTrace();
			HibernateUtil.getSessionFactory().getCurrentSession().getTransaction().rollback();
		}
		return retList;
	}
	
	
	
	public Transaction save(Transaction tx) {
		Transaction retTx = null;
		try {
			if(HibernateUtil.getSessionFactory().isClosed()) {
				HibernateUtil.getSessionFactory().openSession();
			}
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			
			session.saveOrUpdate(tx);
			retTx = (Transaction)session.get(Transaction.class, tx.getId());
			
			session.getTransaction().commit();
			
		} catch(Exception ex) {
			log.error(ex.getMessage());
			ex.printStackTrace();
			HibernateUtil.getSessionFactory().getCurrentSession().getTransaction().rollback();
		}
		return retTx;
	}
}
