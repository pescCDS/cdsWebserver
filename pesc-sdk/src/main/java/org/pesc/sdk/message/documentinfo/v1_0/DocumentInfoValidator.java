/*
 * Copyright (c) 2017. California Community Colleges Technology Center
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.pesc.sdk.message.documentinfo.v1_0;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;
import org.pesc.sdk.core.coremain.v1_14.SeverityCodeType;
import org.pesc.sdk.message.functionalacknowledgement.v1_2.ValidationResponse;
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
