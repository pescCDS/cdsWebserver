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

package org.pesc.api.model;

/**
 * Created by James Whetstone (jwhetstone@ccctechcenter.org) on 4/27/16.
 */
public enum MessageTopic {
    REGISTRATION("EDExchange registration"), INFO("EDExchange information"), NETWORK_CERTIFICATE_CHANGED("Network Certificate Changed"), SIGNING_CERTIFICATE_CHANGED("Signing Certificate Changed");

    private String friendlyName;

    private MessageTopic(String value) {
        friendlyName = value;
    }

    public String getFriendlyName() {
        return friendlyName;
    }
}

