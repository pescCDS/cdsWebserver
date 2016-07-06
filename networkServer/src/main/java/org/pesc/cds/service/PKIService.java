package org.pesc.cds.service;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Enumeration;

/**
 * Created by James Whetstone (jwhetstone@ccctechcenter.org) on 6/1/16.
 */
@Service
public class PKIService {

    @Value("${networkServer.keystore}")
    private String keystoreFile;

    @Value("${networkServer.keystore.password}")
    private String keystorePassword;

    @Value("${networkServer.keystore.signing_key_alias}")
    private String signingKeyAlias;


    public PublicKey convertPEMPublicKey(String pemPublicKey) throws NoSuchAlgorithmException, InvalidKeySpecException {

        pemPublicKey = pemPublicKey.replace("-----BEGIN PUBLIC KEY-----\r\n", "");
        pemPublicKey = pemPublicKey.replace("-----END PUBLIC KEY-----", "");
        byte[] encoded = Base64.decodeBase64(pemPublicKey.getBytes());
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PublicKey pubKey = kf.generatePublic(new X509EncodedKeySpec(encoded));
        return pubKey;

    }

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

    public KeyPair getSigningKeys() {
        InputStream is = null;
        try {
            is = getClass().getResourceAsStream(keystoreFile);


            KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
            keystore.load(is, null);

            return getPrivateKey(keystore, signingKeyAlias, keystorePassword.toCharArray());


        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    public byte [] createDigitalSignature(InputStream dataToSign, PrivateKey privateKey) {
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


    public boolean verifySignature(InputStream dataStream, byte[] signature, PublicKey publicKey) {
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

}
