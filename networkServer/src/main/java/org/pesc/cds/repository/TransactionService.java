package org.pesc.cds.repository;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pesc.cds.domain.PagedData;
import org.pesc.cds.domain.Transaction;
import org.pesc.cds.model.TransactionStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
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
    @PreAuthorize("hasRole('ROLE_ADMIN')")
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
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void delete(Transaction transaction)  {
        this.transactionRepository.delete(transaction);
    }

    @Transactional(readOnly=false,propagation = Propagation.REQUIRED)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void delete(Integer id)  {
        this.transactionRepository.delete(id);
    }
    
    @Transactional(readOnly=true,propagation = Propagation.REQUIRED)
    public Transaction findById(Integer id)  {

        return this.transactionRepository.findOne(id);
    }

    public Predicate[] createPredicates(CriteriaBuilder cb, Root<Transaction> transactionRoot,  Integer senderId,
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

            countQuery.orderBy(cb.desc(countRoot.get("occurredAt")));
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

            pagedData.setTotal(getResultLength(senderId,status, operation, deliveryStatus,  startDate,endDate));

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

}
