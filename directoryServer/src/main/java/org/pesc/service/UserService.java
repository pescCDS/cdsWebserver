package org.pesc.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pesc.api.exception.ApiException;
import org.pesc.api.model.DirectoryUser;
import org.pesc.api.model.Role;
import org.pesc.api.repository.RolesRepository;
import org.pesc.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Created by James Whetstone (jwhetstone@ccctechcenter.org) on 3/21/16.
 */
@Service
public class UserService {

    private static final Log log = LogFactory.getLog(UserService.class);

    protected EntityManagerFactory entityManagerFactory;
    private List<Role> roles;

    public static final String PASSWORD_REQUIREMENTS = "The password must be at least 8 characters long, contain 1 upper case letter, 1 lower case letter, 1 number and 1 special character $@$_!%*#?&.";

    private Pattern passwordPattern = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@$!%*#?&])[A-Za-z\\d$@$_!%*#?&]{8,}$");

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public UserService(EntityManagerFactory entityManagerFactory, RolesRepository rolesRepo) {
        roles = (List<Role>)rolesRepo.findAll();
        this.entityManagerFactory = entityManagerFactory;
    }


    public void validatePassword(String password) {
        if (!passwordPattern.matcher(password).matches()) {
            throw new IllegalArgumentException(PASSWORD_REQUIREMENTS);
        }
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    @PreAuthorize("(#userID == principal.id AND hasRole('ROLE_ORG_ADMIN') ) OR hasRole('ROLE_SYSTEM_ADMIN')")
    public void updatePassword(Integer userID, String password) throws IllegalArgumentException {

        validatePassword(password);
        jdbcTemplate.update(
                "update users set password = ? where id = ?", passwordEncoder.encode(password), userID);
    }


    @Autowired
    private UserRepository userRepository;

    public List<Role> getRoles() {
        return roles;
    }

    @Transactional(readOnly=true)
    @PreAuthorize("hasRole('ROLE_SYSTEM_ADMIN')")
    public Iterable<DirectoryUser> findAll(){
        return this.userRepository.findAll();
    }

    @Transactional(readOnly=false,propagation = Propagation.REQUIRED)
    @PreAuthorize("(#user.organizationId == principal.organizationId AND  hasRole('ROLE_ORG_ADMIN') ) OR hasRole('ROLE_SYSTEM_ADMIN')")
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
    public List<DirectoryUser> findByUsername(String username)  {
        return this.userRepository.findByUserName(username);
    }


    @Transactional(readOnly=true,propagation = Propagation.REQUIRED)
    @PostAuthorize("( (returnObject.organizationId == principal.organizationId OR principal.id == returnObject.id) OR hasRole('ROLE_ORG_ADMIN'))")
    public DirectoryUser findById(Integer id)  {
        return this.userRepository.findOne(id);
    }

    @Transactional(readOnly=false,propagation = Propagation.REQUIRED)
    @PreAuthorize("(#user.organizationId == principal.organizationId AND  hasRole('ROLE_ORG_ADMIN') ) OR hasRole('ROLE_SYSTEM_ADMIN')")
    public DirectoryUser create(DirectoryUser user)  {

        return unsecuredCreate(user);
    }

    /**
     * Used during registration
     * @param user
     * @return
     */
    public DirectoryUser unsecuredCreate(DirectoryUser user) {
        validatePassword(user.getPassword());

        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        user.setEnabled(true);

        return this.userRepository.save(user);
    }

    @Transactional(readOnly=false,propagation = Propagation.REQUIRED)
    @PreAuthorize("(#user.organizationId == principal.organizationId AND  hasRole('ROLE_ORG_ADMIN') ) OR hasRole('ROLE_SYSTEM_ADMIN')")
    public DirectoryUser update(DirectoryUser user)  {

        return this.userRepository.save(user);
    }



    public List<DirectoryUser> findByRole(Role role) {


        EntityManager entityManager = entityManagerFactory.createEntityManager();

        try {


            CriteriaBuilder cb = entityManager.getCriteriaBuilder();

            CriteriaQuery<DirectoryUser> cq = cb.createQuery(DirectoryUser.class);
            Root<DirectoryUser> user = cq.from(DirectoryUser.class);
            cq.select(user);

            cq.where(cb.isMember(role, user.<Set<Role>>get("roles")));
            TypedQuery<DirectoryUser> q = entityManager.createQuery(cq);

            return q.getResultList();


        } catch(Exception ex) {
            log.error("Failed to execute user search query.", ex);
        }
        finally {
            entityManager.close();
        }
        return new ArrayList<DirectoryUser>();


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
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        try {


            CriteriaBuilder cb = entityManager.getCriteriaBuilder();

            CriteriaQuery<DirectoryUser> cq = cb.createQuery(DirectoryUser.class);
            Root<DirectoryUser> user = cq.from(DirectoryUser.class);
            cq.select(user);

            List<Predicate> predicates = new LinkedList<Predicate>();

            if (organizationId != null) {
                predicates.add(cb.equal(user.get("organizationId"), organizationId));
            }
            if (userId != null) {
                predicates.add(cb.equal(user.get("id"), userId));
            }
            if (name != null) {
                predicates.add(cb.like(cb.lower(user.get("name")), "%" + name.trim().toLowerCase() + "%"));
            }

            Predicate[] predicateArray = new Predicate[predicates.size()];
            predicates.toArray(predicateArray);

            cq.where(predicateArray);
            TypedQuery<DirectoryUser> q = entityManager.createQuery(cq);

            return q.getResultList();


        } catch(Exception ex) {
            log.error("Failed to execute user search query.", ex);
        }
        finally {
           entityManager.close();
        }
        return new ArrayList<DirectoryUser>();
    }

}
