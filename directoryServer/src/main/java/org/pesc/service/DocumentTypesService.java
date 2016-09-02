package org.pesc.service;

import org.pesc.api.model.Department;
import org.pesc.api.model.DocumentFormat;
import org.pesc.api.model.DocumentType;
import org.pesc.api.repository.DocumentTypesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


/**
 * Created by James Whetstone (jwhetstone@ccctechcenter.org) on 4/6/16.
 */
@Service
public class DocumentTypesService {

    @Autowired
    private DocumentTypesRepository documentTypesRepository;

    public Iterable<DocumentType> getDocumentTypes() {
        return documentTypesRepository.findAll();
    }

    @Transactional(readOnly=false,propagation = Propagation.REQUIRED)
    @PreAuthorize("hasRole('ROLE_SYSTEM_ADMIN')")
    public DocumentType create(DocumentType documentType) {
        return documentTypesRepository.save(documentType);

    }

    @Transactional(readOnly=false,propagation = Propagation.REQUIRED)
    @PreAuthorize("hasRole('ROLE_SYSTEM_ADMIN')")
    public void delete(Integer id)  {
        this.documentTypesRepository.delete(id);
    }

    @Transactional(readOnly=false,propagation = Propagation.REQUIRED)
    @PreAuthorize("hasRole('ROLE_SYSTEM_ADMIN')")
    public DocumentType update(DocumentType documentType) {
        return documentTypesRepository.save(documentType);

    }
}
