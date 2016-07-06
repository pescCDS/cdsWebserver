package org.pesc.api.repository;

import org.pesc.api.model.ServiceProvider;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by James Whetstone (jwhetstone@ccctechcenter.org) on 3/21/16.
 */
@Repository
public interface ServiceProviderRepository extends CrudRepository<ServiceProvider, Integer> {

}
