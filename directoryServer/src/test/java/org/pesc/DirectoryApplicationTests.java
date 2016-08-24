package org.pesc;

import org.json.JSONArray;
import org.json.JSONObject;
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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import java.net.URISyntaxException;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = DirectoryApplication.class)
@WebIntegrationTest("server.port=0")  //let Spring select a random port to use.
@DirtiesContext
public class DirectoryApplicationTests {

	@Value("${http.port}")   //Injects that actual port used in the test
	int port;


	//Test RestTemplate to invoke the APIs.
	private RestTemplate restTemplate = new RestTemplate();
	private RestTemplate secureRestTemplate = new TestRestTemplate("sallen", "admin");
	private RestTemplate butteCollegeRestTemplate = new TestRestTemplate("jwhetstone", "admin");
	private RestTemplate superUserRestTemplate = new TestRestTemplate("admin", "admin");

	private String getBaseDirectoryServerURL() {
		return  "http://localhost:" + port;
	}
	private int directoryID = 2; //The directory ID for Butte College

	@ClassRule
	public static DockerContainerRule dockerContainerRule = new DockerContainerRule("cdswebserver_directoryserver_db_image");

	/**
	 * Tests that the directory server home page is accessible.
	 * @throws Exception
	 */
	@Test
	public void testHome() throws Exception {
		ResponseEntity<String> entity = new TestRestTemplate().getForEntity(
				"http://localhost:" + this.port + "/home", String.class);
		assertEquals(HttpStatus.OK, entity.getStatusCode());
	}

	/**
	 * Tests the organization resource by first obtaining an organization object for an institution, in this case,
	 * Butte college and then updating a property of the institution, in this test case, the short description,
	 * of the institution.  Note that "institutions" and "service providers" are both organizations in the directory
	 * server.
	 */
	@Test
	public void updateOrganization() {

		HttpEntity<String> entity = new HttpEntity<String>("parameters", Utils.createHttpHeaders());

		ResponseEntity<String> responseEntity = butteCollegeRestTemplate.exchange(getBaseDirectoryServerURL() + "/services/rest/v1/organizations/" + directoryID, HttpMethod.GET, entity, String.class) ;

		assertTrue("Failed to get institution, directory id " + directoryID + " ERROR: " + responseEntity.getBody(), responseEntity.getStatusCode() == HttpStatus.OK);

		JSONArray institutions = new JSONArray(responseEntity.getBody());

		assertTrue("There should be only one institution with a directory id of " + directoryID, institutions.length() == 1);

		JSONObject institution = institutions.getJSONObject(0);

		System.out.println(institution.toString(3));

		//update the institution data.
		institution.put("shortDescription", "Butte College is a community college in the Butte-Glenn Community College District which is located in northern California between the towns of Chico, Oroville, and Paradise, about 80 miles north of the state capital.");

		HttpHeaders headers = Utils.createHttpHeaders();
		Utils.addCSRFHeaders(headers);


		responseEntity = butteCollegeRestTemplate.exchange(getBaseDirectoryServerURL() + "/services/rest/v1/organizations/" + directoryID, HttpMethod.PUT,
				new HttpEntity<String>(institution.toString(), headers), String.class);

		assertTrue("Institution update returned status code " + responseEntity.getStatusCode().getReasonPhrase(), responseEntity.getStatusCode() == HttpStatus.OK);


	}

