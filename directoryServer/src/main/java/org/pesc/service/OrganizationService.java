package org.pesc.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pesc.api.StringUtils;
import org.pesc.api.model.Endpoint;
import org.pesc.api.model.Organization;
import org.pesc.api.model.OrganizationType;
import org.pesc.api.model.SchoolCode;
import org.pesc.api.repository.OrganizationRepository;
import org.pesc.api.repository.OrganizationTypeRepository;
import org.pesc.api.repository.SchoolCodesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.sql.Timestamp;
import java.util.*;

/**
 * Created by james on 3/21/16.
 *
 * This service mixes JDBC and JPA.  The database configuration for this app instantiates the JpaTransactionManager
 * and according the documentation for JpaTransactionManager for Spring 4,
 * http://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/orm/jpa/JpaTransactionManager.html,
 * it (the JpaTransactionManager) supports direct access (JDBC) to the data source within a transaction.
 *
 * From the documentation:
 *
 * This transaction manager also supports direct DataSource access within a transaction
 * (i.e. plain JDBC code working with the same DataSource). This allows for mixing services which access JPA
 * and services which use plain JDBC (without being aware of JPA)! Application code needs to stick to the same
 * simple Connection lookup pattern as with DataSourceTransactionManager
 * (i.e. DataSourceUtils.getConnection(javax.sql.DataSource) or going through a
 * TransactionAwareDataSourceProxy). Note that this requires a vendor-specific JpaDialect to be configured.
 *
 */
@Service
public class OrganizationService {

    private static final Log log = LogFactory.getLog(OrganizationService.class);

    private EntityManagerFactory entityManagerFactory;


    private List<OrganizationType> organizationTypes;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public OrganizationService(EntityManagerFactory entityManagerFactory, OrganizationTypeRepository organizationTypeRepository) {
        this.organizationTypes = (List<OrganizationType>)organizationTypeRepository.findAll();

        //remove the 'System' type, it should never be used.
        //organizationTypes.remove(0);

        this.entityManagerFactory = entityManagerFactory;

    }

    @Autowired
    private OrganizationRepository organizationRepository;
    @Autowired
    SchoolCodesRepository schoolCodesRepository;

    @Transactional(readOnly=true)
    public Iterable<Organization> findAll(){
        return this.organizationRepository.findAll();
    }

    /**
     * This method is intended to be invoked by a user who has already validated the organization, so it does
     * not require a review/approval, as when a unknown user registers a new organization.
     * @param organization
     * @return
     */
    @Transactional(readOnly=false,propagation = Propagation.REQUIRED)
    @PreAuthorize("hasRole('ROLE_SYSTEM_ADMIN') ")
    public Organization create(Organization organization){
        organization.setEnabled(true);
        organization.setActive(true);
        return organizationRepository.save(organization);
    }

    /**
     * Insert a new organization and school codes, associating each school code with the organization and inserting
     * the school codes.
     * @param organization
     * @return
     */
    @Transactional(readOnly=false,propagation = Propagation.REQUIRED)
    @PreAuthorize("hasRole('ROLE_ORG_ADMIN') ")
    public Organization createInstitution(Organization organization, Set<SchoolCode> schoolCodes, Integer serviceProviderID){
        organization.setEnabled(false);
        organization.setActive(true);
        organization.setOrganizationTypes(new HashSet<OrganizationType>());
        organization.getOrganizationTypes().add(this.getOrganizationTypes().get(1));

        Organization savedOrg= organizationRepository.save(organization);
        for(SchoolCode schoolCode: schoolCodes) {
            schoolCode.setOrganizationId(savedOrg.getId());
            schoolCodesRepository.save(schoolCode);
        }
        savedOrg.setSchoolCodes(schoolCodes);

        linkInstitutionWithServiceProvider(savedOrg.getId(), serviceProviderID);

        return savedOrg;
    }


