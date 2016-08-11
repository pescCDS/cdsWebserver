package org.pesc.service;

import org.pesc.api.model.Department;
import org.pesc.api.repository.DepartmentsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional(readOnly=false,propagation = Propagation.REQUIRED)
    @PreAuthorize("hasRole('ROLE_SYSTEM_ADMIN')")
    public Department create(Department department) {
        return departmentsRepository.save(department);

    }

    @Transactional(readOnly=false,propagation = Propagation.REQUIRED)
    @PreAuthorize("hasRole('ROLE_SYSTEM_ADMIN')")
    public void delete(Integer id)  {
        this.departmentsRepository.delete(id);
    }

    @Transactional(readOnly=false,propagation = Propagation.REQUIRED)
    @PreAuthorize("hasRole('ROLE_SYSTEM_ADMIN')")
    public Department update(Department deliveryMethod) {
        return departmentsRepository.save(deliveryMethod);

    }
}
