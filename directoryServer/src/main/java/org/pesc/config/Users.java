package org.pesc.config;

import org.pesc.api.model.AuthUser;
import org.pesc.api.model.DirectoryUser;
import org.pesc.api.model.Role;
import org.pesc.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by james on 3/30/16.
 */
@Service
class Users implements UserDetailsService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        /*
        //TODO: remove the code below that authenticate "admin" and move into a properties file where the password
        //will be encrypted.
        if ("admin".equals(username))   {
            List<GrantedAuthority> authorities = AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_SYSTEM_ADMIN,ROLE_ORG_ADMIN");

            AuthUser authUser = new AuthUser(username, passwordEncoder.encode("password"), true,true,true,true, authorities);

            authUser.setName("James Whetstone");
            authUser.setId(0);
            authUser.setOrganizationId(1);
            authUser.setHasOrgAdminRole(hasRole(authorities, "ROLE_ORG_ADMIN"));
            authUser.setHasSystemAdminRole(hasRole(authorities, "ROLE_SYSTEM_ADMIN"));

            return authUser;
        }
        */


        List<DirectoryUser> users = userRepo.findByUserName(username);
        if (users.isEmpty()) {
            return null;
        }


        DirectoryUser cdsUser = users.get(0);

        List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList(buildAuthorities(cdsUser));

        //List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList((String[])cdsUser.getRoles().toArray());

        String password = users.get(0).getPassword();
        AuthUser authUser = new AuthUser(username, password, cdsUser.isEnabled(),true,true,true, authorities);
        authUser.setId(cdsUser.getId());
        authUser.setOrganizationId(cdsUser.getOrganizationId());

        return authUser;
    }


    private String[] buildAuthorities(DirectoryUser user) {

        String[] roles = new String[user.getRoles().size()];


        for(int i=0; i<user.getRoles().size(); i++) {
            roles[i] = user.getRoles().get(i).getName();
        }
        return roles;
    }

}
