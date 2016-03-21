package org.pesc;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.pesc.service.DocumentResource;
import org.pesc.web.model.Organization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = DirectoryApplication.class)
@WebIntegrationTest("server.port=0")  //let Spring select a random port to use.
public class DirectoryApplicationTests {

	@Value("${local.server.port}")   //Injects that actual port used in the test
	int port;

	final String USERNAME = "admin";
	final String PASSWORD = "password";

	//Test RestTemplate to invoke the APIs.
	private RestTemplate restTemplate = new TestRestTemplate(USERNAME, PASSWORD);

	private HttpEntity<String> headersEntity;

	@ClassRule
	public static DockerContainerRule dockerContainerRule = new DockerContainerRule("directoryserver_db_image");

	@Test
	public void contextLoads() {
	}

	@Autowired
	private DocumentResource documentService;


	@Before
	public void initialize() {
		//Make sure that the REST API returns JSON
		//This will be used in all API tests.
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

		headersEntity = new HttpEntity<String>("parameters", headers);
	}


	@Test
	public void testDocumentService() {

		//ResponseEntity<Organization> response = restTemplate.exchange("http://localhost:" + port + "/services/rest/document", HttpMethod.GET, headersEntity, Organization.class);

		//Organization org = response.getBody();

		//assertThat(org.getName(), equalTo("Butte College"));

	}



}
