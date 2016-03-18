package org.pesc.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;

/**
 * Created by james on 2/18/16.
 */

@Controller
public class AdminController {

    private static final Log log = LogFactory.getLog(AdminController.class);

    @RequestMapping("/admin")
    public String adminConsole(Model model) {
        User auth = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Collection<GrantedAuthority> authorities = auth.getAuthorities();


        org.pesc.web.model.User cdsUser = new org.pesc.web.model.User();
        cdsUser.setName("Lou Delzompo");
        cdsUser.setUsername(auth.getUsername());
        cdsUser.setHasOrgAdminRole(hasRole(authorities, "ROLE_ORG_ADMIN"));
        cdsUser.setHasSystemAdminRole(hasRole(authorities, "ROLE_SYSTEM_ADMIN"));
        model.addAttribute("activeUser", cdsUser);

        return "admin";
    }

    private void getUserDetails() {
        UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().
                getAuthentication().getPrincipal();
        log.info(userDetails.getUsername());
        log.info(userDetails.isEnabled());
    }

    private boolean hasRole(Collection<GrantedAuthority> authorities, String role) {
        boolean hasRole = false;
        for (GrantedAuthority authority : authorities) {
            hasRole = authority.getAuthority().equals(role);
            if (hasRole) {
                break;
            }
        }
        return hasRole;
    }

}