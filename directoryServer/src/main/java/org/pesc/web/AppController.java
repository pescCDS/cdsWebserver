package org.pesc.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Collection;

/**
 * Created by james on 2/18/16.
 */

@Controller
public class AppController {

    private static final Log log = LogFactory.getLog(AppController.class);

    private boolean getCDSUser(org.pesc.api.model.User cdsUser) {
        boolean isAuthenticated = false;


        //Check if the user is autenticated
        if (SecurityContextHolder.getContext().getAuthentication() != null &&
                SecurityContextHolder.getContext().getAuthentication().isAuthenticated() &&
                //when Anonymous Authentication is enabled
                !(SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken)) {

            User auth = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Collection<GrantedAuthority> authorities = auth.getAuthorities();
            isAuthenticated = true;
            cdsUser.setName("James Whetstone");
            cdsUser.setUsername(auth.getUsername());
            cdsUser.setHasOrgAdminRole(hasRole(authorities, "ROLE_ORG_ADMIN"));
            cdsUser.setHasSystemAdminRole(hasRole(authorities, "ROLE_SYSTEM_ADMIN"));

        }

        return isAuthenticated;

    }


    @RequestMapping(value="/",method = RequestMethod.GET)
    public String gotoHomePage(Model model){
        return "redirect:home";
    }

    @RequestMapping({"/organization"})
    public String getDocs(Model model) {
        org.pesc.api.model.User cdsUser = new org.pesc.api.model.User();

        boolean isAuthenticated = getCDSUser(cdsUser);

        model.addAttribute("isAuthenticated", isAuthenticated);
        model.addAttribute("activeUser", cdsUser);

        return "fragments :: organization";
    }

    @RequestMapping({"/organization-details"})
    public String getOrganizationDetails(Model model) {
        org.pesc.api.model.User cdsUser = new org.pesc.api.model.User();

        boolean isAuthenticated = getCDSUser(cdsUser);

        model.addAttribute("isAuthenticated", isAuthenticated);
        model.addAttribute("activeUser", cdsUser);

        return "fragments :: organization-details";
    }

    @RequestMapping({"/home", "/admin"})
    public String getHomePage(Model model) {

        org.pesc.api.model.User cdsUser = new org.pesc.api.model.User();

        boolean isAuthenticated = getCDSUser(cdsUser);

        model.addAttribute("isAuthenticated", isAuthenticated);
        model.addAttribute("activeUser", cdsUser);

        return "home";
    }


    @RequestMapping({"/organizations"})
    public String getOrganizationsTemplate(Model model) {
        org.pesc.api.model.User cdsUser = new org.pesc.api.model.User();

        boolean isAuthenticated = getCDSUser(cdsUser);

        model.addAttribute("isAuthenticated", isAuthenticated);
        model.addAttribute("activeUser", cdsUser);


        return "fragments :: organizations";
    }

    @RequestMapping({"/settings"})
    public String getSettingsFragment(Model model) {
        org.pesc.api.model.User cdsUser = new org.pesc.api.model.User();

        boolean isAuthenticated = getCDSUser(cdsUser);

        model.addAttribute("isAuthenticated", isAuthenticated);
        model.addAttribute("activeUser", cdsUser);


        return "fragments :: settings";
    }



    private void getUserDetails() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().
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