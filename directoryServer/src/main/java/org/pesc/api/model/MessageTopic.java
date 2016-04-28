package org.pesc.api.model;

/**
 * Created by james on 4/27/16.
 */
public enum MessageTopic {
    REGISTRATION("EDExchange registration"), INFO("EDExchange information");

    private String friendlyName;

    private MessageTopic(String value) {
        friendlyName = value;
    }

    public String getFriendlyName() {
        return friendlyName;
    }
}

