package org.pesc.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pesc.api.model.Contact;
import org.pesc.api.model.DirectoryUser;
import org.pesc.api.model.Organization;
import org.pesc.api.model.Role;
import org.pesc.api.repository.ContactsRepository;
import org.pesc.api.repository.OrganizationRepository;
import org.pesc.api.repository.RolesRepository;
import org.pesc.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by James Whetstone (jwhetstone@ccctechcenter.org) on 4/26/16.
 */
@Service
public class RegistrationService {

    private static final Log log = LogFactory.getLog(RegistrationService.class);

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    OrganizationRepository organizationRepository;

    @Autowired
    UserService userService;

    @Autowired
    ContactsRepository contactsRepository;

    private Role organizationAdminRole;

    @Autowired
    public RegistrationService(RolesRepository roleRepo) {
          organizationAdminRole = roleRepo.findByName("ROLE_ORG_ADMIN");
    }

    private void saveContact(DirectoryUser user, Organization organization) {
        Contact contact = new Contact();
        contact.setName(user.getName());
        contact.setEmail(user.getEmail());
        contact.setPhone1(user.getPhone());
        contact.setAddress(organization.getStreet() + " " + organization.getCity() + " " + organization.getState() + " " + organization.getZip());
        contact.setTitle(user.getTitle());
        contact.setOrganizationId(user.getOrganizationId());
        contactsRepository.save(contact);
    }

    /**
     * Persists a new organization that will be queued for approval.
     * @param organization
     * @return
     */
    @Transactional(readOnly=false,propagation = Propagation.REQUIRED)
    public void register(Organization organization, DirectoryUser user){


        if (organization.getOrganizationTypes().contains(new Integer(0))) {
            throw new IllegalArgumentException("Organization type of 'System' is not allowed for registration.");
        }
        organization.setEnabled(false);  //The info requires validation/approval before becoming active

        organization =organizationRepository.save(organization);

        user.setOrganizationId(organization.getId());

        saveContact(user, organization);

        Set<Role> roles = new HashSet<Role>();
        roles.add(organizationAdminRole);

        user.setRoles(roles);


        try {
            userService.unsecuredCreate(user);
        }
        catch (Exception e) {
            log.warn(e);
            throw new IllegalArgumentException(String.format("Username '%s' is not available. Please try a different username.", user.getUsername()));
        }


    }
}

