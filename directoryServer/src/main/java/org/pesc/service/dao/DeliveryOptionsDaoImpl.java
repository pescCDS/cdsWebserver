package org.pesc.service.dao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.pesc.edexchange.v1_0.DeliveryOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManagerFactory;
import java.util.ArrayList;
import java.util.List;

@Repository
public class DeliveryOptionsDaoImpl extends AbstractDaoImpl<DeliveryOption> implements DeliveryOptionsDao {
	private static final Log log = LogFactory.getLog(DeliveryOptionsDaoImpl.class);

	@Autowired
	public DeliveryOptionsDaoImpl(EntityManagerFactory factory) {
		super(factory);
	}

	@Override
	public List<DeliveryOption> search(
			Integer id,
			Integer memberId,
			Integer formatId,
			String webserviceUrl,
			Integer deliveryMethodId,
			Boolean deliveryConfirm,
			Boolean error,
			String operationalStatus
	) {
		List<DeliveryOption> retList = new ArrayList<DeliveryOption>();
		try {
			if(hibernateFactory.isClosed()) {
				hibernateFactory.openSession();
			}
			Session session = hibernateFactory.getCurrentSession();
			session.beginTransaction();
			
			Criteria ct = session.createCriteria(DeliveryOption.class);
			boolean hasCriteria = false;
			
			if(id!=null) {
				ct.add(Restrictions.idEq(id));
				hasCriteria = true;
			}
			
			if(memberId!=null) {
				ct.createAlias("member", "membr");
				ct.add(Restrictions.eq("membr.directoryId", memberId));
				hasCriteria = true;
			}
			
			if(formatId!=null) {
				ct.createAlias("format", "frmt");
				ct.add(Restrictions.eq("frmt.id", formatId));
				hasCriteria = true;
			}
			
			if(webserviceUrl!=null) {
				ct.add(Restrictions.ilike("webserviceUrl", webserviceUrl, MatchMode.ANYWHERE));
				hasCriteria = true;
			}
			
			if(deliveryMethodId!=null) {
				ct.createAlias("deliveryMethod", "dmethod");
				ct.add(Restrictions.eq("dmethod.id", deliveryMethodId));
				hasCriteria = true;
			}
			
			if(deliveryConfirm!=null) {
				ct.add(Restrictions.eq("deliveryConfirm", deliveryConfirm));
				hasCriteria = true;
			}
			
			if(error!=null) {
				ct.add(Restrictions.eq("error", error));
				hasCriteria = true;
			}
			
			if(operationalStatus!=null) {
				ct.add(Restrictions.eq("operationalStatus", operationalStatus.toUpperCase()));
				hasCriteria = true;
			}
			
			if(hasCriteria) {
				retList = ct.list();
			}
			
			session.getTransaction().commit();
			
		} catch(Exception ex) {
			log.error(ex.getMessage());
			ex.printStackTrace();
			hibernateFactory.getCurrentSession().getTransaction().rollback();
		}
		return retList;
	}
	
	
	public DeliveryOption byId(Integer id) {
		DeliveryOption retOption = null;
		try {
			if(hibernateFactory.isClosed()) {
				hibernateFactory.openSession();
			}
			Session session = hibernateFactory.getCurrentSession();
			session.beginTransaction();
			
			retOption = (DeliveryOption)session.get(DeliveryOption.class, id);
			
			session.getTransaction().commit();
		} catch(Exception ex) {
			log.error(ex.getMessage());
			ex.printStackTrace();
			hibernateFactory.getCurrentSession().getTransaction().rollback();
		}
		return retOption;
	}

	public DeliveryOption save(DeliveryOption option) {
		DeliveryOption retOption = null;
		try {
			if(hibernateFactory.isClosed()) {
				hibernateFactory.openSession();
			}
			Session session = hibernateFactory.getCurrentSession();
			session.beginTransaction();
			
			session.saveOrUpdate(option);
			retOption = (DeliveryOption)session.get(DeliveryOption.class, option.getId());
			
			session.getTransaction().commit();
		} catch(Exception ex) {
			log.error(ex.getMessage());
			ex.printStackTrace();
			hibernateFactory.getCurrentSession().getTransaction().rollback();
		}
		return retOption;
	}
	
	public DeliveryOption remove(DeliveryOption option) {
		try {
			if(hibernateFactory.isClosed()) {
				hibernateFactory.openSession();
			}
			Session session = hibernateFactory.getCurrentSession();
			session.beginTransaction();
			
			DeliveryOption remOpt = (DeliveryOption)session.get(DeliveryOption.class, option.getId());
			session.delete(remOpt);
			
			session.getTransaction().commit();
		} catch(Exception ex) {
			log.error(ex.getMessage());
			ex.printStackTrace();
			hibernateFactory.getCurrentSession().getTransaction().rollback();
		}
		return option;
	}
}
