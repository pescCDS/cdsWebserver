package org.pesc.cds.domain;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManagerFactory;
import javax.transaction.Transactional;
import java.lang.reflect.ParameterizedType;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Repository
@Transactional
public class TransactionsDao {
	private static final Log log = LogFactory.getLog(TransactionsDao.class);


	protected SessionFactory hibernateFactory;

	@Autowired
	public TransactionsDao(EntityManagerFactory factory) {
		if (factory.unwrap(SessionFactory.class) == null) {
			throw new NullPointerException("Expected Hibernate factory doesn't exist.");
		}
		this.hibernateFactory = factory.unwrap(SessionFactory.class);

	}
	
	
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
			if(hibernateFactory.isClosed()) {
				hibernateFactory.openSession();
			}
			Session session = hibernateFactory.getCurrentSession();
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
			hibernateFactory.getCurrentSession().getTransaction().rollback();
		}
		return retList;
	}
	
	
	public List<Transaction> all() {
		List<Transaction> retList = new ArrayList<Transaction>();
		try {
			if(hibernateFactory.isClosed()) {
				hibernateFactory.openSession();
			}
			Session session = hibernateFactory.getCurrentSession();
			session.beginTransaction();
			
			retList = session.createCriteria(Transaction.class).list();
			
			session.getTransaction().commit();
			
		} catch(Exception ex) {
			log.error(ex.getMessage());
			ex.printStackTrace();
			hibernateFactory.getCurrentSession().getTransaction().rollback();
		}
		return retList;
	}
	
	
	public Transaction byId(Integer id) {
		Transaction retTx = null;
		try {
			if(hibernateFactory.isClosed()) {
				hibernateFactory.openSession();
			}
			Session session = hibernateFactory.getCurrentSession();
			session.beginTransaction();
			
			Criteria ct = session.createCriteria(Transaction.class).add(Restrictions.idEq(id));
			
			List<Transaction> txList = ct.list();
			if(txList.size()<1) {
				throw new Exception("Transaction query returned zero results");
			} else {
				retTx = txList.get(0);
			}
			
			session.getTransaction().commit();
			
		} catch(Exception ex) {
			log.error(ex.getMessage());
			ex.printStackTrace();
			hibernateFactory.getCurrentSession().getTransaction().rollback();
		}
		return retTx;
	}
	
	
	
	public Transaction save(Transaction tx) {
		Transaction retTx = null;
		try {
			if(hibernateFactory.isClosed()) {
				hibernateFactory.openSession();
			}
			Session session = hibernateFactory.getCurrentSession();
			session.beginTransaction();
			
			session.saveOrUpdate(tx);
			retTx = (Transaction)session.get(Transaction.class, tx.getId());
			
			session.getTransaction().commit();
			
		} catch(Exception ex) {
			log.error(ex.getMessage());
			ex.printStackTrace();
			hibernateFactory.getCurrentSession().getTransaction().rollback();
		}
		return retTx;
	}
}
