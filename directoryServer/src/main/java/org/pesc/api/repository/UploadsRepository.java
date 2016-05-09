package org.pesc.api.repository;

import org.pesc.api.model.Upload;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by james on 3/21/16.
 */
@Repository
public interface UploadsRepository extends CrudRepository<Upload, Integer> {

    @Query("from Upload where userId = ?1")
    List<Upload> findByUserId(Integer userID);

    @Query("from Upload where organizationId = ?1")
    List<Upload> findByOrgId(Integer orgID);

}
