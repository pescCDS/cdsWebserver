package org.pesc.cds.repository;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pesc.cds.domain.Transaction;
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
 * Created by james on 3/21/16.
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



    /**
     *
     * @return
     */
    @Transactional(readOnly=true,propagation = Propagation.REQUIRED)
    public List<Transaction> search(
            Integer senderId,
            String status,
            Date startDate,
            Date endDate,
            Long limit,
            Long offset
    ) {
        EntityManager entityManager =  entityManagerFactory.createEntityManager();
        try {

            CriteriaBuilder cb = entityManager.getCriteriaBuilder();

            CriteriaQuery<Transaction> cq = cb.createQuery(Transaction.class);
            Root<Transaction> transactionRoot = cq.from(Transaction.class);
            cq.select(transactionRoot);

            List<Predicate> predicates = new LinkedList<Predicate>();

            if(senderId!=null) {
                predicates.add(cb.equal(transactionRoot.get("senderId"), senderId));
            }


            if(status != null) {
                predicates.add(cb.equal(transactionRoot.get("status"), "complete".equalsIgnoreCase(status) ? true : false));
            }

            if(startDate!=null && endDate != null) {

                predicates.add(cb.between(transactionRoot.<Date>get("sent"), startDate, endDate));
                predicates.add(cb.between(transactionRoot.<Date>get("received"), startDate, endDate));

            }

            Predicate[] predicateArray = new Predicate[predicates.size()];
            predicates.toArray(predicateArray);

            cq.where(predicateArray);
            TypedQuery<Transaction> q = entityManager.createQuery(cq);

            return q.getResultList();


        } catch(Exception ex) {
            log.error("Failed to execute transaction search query.", ex);
        }
        finally {
            entityManager.close();
        }
        return new ArrayList<Transaction>();
    }

}
