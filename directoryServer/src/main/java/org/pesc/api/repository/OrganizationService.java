package org.pesc.api.repository;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.pesc.api.model.Organization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManagerFactory;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by james on 3/21/16.
 */
@Service
public class OrganizationService {

    private static final Log log = LogFactory.getLog(OrganizationService.class);

    protected SessionFactory hibernateFactory;

    @Autowired
    public OrganizationService(EntityManagerFactory factory) {
        if (factory.unwrap(SessionFactory.class) == null) {
            throw new NullPointerException("Expected Hibernate factory doesn't exist.");
        }
        this.hibernateFactory = factory.unwrap(SessionFactory.class);
    }

    @Autowired
    private OrganizationRepository organizationRepository;

    @Transactional(readOnly=true)
    public Iterable<Organization> findAll(){
        return this.organizationRepository.findAll();
    }

    @Transactional(readOnly=false,propagation = Propagation.REQUIRED)
    public void persist(Organization organization){
        this.organizationRepository.save(organization);
    }

    @Transactional(readOnly=false,propagation = Propagation.REQUIRED)
    public void delete(Organization organization)  {
        this.organizationRepository.delete(organization);
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
     * @param organizationId
     * @param organizationIdType
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
            String organizationId,
            String organizationIdType,
            String organizationName,
            String organizationSubcode,
            Integer organizationType,
            String organizationEin,
            Long createdTime,
            Long modifiedTime
    ) {
        List<Organization> retList = new ArrayList<Organization>();
        try {

            //TODO: implement the search criteria using JPA 2.0 standard instead of the
            //Hibernate specific criteria.  This additional abstraction will decouple the service
            //from Hibernate and enforce type safety on the criteria.
            Session session = hibernateFactory.getCurrentSession();

            Criteria ct = session.createCriteria(Organization.class);
            boolean hasCriteria = false;

            if(directoryId!=null) {
                ct.add(Restrictions.idEq(directoryId));
                hasCriteria = true;
            }

            if(organizationId!=null && organizationId.trim().length()>0) {
                ct.add(Restrictions.ilike("organizationCode", organizationId.trim(), MatchMode.START));
                hasCriteria = true;
            }

            if(organizationIdType!=null && organizationIdType.trim().length()>0) {
                ct.add(Restrictions.ilike("organizationCodeType", organizationIdType.trim(), MatchMode.START));
                hasCriteria = true;
            }

            if(organizationName!=null && organizationName.trim().length()>0) {
                // tokenize string by whitespace and use each token as
                // a LIKE clause for the organizationName column
                StringTokenizer orgNametokens = new StringTokenizer(organizationName.trim());
                while(orgNametokens.hasMoreElements()) {
                    String orgNametoken = orgNametokens.nextToken();
                    ct.add( Restrictions.ilike("name", orgNametoken, MatchMode.ANYWHERE));
                }
                hasCriteria = true;
            }

            if(organizationSubcode!=null && organizationSubcode.trim().length()>0) {
                StringTokenizer subCodetokens = new StringTokenizer(organizationSubcode.trim());
                while(subCodetokens.hasMoreElements()) {
                    String subCodetoken = subCodetokens.nextToken();
                    ct.add( Restrictions.ilike("subcode", subCodetoken, MatchMode.ANYWHERE));
                }
                hasCriteria = true;
            }

            if(organizationType!=null) {

                ct.add(Restrictions.eq("type", organizationType));
                hasCriteria = true;
            }

            if(organizationEin!=null && organizationEin.trim().length()>0) {
                ct.add(Restrictions.ilike("ein", organizationEin, MatchMode.START));
                hasCriteria = true;
            }

            if(createdTime!=null) {
                ct.add(Restrictions.eq("createdTime", new Timestamp(createdTime)));
                hasCriteria = true;
            }

            if(modifiedTime!=null) {
                ct.add(Restrictions.eq("modifiedTime", new Timestamp(modifiedTime)));
                hasCriteria = true;
            }

            if(hasCriteria) {
                retList = ct.list();
            }
            else {
                retList = (List<Organization>)this.organizationRepository.findAll();
            }

        } catch(Exception ex) {
            log.error("Failed to execute organization search query.", ex);
        }
        return retList;
    }

}
