package org.pesc.cds.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.pesc.cds.domain.Transaction;
import org.pesc.cds.repository.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Controller
@RequestMapping(value="/documents")
public class TransferController {
	
	private static final Log log = LogFactory.getLog(TransferController.class);

	@Value("${directory.server}")
	private String directoryServer;
	@Value("${directory.server.port}")
	private String directortyServerPort;


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

	@Value("${networkServer.file.path}")
	private String localServerFilePath;

	@Autowired
	private TransactionService transactionService;



	/**
	 * receiveFile REST endpoint<p>
	 * This is the REST method for a network server to receive a transaction from another network server.
	 * <ul>
	 *     <li>is this network server's id == recipientId</li>
	 *     <li></li>
	 * </ul>
	 * 
	 * @param recipientId     Will use the recipientId to send to end point 
	 * @param multipartFile            <code>MultipartFile (required)</code>
	 * @param networkServerId id of sending network server 
	 * @param senderId        id of sending organization 
	 * @param fileFormat      compliant file format 
	 * @param fileSize        <code>Long</code>  
	 * @param webServiceUrl   <code>String</code>
	 * @return
	 */
	@RequestMapping(value="/outbox", method= RequestMethod.POST)
	public ModelAndView sendFile(
			HttpServletRequest request,
			@RequestParam(value="recipientId", required=true) Integer recipientId,
			@RequestParam(value="file") MultipartFile multipartFile,
			@RequestParam(value="networkServerId", required=true) Integer networkServerId,
			@RequestParam(value="senderId") Integer senderId,
			@RequestParam(value="fileFormat", required=true) String fileFormat,
			@RequestParam(value="fileSize", defaultValue="0") Long fileSize,
			@RequestParam(value="webServiceUrl", required=true) String webServiceUrl,
			RedirectAttributes redir
		) {

		ModelAndView mav = new ModelAndView("redirect:/transfers");

		if (!multipartFile.isEmpty()) {
	        try {


				File outboxDirectory = new File( request.getServletContext().getRealPath("/") + localServerOutboxPath);
				outboxDirectory.mkdirs();

				File outboxFile = new File(outboxDirectory, multipartFile.getOriginalFilename());
				multipartFile.transferTo(outboxFile);

	        	// write action to database
	            // [RECEIVED FILE] recipientId:p1, neworkServerId:p3, senderId:p4, fileFormat:p5, fileSize:p6
	            Transaction tx = new Transaction();
	            tx.setRecipientId(recipientId);
	            tx.setNetworkServerId(networkServerId);
	            tx.setSenderId(senderId == null ? networkServerId : senderId);
	            tx.setFileFormat(fileFormat);
				tx.setFilePath(outboxFile.getAbsolutePath());
	            tx.setFileSize(multipartFile.getSize());
	            tx.setDirection("SEND");
	            tx.setSent(new Timestamp(Calendar.getInstance().getTimeInMillis()));
	        	
	        	// update response map
	            Transaction savedTx = transactionService.create(tx);
	            
	            log.debug(String.format(
	            	"saved Transaction: {%n  recipientId: %s,%n  networkServerId: %s,%n  senderId: %s,%n  fileFormat: %s%n}",
	            	savedTx.getRecipientId(),
	            	savedTx.getNetworkServerId(),
	            	savedTx.getSenderId(),
	            	savedTx.getFileFormat()
	            ));
	            
	            redir.addAttribute("error", false);
	            redir.addAttribute("status","upload successfull");
	            
	            // send http post to network server
	            CloseableHttpClient client = HttpClients.createDefault();
	            try {
	            	HttpPost post = new HttpPost(webServiceUrl);
	            	
	            	HttpEntity reqEntity = MultipartEntityBuilder.create()
            			.addPart("recipientId", new StringBody(localServerId))
            			.addPart("networkServerId", new StringBody(recipientId.toString()))
            			.addPart("fileFormat", new StringBody(fileFormat))
            			.addPart("transactionId", new StringBody(tx.getId().toString()))
            			.addPart("webServiceUrl", new StringBody(localServerWebServiceURL))
            			.addPart("file", new FileBody(outboxFile))
            			.build();
	            	post.setEntity(reqEntity);

					CloseableHttpResponse response = client.execute(post);

		            try {
						log.debug(response.getStatusLine());
						HttpEntity resEntity = response.getEntity();
						if (resEntity != null) {
							log.debug("Response content length: " + resEntity.getContentLength());
						}
						EntityUtils.consume(resEntity);
					}
					finally {
						response.close();
					}


	            	
	            } finally {
	            	client.close();
	            }
	            
	            
	        } catch (Exception e) {
				log.error(e);
	        	redir.addAttribute("error", true);
	        	redir.addAttribute("status", String.format("upload failed: %s", e.getMessage()));
	        }
	    } else {

	    	redir.addAttribute("error", true);
	    	redir.addAttribute("status", "missing file");
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
	 * @param webServiceUrl  This is the url to the network server that we will send the response back to
	 */
	@RequestMapping(value="/inbox", method= RequestMethod.POST)
	public void receiveFile(
			HttpServletRequest request,
			@RequestParam(value="recipientId", required=false) Integer recipientId,
			@RequestParam(value="networkServerId", required=false) Integer networkServerId,
			@RequestParam(value="file") MultipartFile multipartFile,
			@RequestParam(value="fileFormat", required=false) String fileFormat,
			@RequestParam(value="transactionId", required=false) Integer transactionId,
			@RequestParam(value="webServiceUrl", required=false) String webServiceUrl
		) {
		
		log.debug(String.format("received file from network server " + recipientId));

		Transaction tx = new Transaction();
		// we need the directoryId for this organization in the organizations table
		tx.setRecipientId(networkServerId);
        tx.setNetworkServerId(recipientId);
        tx.setFileFormat(fileFormat);
        tx.setFileSize(multipartFile.getSize());
        tx.setDirection("RECEIVE");
        tx.setReceived(new Timestamp(Calendar.getInstance().getTimeInMillis()));
        tx.setStatus(true);
		
        Transaction savedTx = transactionService.create(tx);

		File inboxDirectory = new File(request.getServletContext().getRealPath("/") + localServerInboxPath);
		inboxDirectory.mkdirs();


		try {


			File f =  new File(inboxDirectory, savedTx.getId().toString()+"-"+multipartFile.getOriginalFilename());


			byte[] bytes = multipartFile.getBytes();

	    	File fp = f.getParentFile();
	    	if(!fp.exists() && !fp.mkdirs()) {
	    		tx.setError("Could not create directory: " + fp);
			} else {
				try {
					if(!f.createNewFile()) {
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
			tx.setError(ex.getMessage());
		}

		transactionService.update(tx);
		
		// send response back to sending network server
		try {
			Request.Post(webServiceUrl).bodyForm(Form.form().add("transactionId", transactionId.toString()).build()).execute().returnContent();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
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
	public List<String> listFilesFromOutbox(HttpServletRequest request) {
		List<String> retList = new ArrayList<String>();
		File directory = new File(request.getServletContext().getRealPath("/") + localServerOutboxPath);
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
	public List<String> listFilesFromInbox(HttpServletRequest request) {
		List<String> retList = new ArrayList<String>();
		File directory = new File(request.getServletContext().getRealPath("/") + localServerInboxPath);
		File[] fList = directory.listFiles();
		for (File file : fList) {
			if (file.isFile()){
				retList.add(file.getName());
			}
		}
		return retList;
	}
}
