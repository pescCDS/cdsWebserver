package org.pesc.service;


import org.pesc.api.model.Institution;
import org.pesc.api.repository.InstitutionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by james on 6/21/16.
 */
@Service
public class InstitutionService {

    @Autowired
    private InstitutionRepository institutionRepository;


    public Institution findById(Integer id) {
        return institutionRepository.findOne(id);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    @PreAuthorize("hasRole('ROLE_SYSTEM_ADMIN') OR hasRole('ROLE_ORG_ADMIN')")
    public Institution save(Institution institution) {
        return institutionRepository.save(institution);

    }
}
