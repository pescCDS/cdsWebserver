package org.pesc.api.repository;

import org.pesc.api.model.Message;
import org.pesc.api.model.Organization;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by James Whetstone (jwhetstone@ccctechcenter.org) on 3/21/16.
 */
@Repository
public interface MessageRepository extends CrudRepository<Message, Integer>, JpaSpecificationExecutor {

    @Query("from Message where actionRequired = ?1 AND organizationId = ?2")
    List<Message> findByActionRequiredAndOrganization(boolean isActionRequired, Integer organizationId);

    @Query("from Message where organizationId = ?1")
    List<Message> findByOrganizationId(Integer organizationId);
}
