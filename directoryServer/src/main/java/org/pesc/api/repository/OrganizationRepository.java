package org.pesc.api.repository;

import org.pesc.api.model.Organization;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by james on 3/21/16.
 */
@Repository
public interface OrganizationRepository extends CrudRepository<Organization, Integer> {

    @Query("from Organization where name = ?1")
    List<Organization> findByName(String name);
}