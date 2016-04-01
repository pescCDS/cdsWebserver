package org.pesc.config;

import org.pesc.api.model.User;
import org.pesc.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

        //TODO: remove the code below that authenticate "admin" and move into a properties file where the password
        //will be encrypted.
        if ("admin".equals(username))   {
            List<GrantedAuthority> auth = AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_SYSTEM_ADMIN,ROLE_ORG_ADMIN");

            return new org.springframework.security.core.userdetails.User(username, passwordEncoder.encode("password"),auth);

        }
        List<User> users = userRepo.findByName(username);
        if (users.isEmpty()) {
            return null;
        }

        User cdsUser = users.get(0);

        List<GrantedAuthority> auth = AuthorityUtils.createAuthorityList(cdsUser.getRoles().toArray(new String[0]));

        String password = users.get(0).getPassword();
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(username, password, cdsUser.isEnabled(),true,true,true, auth);

        return userDetails;
    }

}
