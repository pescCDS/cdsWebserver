package org.pesc.api.repository;

import org.pesc.api.model.Organization;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by james on 3/21/16.
 */
@Repository
public interface OrganizationRepository extends CrudRepository<Organization, Integer> {

    @Query("from Organization where name = ?1")
    Organization findByName(String name);
}
