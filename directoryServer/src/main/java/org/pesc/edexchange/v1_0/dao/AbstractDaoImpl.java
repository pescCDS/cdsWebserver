package org.pesc.edexchange.v1_0.dao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.pesc.cds.webservice.service.HibernateUtil;
import org.springframework.orm.hibernate3.HibernateTemplate;

import javax.annotation.Resource;
import javax.transaction.Transaction;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rgehbauer on 9/15/15.
 */
public abstract class AbstractDaoImpl<T> {
    private static final Log log = LogFactory.getLog(AbstractDaoImpl.class);

    private final Class<T> entityClass;

    public AbstractDaoImpl() {
        entityClass = (Class<T>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0];
    }

    protected Class<T> getEntityClass() {
        return entityClass;
    }

    public List<T> all() {
        List<T> retList = new ArrayList<T>();
        try {
            if(HibernateUtil.getSessionFactory().isClosed()) {
                HibernateUtil.getSessionFactory().openSession();
            }
            Session session = HibernateUtil.getSessionFactory().getCurrentSession();
            org.hibernate.Transaction transaction = session.beginTransaction();

            retList = session.createCriteria(getEntityClass()).list();

            transaction.commit();
        } catch(Exception ex) {
            log.error(ex.getMessage());
            ex.printStackTrace();
        }
        finally {
            HibernateUtil.getSessionFactory().getCurrentSession().close();
        }
        return retList;
    }

    public abstract T byId(Integer id);   // This should implemented in the Abstract class, but we need to refactor the Entity objects a bit to achieve this

    public abstract T save(T contact);  // This should implemented in the Abstract class, but we need to refactor the Entity objects a bit to achieve this

    public abstract T remove(T contact); // This should implemented in the Abstract class, but we need to refactor the Entity objects a bit to achieve this

}
