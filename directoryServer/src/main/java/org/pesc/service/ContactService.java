package org.pesc.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pesc.api.model.Contact;
import org.pesc.api.model.DirectoryUser;
import org.pesc.api.repository.ContactsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by James Whetstone (jwhetstone@ccctechcenter.org) on 6/6/16.
 */
@Service
public class ContactService {

    private static final Log log = LogFactory.getLog(ContactService.class);

    protected EntityManagerFactory entityManagerFactory;

    @Autowired
    private ContactsRepository contactsRepository;

    @Autowired
    public ContactService(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }


    @Transactional(readOnly=false,propagation = Propagation.REQUIRED)
    @PreAuthorize("(#contact.organizationId == principal.organizationId AND  hasRole('ROLE_ORG_ADMIN') ) OR hasRole('ROLE_SYSTEM_ADMIN')")
    public void delete(Contact contact)  {
        this.contactsRepository.delete(contact);
    }

    @Transactional(readOnly=false,propagation = Propagation.REQUIRED)
    @PreAuthorize("hasRole('ROLE_ORG_ADMIN') OR hasRole('ROLE_SYSTEM_ADMIN')")
    public void delete(Integer id)  {
        this.contactsRepository.delete(id);
    }


    @Transactional(readOnly=true,propagation = Propagation.REQUIRED)
    public Contact findById(Integer id)  {
        return this.contactsRepository.findOne(id);
    }


    @Transactional(readOnly=true,propagation = Propagation.REQUIRED)
    public List<Contact> search(
            Integer organizationId
    ) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        try {


            CriteriaBuilder cb = entityManager.getCriteriaBuilder();

            CriteriaQuery<Contact> cq = cb.createQuery(Contact.class);
            Root<Contact> contactRoot = cq.from(Contact.class);
            cq.select(contactRoot);

            List<Predicate> predicates = new LinkedList<Predicate>();

            if (organizationId != null) {
                predicates.add(cb.equal(contactRoot.get("organizationId"), organizationId));
            }

            Predicate[] predicateArray = new Predicate[predicates.size()];
            predicates.toArray(predicateArray);

            cq.where(predicateArray);
            TypedQuery<Contact> q = entityManager.createQuery(cq);

            return q.getResultList();


        } catch(Exception ex) {
            log.error("Failed to execute contact search query.", ex);
        }
        finally {
            entityManager.close();
        }
        return new ArrayList<Contact>();
    }


    @Transactional(readOnly=false,propagation = Propagation.REQUIRED)
    @PreAuthorize("(#contact.organizationId == principal.organizationId AND  hasRole('ROLE_ORG_ADMIN') ) OR hasRole('ROLE_SYSTEM_ADMIN')")
    public Contact create(Contact contact)  {

        return this.contactsRepository.save(contact);
    }

    @Transactional(readOnly=false,propagation = Propagation.REQUIRED)
    @PreAuthorize("(#contact.organizationId == principal.organizationId AND  hasRole('ROLE_ORG_ADMIN') ) OR hasRole('ROLE_SYSTEM_ADMIN')")
    public Contact update(Contact contact)  {

        return this.contactsRepository.save(contact);
    }

}
