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

package org.pesc.cds.model;

import java.util.List;

/**
 * Created by james on 9/12/16.
 */
public class TranscriptRequestDTO {


    private List<RecordHolderDTO> sourceInstitutions;
    private List<RequestingSchoolDTO> destinationInstitutions;

    private boolean studentRelease;
    private String studentReleasedMethod;
    private String studentBirthDate;
    private String studentFirstName;
    private String studentLastName;
    private String studentMiddleName;
    private String studentEmail;
    private String studentPartialSSN;

    public List<RecordHolderDTO> getSourceInstitutions() {
        return sourceInstitutions;
    }

    public String getStudentLastName() {
        return studentLastName;
    }

    public void setStudentLastName(String studentLastName) {
        this.studentLastName = studentLastName;
    }

    public void setSourceInstitutions(List<RecordHolderDTO> sourceInstitutions) {
        this.sourceInstitutions = sourceInstitutions;
    }

    public List<RequestingSchoolDTO> getDestinationInstitutions() {
        return destinationInstitutions;
    }

    public void setDestinationInstitutions(List<RequestingSchoolDTO> destinationInstitutions) {
        this.destinationInstitutions = destinationInstitutions;
    }

    public boolean isStudentRelease() {
        return studentRelease;
    }

    public void setStudentRelease(boolean studentRelease) {
        this.studentRelease = studentRelease;
    }

    public String getStudentReleasedMethod() {
        return studentReleasedMethod;
    }

    public void setStudentReleasedMethod(String studentReleasedMethod) {
        this.studentReleasedMethod = studentReleasedMethod;
    }

    public String getStudentBirthDate() {
        return studentBirthDate;
    }

    public void setStudentBirthDate(String studentBirthDate) {
        this.studentBirthDate = studentBirthDate;
    }

    public String getStudentFirstName() {
        return studentFirstName;
    }

    public void setStudentFirstName(String studentFirstName) {
        this.studentFirstName = studentFirstName;
    }

    public String getStudentMiddleName() {
        return studentMiddleName;
    }

    public void setStudentMiddleName(String studentMiddleName) {
        this.studentMiddleName = studentMiddleName;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public void setStudentEmail(String studentEmail) {
        this.studentEmail = studentEmail;
    }

    public String getStudentPartialSSN() {
        return studentPartialSSN;
    }

    public void setStudentPartialSSN(String studentPartialSSN) {
        this.studentPartialSSN = studentPartialSSN;
    }
}
