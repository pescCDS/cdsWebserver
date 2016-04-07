package org.pesc.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pesc.api.model.DirectoryUser;
import org.pesc.api.repository.RolesRepository;
import org.pesc.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.List;

/**
 * Created by james on 2/18/16.
 */

@Controller
public class AppController {

    private static final Log log = LogFactory.getLog(AppController.class);

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private RolesRepository roleRepo;

    private boolean buildUserModel(Model model) {
        boolean isAuthenticated = false;


        //Check if the user is autenticated
        if (SecurityContextHolder.getContext().getAuthentication() != null &&
                SecurityContextHolder.getContext().getAuthentication().isAuthenticated() &&
                //when Anonymous Authentication is enabled
                !(SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken)) {

            User auth = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Collection<GrantedAuthority> authorities = auth.getAuthorities();
            isAuthenticated = true;

            model.addAttribute("hasSystemAdminRole", hasRole(authorities, "ROLE_SYSTEM_ADMIN"));
            model.addAttribute("hasOrgAdminRole", hasRole(authorities, "ROLE_ORG_ADMIN"));

            model.addAttribute("roles", roleRepo.findAll() );
        }
        else {
            model.addAttribute("hasSystemAdminRole", false);
            model.addAttribute("hasOrgAdminRole", false);
        }

        model.addAttribute("isAuthenticated", isAuthenticated);

        return isAuthenticated;

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

        //boolean isSystemAdmin = request.isUserInRole("ROLE_SYSTEM_ADMIN");

        //getUserDetails();

        buildUserModel(model);

        return "fragments :: organization-details";
    }

    @RequestMapping({"/user-details"})
    public String getUserDetails(HttpServletRequest request, Model model) {


        buildUserModel(model);

        return "fragments :: user-details";
    }


    @RequestMapping({"/about"})
    public String getAboutPage(Model model) {

        buildUserModel(model);

        return "fragments :: about";
    }

    @RequestMapping({"/home", "/admin"})
    public String getHomePage(Model model) {

        if (buildUserModel(model) == true) {
            User auth = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            List<DirectoryUser> dirUser = userRepo.findByUserName(auth.getUsername());

            if (dirUser.size() == 1) {
                model.addAttribute("activeUser", dirUser.get(0));
            }
        }

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