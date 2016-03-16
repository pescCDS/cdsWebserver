package org.pesc.web.model;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;

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
public class OrganizationContactDao {

  private SessionFactory hibernateFactory;

  @Autowired
  public OrganizationContactDao(EntityManagerFactory factory) {
    if (factory.unwrap(SessionFactory.class) == null) {
      throw new NullPointerException("Expected Hibernate factory doesn't exist.");
    }
    this.hibernateFactory = factory.unwrap(SessionFactory.class);
  }


  public OrganizationContact save(OrganizationContact contact) {
    OrganizationContact retContact = null;
    try {

      Session session = hibernateFactory.getCurrentSession();
      session.beginTransaction();

      // database handles create/modified values


      session.saveOrUpdate(contact);
      // using .get now instead of .load
      retContact = (OrganizationContact)session.get(OrganizationContact.class, contact.getContactId());

      session.getTransaction().commit();
    } catch(Exception ex) {

      ex.printStackTrace();
      entityManager.unwrap(SessionFactory.class).getCurrentSession().getTransaction().rollback();
    }
    return retContact;
  }

  // ------------------------
  // PRIVATE FIELDS
  // ------------------------
  
  // An EntityManager will be automatically injected from entityManagerFactory
  // setup on DatabaseConfig class.
  @PersistenceContext
  private EntityManager entityManager;
  
} // class OrganizationContactDao
