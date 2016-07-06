package org.pesc.service;

import org.pesc.api.model.Message;
import org.pesc.api.model.Organization;
import org.pesc.api.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by James Whetstone (jwhetstone@ccctechcenter.org) on 4/27/16.
 */
@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Transactional(readOnly=false,propagation = Propagation.REQUIRED)
    public void setDismissed(Integer messageId, Boolean dismiss) {

        jdbcTemplate.update(
                "update messages set dismissed=? where id = ?", dismiss, messageId);
    }


    @Transactional(readOnly=true,propagation = Propagation.REQUIRED)
    @PreAuthorize("( (#orgID == principal.organizationId AND hasRole('ROLE_ORG_ADMIN')) OR hasRole('ROLE_SYSTEM_ADMIN') )")
    public List<Message> findByOrganizationId(Integer orgID)  {
        return messageRepository.findByOrganizationId(orgID);
    }

    @Transactional(readOnly=false,propagation = Propagation.REQUIRED)
    public Message createMessage(Message message){
        return messageRepository.save(message);
    }

    @Transactional(readOnly=false,propagation = Propagation.REQUIRED)
    public Message createMessage(String topic, String content, Boolean actionRequired, Integer orgID, Integer userID) {
        Message msg = new Message();

        msg.setTopic(topic);
        msg.setContent(content);
        msg.setActionRequired(actionRequired);
        msg.setDismissed(false);
        msg.setOrganizationId(orgID);
        msg.setUserId(userID);

        return messageRepository.save(msg);
    }

}
