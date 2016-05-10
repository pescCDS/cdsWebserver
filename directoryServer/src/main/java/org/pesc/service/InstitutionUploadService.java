package org.pesc.service;

import org.pesc.api.model.InstitutionsUpload;
import org.pesc.api.model.InstitutionsUploadResult;
import org.pesc.api.repository.InstitutionUploadResultsRepository;
import org.pesc.api.repository.InstitutionUploadsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by james on 5/6/16.
 */
@Service
public class InstitutionUploadService {
    @Autowired
    private InstitutionUploadsRepository uploadsRepository;

    @Autowired
    private InstitutionUploadResultsRepository resultsRepository;

    @Transactional(readOnly=false,propagation = Propagation.REQUIRED)
    @PreAuthorize("(#institutionsUpload.organizationId == principal.organizationId AND  hasRole('ROLE_ORG_ADMIN') ) OR hasRole('ROLE_SYSTEM_ADMIN')")
    public InstitutionsUpload create(InstitutionsUpload institutionsUpload)  {
        return uploadsRepository.save(institutionsUpload);
    }

    @Transactional(readOnly=false,propagation = Propagation.REQUIRED)
    @PreAuthorize("(#institutionsUpload.organizationId == principal.organizationId AND  hasRole('ROLE_ORG_ADMIN') ) OR hasRole('ROLE_SYSTEM_ADMIN')")
    public InstitutionsUpload update(InstitutionsUpload institutionsUpload)  {
        return uploadsRepository.save(institutionsUpload);
    }

    @Transactional(readOnly=true,propagation = Propagation.REQUIRED)
    @PreAuthorize("(#orgID == principal.organizationId AND  hasRole('ROLE_ORG_ADMIN') ) OR hasRole('ROLE_SYSTEM_ADMIN')")
    public List<InstitutionsUpload> getUploadsByOrganization(Integer orgID)  {
        return uploadsRepository.findByOrgId(orgID);
    }

    @Transactional(readOnly=true,propagation = Propagation.REQUIRED)
    @PostAuthorize("( (returnObject.organizationId == principal.organizationId AND hasRole('ROLE_ORG_ADMIN') ) OR hasRole('ROLE_SYSTEM_ADMIN'))")
    public InstitutionsUpload getUploadById(Integer uploadId)  {
        return uploadsRepository.findOne(uploadId);
    }

    @Transactional(readOnly=true,propagation = Propagation.REQUIRED)
    public List<InstitutionsUploadResult> getUploadResultsByUploadId(Integer uploadId)  {
        return resultsRepository.findByUploadId(uploadId);
    }

    @Transactional(readOnly=true,propagation = Propagation.REQUIRED)
    public List<InstitutionsUploadResult> getUploadResultsByOrgId(Integer orgID)  {
        return resultsRepository.findByOrgId(orgID);
    }


    @Transactional(readOnly=false,propagation = Propagation.REQUIRED)
    @PreAuthorize("(#institutionsResultUpload.organizationID == principal.organizationId AND  hasRole('ROLE_ORG_ADMIN') ) OR hasRole('ROLE_SYSTEM_ADMIN')")
    public InstitutionsUploadResult create(InstitutionsUploadResult institutionsResultUpload)  {
        return resultsRepository.save(institutionsResultUpload);
    }
}
