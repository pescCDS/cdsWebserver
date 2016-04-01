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

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;

/**
 * Created by james on 2/18/16.
 */

@Controller
public class AppController {

    private static final Log log = LogFactory.getLog(AppController.class);

    private Model buildUserModel(Model model) {
        boolean isAuthenticated = false;

        org.pesc.api.model.TempUser cdsUser = new org.pesc.api.model.TempUser();
        //Check if the user is autenticated
        if (SecurityContextHolder.getContext().getAuthentication() != null &&
                SecurityContextHolder.getContext().getAuthentication().isAuthenticated() &&
                //when Anonymous Authentication is enabled
                !(SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken)) {

            User auth = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Collection<GrantedAuthority> authorities = auth.getAuthorities();
            isAuthenticated = true;
            cdsUser.setId(1);
            cdsUser.setName("James Whetstone");
            cdsUser.setUsername(auth.getUsername());
            cdsUser.setHasOrgAdminRole(hasRole(authorities, "ROLE_ORG_ADMIN"));
            cdsUser.setHasSystemAdminRole(hasRole(authorities, "ROLE_SYSTEM_ADMIN"));
            cdsUser.setOrganizationId(1);

            model.addAttribute("hasSystemAdminRole", hasRole(authorities, "ROLE_SYSTEM_ADMIN"));
            model.addAttribute("hasOrgAdminRole", hasRole(authorities, "ROLE_ORG_ADMIN"));
        }
        else {
            model.addAttribute("hasSystemAdminRole", false);
            model.addAttribute("hasOrgAdminRole", false);
        }

        model.addAttribute("isAuthenticated", isAuthenticated);
        model.addAttribute("activeUser", cdsUser);

        return model;

    }


    @RequestMapping(value="/",method = RequestMethod.GET)
    public String gotoHomePage(Model model){
        return "redirect:home";
    }

    @RequestMapping({"/organization"})
    public String getDocs(Model model) {
        buildUserModel(model);

        return "fragments :: organization";
    }

    @RequestMapping({"/organization-details"})
    public String getOrganizationDetails(HttpServletRequest request, Model model) {

        boolean isSystemAdmin = request.isUserInRole("ROLE_SYSTEM_ADMIN");

        getUserDetails();

        buildUserModel(model);

        return "fragments :: organization-details";
    }

    @RequestMapping({"/home", "/admin"})
    public String getHomePage(Model model) {

        buildUserModel(model);

        return "home";
    }


    @RequestMapping({"/organizations"})
    public String getOrganizationsTemplate(Model model) {
        buildUserModel(model);

        return "fragments :: organizations";
    }


    @RequestMapping({"/users"})
    public String getUsersTemplate(Model model) {
        buildUserModel(model);


        return "fragments :: users";
    }

    @RequestMapping({"/settings"})
    public String getSettingsFragment(Model model) {
        buildUserModel(model);


        return "fragments :: settings";
    }



    private void getUserDetails() {
        Object details = SecurityContextHolder.getContext().
                getAuthentication().getDetails();

        log.debug(details.toString());

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