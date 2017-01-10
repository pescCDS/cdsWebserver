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

/**
 * Created by james on 1/2/17.
 */
public enum DocumentType {
    COLLEGE_TRANSCRIPT("College Transcript"),
    HIGHSCHOOL_TRANSCRIPT("Highschool Transcript"),
    TRANSCRIPT_RESPONSE("Transcript Response"),
    FUNCTIONAL_ACKNOWLEDGEMENT("Functional Acknowledgement"),
    TRANSCRIPT_REQUEST("Transcript Request"),
    TRANSCRIPT_ACKNOWLEDGEMENT("Transcript Acknowledgement");

    private final String documentName;

    DocumentType(String documentName) {
        this.documentName = documentName;
    }

    public String getDocumentName() {
        return documentName;
    }
}
