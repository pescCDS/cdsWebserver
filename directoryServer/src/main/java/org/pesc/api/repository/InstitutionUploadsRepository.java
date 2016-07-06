package org.pesc.api.repository;

import org.pesc.api.model.InstitutionsUpload;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by James Whetstone (jwhetstone@ccctechcenter.org) on 3/21/16.
 */
@Repository
public interface InstitutionUploadsRepository extends CrudRepository<InstitutionsUpload, Integer> {

    @Query("from InstitutionsUpload where userId = ?1")
    List<InstitutionsUpload> findByUserId(Integer userID);

    @Query("from InstitutionsUpload where organizationId = ?1")
    List<InstitutionsUpload> findByOrgId(Integer orgID);

}
