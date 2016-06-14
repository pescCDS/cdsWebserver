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
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = NetworkServerApplication.class)
@WebAppConfiguration
public class DigitalSignatureTests {

	@Autowired
	PKIService pkiService;

	@Test
	public void testDocumentSigning() {

		KeyPair keyPair = pkiService.getSigningKeys();

		InputStream inputStream = getClass().getResourceAsStream("/test.txt");

		byte[] signature = pkiService.createDigitalSignature(inputStream, keyPair.getPrivate());

		assertTrue("File signatures do not match.", pkiService.verifySignature(getClass().getResourceAsStream("/test.txt"), signature, keyPair.getPublic()));

	}

}
