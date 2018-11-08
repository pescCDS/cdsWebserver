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
            throw new IllegalArgumentException("No user found.");
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
