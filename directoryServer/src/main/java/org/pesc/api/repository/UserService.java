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

import javax.persistence.EntityManagerFactory;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by james on 3/21/16.
 */
@Service
public class UserService {

    private static final Log log = LogFactory.getLog(UserService.class);

    protected SessionFactory hibernateFactory;

    @Autowired
    public UserService(EntityManagerFactory factory) {
        if (factory.unwrap(SessionFactory.class) == null) {
            throw new NullPointerException("Expected Hibernate factory doesn't exist.");
        }
        this.hibernateFactory = factory.unwrap(SessionFactory.class);
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
        List<DirectoryUser> retList = new ArrayList<DirectoryUser>();
        try {

            //TODO: implement the search criteria using JPA 2.0 standard instead of the
            //Hibernate specific criteria.  This additional abstraction will decouple the service
            //from Hibernate and enforce type safety on the criteria.
            Session session = hibernateFactory.getCurrentSession();

            Criteria ct = session.createCriteria(DirectoryUser.class);
            boolean hasCriteria = false;

            if(userId!=null) {
                ct.add(Restrictions.idEq(userId));
                hasCriteria = true;
            }

            if(name!=null && name.trim().length()>0) {
                ct.add(Restrictions.ilike("name", name.trim(), MatchMode.START));
                hasCriteria = true;
            }

            if(organizationId!=null) {

                ct.add(Restrictions.eq("organizationId", organizationId));
                hasCriteria = true;
            }



            if(hasCriteria) {
                retList = ct.list();
            }
            else {
                retList = (List<DirectoryUser>)this.userRepository.findAll();
            }

        } catch(Exception ex) {
            log.error("Failed to execute user search query.", ex);
        }
        return retList;
    }

}
