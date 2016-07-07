package org.pesc.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pesc.api.exception.ApiException;
import org.pesc.api.model.DirectoryUser;
import org.pesc.api.model.Message;
import org.pesc.api.model.MessageTopic;
import org.pesc.api.model.RegistrationForm;
import org.pesc.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.WebContext;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

/**
 * Created by James Whetstone (jwhetstone@ccctechcenter.org) on 2/18/16.
 */

@Controller
public class AppController {

    private static final Log log = LogFactory.getLog(AppController.class);

    @Value("${rest.api.host}")
    private String restAPIHost;


    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private UserService userService;

    @Autowired
    private MessageService messageService;



    @Autowired
    private EmailService mailService;


    @Autowired
    private OrganizationService organizationService;

    @Value("${github.url}")
    private String githubURL;

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

        }
        else {
            model.addAttribute("hasSystemAdminRole", false);
            model.addAttribute("hasOrgAdminRole", false);
        }

        model.addAttribute("roles", userService.getRoles() );
        model.addAttribute("organizationTypes", organizationService.getOrganizationTypes());

        model.addAttribute("isAuthenticated", isAuthenticated);

        return isAuthenticated;

    }

    @RequestMapping(value="/",method = RequestMethod.GET)
    public String gotoHomePage(Model model){
        return "redirect:home";
    }

    @RequestMapping({"/organization"})
    public String getOrganizationFragment(Model model) {
        buildUserModel(model);

        return "fragments :: organization";
    }

    @RequestMapping({"/messages"})
    public String getMessagesFragment(Model model) {
        buildUserModel(model);

        return "fragments :: messages";
    }

    @RequestMapping({"/documentation"})
    public String getDocumentation(Model model) {
        buildUserModel(model);

        model.addAttribute("github", githubURL);
        model.addAttribute("apiURL", "https://" + restAPIHost);

        return "documentation";
    }

    @RequestMapping({"/endpoint-selector"})
    public String getEndpointSelectorFragment(Model model) {
        buildUserModel(model);

        return "fragments :: endpoint-selector";
    }



    @ExceptionHandler
    void handleIllegalArgumentException(IllegalArgumentException e, HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.BAD_REQUEST.value());
    }


    @RequestMapping({"/exception"})
    public void getException(Model model) {
        buildUserModel(model);

        throw new IllegalArgumentException("Bad args!");
    }

    @RequestMapping({"/registration-form"})
    public String getRegistrationFormFragment(Model model) {

        return "fragments :: registration-form";
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


    @RequestMapping(value="/registration", method= RequestMethod.POST)
    @ResponseBody
    public Message createOrganization(HttpServletRequest request, HttpServletResponse response, @RequestBody RegistrationForm regForm)  {


        final WebContext ctx = new WebContext(request,response, request.getServletContext());


        registrationService.register(regForm.getOrganization(), regForm.getUser());


        try {
            final String content = mailService.createContent(ctx, "mail/registration", regForm.getOrganization(), regForm.getUser());

            mailService.sendSimpleMail(regForm.getUser().getEmail(), "noreply@edexchange.net", MessageTopic.REGISTRATION.getFriendlyName(), content);
            messageService.createMessage(MessageTopic.REGISTRATION.name(), content, false,regForm.getOrganization().getId(), null );


        }
        catch (MessagingException e) {
            log.error("Failed to send registration email.", e);
        }


        final String content = mailService.createContent(ctx, "mail/registration-admin", regForm.getOrganization(), regForm.getUser());

        Message regMessage = messageService.createMessage(MessageTopic.REGISTRATION.name(), content, true,1, null );


        return regMessage;

    }


    @RequestMapping({"/about"})
    public String getAboutPage(Model model) {

        buildUserModel(model);

        return "fragments :: about";
    }

    @RequestMapping({"/registration-thank-you"})
    public String getRegistrationThankYou(Model model) {

        buildUserModel(model);

        return "fragments :: registration-thank-you";
    }

    @RequestMapping({"/home", "/admin"})
    public String getHomePage(Model model) {

        if (buildUserModel(model) == true) {
            User auth = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            List<DirectoryUser> dirUser = userService.findByUsername(auth.getUsername());

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

    public static boolean hasRole(Collection<GrantedAuthority> authorities, String role) {
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