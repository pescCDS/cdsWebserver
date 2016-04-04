package org.pesc.api.repository;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.pesc.api.model.DirectoryUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by james on 3/21/16.
 */
@Service
public class UserService {

    private static final Log log = LogFactory.getLog(UserService.class);

    protected EntityManager em;

    @Autowired
    public UserService(EntityManagerFactory factory) {
        if (factory.unwrap(SessionFactory.class) == null) {
            throw new NullPointerException("Expected Hibernate factory doesn't exist.");
        }
        this.em = factory.createEntityManager();
    }

    @Autowired
    private UserRepository userRepository;

    @Transactional(readOnly=true)
    public Iterable<DirectoryUser> findAll(){
        return this.userRepository.findAll();
    }

    @Transactional(readOnly=false,propagation = Propagation.REQUIRED)
    public void persist(DirectoryUser user){
        this.userRepository.save(user);
    }

    @Transactional(readOnly=false,propagation = Propagation.REQUIRED)

    public void delete(DirectoryUser user)  {
        this.userRepository.delete(user);
    }

    @Transactional(readOnly=false,propagation = Propagation.REQUIRED)
    @PreAuthorize("hasRole('ROLE_SYSTEM_ADMIN')")
    public void delete(Integer id)  {
        this.userRepository.delete(id);
    }

    @Transactional(readOnly=true,propagation = Propagation.REQUIRED)
    public List<DirectoryUser> findByName(String name)  {
        return this.userRepository.findByName(name);
    }


    @Transactional(readOnly=true,propagation = Propagation.REQUIRED)
    @PostAuthorize("returnObject.organization_id == principal.organizationId AND (principal.id == returnObject.id OR hasRole('ROLE_ORG_ADMIN'))")
    public DirectoryUser findById(Integer id)  {

        return this.userRepository.findOne(id);
    }



    /**
     * @param userId
     * @param organizationId
     * @param name
     *
     * @return
     */
    @Transactional(readOnly=true,propagation = Propagation.REQUIRED)
    public List<DirectoryUser> search(
            Integer userId,
            Integer organizationId,
            String name
    ) {

        try {

            CriteriaBuilder cb = em.getCriteriaBuilder();

            CriteriaQuery<DirectoryUser> cq = cb.createQuery(DirectoryUser.class);
            Root<DirectoryUser> user = cq.from(DirectoryUser.class);
            cq.select(user);

            List<Predicate> predicates = new LinkedList<Predicate>();

            if (organizationId != null) {
                predicates.add(cb.equal(user.get("organizationId"), organizationId));
            }
            if (userId != null) {
                predicates.add(cb.and(cb.equal(user.get("id"), userId)));
            }
            if (name != null) {
                predicates.add(cb.and(cb.like(cb.lower(user.get("name")), "%" + name.trim().toLowerCase() + "%")));
            }

            Predicate[] predicateArray = new Predicate[predicates.size()];
            predicates.toArray(predicateArray);

            cq.where(predicateArray);
            TypedQuery<DirectoryUser> q = em.createQuery(cq);

            return q.getResultList();


        } catch(Exception ex) {
            log.error("Failed to execute user search query.", ex);
        }
        return new ArrayList<DirectoryUser>();
    }

}
