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

package org.pesc.cds.utils;

/**
 * Created by james on 1/5/17.
 */
public class ErrorUtils {

    public static String getNoEndpointFoundMessage(Integer recipientID, String fileFormat, String documentType, String department) {
        return String.format("No endpoint URI exists for the given EDExchange ID (%d), document format (%s), document type (%s) and department (%s).",
                recipientID,
                fileFormat,
                documentType,
                department);
    }
}
