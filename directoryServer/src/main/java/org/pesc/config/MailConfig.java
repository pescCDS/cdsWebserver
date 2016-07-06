package org.pesc.config;

import com.sun.mail.util.MailSSLSocketFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.servlet.view.AbstractTemplateViewResolver;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.security.GeneralSecurityException;
import java.util.Properties;

/**
 * Created by James Whetstone (jwhetstone@ccctechcenter.org) on 4/27/16.
 */
@Configuration
public class MailConfig {


    private static final Log log = LogFactory.getLog(MailConfig.class);

    @Value("${mail.smtp.host}")
    private String smtpHost;

    @Value("${mail.smtp.port}")
    private String smtpPort;

    @Value("${mail.smtp.username}")
    private String username;

    @Value("${mail.smtp.password}")
    private String password;


    @Bean
    public JavaMailSender javaMailService() {
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();

        javaMailSender.setHost(smtpHost);
        javaMailSender.setPort(Integer.valueOf(smtpPort));
        javaMailSender.setUsername(username);
        javaMailSender.setPassword(password);
        javaMailSender.setJavaMailProperties(getMailProperties());

        return javaMailSender;
    }

    private Properties getMailProperties() {

        MailSSLSocketFactory sf = null;
        try {
            sf = new MailSSLSocketFactory();
        } catch (GeneralSecurityException e) {
            log.error("Failed to initialize SSL socket factory for mail client.", e);
            return null;
        }

        sf.setTrustedHosts(new String[] { smtpHost });

        // Setup mail server

        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.ssl.socketFactory", sf);
        //properties.setProperty("mail.smtp.host", smtpHost);
        //properties.setProperty("mail.smtp.port", smtpPort);
        properties.setProperty("mail.debug", "true");


        /*
        properties.setProperty("mail.transport.protocol", "smtp");
        properties.setProperty("mail.smtp.auth", "false");
        properties.setProperty("mail.smtp.starttls.enable", "false");
        properties.setProperty("mail.debug", "false");

        */
        return properties;
    }

}