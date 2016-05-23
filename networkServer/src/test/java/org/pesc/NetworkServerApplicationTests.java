package org.pesc;

import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = NetworkServerApplication.class)
@WebAppConfiguration
public class NetworkServerApplicationTests {

	@ClassRule
	public static DockerContainerRule dockerContainerRule = new DockerContainerRule("cdswebserver_networkserver_db_image");


	private KeyPair getSigningKeys() {
		FileInputStream is = null;
		try {
			is = new FileInputStream("sign.pfx");


			KeyStore keystore = KeyStore.getInstance("pkcs12");
			keystore.load(is, "Welcome@1".toCharArray());

			String alias = "signature";

			Key key = keystore.getKey(alias, "Welcome@1".toCharArray());
			if (key instanceof PrivateKey) {
				// Get certificate of public key
				Certificate cert = keystore.getCertificate(alias);

				// Get public key
				PublicKey publicKey = cert.getPublicKey();

				// Return a key pair
				new KeyPair(publicKey, (PrivateKey) key);
			}
		} catch (Exception e) {

		}

		return null;
	}


	@Test
	public void contextLoads() {

		  KeyPair keyPair = getSigningKeys();



	}

}
