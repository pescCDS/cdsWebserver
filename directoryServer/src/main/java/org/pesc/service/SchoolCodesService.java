package org.pesc.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pesc.api.model.SchoolCode;
import org.pesc.api.repository.SchoolCodesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManagerFactory;

/**
 * Created by james on 3/21/16.
 */
@Service
public class SchoolCodesService {

    private static final Log log = LogFactory.getLog(SchoolCodesService.class);

    protected EntityManagerFactory entityManagerFactory;
    
    @Autowired
    public SchoolCodesService(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Autowired
    private SchoolCodesRepository schoolCodesRepository;

    @Transactional(readOnly=true)
    public Iterable<SchoolCode> findAll(){
        return this.schoolCodesRepository.findAll();
    }

    @Transactional(readOnly=false,propagation = Propagation.REQUIRED)
    @PreAuthorize("( (#schoolCode.organizationId == principal.organizationId AND  hasRole('ROLE_ORG_ADMIN') ) OR hasRole('ROLE_SYSTEM_ADMIN') )")
    public void delete(SchoolCode schoolCode)  {
        this.schoolCodesRepository.delete(schoolCode);
    }

    @Transactional(readOnly=false,propagation = Propagation.REQUIRED)
    @PostAuthorize("( (returnObject.organizationId == principal.organizationId AND hasRole('ROLE_ORG_ADMIN')) OR hasRole('ROLE_SYSTEM_ADMIN'))")
    public void delete(Integer id)  {
        this.schoolCodesRepository.delete(id);
    }



    @Transactional(readOnly=true,propagation = Propagation.REQUIRED)
    public SchoolCode findById(Integer id)  {
        SchoolCode schoolCode = this.schoolCodesRepository.findOne(id);
        return schoolCode;
    }

    @Transactional(readOnly=false,propagation = Propagation.REQUIRED)
    @PreAuthorize("(#schoolCode.organizationId == principal.organizationId AND  hasRole('ROLE_ORG_ADMIN') ) OR hasRole('ROLE_SYSTEM_ADMIN')")
    public SchoolCode create(SchoolCode schoolCode)  {
        return this.schoolCodesRepository.save(schoolCode);
    }

    @Transactional(readOnly=false,propagation = Propagation.REQUIRED)
    @PreAuthorize("(#schoolCode.organizationId == principal.organizationId AND  hasRole('ROLE_ORG_ADMIN') ) OR hasRole('ROLE_SYSTEM_ADMIN')")
    public SchoolCode update(SchoolCode schoolCode)  {

        return this.schoolCodesRepository.save(schoolCode);
    }

}