    @Transactional(readOnly=false,propagation = Propagation.REQUIRED)
    public void linkInstitutionWithServiceProvider(Integer institutionID, Integer serviceProviderID) {
        try {
            jdbcTemplate.update("insert into institutions_service_providers (institution_id, service_provider_id) values(?,?)", institutionID, serviceProviderID);
        }
        catch (DuplicateKeyException e) {
           log.warn("A duplicate key was encountered while associating a service provider with an institution.", e);
        }

    }


    @Transactional(readOnly=false,propagation = Propagation.REQUIRED)
    @PreAuthorize("( (#organization.id == principal.organizationId AND hasRole('ROLE_ORG_ADMIN')) OR hasRole('ROLE_SYSTEM_ADMIN') )")
    public Organization update(Organization organization){
        return this.organizationRepository.save(organization);
    }
    @Transactional(readOnly=false,propagation = Propagation.REQUIRED)
    @PreAuthorize("hasRole('ROLE_SYSTEM_ADMIN')")
    public void delete(Organization organization)  {
        this.organizationRepository.delete(organization);
    }

    @Transactional(readOnly=false,propagation = Propagation.REQUIRED)
    @PreAuthorize("hasRole('ROLE_SYSTEM_ADMIN')")
    public void delete(Integer id)  {
        this.organizationRepository.delete(id);
    }

    @Transactional(readOnly=true,propagation = Propagation.REQUIRED)
    //TODO: add PreAuthorize
    public List<Organization> findByName(String name)  {
        return this.organizationRepository.findByName(name);
    }


    @Transactional(readOnly=true,propagation = Propagation.REQUIRED)
    //TODO: add PreAuthorize
    public Organization findById(Integer id)  {

        return this.organizationRepository.findOne(id);
    }

    @Transactional(readOnly=false,propagation = Propagation.REQUIRED)
    public void addEndpointToOrganization(Integer orgID, Integer endpointID) {

        jdbcTemplate.update(
                "insert into endpoint_organization (endpoint_id, organization_id) values (?, ?)",
                endpointID, orgID);

    }

    public List<OrganizationType> getOrganizationTypes() {
        return organizationTypes;
    }



    @Transactional(readOnly=false,propagation = Propagation.REQUIRED)
    public void removeEndpointToOrganization(Integer orgID,
                                             Integer endpointID) {

        jdbcTemplate.update(
                "delete from endpoint_organization where endpoint_id = ? and organization_id =?", endpointID, orgID);
    }


    @Transactional(readOnly=false,propagation = Propagation.REQUIRED)
    @PreAuthorize("hasRole('ROLE_SYSTEM_ADMIN')")
    public void setEnabledForOrganization(Integer orgID, Boolean enable) {

        jdbcTemplate.update(
                "update organization set enabled = ? where id = ?", orgID, enable);
    }

    @Transactional(readOnly=false,propagation = Propagation.REQUIRED)
    @PreAuthorize("hasRole('ROLE_SYSTEM_ADMIN')")
    public void setProperty(Integer orgID, String propertyName, Object propertyValue) {

        log.info(String.format("Updating %s property to %s.", propertyName, propertyValue));
        jdbcTemplate.update(
                String.format("update organization set %s = ? where id = ?", propertyName), propertyValue, orgID);
    }


