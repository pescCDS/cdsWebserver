package org.pesc.cds.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.pesc.cds.domain.Transaction;
import org.pesc.cds.repository.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

@Controller
public class AppController {

    private static final Log log = LogFactory.getLog(AppController.class);

    @Value("${directory.server.base.url}")
    private String directoryServer;

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
        model.addAttribute("directoryServer", directoryServer);

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


    @Autowired
    private TransactionService transactionService;


    private void setContentType(HttpServletResponse response, String fileFormat) {
        if (fileFormat.equalsIgnoreCase("pdf")) {
            response.setContentType("application/pdf");
        }
        else if (fileFormat.equalsIgnoreCase("text")) {
            response.setContentType("text/plain");
        }
        else if (fileFormat.equalsIgnoreCase("xml")) {
            response.setContentType("text/xml");
        }
        else if (fileFormat.equalsIgnoreCase("pescxml")) {
            response.setContentType("text/xml");
        }
        else if (fileFormat.equalsIgnoreCase("image")) {
            response.setContentType("image/png"); //TODO: how to get actual MIME type ???
        }
        else if (fileFormat.equalsIgnoreCase("edi")) {
            response.setContentType("application/edi-x12"); //TODO: could be application/edifact ???
        }

    }


    @RequestMapping(value = "/files", method = RequestMethod.GET)
    public void getFile(
            @RequestParam("tran_id") Integer tranID,
            HttpServletResponse response) {
        try {

            //TODO for security: create randomize the transaction ID with a lookup table.

            Transaction transaction = transactionService.findById(tranID);

            if (transaction == null) {
                throw new RuntimeException("Invalid transaction id.");
            }


            InputStream is = new FileInputStream(new File(transaction.getFilePath()));

            setContentType(response, transaction.getFileFormat());
            IOUtils.copy(is, response.getOutputStream());
            response.flushBuffer();
        } catch (IOException e) {
            log.error("Error writing file to output stream. Transaction id " + tranID, e);
            throw new RuntimeException("IOError writing file to output stream");
        }
    }
    @RequestMapping("/admin")
    public String getAdminPage(Model model) {

        buildCommonModel(model);

        return "home";
    }

    @RequestMapping("/upload-status")
    public String getUploadStatus(Model model) {

        buildCommonModel(model);

        return "fragments :: upload-status";
    }

    @RequestMapping("/transaction-report")
    public String getTransactionsPage(Model model) {

        buildCommonModel(model);

        return "fragments :: transactions";
    }


    @RequestMapping("/upload")
    public String getTransfersPage(Model model) {

        buildCommonModel(model);

        return "fragments :: upload";
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
