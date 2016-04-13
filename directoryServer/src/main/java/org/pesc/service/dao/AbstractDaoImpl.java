package org.pesc.service.dao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManagerFactory;
import javax.transaction.Transactional;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rgehbauer on 9/15/15.
 */
/**
 * This class is used to access data for the OrganizationContact entity.
 * Repository annotation allows the component scanning support to find and
 * configure the DAO wihtout any XML configuration and also provide the Spring
 * exceptiom translation.
 * Since we've setup setPackagesToScan and transaction manager on
 * DatabaseConfig, any bean method annotated with Transactional will cause
 * Spring to magically call begin() and commit() at the start/end of the
 * method. If exception occurs it will also call rollback().
 */
@Repository
@Transactional
public abstract class AbstractDaoImpl<T> {
    private static final Log log = LogFactory.getLog(AbstractDaoImpl.class);

    private final Class<T> entityClass;

    protected SessionFactory hibernateFactory;

    @Autowired
    public AbstractDaoImpl(EntityManagerFactory factory) {
        if (factory.unwrap(SessionFactory.class) == null) {
            throw new NullPointerException("Expected Hibernate factory doesn't exist.");
        }
        this.hibernateFactory = factory.unwrap(SessionFactory.class);

        entityClass = (Class<T>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0];
    }

    protected Class<T> getEntityClass() {
        return entityClass;
    }

    @Transactional
    public List<T> all() {
        List<T> retList = new ArrayList<T>();
        try {


            Session session = hibernateFactory.getCurrentSession();
            retList = session.createCriteria(getEntityClass()).list();


        } catch(Exception ex) {
            log.error(ex.getMessage());
            ex.printStackTrace();
        }
        finally {
            //hibernateFactory.getCurrentSession().close();
        }
        return retList;
    }

    public abstract T byId(Integer id);   // This should implemented in the Abstract class, but we need to refactor the Entity objects a bit to achieve this

    public abstract T save(T contact);  // This should implemented in the Abstract class, but we need to refactor the Entity objects a bit to achieve this

    public abstract T remove(T contact); // This should implemented in the Abstract class, but we need to refactor the Entity objects a bit to achieve this

}
