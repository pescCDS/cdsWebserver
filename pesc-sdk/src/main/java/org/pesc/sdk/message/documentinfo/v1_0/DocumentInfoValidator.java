package org.pesc.sdk.message.documentinfo.v1_0;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;
import org.pesc.sdk.core.coremain.v1_9.SeverityCodeType;
import org.pesc.sdk.message.functionalacknowledgment.v1_0.ValidationResponse;
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

        Preconditions.checkNotNull(documentInfo);
        fileName = documentInfo.getFileName();
        documentType = documentInfo.getDocumentType();

        String missingRequiredText = " is missing, this field is required.";
        ValidationUtils.checkArgument(StringUtils.isNotBlank(fileName), "TranscriptRequest.TransmissionData.UserDefinedExtensions.DocumentInfo.FileName" + missingRequiredText, validationResponse, SeverityCodeType.ERROR);
        ValidationUtils.checkNotNull(documentType, "TranscriptRequest.TransmissionData.UserDefinedExtensions.DocumentInfo.DocumentType" + missingRequiredText, validationResponse, SeverityCodeType.ERROR);
        return validationResponse;
    }
}
