package org.pesc.cds.repository;

import org.pesc.cds.domain.Transaction;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by James Whetstone (jwhetstone@ccctechcenter.org) on 3/21/16.
 */
@Repository
public interface TransactionRepository extends CrudRepository<Transaction, Integer>, JpaSpecificationExecutor {

    @Query("from Transaction where status = ?1")
    List<Transaction> findByStatus(Boolean acknowledgedByReceiver);

}
