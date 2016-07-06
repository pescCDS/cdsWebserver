package org.pesc.api.repository;

import org.pesc.api.model.Department;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by James Whetstone (jwhetstone@ccctechcenter.org) on 4/6/16.
 */
@Repository
public interface DepartmentsRepository extends CrudRepository<Department, Integer> {
}