    /**
     *
     * @param schoolCodesList
     * @return
     */
    @Transactional(readOnly=true,propagation = Propagation.REQUIRED)
    public List<Organization> findBySchoolCodes(Set<SchoolCode> schoolCodesList
    ) {
        EntityManager entityManager =  entityManagerFactory.createEntityManager();
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        CriteriaQuery<Organization> cq = cb.createQuery(Organization.class);
        Root<Organization> org = cq.from(Organization.class);
        cq.select(org).distinct(true);

        List<Predicate> predicates = new LinkedList<Predicate>();

        //ArrayList<String> codeTypes;


        if ( schoolCodesList != null ) {
            ArrayList<String> codes = new ArrayList<String>();

            for(SchoolCode sc : schoolCodesList) {
                codes.add(sc.getCode());
            }

            Join<SchoolCode, Organization> join = org.join("schoolCodes");

            //predicates.add(org.get("schoolCodes").get("code").in(schoolCodesList));
            predicates.add(join.get("code").in(codes));
        }



        Predicate[] predicateArray = new Predicate[predicates.size()];
        predicates.toArray(predicateArray);

        cq.where(predicateArray);
        TypedQuery<Organization> q = entityManager.createQuery(cq);

        return q.getResultList();

    }
    /**
     *
     * @param directoryId
     * @param organizationCode
     * @param organizationCodeType
     * @param organizationName
     * @param organizationSubcode
     * @param organizationType
     * @param organizationEin
     * @param createdTime
     * @param modifiedTime
     * @return
     */
    @Transactional(readOnly=true,propagation = Propagation.REQUIRED)
    public List<Organization> search(
            Integer directoryId,
            String organizationCode,
            String organizationCodeType,
            String organizationName,
            String organizationSubcode,
            String organizationType,
            String organizationEin,
            Long createdTime,
            Long modifiedTime,
            Boolean active,
            Boolean enabled
    ) {

        try {

            EntityManager entityManager =  entityManagerFactory.createEntityManager();
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();

            CriteriaQuery<Organization> cq = cb.createQuery(Organization.class);
            Root<Organization> org = cq.from(Organization.class);
            cq.select(org);

            List<Predicate> predicates = new LinkedList<Predicate>();

            if(directoryId!=null) {
                predicates.add(cb.equal(org.get("id"), directoryId));
            }


            if(!StringUtils.isEmpty(organizationName)) {
                // tokenize string by whitespace and use each token as
                // a LIKE clause for the organizationName column
                StringTokenizer orgNametokens = new StringTokenizer(organizationName.trim());
                while(orgNametokens.hasMoreElements()) {

                    String orgNametoken = orgNametokens.nextToken().toLowerCase();

                    predicates.add(cb.like(cb.lower(org.get("name")), "%" + orgNametoken + "%"));

                }

            }

            if( !StringUtils.isEmpty(organizationSubcode)) {
                StringTokenizer subCodetokens = new StringTokenizer(organizationSubcode.trim());
                while(subCodetokens.hasMoreElements()) {
                    String subCodetoken = subCodetokens.nextToken().toLowerCase();
                    predicates.add(cb.like(cb.lower(org.get("subcode")), "%" + subCodetoken + "%"));
                }
            }


            if ( !StringUtils.isEmpty(organizationType) ) {

                Join<OrganizationType, Organization> join = org.join("organizationTypes");
                predicates.add(cb.equal(join.get("name"), organizationType));

            }

            if(!StringUtils.isEmpty(organizationEin)) {

                predicates.add(cb.like(cb.lower(org.get("ein")), "%" + organizationEin.trim().toLowerCase()));

            }

            if ( !StringUtils.isEmpty(organizationCode) ) {

                Join<SchoolCode, Organization> join = org.join("schoolCodes");
                predicates.add(cb.equal(join.get("code"), organizationCode));

                if (organizationCodeType != null) {
                    predicates.add(cb.and(cb.equal(join.get("codeType"), organizationCodeType)));
                }

            }



            if(createdTime!=null) {
                predicates.add(cb.equal(org.get("createdTime"), new Timestamp(createdTime)));
            }

            if(modifiedTime!=null) {
                predicates.add(cb.equal(org.get("modifiedTime"), new Timestamp(modifiedTime)));
            }


            if (active != null) {
                predicates.add(cb.equal(org.get("active"), active));
            }

            if (enabled != null) {
                predicates.add(cb.equal(org.get("enabled"), enabled));
            }

            Predicate[] predicateArray = new Predicate[predicates.size()];
            predicates.toArray(predicateArray);

            cq.where(predicateArray);
            TypedQuery<Organization> q = entityManager.createQuery(cq);

            return q.getResultList();


        } catch(Exception ex) {
            log.error("Failed to execute organization search query.", ex);
        }
        return new ArrayList<Organization>();
    }

}
