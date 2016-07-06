package org.pesc.api.repository;

import org.pesc.api.model.Endpoint;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by James Whetstone (jwhetstone@ccctechcenter.org) on 3/21/16.
 */
@Repository
public interface EndpointRepository extends CrudRepository<Endpoint, Integer>, JpaSpecificationExecutor {

}
