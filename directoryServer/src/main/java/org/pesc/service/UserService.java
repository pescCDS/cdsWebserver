package org.pesc.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pesc.api.StringUtils;
import org.pesc.api.model.AuthUser;
import org.pesc.api.model.DirectoryUser;
import org.pesc.api.model.Role;
import org.pesc.api.repository.RolesRepository;
import org.pesc.api.repository.UserRepository;
import org.pesc.web.AppController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
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
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by James Whetstone (jwhetstone@ccctechcenter.org) on 3/21/16.
 */
@Service
public class UserService {

    private static final Log log = LogFactory.getLog(UserService.class);

    protected EntityManagerFactory entityManagerFactory;
    private List<Role> roles;

    public static final String PASSWORD_REQUIREMENTS = "The password must be at least 15 characters long, contain 1 upper case letter, 1 lower case letter, 1 number and 1 special character @$ _!%*#?&.";

    private Pattern passwordPattern = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@$ _!%*#?&])[A-Za-z\\d$@$ _!%*#?&]{15,}$");

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JdbcTemplate jdbcTemplate;


    private Role systemAdminRole;
    private Role supportRole;

    private Set<String> systemAdminEmailAddresses;

    @Autowired
    public UserService(EntityManagerFactory entityManagerFactory, RolesRepository rolesRepo) {

        this.entityManagerFactory = entityManagerFactory;

        roles = (List<Role>)rolesRepo.findAll();
        systemAdminRole = rolesRepo.findByName("ROLE_SYSTEM_ADMIN");
        supportRole = rolesRepo.findByName("ROLE_SUPPORT");

        systemAdminEmailAddresses = getEmailAddressesForUsersWithSystemAdminRole();

    }


    public void validatePassword(String password) {
        if (!passwordPattern.matcher(password).matches()) {
            throw new IllegalArgumentException(PASSWORD_REQUIREMENTS);
        }
    }

    @Transactional(readOnly=false,propagation = Propagation.REQUIRED)
    @PreAuthorize("( (#orgID == principal.organizationId AND hasRole('ROLE_ORG_ADMIN')) OR hasRole('ROLE_SYSTEM_ADMIN') )")
    public void deleteByOrgId(Integer orgID) {

        jdbcTemplate.update("delete from users_roles where users_id in (select id from users where organization_id = ?)", orgID);

        jdbcTemplate.update("delete from users where organization_id = ?", orgID);
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

    void validateRoles(DirectoryUser user) {
        if (user.getRoles().isEmpty()) {
            throw new IllegalArgumentException("A user must have at least one assigned role.");
        }
    }

    void checkPermissionsForRoleAssignment(DirectoryUser user) {

        AuthUser auth = (AuthUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();


        if (!AppController.hasRole(auth.getAuthorities(), "ROLE_SYSTEM_ADMIN")) {
            if (user.getRoles().contains(systemAdminRole)) {
                throw new IllegalArgumentException("You do not have the authority to create a system administrator.") ;
            }
            else if ( user.getRoles().contains(supportRole)){
                throw new IllegalArgumentException("You do not have the authority to create a support engineer.") ;
            }
        }

    }

    @Transactional(readOnly=false,propagation = Propagation.REQUIRED)
    @PreAuthorize("(#user.organizationId == principal.organizationId AND  hasRole('ROLE_ORG_ADMIN') ) OR hasRole('ROLE_SYSTEM_ADMIN')")
    public DirectoryUser create(DirectoryUser user)  {
        checkPermissionsForRoleAssignment(user);
        validateRoles(user);
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

    @Transactional(readOnly=true,propagation = Propagation.NEVER)
    public void updateSystemAdminEmails() {
        AuthUser auth = (AuthUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        //If the current user is a system admin, we need to re-generate the email list for system admins because it's
        //used by the email server to send automated emails to those users.
        if (AppController.hasRole(auth.getAuthorities(), "ROLE_SYSTEM_ADMIN")){
            //Atomic operation is thread safe.
            systemAdminEmailAddresses = getEmailAddressesForUsersWithSystemAdminRole();
        }
    }

    @Transactional(readOnly=false,propagation = Propagation.REQUIRED)
    @PreAuthorize("(#user.organizationId == principal.organizationId AND  hasRole('ROLE_ORG_ADMIN') ) OR hasRole('ROLE_SYSTEM_ADMIN')")
    public DirectoryUser update(DirectoryUser user)  {
        checkPermissionsForRoleAssignment(user);
        validateRoles(user);
        return userRepository.save(user);
    }


    private Set<String> getEmailAddressesForUsersWithSystemAdminRole() {
        return getEmailAddressesForUsersByRole(this.systemAdminRole);
    }

    /**
     * Obtain a list of email address for users that have a particular role.  Originally created to obtain email
     * addresses for system admins but usable for other purposes.
     * @param role
     * @return set of email addresses
     */
    private Set<String> getEmailAddressesForUsersByRole(Role role) {

        List<DirectoryUser> sysadminsList = findByRole(role);

        Set<String> emailAddresses = new HashSet<String>();

        for(DirectoryUser user: sysadminsList) {
            String emailAddress = user.getEmail();

            if (!StringUtils.isEmpty(emailAddress)){
                emailAddresses.add(user.getEmail()) ;
            }

        }
        return emailAddresses;

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

    public Set<String> getSystemAdminEmailAddresses() {
        return systemAdminEmailAddresses;
    }
}
