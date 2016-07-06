package org.pesc.service;

/**
 * Created by James Whetstone (jwhetstone@ccctechcenter.org) on 4/28/16.
 */


import org.pesc.api.model.DirectoryUser;
import org.pesc.api.model.Organization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.context.WebContext;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;


@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;


    public String createContent(WebContext ctx, String templateName, Organization org, DirectoryUser user) {

        //IFragmentSpec fragmentSpec = new DOMSelectorFragmentSpec(fragmentName);

        ctx.setVariable("organization", org);
        ctx.setVariable("user", user);
        final String htmlContent = this.templateEngine.process(templateName, ctx);
        return htmlContent;
    }

    public String createContent(WebContext ctx, String templateName) {
        final String htmlContent = this.templateEngine.process(templateName, ctx);
        return htmlContent;
    }

    /*
     * Send HTML mail (simple)
     */
    @Async
    public String sendSimpleMail(
            final String recipientEmail, final String from, final String subject, final String htmlContent)
            throws MessagingException {


        final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
        final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "UTF-8");
        message.setSubject(subject);
        message.setFrom(from);
        message.setTo(recipientEmail);

        message.setText(htmlContent, true);


        this.mailSender.send(mimeMessage);

        return htmlContent;

    }




    /*
     * Send HTML mail with attachment.
     */
    public void sendMailWithAttachment(
            final String recipientName, final String recipientEmail, final String attachmentFileName,
            final byte[] attachmentBytes, final String attachmentContentType, final Locale locale)
            throws MessagingException {

// Prepare the evaluation context
        final Context ctx = new Context(locale);
        ctx.setVariable("name", recipientName);
        ctx.setVariable("subscriptionDate", new Date());
        ctx.setVariable("hobbies", Arrays.asList("Cinema", "Sports", "Music"));

// Prepare message using a Spring helper
        final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
        final MimeMessageHelper message =
                new MimeMessageHelper(mimeMessage, true /* multipart */, "UTF-8");
        message.setSubject("Example HTML email with attachment");
        message.setFrom("thymeleaf@example.com");
        message.setTo(recipientEmail);

// Create the HTML body using Thymeleaf
        final String htmlContent = this.templateEngine.process("email-withattachment.html", ctx);
        message.setText(htmlContent, true /* isHtml */);

// Add the attachment
        final InputStreamSource attachmentSource = new ByteArrayResource(attachmentBytes);
        message.addAttachment(
                attachmentFileName, attachmentSource, attachmentContentType);

        // Send mail
        this.mailSender.send(mimeMessage);

    }



    /*
     * Send HTML mail with inline image
     */
    public void sendMailWithInline(
            final String recipientName, final String recipientEmail, final String imageResourceName,
            final byte[] imageBytes, final String imageContentType, final Locale locale)
            throws MessagingException {

// Prepare the evaluation context
        final Context ctx = new Context(locale);
        ctx.setVariable("name", recipientName);
        ctx.setVariable("subscriptionDate", new Date());
        ctx.setVariable("hobbies", Arrays.asList("Cinema", "Sports", "Music"));
        ctx.setVariable("imageResourceName", imageResourceName); // so that we can reference it from HTML

// Prepare message using a Spring helper
        final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
        final MimeMessageHelper message =
                new MimeMessageHelper(mimeMessage, true /* multipart */, "UTF-8");
        message.setSubject("Example HTML email with inline image");
        message.setFrom("thymeleaf@example.com");
        message.setTo(recipientEmail);

// Create the HTML body using Thymeleaf
        final String htmlContent = this.templateEngine.process("email-inlineimage.html", ctx);
        message.setText(htmlContent, true /* isHtml */);

// Add the inline image, referenced from the HTML code as "cid:${imageResourceName}"
        final InputStreamSource imageSource = new ByteArrayResource(imageBytes);
        message.addInline(imageResourceName, imageSource, imageContentType);

        // Send mail
        this.mailSender.send(mimeMessage);

    }
}
