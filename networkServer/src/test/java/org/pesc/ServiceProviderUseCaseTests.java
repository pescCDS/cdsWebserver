package org.pesc;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.pesc.cds.service.PKIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Created by james on 6/13/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = NetworkServerApplication.class)
@WebAppConfiguration
public class ServiceProviderUseCaseTests {

    @Value("${directory.server.base.url}")
    String directoryServer;

    final String USERNAME = "sallen";
    final String PASSWORD = "admin";

    //Test RestTemplate to invoke the APIs.
    private RestTemplate restTemplate = new RestTemplate();

    private RestTemplate secureRestTemplate = new TestRestTemplate(USERNAME, PASSWORD);

    private HttpEntity<String> headersEntity;

    private String getBaseDirectoryServerURL() {
        return directoryServer;
    }


    @Before
    public void initialize() {
        //Make sure that the REST API returns JSON
        //This will be used in all API tests.
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

        headersEntity = new HttpEntity<String>("parameters", headers);
    }
    @ClassRule
    public static DockerContainerRule dockerContainerRule = new DockerContainerRule("cdswebserver_networkserver_db_image");

    /**
     * Use case; service provider can create an instition and then add the institution to the provider's group
     * of serviceable institutions.
     */
    @Test
    public void createInstitutionAndLinkToServiceProvider() {
        JSONObject institution = new JSONObject();
        institution.put("name", "Sacramento City College");
        institution.put("website", "http://www.scc.losrios.edu/");
        institution.put("city", "Sacramento");
        institution.put("state", "CA");
        institution.put("zip", "95822");
        institution.put("street", "3835 Freeport Blvd");
        institution.put("telephone", "(916) 558-2351");

        JSONObject organizationType = new JSONObject();
        organizationType.put("id", 2);
        organizationType.put("name", "Institution");

        JSONArray organizationTypes = new JSONArray();
        organizationTypes.put(organizationType);

        institution.put("organizationTypes", organizationTypes);

        JSONObject schoolCode = new JSONObject();
        schoolCode.put("code", "765432");
        schoolCode.put("codeType", "ATP");
        JSONArray schoolCodes = new JSONArray();
        schoolCodes.put(schoolCode);

        institution.put("schoolCodes", schoolCodes);


        MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();

        headers.add("Content-Type", "application/json");

        HttpEntity<String> request = new HttpEntity<String>(institution.toString(), headers);


        ResponseEntity<String> response = secureRestTemplate.exchange(getBaseDirectoryServerURL() + "/services/rest/v1/institutions", HttpMethod.POST, request, String.class);

        assertTrue("Institution creation returned status code " + response.getStatusCode().getReasonPhrase(), response.getStatusCode() == HttpStatus.OK);

        JSONObject createdInstitution = new JSONObject(response.getBody());

        HttpHeaders responseHeaders = response.getHeaders();


        System.out.print(createdInstitution.toString(3));

        assertThat("Institution was not saved correctly.", createdInstitution.getInt("id") > 0);


        //Now add the new institution to the provider's group of serviceable institutions...

        JSONObject relation = new JSONObject();
        relation.put("institutionID", createdInstitution.getInt("id"));
        relation.put("serviceProviderID", 3);

        response = secureRestTemplate.exchange(getBaseDirectoryServerURL() + "/services/rest/v1/institutions/relation", HttpMethod.POST, new HttpEntity<String>(relation.toString(), headers), String.class);

        assertTrue("Institution relation with service provided failed." + response.getStatusCode().getReasonPhrase(), response.getStatusCode() == HttpStatus.OK);


    }


}
