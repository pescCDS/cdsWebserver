package org.pesc.api.repository;

/**
 * Created by James Whetstone (jwhetstone@ccctechcenter.org) on 5/4/16.
 */

import org.pesc.api.model.OrganizationType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;



/**
 * Created by James Whetstone (jwhetstone@ccctechcenter.org) on 3/21/16.
 */
@Repository
public interface OrganizationTypeRepository extends CrudRepository<OrganizationType, Integer> {

}