	/**
	 * This is service provider use case which verifies that a service provider can create an institution and then add the institution to the provider's group
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


		HttpHeaders headers = Utils.createHttpHeaders();
		Utils.addCSRFHeaders(headers);


		HttpEntity<String> request = new HttpEntity<String>(institution.toString(), headers);


		ResponseEntity<String> response = secureRestTemplate.exchange(getBaseDirectoryServerURL() + "/services/rest/v1/institutions", HttpMethod.POST, request, String.class);

		assertTrue("Institution creation returned status code " + response.getStatusCode().getReasonPhrase(), response.getStatusCode() == HttpStatus.OK);

		JSONObject createdInstitution = new JSONObject(response.getBody());

		System.out.print(createdInstitution.toString(3));

		assertThat("Institution was not saved correctly.", createdInstitution.getInt("id") > 0);


		//Now add the new institution to the provider's group of serviceable institutions...

		JSONObject relation = new JSONObject();
		relation.put("institutionID", createdInstitution.getInt("id"));
		relation.put("serviceProviderID", 3);

		response = secureRestTemplate.exchange(getBaseDirectoryServerURL() + "/services/rest/v1/institutions/relation", HttpMethod.POST, new HttpEntity<String>(relation.toString(), headers), String.class);

		assertTrue("Institution relation with service provided failed." + response.getStatusCode().getReasonPhrase(), response.getStatusCode() == HttpStatus.OK);


	}

	/**
	 * Test that verifies that code checks whether the domain name from the network SSL certificate and the domain name
	 * supplied as part of the endpoint URL are valid.
	 */
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

	/**
	 * Common API calls that query the organizations resource using different criteria.
	 */
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
		assertNotNull("Expected a list of organizations, but got null.", organizations);

		organizations = restTemplate.getForObject("http://localhost:" + port +  "/services/rest/v1/organizations?enabled=true&organizationCodeType=ATP&organizationCode=004226&limit=5&offset=0", List.class);
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
		assertThat("Delivery methods should not be null or empty.", response.getBody() != null && !response.getBody().isEmpty());

		return response.getBody();
	}

	public OrganizationDTO getServiceProvider() {
		ParameterizedTypeReference< List<OrganizationDTO> > typeRef = new ParameterizedTypeReference< List<OrganizationDTO>>() {};
		ResponseEntity<List<OrganizationDTO>> response = secureRestTemplate.exchange("http://localhost:" + port + "/services/rest/v1/organizations/3", HttpMethod.GET, null, typeRef);


		assertThat("Organization/service provider with directory ID 3 could not be found.", response.getBody() != null && !response.getBody().isEmpty() && response.getBody().size() == 1);

		return response.getBody().get(0);
	}



	@Test
	public void testPOSTEndpointWithoutCSRFHeaders() {


		Endpoint endpoint = buildTestEndpoint();

		HttpHeaders headers = Utils.createHttpHeaders();

		HttpEntity<Endpoint> request = new HttpEntity<Endpoint>(endpoint, headers);

		ResponseEntity<String> response = secureRestTemplate.exchange("http://localhost:" + port + "/services/rest/v1/endpoints", HttpMethod.POST, request, String.class);

        assertTrue("Expected HTTP status of 403 (Forbidden) due to missing CSRF headers.", response.getStatusCode() == HttpStatus.FORBIDDEN);


	}


	@Test
	public void testPOSTEndpointWithCSRFHeaders() {


		Endpoint endpoint = buildTestEndpoint();

		HttpHeaders headers = Utils.createHttpHeaders();
		Utils.addCSRFHeaders(headers);

		String pemText = Utils.convertStreamToString(getClass().getResourceAsStream("/localhost-certificate.pem"));

		ResponseEntity<String> response = secureRestTemplate.exchange("http://localhost:" + port + "/services/rest/v1/organizations/3/network-certificate",
				HttpMethod.PUT,
				new HttpEntity<String>(pemText, headers),
				String.class);
		assertTrue("Failed to update network server certificate.", response.getStatusCode() == HttpStatus.OK);


		response = superUserRestTemplate.exchange("http://localhost:" + port + "/services/rest/v1/organizations/3/enabled",
				HttpMethod.PUT,
				new HttpEntity<String>("true", headers),
				String.class);
		assertTrue("Failed to enable organization.", response.getStatusCode() == HttpStatus.NO_CONTENT);

		response = secureRestTemplate.exchange("http://localhost:" + port + "/services/rest/v1/endpoints", HttpMethod.POST,  new HttpEntity<Endpoint>(endpoint, headers), String.class);

		assertTrue("Failed to POST endpoint. Error response: " + response.getBody(), response.getStatusCode() == HttpStatus.OK);

	}



	Endpoint buildTestEndpoint() {
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

		return endpoint;
	}





}
