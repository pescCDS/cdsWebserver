package org.pesc.sdk.message.functionalacknowledgment.v1_0;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.pesc.sdk.core.coremain.v1_9.SeverityCodeType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: sallen
 * Date: 6/2/2014
 * Time: 3:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class ValidationResponse {
    private SeverityCodeType severity = null;
    List<SyntaxErrorType> errors = Lists.newArrayList();

    public SeverityCodeType getSeverity() {
        return severity;
    }

    public void setSeverity(SeverityCodeType severity) {
        this.severity = severity;
    }

    /**
     * Add passed in error to <CODE>ValidationResponse.errors</CODE> and sets <CODE>ValidationResponse.severity</CODE> based on error severity.
     * @param error
     */
    public void addError(SyntaxErrorType error){
        addError(error, false);
    }

    /**
     * Add passed in error to top of <CODE>ValidationResponse.errors</CODE> list and sets <CODE>ValidationResponse.severity</CODE> based on error severity.
     * @param error
     */
    public void addErrorToTop(SyntaxErrorType error){
        addError(error, true);
    }

    private void addError(SyntaxErrorType error, boolean addToTop){
        SeverityCodeType severityCode = error.getSeverityCode();
        if(severityCode!=null && severityCode!=this.severity){
            if(this.severity ==null || severityCode==SeverityCodeType.FATAL_ERROR){
                this.severity = severityCode;
            }else if(this.severity ==SeverityCodeType.WARNING && severityCode==SeverityCodeType.ERROR){
                this.severity = severityCode;
            }
        }
        if(addToTop){
            this.getErrors().add(0, error);
        }else {
            this.getErrors().add(error);
        }
    }

    public List<SyntaxErrorType> getErrors() {
        return errors;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("severity", severity)
                .append("errors", errorToString())
                .toString();
    }

    private List<String> errorToString(){
        List<String> errorStrings = Lists.newArrayList();
        for(SyntaxErrorType error: this.errors){
            errorStrings.add(new ToStringBuilder(error)
                    .append("severityCode", error.getSeverityCode())
                    .append("errorMessage", error.getErrorMessage())
                    .toString());
        }
        return errorStrings;
    }
}
