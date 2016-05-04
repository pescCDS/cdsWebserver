package org.pesc.service;

import org.pesc.api.model.DirectoryUser;
import org.pesc.api.model.Organization;
import org.pesc.api.model.Role;
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
 * Created by james on 4/26/16.
 */
@Service
public class RegistrationService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    OrganizationRepository organizationRepository;

    @Autowired
    UserRepository userRepository;

    private Role organizationAdminRole;

    @Autowired
    public RegistrationService(RolesRepository roleRepo) {
          organizationAdminRole = roleRepo.findByName("ROLE_ORG_ADMIN");
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

        Set<Role> roles = new HashSet<Role>();
        roles.add(organizationAdminRole);

        user.setRoles(roles);

        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        user.setEnabled(true);

        userRepository.save(user);

    }
}

