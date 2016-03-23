package org.pesc.api.repository;

import org.pesc.api.model.Organization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by james on 3/21/16.
 */
@Service
public class OrganizationService {

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

    @Transactional(readOnly=false,propagation = Propagation.REQUIRED)
    public List<Organization> findByName(String name)  {
        return this.organizationRepository.findByName(name);
    }


    @Transactional(readOnly=false,propagation = Propagation.REQUIRED)
    public Organization findById(Integer id)  {
        return this.organizationRepository.findOne(id);
    }


}
