package org.pesc.service;

import org.pesc.api.model.Department;
import org.pesc.api.repository.DepartmentsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by James Whetstone (jwhetstone@ccctechcenter.org) on 4/6/16.
 */
@Service
public class DepartmentsService {

    @Autowired
    private DepartmentsRepository departmentsRepository;

    public Iterable<Department> getDepartments() {
        return departmentsRepository.findAll();
    }
}
