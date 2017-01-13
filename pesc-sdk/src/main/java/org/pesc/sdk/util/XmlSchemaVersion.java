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

package org.pesc.sdk.util;

/**
 * Created by rgehbauer on 9/2/15.
 */
public enum XmlSchemaVersion {
    V1_0_0("v1.0.0"),
    V1_5_0("v1.5.0"),
    V1_6_0("v1.6.0"),
    V1_2_0("v1.2.0"),
    V1_3_0("v1.3.0"),
    V1_4_0("v1.4.0");

    private final String versionText;

    XmlSchemaVersion(String versionText) {
        this.versionText = versionText;
    }

    public String getVersionText() {
        return versionText;
    }
}
