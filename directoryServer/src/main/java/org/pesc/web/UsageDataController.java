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

package org.pesc.web;

import org.pesc.api.model.*;
import org.pesc.service.ApiRequestService;
import org.pesc.service.PagedData;
import org.pesc.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;
import java.security.Principal;
import java.util.List;

/**
 * Created by james on 1/22/17.
 */
@RestController
public class UsageDataController {

    @Autowired
    private ApiRequestService apiRequestService;

    @RequestMapping(value = "/usage-data")
    public List<UsageDataDTO> getUsageRecords(Integer limit, Integer offset, HttpServletResponse response) {


        if (limit == null || offset == null) {
            limit = 5;
            offset = 0;
        }
        PagedData<UsageDataDTO> pagedData = new PagedData<UsageDataDTO>(limit,offset);

        apiRequestService.getUsageRecords(pagedData);

        response.addHeader("X-Total-Count", String.valueOf(pagedData.getTotal()));
        return pagedData.getData();

    }

    @RequestMapping(value = "/usage-dashboard-data")
    public UsageDashboardDTO getUsageDashboardData() {
        return apiRequestService.getDashboardData();

    }

}
