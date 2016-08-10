package org.pesc.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pesc.api.StringUtils;
import org.pesc.api.model.Message;
import org.pesc.api.model.Organization;
import org.pesc.api.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by James Whetstone (jwhetstone@ccctechcenter.org) on 4/27/16.
 */
@Service
public class MessageService {

    private static final Log log = LogFactory.getLog(MessageService.class);

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private EntityManagerFactory entityManagerFactory;


    @Autowired
    public MessageService(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;

    }
    
    @Transactional(readOnly=false,propagation = Propagation.REQUIRED)
    @PreAuthorize("hasRole('ROLE_SYSTEM_ADMIN') OR hasRole('ROLE_ORG_ADMIN')")
    public void setDismissed(Integer messageId, Boolean dismiss) {

        jdbcTemplate.update(
                "update messages set dismissed=? where id = ?", dismiss, messageId);
    }


    @Transactional(readOnly=false,propagation = Propagation.REQUIRED)
    @PreAuthorize("( (#orgID == principal.organizationId AND hasRole('ROLE_ORG_ADMIN')) OR hasRole('ROLE_SYSTEM_ADMIN') )")
    public void deleteByOrgId(Integer orgID) {

        jdbcTemplate.update(
                "delete from messages where organization_id = ?", orgID);
    }


    public Predicate[] createPredicates(CriteriaBuilder cb, Root<Message> root,
                                        Integer orgID,
                                        String topic,
                                        String content,
                                        Long createdTime,
                                        Boolean actionRequired) {
        List<Predicate> predicates = new LinkedList<Predicate>();

        if (orgID != null) {
            predicates.add(cb.equal(root.get("organizationId"), orgID));
        }


        if (!StringUtils.isEmpty(content)) {
                predicates.add(cb.like(cb.lower(root.get("content")), "%" + content + "%"));
        }


        if (!StringUtils.isEmpty(topic)) {

            predicates.add(cb.equal(cb.lower(root.get("topic")), topic));

        }

        if (createdTime != null) {
            predicates.add(cb.equal(root.get("createdTime"), new Timestamp(createdTime)));
        }


        if (actionRequired != null) {
            predicates.add(cb.equal(root.get("actionRequired"), actionRequired));
        }

        Predicate[] predicateArray = new Predicate[predicates.size()];
        predicates.toArray(predicateArray);

        return predicateArray;
    }

    private Long getResultLength(  Integer orgID,
                                   String topic,
                                   String content,
                                   Long createdTime,
                                   Boolean actionRequired) {

        EntityManager entityManager = entityManagerFactory.createEntityManager();

        try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
            Root<Message> countRoot = countQuery.from(Message.class);
            countQuery.select(cb.count(countRoot));
            Predicate[] predicates = createPredicates(cb,
                    countRoot,orgID,topic,content,createdTime, actionRequired);

            countQuery.orderBy(cb.desc(countRoot.get("createdTime")));

            countQuery.where(predicates);

            return entityManager.createQuery(countQuery).getSingleResult();
        }
        catch (Exception e){
            log.error("Failed to retrieve result length.", e);
        }
        finally {
            entityManager.close();
        }

        return 0L;
    }
    
    @Transactional(readOnly=true,propagation = Propagation.REQUIRED)
    @PreAuthorize("( (#orgID == principal.organizationId AND hasRole('ROLE_ORG_ADMIN')) OR hasRole('ROLE_SYSTEM_ADMIN') )")
    public PagedData<Message> search(Integer orgID,
                                     String topic,
                                     String content,
                                     Long createdTime,
                                     Boolean actionRequired,
                                     PagedData<Message> pagedData)  {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {


            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<Message> cq = cb.createQuery(Message.class);
            Root<Message> root = cq.from(Message.class);
            cq.select(root);

            Predicate[] predicates = createPredicates(cb,
                    root, orgID,topic,content,createdTime,actionRequired);

            cq.orderBy(cb.desc(root.get("createdTime")));

            cq.where(predicates);
            TypedQuery<Message> q = entityManager.createQuery(cq);
            q.setFirstResult(pagedData.getOffset());
            q.setMaxResults(pagedData.getLimit());
            pagedData.setData(q.getResultList());


            pagedData.setTotal(getResultLength(orgID,topic,content,createdTime,actionRequired));


            return pagedData;


        } catch (Exception ex) {
            log.error("Failed to execute organization search query.", ex);
        }
        finally {
            entityManager.close();
        }
        //return new PageImpl<Message>(new ArrayList<Message>());

        pagedData.setLimit(0);
        pagedData.setOffset(0);
        pagedData.setTotal(0L);
        pagedData.setData(new ArrayList<Message>());
        return pagedData;
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
        msg.setCreatedTime(new Date());
        msg.setActionRequired(actionRequired);
        msg.setDismissed(false);
        msg.setOrganizationId(orgID);
        msg.setUserId(userID);

        return messageRepository.save(msg);
    }

}
