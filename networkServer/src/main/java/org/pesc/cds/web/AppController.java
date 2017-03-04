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

package org.pesc.cds.web;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.json.JSONObject;
import org.pesc.cds.domain.Transaction;
import org.pesc.cds.repository.TransactionService;
import org.pesc.cds.service.OrganizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
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

    private String localServerId;

    private JSONObject organization;

    private OrganizationService organizationService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    public AppController( @Value("${networkServer.id}") String edExID, OrganizationService organizationService) {
        this.organizationService = organizationService;
        localServerId = edExID;
        organization = organizationService.getOrganization(Integer.valueOf(localServerId));
    }


    private boolean hasRole(Collection<? extends GrantedAuthority> authorities, String role) {
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

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            isAuthenticated = true;

            model.addAttribute("hasSupportRole", hasRole(authorities, "ROLE_SUPPORT"));
            model.addAttribute("hasAdminRole", hasRole(authorities, "ROLE_ORG_ADMIN"));

            // model.addAttribute("roles", roleRepo.findAll() );
            org.pesc.cds.model.User activeUser = new org.pesc.cds.model.User();
            activeUser.setName(authentication.getName());
            model.addAttribute("activeUser", activeUser);

        } else {
            model.addAttribute("hasSupportRole", false);
            model.addAttribute("hasAdminRole", false);
        }

        model.addAttribute("isAuthenticated", isAuthenticated);


        return isAuthenticated;

    }

    /*
    @RequestMapping("/unauthenticated")
    public String unauthenticated() {
        return "redirect:/?error=true";
    }
    */


    @RequestMapping({"/documentation"})
    public String getDocumentation(Model model) {
        buildCommonModel(model);

        model.addAttribute("organizationName", organization.getString("name"));
        model.addAttribute("organizationId", organization.getInt("id"));

        return "documentation";
    }



    private void setContentType(HttpServletResponse response, String fileFormat) {
        if (fileFormat.equalsIgnoreCase("pdf")) {
            response.setContentType("application/pdf");
        } else if (fileFormat.equalsIgnoreCase("text")) {
            response.setContentType("text/plain");
        } else if (fileFormat.equalsIgnoreCase("xml")) {
            response.setContentType("text/xml");
        } else if (fileFormat.equalsIgnoreCase("pescxml")) {
            response.setContentType("text/xml");
        } else if (fileFormat.equalsIgnoreCase("image")) {
            response.setContentType("image/png"); //TODO: how to get actual MIME type ???
        } else if (fileFormat.equalsIgnoreCase("edi")) {
            response.setContentType("application/edi-x12"); //TODO: could be application/edifact ???
        }

    }


    @RequestMapping(value = "/files", method = RequestMethod.GET)
    public void getFile(
            @RequestParam("tran_id") Integer tranID,
            @RequestParam(value = "show_request", required = false) Boolean showRequest,
            @RequestParam(value = "show_acknowledgement", required = false) Boolean showAck,
            HttpServletResponse response) {
        try {

            //TODO for security: create randomize the transaction ID with a lookup table.

            Transaction transaction = transactionService.findById(tranID);

            if (transaction == null) {
                throw new RuntimeException("Invalid transaction id.");
            }

            String filePath = BooleanUtils.isTrue(showRequest) ? transaction.getRequestFilePath() : transaction.getFilePath();
            String fileFormat = BooleanUtils.isTrue(showRequest) ? "xml" : transaction.getFileFormat();

            InputStream is = new FileInputStream(new File(filePath));

            setContentType(response, fileFormat);
            IOUtils.copy(is, response.getOutputStream());
            response.flushBuffer();
        } catch (IOException e) {
            log.error("Error writing file to output stream. Transaction id " + tranID, e);
            throw new RuntimeException("IOError writing file to output stream");
        }
    }

    @RequestMapping(value = "/files/acknowledgement", method = RequestMethod.GET)
    public void getFile(
            @RequestParam("tran_id") Integer tranID,
            HttpServletResponse response) {
        try {

            Transaction transaction = transactionService.findById(tranID);

            if (transaction == null) {
                throw new RuntimeException("Invalid transaction id.");
            }

            String filePath = transaction.getAckFilePath();
            String fileFormat = "xml";

            InputStream is = new FileInputStream(new File(filePath));

            setContentType(response, fileFormat);
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
        //If Institution, use OrgId.  Otherwise need to ask and look up Source Institution Info.
        //OrgId known, Source Institution (if applicable) not known.

        if (organization == null) {
            organization = organizationService.getOrganization(Integer.valueOf(localServerId));
        }

        if (organization == null) {
            throw new IllegalStateException("Failed to retrieve organization info from directory for network server ID " + localServerId );

        }

        boolean institution = organizationService.isInstitution(organization);
        model.addAttribute("institution", institution);
        return "fragments :: upload";
    }

    @RequestMapping("/transcript-request-form")
    public String getTranscriptRequestForm(Model model) {

        buildCommonModel(model);

        return "fragments :: transcript-request-form";
    }

    @RequestMapping({"/", "/home"})
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
