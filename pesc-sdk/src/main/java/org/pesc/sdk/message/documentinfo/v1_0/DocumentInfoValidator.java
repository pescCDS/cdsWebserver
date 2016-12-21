package org.pesc.sdk.message.documentinfo.v1_0;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;
import org.pesc.sdk.core.coremain.v1_14.SeverityCodeType;
import org.pesc.sdk.message.functionalacknowledgment.v1_2.ValidationResponse;
import org.pesc.sdk.util.ValidationUtils;

/**
 * Created with IntelliJ IDEA.
 * User: sallen
 * Date: 5/27/2014
 * Time: 10:49 AM
 * To change this template use File | Settings | File Templates.
 */
public class DocumentInfoValidator {

    public static ValidationResponse validateDocumentInfoRequiredContent(DocumentInfo documentInfo){
        ValidationResponse validationResponse = new ValidationResponse();
        String fileName = null;
        DocumentTypeCode documentType = null;
        String format = null;

        Preconditions.checkNotNull(documentInfo);


        ValidationUtils.checkArgument(documentInfo.getDocuments() != null && !documentInfo.getDocuments().isEmpty(),
                "At least one TranscriptRequest.TransmissionData.UserDefinedExtensions.DocumentInfo.document element must be present.",
                validationResponse,
                SeverityCodeType.ERROR);


        for(DocumentInfoType document : documentInfo.getDocuments()) {
            fileName = document.getFileName();
            documentType = document.getDocumentType();
            format = document.getDocumentFormat();

            ValidationUtils.checkArgument(StringUtils.isNotBlank(fileName) || StringUtils.isNotBlank(format),
                    "Either TranscriptRequest.TransmissionData.UserDefinedExtensions.DocumentInfo.document.FileName or TranscriptRequest.TransmissionData.UserDefinedExtensions.DocumentInfo.document.documentFormat must be present.", validationResponse, SeverityCodeType.ERROR);

            ValidationUtils.checkNotNull(documentType, "TranscriptRequest.TransmissionData.UserDefinedExtensions.DocumentInfo.document.DocumentType is missing, this field is required.", validationResponse, SeverityCodeType.ERROR);
        }

        return validationResponse;
    }
}
