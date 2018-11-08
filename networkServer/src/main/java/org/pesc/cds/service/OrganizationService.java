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

package org.pesc.cds.service;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import org.pesc.cds.config.CacheConfig;
import org.pesc.cds.domain.Transaction;
import org.pesc.cds.model.EndpointMode;
import org.pesc.cds.model.IdList;
import org.pesc.cds.model.SchoolCodeType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.List;

/**
 * Created by sallen on 8/12/16.
 */
@Service
public class OrganizationService {
    private static final Log log = LogFactory.getLog(OrganizationService.class);
    private static final String ORGANIZATION = CacheConfig.PREFIX + "Organization";
    private static final String ORGANIZATIONS = CacheConfig.PREFIX + "Organizations";

    @Value("${directory.server.base.url}")
    private String directoryServer;

    @Value("${api.organization}")
    private String organizationApiPath;

    @Value("${api.endpoints}")
    private String endpointsApiPath;

    @Qualifier("directoryServerClient")
    @Autowired
    private RestTemplate directoryServerClient;

    @Value("${networkServer.id}")
    private String orgID;


    @Cacheable(ORGANIZATION)
    public JSONObject getOrganization(Integer organizationId){
        return getOrganization(organizationId, null, null);
    }

    public JSONObject getOrganization(String schoolCode, String schoolCodeType){
        return getOrganization(null, schoolCode, schoolCodeType);
    }

    private JSONObject getOrganization(Integer organizationId, String schoolCode, String schoolCodeType){
        JSONObject organization = null;

        try {
            StringBuilder uri = new StringBuilder(directoryServer + organizationApiPath);
            if(organizationId!=null) {
                uri.append("?id=").append(organizationId);
            }
            if(schoolCode!=null && schoolCodeType!=null){
                uri.append("?organizationCodeType=").append(schoolCodeType).append("&organizationCode=").append(schoolCode);
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            ResponseEntity<String> response = directoryServerClient.getForEntity(uri.toString(),
                    String.class, new HttpEntity<String>(headers));

            if (response.getStatusCodeValue() == 200 ) {
                JSONArray organizations = new JSONArray(response.getBody());
                if (organizations.length() == 1) {
                    organization = organizations.getJSONObject(0);
                }
            }

        }
        catch (Exception e) {
            log.warn("Failed to retrieve organization entity from the directory server.", e);
        }

        return organization;
    }

    public boolean isInstitution(JSONObject organization){
        boolean institution = false;
        JSONArray organizationTypes = organization.getJSONArray("organizationTypes");
        for(int i=0; i<organizationTypes.length(); i++){
            String organizationType = organizationTypes.getJSONObject(i).getString("name");
            if("Institution".equals(organizationType)){
                institution = true;
                break;
            }
        }
        return institution;
    }

    public String getEndpointForOrg(Integer orgID, String documentFormat, String documentType, String department, EndpointMode mode) {

        String endpointURI = null;

        StringBuilder uri = new StringBuilder(directoryServer + endpointsApiPath);
        uri.append("?organizationId=").append(orgID).append("&enabled=true").append("&mode=" + mode.getMode()) ;
        if (StringUtils.isNotBlank(documentFormat))
            uri.append("&documentFormat=").append(documentFormat);

        if (StringUtils.isNotBlank(documentType))
            uri.append("&documentType=").append(documentType);

        if (StringUtils.isNotBlank(department))
            uri.append("&department=").append(department);


        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        ResponseEntity<String> response = directoryServerClient.getForEntity(uri.toString(),
                String.class, new HttpEntity<String>(headers));

        if (response.getStatusCodeValue() == 200 ) {
            JSONArray endpoints = new JSONArray(response.getBody());
            if (endpoints.length() > 0) {

                if (endpoints.length() != 1) {
                    throw new RuntimeException("More than one endpoint was found that fits the given criteria.");
                }
                endpointURI = endpoints.getJSONObject(0).getString("address");
                log.debug(endpoints.toString(3));
            }
        }

        return endpointURI;
    }

    public String getEndpointURIForSchool(String destinationSchoolCode, String destinationSchoolCodeType, String documentFormat, String documentType, String department, Transaction tx, List<String> destinationOrganizationNames, EndpointMode mode) {


        Integer orgID = null;

        if (SchoolCodeType.EDEXCHANGE.toString().equalsIgnoreCase(destinationSchoolCodeType)) {
            orgID = Integer.valueOf(destinationSchoolCode);
        }
        else {
            orgID = getOrganizationId(destinationSchoolCode, destinationSchoolCodeType, destinationOrganizationNames);
        }
        tx.setRecipientId(orgID);
        return getEndpointForOrg(orgID, documentFormat, documentType, department, mode);
    }

    public List<Integer> getInstitutionsForServiceProvider(){
        StringBuilder uri = new StringBuilder(directoryServer + "/services/rest/v1/institutions/id-list?service_provider_id=" + orgID);
        RestTemplate template = new RestTemplate();

        IdList idListEntity = template.getForObject(uri.toString(), IdList.class);

        return idListEntity.idList;
    }

    public int getOrganizationId(String destinationSchoolCode, String destinationSchoolCodeType, List<String> destinationOrganizationNames) {
        log.debug("Getting endpoint for org");
        int orgID = 0;
        JSONObject organization = getOrganization(destinationSchoolCode, destinationSchoolCodeType);
        if(organization!=null){
            orgID = organization.getInt("id");
            destinationOrganizationNames.add(organization.getString("name"));
        }
        return orgID;
    }



}
