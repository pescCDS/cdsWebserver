package org.pesc.sdk.util;

import org.pesc.sdk.core.coremain.v1_9.SeverityCodeType;
import org.pesc.sdk.message.functionalacknowledgment.v1_0.SyntaxErrorType;
import org.pesc.sdk.message.functionalacknowledgment.v1_0.ValidationResponse;

/**
 * Created with IntelliJ IDEA.
 * User: sallen
 * Date: 7/5/2014
 * Time: 1:46 AM
 * To change this template use File | Settings | File Templates.
 */
public class ValidationUtils {
    private static final org.pesc.sdk.message.functionalacknowledgment.v1_0.ObjectFactory functionalAcknowledgmentObjectFactory = new org.pesc.sdk.message.functionalacknowledgment.v1_0.ObjectFactory();

    public static void checkArgument(boolean expression, String errorMessage, ValidationResponse validationResponse, SeverityCodeType severity){
        if(!expression){
            SyntaxErrorType syntaxError = functionalAcknowledgmentObjectFactory.createSyntaxErrorType();
            syntaxError.setErrorMessage(errorMessage);
            syntaxError.setSeverityCode(severity);
            validationResponse.addError(syntaxError);
        }
    }

    public static void checkNotNull(Object reference, String errorMessage, ValidationResponse validationResponse, SeverityCodeType severity){
        if(reference == null){
            SyntaxErrorType syntaxError = functionalAcknowledgmentObjectFactory.createSyntaxErrorType();
            syntaxError.setErrorMessage(errorMessage);
            syntaxError.setSeverityCode(severity);
            validationResponse.addError(syntaxError);
        }
    }
}
