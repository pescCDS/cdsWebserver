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
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.pesc.cds.config.CacheConfig;
import org.pesc.cds.domain.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;
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


    @Cacheable(ORGANIZATION)
    public JSONObject getOrganization(Integer organizationId){
        return getOrganization(organizationId, null, null);
    }

    public JSONObject getOrganization(String schoolCode, String schoolCodeType){
        return getOrganization(null, schoolCode, schoolCodeType);
    }

    private JSONObject getOrganization(Integer organizationId, String schoolCode, String schoolCodeType){
        JSONObject organization = null;
        StringBuilder uri = new StringBuilder(directoryServer + organizationApiPath);
        if(organizationId!=null) {
            uri.append("?id=").append(organizationId);
        }
        if(schoolCode!=null && schoolCodeType!=null){
            uri.append("?organizationCodeType=").append(schoolCodeType).append("&organizationCode=").append(schoolCode);
        }
        try(CloseableHttpClient client = HttpClients.custom().build()) {
            HttpGet get = new HttpGet(uri.toString());
            get.setHeader(HttpHeaders.ACCEPT, "application/json");

            try(CloseableHttpResponse response = client.execute(get)) {

                HttpEntity resEntity = response.getEntity();
                if (response.getStatusLine().getStatusCode() == 200 && resEntity != null) {
                    JSONArray organizations = new JSONArray(EntityUtils.toString(resEntity));
                    if (organizations.length() == 1) {
                        organization = organizations.getJSONObject(0);
                    }
                }
                EntityUtils.consume(resEntity);
            }
        } catch (ClientProtocolException e) {
            log.error(e);
        } catch (IOException e) {
            log.error(e);
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

    public String getEndpointForOrg(int orgID, String documentFormat, String documentType, String department) {

        CloseableHttpClient client = HttpClients.custom().build();
        String endpointURI = null;
        try {
            StringBuilder uri = new StringBuilder(directoryServer + endpointsApiPath);
            uri.append("?organizationId=").append(orgID).append("&enabled=true").append("&mode=LIVE") ;
            if (StringUtils.isNotBlank(documentFormat))
                uri.append("&documentFormat=").append(documentFormat);

            if (StringUtils.isNotBlank(documentType))
                uri.append("&documentType=").append(URLEncoder.encode(documentType, "UTF-8"));

            if (StringUtils.isNotBlank(department))
                uri.append("&department=").append(department);


            HttpGet get = new HttpGet(uri.toString());
            get.setHeader(HttpHeaders.ACCEPT, "application/json");
            CloseableHttpResponse response = client.execute(get);
            try {

                HttpEntity resEntity = response.getEntity();
                if (response.getStatusLine().getStatusCode() == 200 && resEntity != null) {
                    JSONArray endpoints = new JSONArray(EntityUtils.toString(resEntity));
                    if (endpoints.length() > 0) {

                        if (endpoints.length() != 1) {
                            throw new RuntimeException("More than one endpoint was found that fits the given criteria.");
                        }
                        endpointURI = endpoints.getJSONObject(0).getString("address");
                        log.debug(endpoints.toString(3));
                    }
                }
                EntityUtils.consume(resEntity);
            }
            finally {
                response.close();
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                client.close();
            }
            catch (IOException e) {

            }
        }
        return endpointURI;
    }

    public String getEndpointURIForSchool(String destinationSchoolCode, String destinationSchoolCodeType, String documentFormat, String documentType, String department, Transaction tx, List<String> destinationOrganizationNames) {

        int orgID = getOrganizationId(destinationSchoolCode, destinationSchoolCodeType, destinationOrganizationNames);
        tx.setRecipientId(orgID);
        return getEndpointForOrg(orgID, documentFormat, documentType, department);
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
