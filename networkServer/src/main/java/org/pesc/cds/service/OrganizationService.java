package org.pesc.cds.service;

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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.IOException;

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

    @Cacheable(ORGANIZATION)
    public JSONObject getOrganization(Integer organizationId){
        return getOrganization(organizationId, null, null);
    }

    public JSONObject getOrganization(String destinationSchoolCode, String destinationSchoolCodeType){
        return getOrganization(null, destinationSchoolCode, destinationSchoolCodeType);
    }

    private JSONObject getOrganization(Integer organizationId, String destinationSchoolCode, String destinationSchoolCodeType){
        JSONObject organization = null;
        StringBuilder uri = new StringBuilder(directoryServer + organizationApiPath);
        if(organizationId!=null) {
            uri.append("?id=").append(organizationId);
        }
        if(destinationSchoolCode!=null && destinationSchoolCodeType!=null){
            uri.append("?organizationCodeType=").append(destinationSchoolCodeType).append("&organizationCode=").append(destinationSchoolCode);
        }
        CloseableHttpClient client = HttpClients.custom().build();
        try {
            HttpGet get = new HttpGet(uri.toString());
            get.setHeader(HttpHeaders.ACCEPT, "application/json");
            CloseableHttpResponse response = client.execute(get);
            try {

                HttpEntity resEntity = response.getEntity();
                if (response.getStatusLine().getStatusCode() == 200 && resEntity != null) {
                    JSONArray organizations = new JSONArray(EntityUtils.toString(resEntity));
                    if (organizations.length() == 1) {
                        organization = organizations.getJSONObject(0);
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

}
