package org.pesc.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pesc.api.model.DirectoryUser;
import org.pesc.api.model.Organization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;


/**
 * Created by james on 4/27/16.
 */
@Service
public class MailService {

    private static final Log log = LogFactory.getLog(MailService.class);


    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private SpringTemplateEngine templateEngine;

    public String constructMessageContent(DirectoryUser user, Organization organization) {

        Context context = new Context();
        context.setVariable("organization", organization);
        context.setVariable("user", user);

        return templateEngine.process("mail/registration", context);
    }

    public void sendMail(String[] recipientList, String from, String subject, String body) {

        try{

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "utf-8");
            mimeMessage.setContent(body, "text/html");
            helper.setTo(recipientList);
            helper.setSubject(subject);
            helper.setFrom(from);
            mailSender.send(mimeMessage);


        }catch (MessagingException e) {
            log.error("Failed to send email.", e);
        }

    }

}
