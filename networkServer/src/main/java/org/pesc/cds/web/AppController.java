package org.pesc.cds.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Collection;

@Controller
public class AppController {

    private static final Log log = LogFactory.getLog(AppController.class);

    @Value("${directory.server}")
    private String directoryServer;
    @Value("${directory.server.port}")
    private String directortyServerPort;

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


    private boolean buildCommonModel(Model model) {
        model.addAttribute("directoryServer", directoryServer + ":" + directortyServerPort);

        boolean isAuthenticated = false;


        //Check if the user is autenticated
        if (SecurityContextHolder.getContext().getAuthentication() != null &&
                SecurityContextHolder.getContext().getAuthentication().isAuthenticated() &&
                //when Anonymous Authentication is enabled
                !(SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken)) {

            User auth = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Collection<GrantedAuthority> authorities = auth.getAuthorities();
            isAuthenticated = true;

            //model.addAttribute("hasSupportRole", hasRole(authorities, "ROLE_SUPPORT"));
            //model.addAttribute("hasAdminRole", hasRole(authorities, "ROLE_ADMIN"));

            // model.addAttribute("roles", roleRepo.findAll() );
        }
        else {
            model.addAttribute("hasSupportRole", false);
            model.addAttribute("hasAdminRole", false);
        }

        model.addAttribute("isAuthenticated", isAuthenticated);


        if (isAuthenticated) {
            org.pesc.cds.model.User activeUser = new org.pesc.cds.model.User();
            activeUser.setName("Admin");

            model.addAttribute("activeUser", activeUser);
        }
        return isAuthenticated;

    }


    @RequestMapping("/admin")
    public String getAdminPage(Model model) {

        buildCommonModel(model);

        return "home";
    }

    @RequestMapping("/transaction-report")
    public String getTransactionsPage(Model model) {

        buildCommonModel(model);

        return "fragments :: transactions";
    }

    @RequestMapping("/transfers")
    public String getTransfersPage(Model model) {

        buildCommonModel(model);

        return "fragments :: transfers";
    }

    @RequestMapping({ "/", "/home" })
    public String viewHome(Model model) {

        buildCommonModel(model);
        return "home";
    }

    @RequestMapping("/about")
    public String getAboutPage(Model model) {
        buildCommonModel(model);

        return "fragments :: about";
    }



}
