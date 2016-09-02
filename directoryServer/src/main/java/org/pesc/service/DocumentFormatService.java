package org.pesc.service;

import org.pesc.api.model.DocumentFormat;
import org.pesc.api.repository.DocumentFormatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by James Whetstone (jwhetstone@ccctechcenter.org) on 4/6/16.
 */
@Service
public class DocumentFormatService {

    @Autowired
    private DocumentFormatRepository documentFormatRepository;

    public Iterable<DocumentFormat> getDocumentFormats() {
        return documentFormatRepository.findAll();
    }

    @Transactional(readOnly=false,propagation = Propagation.REQUIRED)
    @PreAuthorize("hasRole('ROLE_SYSTEM_ADMIN')")
    public DocumentFormat create(DocumentFormat documentFormat) {
        return documentFormatRepository.save(documentFormat);

    }

    @Transactional(readOnly=false,propagation = Propagation.REQUIRED)
    @PreAuthorize("hasRole('ROLE_SYSTEM_ADMIN')")
    public void delete(Integer id)  {
        this.documentFormatRepository.delete(id);
    }

    @Transactional(readOnly=false,propagation = Propagation.REQUIRED)
    @PreAuthorize("hasRole('ROLE_SYSTEM_ADMIN')")
    public DocumentFormat update(DocumentFormat documentFormat) {
        return documentFormatRepository.save(documentFormat);

    }
}
