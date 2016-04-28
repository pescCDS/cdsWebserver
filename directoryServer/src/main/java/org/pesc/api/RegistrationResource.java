package org.pesc.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pesc.api.model.Message;
import org.pesc.api.model.MessageTopic;
import org.pesc.api.model.RegistrationForm;
import org.pesc.service.MailService;
import org.pesc.service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jws.WebService;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by james on 3/22/16.
 */
@Component
@WebService
@Path("/registration")
@Api("/registration")
public class RegistrationResource {

    private static final Log log = LogFactory.getLog(RegistrationResource.class);

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private MailService mailService;


    @POST
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @ApiOperation("Create an organization.")
    public void createOrganization(RegistrationForm regForm) {


        registrationService.register(regForm.getOrganization(), regForm.getUser());

        Message message = new Message();
        message.setTopic(MessageTopic.REGISTRATION.name());
        message.setContent(mailService.constructMessageContent(regForm.getUser(), regForm.getOrganization()));
        message.setActionRequired(false);
        message.setDismissed(false);


        mailService.sendMail(new String[]{regForm.getUser().getEmail()},
                "noreply@edexchange.net",
                MessageTopic.REGISTRATION.getFriendlyName(),
                message.getContent());

    }



}
