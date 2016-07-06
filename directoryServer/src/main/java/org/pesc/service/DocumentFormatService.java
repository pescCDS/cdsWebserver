package org.pesc.service;

import org.pesc.api.model.DocumentFormat;
import org.pesc.api.repository.DocumentFormatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
