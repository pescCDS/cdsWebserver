package org.pesc.service;

import org.pesc.api.model.Upload;
import org.pesc.api.repository.UploadsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by james on 5/6/16.
 */
@Service
public class UploadService {
    @Autowired
    private UploadsRepository uploadsRepository;

    @Transactional(readOnly=false,propagation = Propagation.REQUIRED)
    @PreAuthorize("(#upload.organizationId == principal.organizationId AND  hasRole('ROLE_ORG_ADMIN') ) OR hasRole('ROLE_SYSTEM_ADMIN')")
    public Upload create(Upload upload)  {
        return uploadsRepository.save(upload);
    }

    @Transactional(readOnly=false,propagation = Propagation.REQUIRED)
    @PreAuthorize("(#upload.organizationId == principal.organizationId AND  hasRole('ROLE_ORG_ADMIN') ) OR hasRole('ROLE_SYSTEM_ADMIN')")
    public Upload update(Upload upload)  {
        return uploadsRepository.save(upload);
    }

    @Transactional(readOnly=false,propagation = Propagation.REQUIRED)
    @PreAuthorize("(#orgID == principal.organizationId AND  hasRole('ROLE_ORG_ADMIN') ) OR hasRole('ROLE_SYSTEM_ADMIN')")
    public List<Upload> getUploadsByOrganization(Integer orgID)  {
        return uploadsRepository.findByOrgId(orgID);
    }
}
