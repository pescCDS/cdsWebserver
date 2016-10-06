package org.pesc.api.repository;

import org.pesc.api.model.OAuthClientDetails;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by James Whetstone (jwhetstone@ccctechcenter.org) on 3/21/16.
 */
@Repository
public interface OAuthClientDetailsRepository extends CrudRepository<OAuthClientDetails, Integer>, JpaSpecificationExecutor {

}
