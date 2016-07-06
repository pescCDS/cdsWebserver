package org.pesc;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
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
import static org.junit.Assert.assertTrue;

/**
 * Created by James Whetstone (jwhetstone@ccctechcenter.org) on 6/13/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = NetworkServerApplication.class)
@WebAppConfiguration
public class InstitutionUseCaseTests {

    @Value("${directory.server.base.url}")
    String directoryServer;

    private final String USERNAME = "jwhetstone";
    private final String PASSWORD = "admin";


    private int directoryID = 2; //The directory ID for Butte College

    //Test RestTemplate to invoke the APIs.
    private RestTemplate restTemplate = new RestTemplate();

    private RestTemplate secureRestTemplate = new TestRestTemplate(USERNAME, PASSWORD);


    private String getBaseDirectoryServerURL() {
        return  directoryServer;
    }

    private HttpEntity<String> headersEntity;


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

    @Test
    public void updateOrganization() {

        ResponseEntity<String> responseEntity = restTemplate.getForEntity(getBaseDirectoryServerURL() + "/services/rest/v1/organizations/" + directoryID, String.class) ;

        assertTrue("Failed to get institution, directory id " + directoryID, responseEntity.getStatusCode() == HttpStatus.OK);

        JSONArray institutions = new JSONArray(responseEntity.getBody());

        assertTrue("There should be only one institution with a directory id of " + directoryID, institutions.length() == 1);

        JSONObject institution = institutions.getJSONObject(0);

        System.out.println(institution.toString(3));

        //update the institution data.
        institution.put("shortDescription", "Butte College is a community college in the Butte-Glenn Community College District which is located in northern California between the towns of Chico, Oroville, and Paradise, about 80 miles north of the state capital.");

        MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();

        headers.add("Content-Type", "application/json");


        responseEntity = secureRestTemplate.exchange(getBaseDirectoryServerURL() + "/services/rest/v1/organizations/" + directoryID, HttpMethod.PUT,
                new HttpEntity<String>(institution.toString(), headers), String.class);

        assertTrue("Institution update returned status code " + responseEntity.getStatusCode().getReasonPhrase(), responseEntity.getStatusCode() == HttpStatus.OK);

        JSONObject schoolCode = new JSONObject();
        schoolCode.put("code", "543210");
        schoolCode.put("codeType", "FICE");
        schoolCode.put("organizationId", institution.getInt("id"));

        institution.getJSONArray("schoolCodes").put(schoolCode);

    }

}
