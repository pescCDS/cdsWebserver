/*
 * Copyright (c) 2017. California Community Colleges Technology Center
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.pesc.service;


import org.pesc.api.model.Institution;
import org.pesc.api.repository.InstitutionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by James Whetstone (jwhetstone@ccctechcenter.org) on 6/21/16.
 */
@Service
public class InstitutionService {

    @Autowired
    private InstitutionRepository institutionRepository;


    public Institution findById(Integer id) {
        return institutionRepository.findOne(id);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    @PreAuthorize("hasRole('ROLE_SYSTEM_ADMIN') OR hasRole('ROLE_ORG_ADMIN')")
    public Institution save(Institution institution) {
        return institutionRepository.save(institution);

    }
}
