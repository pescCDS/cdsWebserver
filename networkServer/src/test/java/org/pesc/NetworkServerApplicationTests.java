package org.pesc;

import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.*;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.Set;

import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = NetworkServerApplication.class)
@WebAppConfiguration
public class NetworkServerApplicationTests {

	@ClassRule
	public static DockerContainerRule dockerContainerRule = new DockerContainerRule("cdswebserver_networkserver_db_image");


	public KeyPair getPrivateKey(KeyStore keystore, String alias, char[] password) {

		try {

			Enumeration enumeration = keystore.aliases();
			while (enumeration.hasMoreElements()) {
				String name = (String)enumeration.nextElement();
				System.out.println("alias name: " + name);

			}

			KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keystore.getEntry(alias, new KeyStore.PasswordProtection(password));

			Certificate[] cc = privateKeyEntry.getCertificateChain();
			X509Certificate cert = (X509Certificate) cc[0];


			return new KeyPair(cert.getPublicKey(), privateKeyEntry.getPrivateKey() );

		} catch (UnrecoverableKeyException e) {
		   e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
		   e.printStackTrace();
		} catch (KeyStoreException e) {
		   e.printStackTrace();
		} catch (UnrecoverableEntryException e) {
			e.printStackTrace();
		}

		return null;

	}



	private KeyPair getSigningKeys() {
		InputStream is = null;
		try {
			is = getClass().getResourceAsStream("/test.jks");


			KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
			keystore.load(is, null);

			return getPrivateKey(keystore, "test", "password".toCharArray());


		} catch (Exception e) {
		   e.printStackTrace();
		}

		return null;
	}


	byte [] createDigitalSignature(InputStream dataToSign, PrivateKey privateKey) {
		Signature dsa = null;
		BufferedInputStream bufin = new BufferedInputStream(dataToSign);
		try {
			dsa = Signature.getInstance("SHA1withRSA");
			dsa.initSign(privateKey);


			byte[] buffer = new byte[1024];
			int len;
			while ((len = bufin.read(buffer)) >= 0) {
				dsa.update(buffer, 0, len);
			};
			bufin.close();

			return dsa.sign();

		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				bufin.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return  null;

	}


	private boolean verifySignature(InputStream dataStream, byte[] signature, PublicKey publicKey) {
		BufferedInputStream bufin = new BufferedInputStream(dataStream);


		try {
			Signature sig = Signature.getInstance("SHA1withRSA");
			sig.initVerify(publicKey);

			byte[] buffer = new byte[1024];
			int len;
			while (bufin.available() != 0) {
				len = bufin.read(buffer);
				sig.update(buffer, 0, len);
			};

			return sig.verify(signature);

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (SignatureException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				bufin.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return false;
	}
	@Test
	public void testDocumentSigning() {

		KeyPair keyPair = getSigningKeys();

		InputStream inputStream = getClass().getResourceAsStream("/test.txt");

		byte[] signature = createDigitalSignature(inputStream, keyPair.getPrivate());

		assertTrue("File signatures do not match.", verifySignature(getClass().getResourceAsStream("/test.txt"), signature, keyPair.getPublic()));

	}

}
