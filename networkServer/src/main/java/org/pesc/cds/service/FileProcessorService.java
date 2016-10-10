package org.pesc.cds.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.pesc.cds.domain.Transaction;
import org.pesc.cds.model.TransactionStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.ResourceAccessException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by James Whetstone (jwhetstone@ccctechcenter.org) on 7/19/16.
 */
@Service
public class FileProcessorService {

    private static final Log log = LogFactory.getLog(FileProcessorService.class);

    @Value("${networkServer.ssl.trust-certificates}")
    private Boolean trustCertificates;

    @Value("${networkServer.outbox.path}")
    private String localServerOutboxPath;

    @Value("${networkServer.inbox.path}")
    private String localServerInboxPath;


    @Autowired
    @Qualifier("myRestTemplate")
    private OAuth2RestOperations restTemplate;


    public static final String DEFAULT_DELIVERY_MESSAGE = "Successfully delivered document.";
    public static final String DEFAULT_RECEIVE_MESSAGE = "Received document.";


    public CloseableHttpClient makeHttpClient()  {

        CloseableHttpClient httpclient = null;

        if (trustCertificates == true) {

            try {
                SSLContextBuilder builder = new SSLContextBuilder();
                builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
                SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                        builder.build(), NoopHostnameVerifier.INSTANCE);
                httpclient = HttpClients.custom().setSSLSocketFactory(
                        sslsf).build();
            }
            catch (Exception e) {
                log.error("Failed to create test HTTPS client.");
            }


        }
        else {
            httpclient = HttpClients.createDefault();
        }

        return httpclient;
    }

    public void sendAck(String ackURL, Integer transactionId) {
        // send response back to sending network server

        if (ackURL != null && !ackURL.isEmpty() && transactionId != null) {

            LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
            map.add("transactionId", transactionId.toString());
            map.add("status", TransactionStatus.SUCCESS.name());
            map.add("message", DEFAULT_DELIVERY_MESSAGE);

            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.setContentType(org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED);


            try {
                ResponseEntity<String> response = restTemplate.exchange
                        (ackURL, HttpMethod.POST, new org.springframework.http.HttpEntity<Object>(map, headers), String.class);

                log.debug(response.getStatusCode());
                if (response.getStatusCode() != HttpStatus.OK) {
                    throw new RuntimeException(response.getStatusCode().getReasonPhrase());
                }

            }
            catch(ResourceAccessException e) {

                //Force the OAuth client to retrieve the token again whenever it is used again.
                restTemplate.getOAuth2ClientContext().setAccessToken(null);

                log.error(e);
                throw e;
            }

        }

    }

    /**
     * This asynchronous method is intended to be starting point for modeling and automating the receiver's delivery process.
     * When this method is invoked, the document has already been received and stored in the file system.
     * transaction.getFilePath() returns the location of the file.
     */
    @Async
    public void deliverFile(Transaction transaction){

        //If appropriate add logic here to model your custom file processing logic.

        sendAck(transaction.getAckURL(), transaction.getSenderTransactionId());
    }


    @PreAuthorize("(hasRole('ROLE_ADMIN')")
    public List<String> getInboxDocumentList() {
        List<String> retList = new ArrayList<String>();
        File directory = new File(localServerInboxPath);
        File[] fList = directory.listFiles();
        for (File file : fList) {
            if (file.isFile()){
                retList.add(file.getName());
            }
        }
        return retList;
    }

    @PreAuthorize("(hasRole('ROLE_ADMIN')")
    public List<String> getOutboxDocumentList() {
        List<String> retList = new ArrayList<String>();
        File directory = new File(localServerOutboxPath);
        File[] fList = directory.listFiles();
        for (File file : fList) {
            if (file.isFile()){
                retList.add(file.getName());
            }
        }
        return retList;
    }
}
