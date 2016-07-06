package org.pesc.api.repository;

import org.pesc.api.model.InstitutionsUploadResult;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by James Whetstone (jwhetstone@ccctechcenter.org) on 5/10/16.
 */
@Repository
public interface InstitutionUploadResultsRepository extends CrudRepository<InstitutionsUploadResult, Integer> {

    public static String SUCCESS = "SUCCESS";
    public static String ERROR  = "ERROR";
    public static String WARNING = "WARNING";

    @Query("from InstitutionsUploadResult where organizationID = ?1")
    List<InstitutionsUploadResult> findByOrgId(Integer orgID);

    @Query("from InstitutionsUploadResult where institutionUploadID = ?1")
    List<InstitutionsUploadResult> findByUploadId(Integer institutionUploadID);

    @Query("from InstitutionsUploadResult where outcome = ?1")
    List<InstitutionsUploadResult> findByOutcome(String outcome);

    @Query("from InstitutionsUploadResult where outcome = ?1 and organizationID = ?2")
    List<InstitutionsUploadResult> findByOutcomeAndOrgID(String outcome, Integer orgID);

}