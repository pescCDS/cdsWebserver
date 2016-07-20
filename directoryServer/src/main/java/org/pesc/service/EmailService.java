package org.pesc.service;

/**
 * Created by James Whetstone (jwhetstone@ccctechcenter.org) on 4/28/16.
 */


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pesc.api.StringUtils;
import org.pesc.api.model.DirectoryUser;
import org.pesc.api.model.Organization;
import org.pesc.api.model.Role;
import org.pesc.api.repository.RolesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Service
public class EmailService {

    private static final Log log = LogFactory.getLog(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private UserService userService;

    @Value("${url.login}")
    private String loginURL;

    @Value("${url.user}")
    private String userURL;

    @Value("${url.organization}")
    private String organizationURL;

    @Value("${url.messages}")
    private String messagesURL;

    @Value("${email.from}")
    private String fromEmailAddress;


    public String createContent(WebContext ctx, String templateName, Organization org, DirectoryUser user) {

        //IFragmentSpec fragmentSpec = new DOMSelectorFragmentSpec(fragmentName);

        ctx.setVariable("organization", org);
        ctx.setVariable("user", user);
        ctx.setVariable("loginURL", loginURL);
        ctx.setVariable("userURL", userURL + user.getId());
        ctx.setVariable("organizationURL", organizationURL + org.getId());
        ctx.setVariable("messagesURL", messagesURL);

        final String htmlContent = this.templateEngine.process(templateName, ctx);
        return htmlContent;
    }

    public String createContent(WebContext ctx, String templateName) {
        final String htmlContent = this.templateEngine.process(templateName, ctx);
        return htmlContent;
    }

    @Async
    public void sendEmailToSysAdmins(final String subject, final String htmlContent) {

        try {
            Set<String> recipients = userService.getSystemAdminEmailAddresses();

            if (recipients.isEmpty()) {
                return;
            }

            final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
            final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "UTF-8");
            message.setSubject(subject);
            message.setFrom(fromEmailAddress);
            String[] emailRecipients = new String[recipients.size()];
            recipients.toArray(emailRecipients);
            message.setTo(emailRecipients);

            message.setText(htmlContent, true);

            this.mailSender.send(mimeMessage);

        }
        catch (MessagingException e) {
            log.error(e);
        }

    }
    /*
     * Send HTML mail (simple)
     */
    @Async
    public void sendSimpleMail(
            final String recipientEmail, final String subject, final String htmlContent) {

         try {
            final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
            final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "UTF-8");
            message.setSubject(subject);
            message.setFrom(fromEmailAddress);
            message.setTo(recipientEmail);

            message.setText(htmlContent, true);


            this.mailSender.send(mimeMessage);

        }
        catch (MessagingException e) {
            log.error(e);
        }
    }

    public String getLoginURL() {
        return loginURL;
    }

    public void setLoginURL(String loginURL) {
        this.loginURL = loginURL;
    }

    public String getUserURL() {
        return userURL;
    }

    public void setUserURL(String userURL) {
        this.userURL = userURL;
    }

    public String getOrganizationURL() {
        return organizationURL;
    }

    public void setOrganizationURL(String organizationURL) {
        this.organizationURL = organizationURL;
    }

    public String getMessagesURL() {
        return messagesURL;
    }

    public void setMessagesURL(String messagesURL) {
        this.messagesURL = messagesURL;
    }
}
