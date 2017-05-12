/*
 * Copyright (c) 2017. California Community Colleges Technology Center
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.pesc.cds.service;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

/**
 * Created by James Whetstone (jwhetstone@ccctechcenter.org) on 6/1/16.
 */
@Service
public class PKIService {

    private static final Log log = LogFactory.getLog(PKIService.class);


    @Value("${networkServer.keystore}")
    private String keystoreFile;

    @Value("${networkServer.keystore.password}")
    private String keystorePassword;

    @Value("${networkServer.keystore.signing_key_alias}")
    private String signingKeyAlias;

    @Value("${networkServer.hash.algorithm}")
    private String hashAlgorithm;


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

            KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keystore.getEntry(alias, new KeyStore.PasswordProtection(password));

            Certificate[] cc = privateKeyEntry.getCertificateChain();
            X509Certificate cert = (X509Certificate) cc[0];


            return new KeyPair(cert.getPublicKey(), privateKeyEntry.getPrivateKey() );

        } catch (UnrecoverableKeyException e) {
            log.error("Failed to obtain private key from local keystore.", e);
        } catch (NoSuchAlgorithmException e) {
            log.error("Failed to obtain private key from local keystore.",e);
        } catch (KeyStoreException e) {
            log.error(e);
        } catch (UnrecoverableEntryException e) {
            log.error("Failed to obtain private key from local keystore.",e);
        }

        return null;

    }

    public KeyPair getSigningKeys() {
        InputStream is = null;
        try {

            File f = new java.io.File(keystoreFile);
            if (f.isFile()) {
                is = new FileInputStream(f);
            } else {
                is = getClass().getResourceAsStream(keystoreFile);
            }

            KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
            keystore.load(is, null);

            return getPrivateKey(keystore, signingKeyAlias, keystorePassword.toCharArray());


        } catch (Exception e) {
            log.error("Failed to obtain signing keys from local keystore.", e);
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
            
             log.error("Failed to create digital signature", e);
        }
        finally {
            try {
                bufin.close();
            } catch (IOException e) {
                log.error(e);
            }
        }

        return  null;

    }


    public boolean verifySignature(InputStream dataStream, byte[] signature, PublicKey publicKey) {
        BufferedInputStream bufin = new BufferedInputStream(dataStream);


        try {
            Signature sig = Signature.getInstance(hashAlgorithm);
            sig.initVerify(publicKey);

            byte[] buffer = new byte[1024];
            int len;
            while (bufin.available() != 0) {
                len = bufin.read(buffer);
                sig.update(buffer, 0, len);
            };

            return sig.verify(signature);

        } catch (NoSuchAlgorithmException e) {
           log.error("Failed to verify signature.", e);
        } catch (InvalidKeyException e) {
            log.error("Failed to verify signature.", e);
        } catch (SignatureException e) {
            log.error("Failed to verify signature.", e);
        } catch (IOException e) {
            log.error("Failed to verify signature.", e);
        } finally {
            try {
                bufin.close();
            } catch (IOException e) {
                log.error(e);
            }
        }

        return false;
    }

}
