package org.pesc.service;

import com.sun.org.apache.xpath.internal.operations.Bool;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pesc.api.StringUtils;
import org.pesc.api.model.Endpoint;
import org.pesc.api.model.Organization;
import org.pesc.api.repository.EndpointRepository;
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
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by James Whetstone (jwhetstone@ccctechcenter.org) on 3/21/16.
 */
@Service
public class EndpointService {

    private static final Log log = LogFactory.getLog(EndpointService.class);

    protected EntityManagerFactory entityManagerFactory;
    
    @Autowired
    public EndpointService(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Autowired
    private EndpointRepository endpointRepository;

    @Transactional(readOnly=true)
    public Iterable<Endpoint> findAll(){
        return this.endpointRepository.findAll();
    }

    @Transactional(readOnly=false,propagation = Propagation.REQUIRED)
    @PreAuthorize("(#endpoint.organization.id == principal.organizationId AND hasRole('ROLE_ORG_ADMIN') ) OR hasRole('ROLE_SYSTEM_ADMIN')")
    public void delete(Endpoint endpoint)  {
        this.endpointRepository.delete(endpoint);
    }


    @Transactional(readOnly=false,propagation = Propagation.REQUIRED)
    @PostAuthorize("( (returnObject.organization.id == principal.organizationId AND hasRole('ROLE_ORG_ADMIN') ) OR hasRole('ROLE_SYSTEM_ADMIN'))")
    public Endpoint delete(Integer id)  {
        Endpoint endpoint = endpointRepository.findOne(id) ;
        this.endpointRepository.delete(endpoint);
        return endpoint;
    }


    @Transactional(readOnly=true,propagation = Propagation.REQUIRED)
    public Endpoint findById(Integer id)  {
        return this.endpointRepository.findOne(id);
    }

    @Transactional(readOnly=true,propagation = Propagation.REQUIRED)
    public List<Endpoint> findByIDList(List<Integer> idList)  {
        return (List<Endpoint>)this.endpointRepository.findAll(idList);
    }


    @Transactional(readOnly=false,propagation = Propagation.REQUIRED)
    @PreAuthorize("( (!(principal.organizationTypes.?[#this.name == 'Service Provider'].empty) AND #endpoint.organization.id == principal.organizationId AND hasRole('ROLE_ORG_ADMIN')) OR hasRole('ROLE_SYSTEM_ADMIN')  )")
    public Endpoint create(Endpoint endpoint)  {
        return this.endpointRepository.save(endpoint);
    }

    @Transactional(readOnly=false,propagation = Propagation.REQUIRED)
    @PreAuthorize("(#endpoint.organization.id == principal.organizationId AND  hasRole('ROLE_ORG_ADMIN') ) OR hasRole('ROLE_SYSTEM_ADMIN')")
    public Endpoint update(Endpoint endpoint)  {

        return this.endpointRepository.save(endpoint);
    }


    /**
     *
     * @param documentFormat
     * @param endpointId
     * @param hostingOrganizationId
     * @param organizationIdList
     * @return
     */
    @Transactional(readOnly=true,propagation = Propagation.REQUIRED)
    public List<Endpoint> search(
            String documentFormat,
            String documentType,
            String departmentName,
            Integer endpointId,
            Integer hostingOrganizationId,
            List<Integer> organizationIdList,
            String mode,
            String enabled
    ) {

        try {

            EntityManager entityManager = entityManagerFactory.createEntityManager();

            CriteriaBuilder cb = entityManager.getCriteriaBuilder();

            CriteriaQuery<Endpoint> cq = cb.createQuery(Endpoint.class);
            Root<Endpoint> endpoint = cq.from(Endpoint.class);
            cq.select(endpoint);

            List<Predicate> predicates = new LinkedList<Predicate>();

            if ( organizationIdList != null ) {

                Join<Endpoint, Organization> organizations = endpoint.join("organizations");
                predicates.add(organizations.get("id").in(organizationIdList));


            }

            if (hostingOrganizationId != null) {
                predicates.add(cb.equal(endpoint.get("organization").get("id"), hostingOrganizationId));
            }

            if (!StringUtils.isEmpty(enabled)) {
                predicates.add(cb.equal(endpoint.get("organization").get("enabled"), Boolean.valueOf(enabled)));
            }

            if (endpointId != null) {
                predicates.add(cb.equal(endpoint.get("id"), endpointId));
            }
            if (documentFormat != null) {
                predicates.add(cb.equal(endpoint.get("documentFormat").get("name"), documentFormat));
            }
            if (documentType != null) {
                predicates.add(cb.equal(endpoint.get("documentType").get("name"), documentType));
            }
            if (departmentName != null) {
                predicates.add(cb.equal(endpoint.get("department").get("name"), departmentName));
            }
            if (mode != null) {
                predicates.add(cb.equal(endpoint.get("mode"), mode.toUpperCase()));
            }

            Predicate[] predicateArray = new Predicate[predicates.size()];
            predicates.toArray(predicateArray);

            cq.where(predicateArray);
            TypedQuery<Endpoint> q = entityManager.createQuery(cq);

            return q.getResultList();


        } catch(Exception ex) {
            log.error("Failed to execute endpoint search query.", ex);
        }
        return new ArrayList<Endpoint>();
    }

}
