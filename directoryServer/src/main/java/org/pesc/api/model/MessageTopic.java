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

