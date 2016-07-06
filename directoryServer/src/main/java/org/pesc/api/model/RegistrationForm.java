package org.pesc.api.model;

/**
 * Created by James Whetstone (jwhetstone@ccctechcenter.org) on 4/26/16.
 */
public class RegistrationForm {
    private Organization organization;
    private DirectoryUser user;

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public DirectoryUser getUser() {
        return user;
    }

    public void setUser(DirectoryUser user) {
        this.user = user;
    }
}
