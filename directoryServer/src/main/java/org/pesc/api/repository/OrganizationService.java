package org.pesc.api.repository;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pesc.api.model.Organization;
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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by james on 3/21/16.
 */
@Service
public class OrganizationService {

    private static final Log log = LogFactory.getLog(OrganizationService.class);

    private EntityManagerFactory entityManagerFactory;

    @Autowired
    public OrganizationService(EntityManagerFactory entityManagerFactory) {
       this.entityManagerFactory = entityManagerFactory;

    }

    @Autowired
    private OrganizationRepository organizationRepository;

    @Transactional(readOnly=true)
    public Iterable<Organization> findAll(){
        return this.organizationRepository.findAll();
    }

    @Transactional(readOnly=false,propagation = Propagation.REQUIRED)
    @PreAuthorize("hasRole('ROLE_SYSTEM_ROLE') ")
    public Organization create(Organization organization){
        return this.organizationRepository.save(organization);
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
    public List<Organization> findByName(String name)  {
        return this.organizationRepository.findByName(name);
    }


    @Transactional(readOnly=true,propagation = Propagation.REQUIRED)
    public Organization findById(Integer id)  {

        return this.organizationRepository.findOne(id);
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
            Integer organizationType,
            String organizationEin,
            Long createdTime,
            Long modifiedTime
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

            if(organizationCode!=null && organizationCode.trim().length()>0) {
                predicates.add(cb.like(cb.lower(org.get("organizationCode")), "%" + organizationCode.trim().toLowerCase()));
            }

            if(organizationCodeType!=null && organizationCodeType.trim().length()>0) {
                predicates.add(cb.like(cb.lower(org.get("organizationCodeType")), "%" + organizationCodeType.trim().toLowerCase()));
            }

            if(organizationName!=null && organizationName.trim().length()>0) {
                // tokenize string by whitespace and use each token as
                // a LIKE clause for the organizationName column
                StringTokenizer orgNametokens = new StringTokenizer(organizationName.trim());
                while(orgNametokens.hasMoreElements()) {

                    String orgNametoken = orgNametokens.nextToken().toLowerCase();

                    predicates.add(cb.like(cb.lower(org.get("name")), "%" + orgNametoken + "%"));

                }

            }

            if(organizationSubcode!=null && organizationSubcode.trim().length()>0) {
                StringTokenizer subCodetokens = new StringTokenizer(organizationSubcode.trim());
                while(subCodetokens.hasMoreElements()) {
                    String subCodetoken = subCodetokens.nextToken().toLowerCase();
                    predicates.add(cb.like(cb.lower(org.get("subcode")), "%" + subCodetoken + "%"));
                }
            }

            if(organizationType!=null) {
                predicates.add(cb.equal(org.get("type"), organizationType));
            }

            if(organizationEin!=null && organizationEin.trim().length()>0) {

                predicates.add(cb.like(cb.lower(org.get("ein")), "%" + organizationEin.trim().toLowerCase()));

            }

            if(createdTime!=null) {
                predicates.add(cb.equal(org.get("createdTime"), new Timestamp(createdTime)));
            }

            if(modifiedTime!=null) {
                predicates.add(cb.equal(org.get("modifiedTime"), new Timestamp(modifiedTime)));
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
