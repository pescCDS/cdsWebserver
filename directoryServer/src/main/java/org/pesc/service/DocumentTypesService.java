package org.pesc.service;

import org.pesc.api.model.DocumentType;
import org.pesc.api.repository.DocumentTypesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


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
}
