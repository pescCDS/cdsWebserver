package org.pesc.cds.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.pesc.cds.domain.Transaction;
import org.pesc.cds.model.TransactionStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * Created by James Whetstone (jwhetstone@ccctechcenter.org) on 7/19/16.
 */
@Service
public class FileProcessorService {

    private static final Log log = LogFactory.getLog(FileProcessorService.class);

    @Value("${networkServer.ssl.trust-certificates}")
    private Boolean trustCertificates;

    public static final String DEFAULT_DELIVERY_MESSAGE = "Successfully delivered document.";
    public static final String DEFAULT_RECEIVE_MESSAGE = "Received document.";


    public CloseableHttpClient makeHttpClient()  {

        CloseableHttpClient httpclient = null;

        if (trustCertificates == true) {

            try {
                SSLContextBuilder builder = new SSLContextBuilder();
                builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
                SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                        builder.build());
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
        CloseableHttpClient client = makeHttpClient();

        try {

            if (ackURL != null && !ackURL.isEmpty() && transactionId != null) {


                HttpPost post = new HttpPost(ackURL);

                post.setEntity(new UrlEncodedFormEntity(
                        Form.form().add("transactionId", transactionId.toString())
                                .add("status", TransactionStatus.SUCCESS.name())
                                .add("message", DEFAULT_DELIVERY_MESSAGE).build()));

                CloseableHttpResponse response = client.execute(post);

                try {
                    log.debug(response.getStatusLine());
                    if (response.getStatusLine().getStatusCode() != 200)  {
                        throw new RuntimeException(response.getStatusLine().toString());
                    }
                    else {
                        HttpEntity resEntity = response.getEntity();
                        if (resEntity != null) {
                            log.debug("Response content length: " + resEntity.getContentLength());
                        }
                        EntityUtils.consume(resEntity);
                    }

                }
                finally {
                    response.close();
                }

            }
        } catch (ClientProtocolException e) {
            log.error(e);
        } catch (IOException e) {
            log.error(e);
        }
        finally {
            try {
                client.close();
            } catch (IOException e) {
                log.error(e);
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
}
