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

import org.pesc.api.model.*;
import org.pesc.api.repository.ApiRequestRepository;
import org.pesc.utils.TimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by james on 3/26/17.
 */
@Service
public class ApiRequestService {

    @Value("${api.base.uri}")
    private String baseURI;

    @Autowired
    private JdbcTemplate jdbcTemplate;


    @Autowired
    private ApiRequestRepository apiRequestRepository;

    public void save(ApiRequest apiRequest) {
        apiRequestRepository.save(apiRequest);
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    @PreAuthorize("hasRole('ROLE_SYSTEM_ADMIN') ")
    public PagedData<UsageDataDTO> getUsageRecords(PagedData<UsageDataDTO> pagedData){

        String sql = "select resource, url, result_count, occurred_at from api_request LIMIT ? OFFSET ?";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, pagedData.getLimit(), pagedData.getOffset());

        ArrayList<UsageDataDTO> usageDataList = new ArrayList<UsageDataDTO>();

        for (Map row : rows) {
            UsageDataDTO usageData = new UsageDataDTO();
            usageData.setResource((String) row.get("resource"));
            usageData.setUrl((String) row.get("url"));
            usageData.setResultCount((Integer) row.get("result_count"));
            usageData.setOccurredAt((Date)row.get("occurred_at"));
            usageDataList.add(usageData);
        }

        pagedData.setTotal((Long) jdbcTemplate.queryForObject("SELECT COUNT(*) FROM api_request", Long.class));
        pagedData.setData(usageDataList);
        return pagedData;
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    @PreAuthorize("hasRole('ROLE_SYSTEM_ADMIN') ")
    public UsageDashboardDTO getDashboardData(){

        String sql = "select (select count(*) from api_request where result_count = 0 and resource = 'endpoints') as emptyResultEndpointQueries, \n" +
                "(select count(*) from api_request where result_count = 0 and url like '%public-key') as emptyResultPublicKeyQueries,\n" +
                "(select count(*) from api_request where url like '%public-key') as totalPublicKeyQueries,\n" +
                "(select count(*) from api_request where resource = 'endpoints') as totalEndpointQueries;";

        UsageDashboardDTO usageDashboardData = jdbcTemplate.queryForObject(sql, new RowMapper<UsageDashboardDTO>() {
            @Override
            public UsageDashboardDTO mapRow(ResultSet rs, int i) throws SQLException {
                UsageDashboardDTO dashboardData = new UsageDashboardDTO();
                dashboardData.setEmptyResultEndpointQueries(rs.getInt("emptyResultEndpointQueries"));
                dashboardData.setEmptyResultPublicKeyQueries(rs.getInt("emptyResultPublicKeyQueries"));
                dashboardData.setTotalEndpointQueries(rs.getInt("totalEndpointQueries"));
                dashboardData.setTotalPublicKeyQueries(rs.getInt("totalPublicKeyQueries"));
                return dashboardData;
            }
        });
        return usageDashboardData;
    }


    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public ApiRequest createAndSave(Map<String, String[]> parameters, String uri, Integer resultCount) {

        ApiRequest apiRequest = new ApiRequest();
        apiRequest.setOccurredAt(TimeUtils.getCurrentUTCTime());
        int index = uri.indexOf(baseURI) + baseURI.length();
        String url =   uri.substring(index + 1);

        apiRequest.setUrl(url);
        index = url.indexOf('/');

        if (index > -1) {
            apiRequest.setResource(url.substring(0, index));
        }
        else {
            apiRequest.setResource(url);
        }


        List<ApiRequestParameter> apiParameters = new ArrayList<ApiRequestParameter>();

        for (Map.Entry<String, String[]> entry : parameters.entrySet()) {
            for(String value : entry.getValue()) {
                apiParameters.add(new ApiRequestParameter(entry.getKey(), value, apiRequest));
            }
        }
        apiRequest.setParameters(apiParameters);
        apiRequest.setResultCount(resultCount);

        return apiRequestRepository.save(apiRequest);
    }

}
