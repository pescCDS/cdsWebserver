package org.pesc;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.pesc.api.EndpointResource;
import org.pesc.api.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = DirectoryApplication.class)
@WebIntegrationTest("server.port=0")  //let Spring select a random port to use.
public class DirectoryApplicationTests {

	@Value("${http.port}")   //Injects that actual port used in the test
	int port;

	final String USERNAME = "sallen";
	final String PASSWORD = "admin";

	//Test RestTemplate to invoke the APIs.
	private RestTemplate restTemplate = new RestTemplate();

	private RestTemplate secureRestTemplate = new TestRestTemplate(USERNAME, PASSWORD); //new TestRestTemplate(USERNAME, PASSWORD);

	private HttpEntity<String> headersEntity;

	@ClassRule
	public static DockerContainerRule dockerContainerRule = new DockerContainerRule("cdswebserver_directoryserver_db_image");

	@Test
	public void contextLoads() {
	}


	@Before
	public void initialize() {
		//Make sure that the REST API returns JSON
		//This will be used in all API tests.
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

		headersEntity = new HttpEntity<String>("parameters", headers);
	}


	@Test
	public void testCSVOrganizationConversion() {


	}


	@Test
	public void testDomainComparison() {

		String certificateName = "*.ccctechcenter.org";
		String endpointURL = "https://edex-network-qa.ccctechcenter.org:9443/api/v1/documents/inbox";

		try {
			EndpointResource.validateEndpointURL(endpointURL, certificateName);

		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

	}

	@Test
	public void testOrganizationsAPI() {

		List<Organization> organizations = restTemplate.getForObject("http://localhost:" + port + "/services/rest/v1/organizations", List.class);
		assertThat ("Organizations should not be null or empty.", organizations != null && !organizations.isEmpty());

		organizations = restTemplate.getForObject("http://localhost:" + port + "/services/rest/v1/organizations?enabled=true&institution=true&limit=1&&offset=0", List.class);
		assertThat ("There should be exactly 1 organization in the list.", organizations != null && organizations.size() == 1);

		organizations = restTemplate.getForObject("http://localhost:" + port + "/services/rest/v1/organizations?enabled=true&serviceprovider=true&limit=1&&offset=0", List.class);
		assertThat ("There should be exactly 1 organization in the list.", organizations != null && organizations.size() == 1);

		organizations = restTemplate.getForObject("http://localhost:" + port + "/services/rest/v1/organizations?enabled=true&name=PESC&limit=1&&offset=0", List.class);
		assertThat ("There should be exactly 1 organization in the list.", organizations != null && organizations.size() == 1);

		organizations = restTemplate.getForObject("http://localhost:" + port +  "/services/rest/v1/organizations?enabled=true&limit=5&offset=0", List.class);
		assertThat ("There should be at least 3 organizations!", organizations.size() > 2);

		organizations = restTemplate.getForObject("http://localhost:" + port +  "/services/rest/v1/organizations?enabled=false&limit=5&offset=0", List.class);
		assertThat ("There should be at 0 organizations!", organizations.isEmpty());

		organizations = restTemplate.getForObject("http://localhost:" + port +  "/services/rest/v1/organizations?enabled=true&organizationCodeType=ATP&organizationCode=4226&limit=5&offset=0", List.class);
		assertThat ("There should be at least 1 organizations!", organizations.size() == 1);

	}


	public List<Department> getDepartments() {
		ParameterizedTypeReference<List<Department>> typeRef = new ParameterizedTypeReference<List<Department>>() {};
		ResponseEntity<List<Department>> response = secureRestTemplate.exchange("http://localhost:" + port + "/services/rest/v1/departments", HttpMethod.GET, null, typeRef);
		assertThat ("Departments list should not be null or empty.", response.getBody() != null && !response.getBody().isEmpty());

		return response.getBody();
	}

	public List<DocumentFormat> getDocumentFormats() {
		ParameterizedTypeReference<List<DocumentFormat>> typeRef = new ParameterizedTypeReference<List<DocumentFormat>>() {};
		ResponseEntity<List<DocumentFormat>> response = secureRestTemplate.exchange("http://localhost:" + port + "/services/rest/v1/document-formats", HttpMethod.GET, null, typeRef);
		assertThat ("Document formats list should not be null or empty.", response.getBody() != null && !response.getBody().isEmpty());

		return response.getBody();
	}


	public List<DocumentType> getDocumentTypes() {
		ParameterizedTypeReference<List<DocumentType>> typeRef = new ParameterizedTypeReference<List<DocumentType>>() {};
		ResponseEntity<List<DocumentType>> response = secureRestTemplate.exchange("http://localhost:" + port + "/services/rest/v1/document-formats", HttpMethod.GET, null, typeRef);
		assertThat ("Document types list should not be null or empty.", response.getBody() != null && !response.getBody().isEmpty());

		return response.getBody();
	}

	public List<DeliveryMethod> getDeliveryMethods() {
		ParameterizedTypeReference<List<DeliveryMethod>> typeRef = new ParameterizedTypeReference<List<DeliveryMethod>>() {};
		ResponseEntity<List<DeliveryMethod>> response = secureRestTemplate.exchange("http://localhost:" + port + "/services/rest/v1/delivery-methods", HttpMethod.GET, null, typeRef);
		assertThat ("Delivery methods should not be null or empty.", response.getBody() != null && !response.getBody().isEmpty());

		return response.getBody();
	}

	public OrganizationDTO getServiceProvider() {
		ParameterizedTypeReference< List<OrganizationDTO> > typeRef = new ParameterizedTypeReference< List<OrganizationDTO>>() {};
		ResponseEntity<List<OrganizationDTO>> response = secureRestTemplate.exchange("http://localhost:" + port + "/services/rest/v1/organizations/3", HttpMethod.GET, null, typeRef);


		assertThat ("Organization/service provider with directory ID 3 could not be found.", response.getBody() != null && !response.getBody().isEmpty() && response.getBody().size() == 1);

		return response.getBody().get(0);
	}
	@Test
	public void testEndpointAPI() {

		DeliveryMethod deliveryMethod = getDeliveryMethods().get(0);
		Department department = getDepartments().get(0);
		DocumentFormat documentFormat = getDocumentFormats().get(0);
		DocumentType documentType = getDocumentTypes().get(0);


		Endpoint endpoint = new Endpoint();
		endpoint.setAddress("https://localhost:8000/api/documents/outbox");
		endpoint.setConfirmDelivery(true);
		endpoint.setDeliveryMethod(deliveryMethod);
		endpoint.setDocumentType(documentType);
		endpoint.setDepartment(department);
		endpoint.setDocumentFormat(documentFormat);
		endpoint.setOrganization(getServiceProvider());
		endpoint.setError(false);
		endpoint.setMode("LIVE");


		MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();

		headers.add("Content-Type", "application/json");
		headers.add("Accept", "application/json");

		HttpEntity<Endpoint> request = new HttpEntity<Endpoint>(endpoint, headers);

		Endpoint persistedEndpoint = secureRestTemplate.postForObject("http://localhost:" + port + "/services/rest/v1/endpoints", request, Endpoint.class);


		assertThat("Endpoint was not saved correctly.", persistedEndpoint.getId() > 0);

	}



}
