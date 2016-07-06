package org.pesc.config;

import org.pesc.api.model.AuthUser;
import org.pesc.api.model.Credentials;
import org.pesc.api.model.Role;
import org.pesc.api.repository.CredentialsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by James Whetstone (jwhetstone@ccctechcenter.org) on 3/30/16.
 */
@Service
class CredentialsService implements UserDetailsService {

    @Autowired
    private CredentialsRepository credentialsRepository;

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {


        List<Credentials> credentials = credentialsRepository.findByUserName(username);
        if (credentials.isEmpty()) {
            return null;
        }

        if (credentials.size() != 1) {
            throw new RuntimeException( String.format("Only one user record should exist for the given username %s.", username));
        }

        Credentials principal = credentials.get(0);

        List<GrantedAuthority> authorities = buildUserAuthority(principal.getRoles());

        AuthUser authUser = new AuthUser(principal.getUsername(),
                principal.getPassword(), principal.isEnabled(),true,true,true, authorities);

        authUser.setId(principal.getId());
        authUser.setOrganizationId(principal.getOrganization().getId());
        authUser.setOrganizationTypes(principal.getOrganization().getOrganizationTypes());



        return authUser;
    }

    private List<GrantedAuthority> buildUserAuthority(Set<Role> userRoles) {

        List<GrantedAuthority> setAuths = new ArrayList<GrantedAuthority>();

        for (Role userRole : userRoles) {
            setAuths.add(new SimpleGrantedAuthority(userRole.getName()));
        }
        return setAuths;
    }


}
