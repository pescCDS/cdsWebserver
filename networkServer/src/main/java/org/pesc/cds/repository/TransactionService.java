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

package org.pesc.cds.repository;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pesc.cds.domain.PagedData;
import org.pesc.cds.domain.Transaction;
import org.pesc.cds.model.TransactionStatus;
import org.pesc.sdk.message.functionalacknowledgement.v1_2.Acknowledgment;
import org.pesc.sdk.util.ValidationUtils;
import org.pesc.sdk.util.XmlFileType;
import org.pesc.sdk.util.XmlSchemaVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.xml.sax.SAXException;

import javax.naming.OperationNotSupportedException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.validation.Schema;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.util.*;

/**
 * Created by James Whetstone (jwhetstone@ccctechcenter.org) on 3/21/16.
 */
@Service
public class TransactionService {

    private static final Log log = LogFactory.getLog(TransactionService.class);

    private EntityManagerFactory entityManagerFactory;

    @Autowired
    public TransactionService(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;

    }

    @Autowired
    private TransactionRepository transactionRepository;

    @Transactional(readOnly=true)
    @PreAuthorize(" hasRole('ROLE_ORG_ADMIN') || hasRole('ROLE_NETWORK_SERVER') ")
    public Iterable<Transaction> findAll(){
        return this.transactionRepository.findAll();
    }

    @Transactional(readOnly=false,propagation = Propagation.REQUIRED)
    public Transaction create(Transaction transaction){
        return this.transactionRepository.save(transaction);
    }

    @Transactional(readOnly=false,propagation = Propagation.REQUIRED)
    public Transaction update(Transaction transaction){
        return this.transactionRepository.save(transaction);
    }

    @Transactional(readOnly=false,propagation = Propagation.REQUIRED)
    @PreAuthorize(" hasRole('ROLE_ORG_ADMIN') || hasRole('ROLE_NETWORK_SERVER') ")
    public void delete(Transaction transaction)  {
        this.transactionRepository.delete(transaction);
    }

    @Transactional(readOnly=false,propagation = Propagation.REQUIRED)
    @PreAuthorize(" hasRole('ROLE_ORG_ADMIN') || hasRole('ROLE_NETWORK_SERVER') ")
    public void delete(Integer id)  {
        this.transactionRepository.delete(id);
    }
    
    @Transactional(readOnly=true,propagation = Propagation.REQUIRED)
    @PreAuthorize(" hasRole('ROLE_ORG_ADMIN') || hasRole('ROLE_NETWORK_SERVER') ")
    public Transaction findById(Integer id)  {

        return this.transactionRepository.findOne(id);
    }

    private Predicate[] createPredicates(CriteriaBuilder cb, Root<Transaction> transactionRoot,  Integer senderId,
                                        String status,
                                        String operation,
                                        TransactionStatus deliveryStatus,
                                        Date startDate,
                                        Date endDate) {
        List<Predicate> predicates = new LinkedList<Predicate>();

        if(senderId!=null) {
            predicates.add(cb.equal(transactionRoot.get("senderId"), senderId));
        }


        if(!StringUtils.isEmpty(status)) {
            predicates.add(cb.equal(transactionRoot.get("acknowledged"), "complete".equalsIgnoreCase(status) ? true : false));
        }

        if(operation != null && !"both".equalsIgnoreCase(operation)) {
            predicates.add(cb.equal(transactionRoot.get("operation"), "send".equalsIgnoreCase(operation) ? "SEND" : "RECEIVE"));
        }

        if(startDate!=null && endDate != null) {

            predicates.add(cb.between(transactionRoot.<Date>get("occurredAt"), startDate, endDate));

        }

        if (deliveryStatus != null) {
            predicates.add(cb.equal(transactionRoot.get("status"), deliveryStatus));
        }


        Predicate[] predicateArray = new Predicate[predicates.size()];
        predicates.toArray(predicateArray);

        return predicateArray;
    }


    private Long getResultLength(  Integer senderId,
                                   String status,
                                   String operation,
                                   TransactionStatus deliveryStatus,
                                   Date startDate,
                                   Date endDate ) {

        EntityManager entityManager = entityManagerFactory.createEntityManager();

        try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
            Root<Transaction> countRoot = countQuery.from(Transaction.class);
            countQuery.select(cb.count(countRoot));
            Predicate[] predicates = createPredicates(cb,
                    countRoot,
                    senderId,
                    status,
                    operation,
                    deliveryStatus,
                    startDate,
                    endDate
                    );

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

    /**
     *
     * @return
     */
    @PreAuthorize("hasRole('ROLE_ORG_ADMIN')")
    @Transactional(readOnly=true,propagation = Propagation.REQUIRED)
    public PagedData<Transaction> search(
            Integer senderId,
            String status,
            String operation,
            TransactionStatus deliveryStatus,
            Date startDate,
            Date endDate,
            PagedData<Transaction> pagedData
    ) {
        EntityManager entityManager =  entityManagerFactory.createEntityManager();
        try {

            CriteriaBuilder cb = entityManager.getCriteriaBuilder();

            CriteriaQuery<Transaction> cq = cb.createQuery(Transaction.class);
            Root<Transaction> transactionRoot = cq.from(Transaction.class);
            cq.select(transactionRoot);

            Predicate[] predicates = createPredicates(cb, transactionRoot, senderId, status, operation, deliveryStatus, startDate, endDate);

            cq.orderBy(cb.desc(transactionRoot.get("occurredAt")));
            cq.where(predicates);
            TypedQuery<Transaction> q = entityManager.createQuery(cq);
            q.setFirstResult(pagedData.getOffset());
            q.setMaxResults(pagedData.getLimit());
            pagedData.setData(q.getResultList());

            pagedData.setTotal(getResultLength(senderId, status, operation, deliveryStatus, startDate, endDate));

            return pagedData;

        } catch(Exception ex) {
            log.error("Failed to execute transaction search query.", ex);
        }
        finally {
            entityManager.close();
        }

        pagedData.setLimit(0);
        pagedData.setOffset(0);
        pagedData.setTotal(0L);
        pagedData.setData(new ArrayList<Transaction>());
        return pagedData;
    }

    public String toXml(Acknowledgment acknowledgment) {
        try {

            JAXBContext jc = JAXBContext.newInstance("org.pesc.sdk.message.functionalacknowledgement.v1_2.impl");
            Marshaller marshaller = jc.createMarshaller();

            Schema schema = ValidationUtils.getSchema(XmlFileType.FUNCTIONAL_ACKNOWLEDGEMENT, XmlSchemaVersion.V1_2_0);

            marshaller.setSchema(schema);
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );

            StringWriter writer = new StringWriter();
            marshaller.marshal(acknowledgment, writer);

            return writer.toString();

        } catch (JAXBException e) {
            log.error(e);
        } catch (SAXException e) {
            log.error(e);
        } catch (OperationNotSupportedException e) {
            log.error(e);
        }

        return null;
    }

}
