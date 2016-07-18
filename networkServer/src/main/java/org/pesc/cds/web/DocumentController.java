package org.pesc.cds.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.pesc.cds.domain.Transaction;
import org.pesc.cds.model.TransactionStatus;
import org.pesc.cds.repository.TransactionService;
import org.pesc.cds.service.PKIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import java.io.*;
import java.security.PublicKey;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@RestController
@RequestMapping(value="api/v1/documents")
public class DocumentController {

    private static final Log log = LogFactory.getLog(DocumentController.class);

    @Value("${directory.server.base.url}")
    private String directoryServer;

    @Value("${networkServer.id}")
    private String localServerId;

    @Value("${networkServer.name}")
    private String localServerName;

    @Value("${networkServer.subcode}")
    private String localServerSubcode;

    @Value("${networkServer.ein}")
    private String localServerEIN;

    @Value("${networkServer.webServiceURL}")
    private String localServerWebServiceURL;

    @Value("${networkServer.outbox.path}")
    private String localServerOutboxPath;

    @Value("${networkServer.inbox.path}")
    private String localServerInboxPath;

    @Value("${api.organization}")
    private String organizationApiPath;


    @Value("${api.endpoints}")
    private String endpointsApiPath;


    @Value("${api.public_key}")
    private String publicKeyApiPath;

    @Value("${networkServer.ssl.trust-certificates}")
    private Boolean trustCertificates;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    PKIService pkiService;



