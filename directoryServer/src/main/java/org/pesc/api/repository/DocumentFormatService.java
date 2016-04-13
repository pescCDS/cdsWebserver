package org.pesc.api.repository;

import org.pesc.api.model.DocumentFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by james on 4/6/16.
 */
@Service
public class DocumentFormatService {

    @Autowired
    private DocumentFormatRepository documentFormatRepository;

    public Iterable<DocumentFormat> getDocumentFormats() {
        return documentFormatRepository.findAll();
    }
}
