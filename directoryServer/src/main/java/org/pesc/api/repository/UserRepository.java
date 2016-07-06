package org.pesc.api.repository;

import org.pesc.api.model.DirectoryUser;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by James Whetstone (jwhetstone@ccctechcenter.org) on 3/21/16.
 */
@Repository
public interface UserRepository extends CrudRepository<DirectoryUser, Integer>, JpaSpecificationExecutor {

    @Query("from DirectoryUser where name = ?1")
    List<DirectoryUser> findByName(String name);

    @Query("from DirectoryUser where username = ?1")
    List<DirectoryUser> findByUserName(String username);

}