    private CloseableHttpClient makeHttpClient()  {

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


    @RequestMapping(value="/send", method= RequestMethod.GET)
    @ResponseBody
    @Produces(MediaType.APPLICATION_JSON)
    public Transaction sendDocument(@RequestParam("transaction_id")Integer tranID) {

        Transaction tx = transactionService.findById(tranID);

        Transaction tran = new Transaction();
        tran.setOperation(tx.getOperation());
        tran.setFileFormat(tx.getFileFormat());
        tran.setDepartment(tx.getDepartment());
        tran.setDocumentType(tx.getDocumentType());
        tran.setFilePath(tx.getFilePath());
        tran.setRecipientId(tx.getRecipientId());
        tran.setSenderId(tx.getSenderId());
        tran.setFileSize(tx.getFileSize());
        tran.setOccurredAt(new Timestamp(Calendar.getInstance().getTimeInMillis()));


        tran = transactionService.create(tran);

        String endpointURI = getEndpointForOrg(tran.getRecipientId(), tran.getFileFormat(), tran.getDocumentType(), tran.getDepartment());

        if (endpointURI == null) {
            tran.setError("No endpoint found for this organization and document type.");
            transactionService.update(tran);
            throw new RuntimeException("No endpoint found for this organization and document type.");
        }
        // send http post to network server
        CloseableHttpClient client = makeHttpClient();
        try {

            HttpPost post = new HttpPost(endpointURI);

            File outboxFile = new File(tran.getFilePath());
            byte[] fileSignature = pkiService.createDigitalSignature(new FileInputStream(tran.getFilePath()), pkiService.getSigningKeys().getPrivate());

            ContentBody signature = new ByteArrayBody(fileSignature, "signature.dat");

            HttpEntity reqEntity = MultipartEntityBuilder.create()
                    .addPart("recipient_id", new StringBody(tran.getRecipientId().toString()))
                    .addPart("sender_id", new StringBody(tran.getSenderId().toString()))
                    .addPart("file_format", new StringBody(tran.getFileFormat()))
                    .addPart("document_type", new StringBody(tran.getDocumentType()))
                    .addPart("department", new StringBody(tran.getDepartment()))
                    .addPart("transaction_id", new StringBody(tran.getId().toString()))
                    .addPart("ack_url", new StringBody(localServerWebServiceURL))
                    .addPart("file", new FileBody(outboxFile))
                    .addPart("signature", signature)
                    .build();
            post.setEntity(reqEntity);

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

                    return tran;
                }


            }
            finally {
                response.close();
            }

        } catch (Exception e) {
            tran.setError(e.getMessage());
            transactionService.update(tran);
            log.error(e);
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                log.error(e);
            }
        }

        return tran;
    }

    private String getEndpointForOrg(int orgID, String documentFormat, String documentType, String department) {
        StringBuilder uri = new StringBuilder(directoryServer + endpointsApiPath);
        uri.append("?organizationId=").append(orgID).append("&documentFormat=").append(documentFormat).append("&documentType=").append(documentType).append("&enabled=true").append("&mode=LIVE")
                .append("&department=").append(department);

        CloseableHttpClient client = HttpClients.custom().build();
        String endpointURI = null;
        try {
            HttpGet get = new HttpGet(uri.toString());
            get.setHeader(HttpHeaders.ACCEPT, "application/json");
            CloseableHttpResponse response = client.execute(get);
            try {

                HttpEntity resEntity = response.getEntity();
                if (response.getStatusLine().getStatusCode() == 200 && resEntity != null) {
                    JSONArray endpoints = new JSONArray(EntityUtils.toString(resEntity));
                    if (endpoints.length() > 0) {

                        if (endpoints.length() != 1) {
                            throw new RuntimeException("More than one endpoint was found that fits the given criteria.");
                        }
                        endpointURI = endpoints.getJSONObject(0).getString("address");
                        log.debug(endpoints.toString(3));
                    }
                }
                EntityUtils.consume(resEntity);
            }
            finally {
                response.close();
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                client.close();
            }
            catch (IOException e) {

            }
        }
        return endpointURI;
    }

    private String getPEMPublicKeyByOrgID(int orgID) {
        StringBuilder uri = new StringBuilder(directoryServer + "/services/rest/v1/organizations/" + orgID + "/public-key");

        CloseableHttpClient client = HttpClients.custom().build();
        String pemPublicKey = null;
        try {
            HttpGet get = new HttpGet(uri.toString());
            get.setHeader(HttpHeaders.ACCEPT, "text/html");
            CloseableHttpResponse response = client.execute(get);
            try {

                HttpEntity resEntity = response.getEntity();
                if (response.getStatusLine().getStatusCode() == 200 && resEntity != null) {
                    pemPublicKey = EntityUtils.toString(resEntity);
                }
                EntityUtils.consume(resEntity);
            }
            finally {
                response.close();
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                client.close();
            }
            catch (IOException e) {

            }
        }
        return pemPublicKey;
    }


    private String getEndpointURIForSchool(String schoolCode, String schoolCodeType, String documentFormat, String documentType, String department, Transaction tx) {

        int orgID = getOrganizationId(schoolCode, schoolCodeType);
        tx.setRecipientId(orgID);
        return getEndpointForOrg(orgID, documentFormat, documentType, department);
    }

    private int getOrganizationId(String schoolCode, String schoolCodeType) {

        int orgID = 0;

        StringBuilder uri = new StringBuilder(directoryServer + organizationApiPath);
        uri.append("?organizationCodeType=").append(schoolCodeType).append("&organizationCode=").append(schoolCode);
        CloseableHttpClient client = HttpClients.custom().build();
        try {
            HttpGet get = new HttpGet(uri.toString());
            get.setHeader(HttpHeaders.ACCEPT, "application/json");
            CloseableHttpResponse response = client.execute(get);

            try {

                HttpEntity resEntity = response.getEntity();
                if (response.getStatusLine().getStatusCode() == 200 && resEntity != null) {
                    JSONArray organizations = new JSONArray(EntityUtils.toString(resEntity));
                    if (organizations.length() == 1) {
                        orgID = organizations.getJSONObject(0).getInt("id");

                        log.debug("Getting endpoint for org");
                    }
                }
                EntityUtils.consume(resEntity);
            }
            finally {
                response.close();
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                client.close();
            }
            catch (IOException e) {

            }

        }
        return orgID;
    }


    /**
     *
     * @param multipartFile
     * @param fileFormat
     * @param documentType
     * @param department
     * @param schoolCode
     * @param schoolCodeType
     * @param redir
     * @return
     */
    @RequestMapping(value="/outbox", method= RequestMethod.POST)
    public ModelAndView sendFile(
            @RequestParam(value="file") MultipartFile multipartFile,
            @RequestParam(value="file_format", required=true) String fileFormat,
            @RequestParam(value="document_type", required=false) String documentType,
            @RequestParam(value="department", required=false) String department,
            @RequestParam(value="school_code", required=true) String schoolCode,
            @RequestParam(value="school_code_type", required=true) String schoolCodeType,
            RedirectAttributes redir
    ) {

        ModelAndView mav = new ModelAndView("redirect:/upload-status");

        Transaction tran = null;

        if (!multipartFile.isEmpty()) {
            try {

                Transaction tx = new Transaction();
                String endpointURI = getEndpointURIForSchool(schoolCode, schoolCodeType, fileFormat, documentType, department, tx);

                if (endpointURI == null) {
                    throw new IllegalArgumentException("No endpoint URI exists for the given school and document format.");
                }

                File outboxDirectory = new File(localServerOutboxPath);
                outboxDirectory.mkdirs();

                File outboxFile = new File(outboxDirectory, multipartFile.getOriginalFilename());
                multipartFile.transferTo(outboxFile);

                tx.setSenderId( Integer.valueOf(localServerId) );
                tx.setFileFormat(fileFormat);
                tx.setFilePath(outboxFile.getAbsolutePath());
                tx.setFileSize(multipartFile.getSize());
                tx.setDocumentType(documentType);
                tx.setDepartment(department);
                tx.setOperation("SEND");
                tx.setOccurredAt(new Timestamp(Calendar.getInstance().getTimeInMillis()));

                // update response map
                tran = transactionService.create(tx);

                log.debug(String.format(
                        "saved Transaction: {%n  recipientId: %s,%n  senderId: %s,%n  fileFormat: %s%n}",
                        tran.getRecipientId(),
                        tran.getSenderId(),
                        tran.getFileFormat()
                ));

                redir.addFlashAttribute("error", false);
                redir.addFlashAttribute("status", "Upload successfull");

                byte[] fileSignature = pkiService.createDigitalSignature(new FileInputStream(outboxFile), pkiService.getSigningKeys().getPrivate());

                ContentBody signature = new ByteArrayBody(fileSignature, "signature.dat");

                // send http post to network server
                CloseableHttpClient client = makeHttpClient();
                try {
                    HttpPost post = new HttpPost(endpointURI);

                    HttpEntity reqEntity = MultipartEntityBuilder.create()
                            .addPart("recipient_id", new StringBody(tx.getRecipientId().toString()))
                            .addPart("sender_id", new StringBody(localServerId))
                            .addPart("file_format", new StringBody(fileFormat))
                            .addPart("document_type", new StringBody(documentType))
                            .addPart("department", new StringBody(department))
                            .addPart("transaction_id", new StringBody(tx.getId().toString()))
                            .addPart("ack_url", new StringBody(localServerWebServiceURL))
                            .addPart("file", new FileBody(outboxFile))
                            .addPart("signature", signature)
                            .build();
                    post.setEntity(reqEntity);

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

                } finally {
                    try {
                        client.close();
                    } catch (IOException e) {
                        log.error(e);
                    }
                }


            } catch (Exception e) {

                tran.setError(e.getMessage());
                transactionService.update(tran);

                log.error(e);

            }

        } else {

            redir.addFlashAttribute("error", true);
            redir.addFlashAttribute("status", "Missing file");
        }

        return mav;
    }


    /**
     * When another network server sends a file
     *
     * @param recipientId    In this case this is the network server that we need to send the response to
     * @param multipartFile           The transferred file
     * @param fileFormat     The expected format of the file
     * @param transactionId  This is the identifier of the transaction record from the sending network server, we send it back
     * @param ackURL  This is the url to the network server that we will send the response back to
     */
    @RequestMapping(value="/inbox", method= RequestMethod.POST)
    public void receiveFile(
            @RequestParam(value="recipient_id", required=false) Integer recipientId,
            @RequestParam(value="sender_id", required=false) Integer senderId,
            @RequestParam(value="file") MultipartFile multipartFile,
            @RequestParam(value="signature") MultipartFile signatureFile,
            @RequestParam(value="file_format", required=false) String fileFormat,
            @RequestParam(value="document_type", required=false) String documentType,
            @RequestParam(value="department", required=false) String department,
            @RequestParam(value="transaction_id", required=false) Integer transactionId,
            @RequestParam(value="ack_url", required=false) String ackURL,
            HttpServletRequest request
    ) {

        log.info(request.getRequestURI().toString());

        log.debug(String.format("received file from network server " + recipientId));


        if (multipartFile == null || signatureFile == null){
            log.error("Incorrect number of file uploaded.  Is the digital signature file present?");
            throw new WebApplicationException("A file and it's digital signature are required.");
        }
        Transaction tx = new Transaction();
        // we need the directoryId for this organization in the organizations table
        tx.setRecipientId(recipientId);
        tx.setSenderId(senderId);
        tx.setFileFormat(fileFormat);
        tx.setFileSize(multipartFile.getSize());
        tx.setDepartment(department);
        tx.setDocumentType(documentType);
        tx.setOperation("RECEIVE");
        Timestamp occurredAt = new Timestamp(Calendar.getInstance().getTimeInMillis());
        tx.setOccurredAt(occurredAt);
        tx.setAcknowledgedAt(occurredAt);
        tx.setAcknowledged(true);

        Transaction savedTx = transactionService.create(tx);

        File inboxDirectory = new File(localServerInboxPath);
        inboxDirectory.mkdirs();


        try {

            File f =  new File(inboxDirectory, savedTx.getId().toString()+"-"+multipartFile.getOriginalFilename());


            byte[] bytes = multipartFile.getBytes();

            String pemPublicKey = getPEMPublicKeyByOrgID(senderId);


            if (pemPublicKey == null) {
                throw new IllegalArgumentException("The sender's signing certificate was invalid or non existent.  File discarded.");
            }


            PublicKey senderPublicKey = pkiService.convertPEMPublicKey(pemPublicKey);


            if ( true == pkiService.verifySignature(signatureFile.getInputStream(),signatureFile.getBytes(), senderPublicKey)) {
                throw new WebApplicationException("Invalid digital signature found.  File discarded.");
            }


            File fp = f.getParentFile();
            if(!fp.exists() && !fp.mkdirs()) {
                tx.setError("Could not create directory: " + fp);
            } else {
                try {
                    if (!f.createNewFile()) {
                        tx.setError(String.format("file %s already exists", multipartFile.getOriginalFilename()));
                    } else {
                        tx.setFilePath(f.getPath());
                        BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(f));
                        stream.write(bytes);
                        stream.close();
                    }


                } catch(IOException ioex) {
                    tx.setError(ioex.getMessage());
                }
            }
        } catch(Exception ex) {
            log.error(ex);
            tx.setError(ex.getMessage() == null ? ex.getClass().getSimpleName() : ex.getMessage());
        }

        transactionService.update(tx);

        // send response back to sending network server
        CloseableHttpClient client = makeHttpClient();

        try {

            if (ackURL != null && !ackURL.isEmpty() && transactionId != null) {


                HttpPost post = new HttpPost(ackURL);

                post.setEntity(new UrlEncodedFormEntity(
                        Form.form().add("transactionId", transactionId.toString())
                                .add("status", TransactionStatus.SUCCESS.name())
                                .add("message", "Successfully delivered document.").build()));

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
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
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
     * List Files endpoint<p>
     * TODO finish this
     *
     * @return <code>List&lt;String&gt;</code> A list of paths to files uploaded to the server.
     */
    @RequestMapping(value="/outbox", method= RequestMethod.GET)
    @Produces(MediaType.APPLICATION_JSON)
    @ResponseBody
    public List<String> listFilesFromOutbox() {
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


    /**
     * List Files endpoint<p>
     * TODO finish this
     *
     * @return <code>List&lt;String&gt;</code> A list of paths to files uploaded to the server.
     */
    @RequestMapping(value="/inbox", method= RequestMethod.GET)
    @Produces(MediaType.APPLICATION_JSON)
    @ResponseBody
    public List<String> listFilesFromInbox() {
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
}
