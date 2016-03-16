package org.pesc.cds.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.pesc.cds.domain.Transaction;
import org.pesc.cds.domain.TransactionsDao;
import org.pesc.cds.service.DatasourceManagerUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.Calendar;

@Controller
public class TransferController {
	
	private static final Log log = LogFactory.getLog(TransferController.class);

	@Value("${directory.server}")
	private String directoryServer;
	@Value("${directory.server.port}")
	private String directortyServerPort;


	@Autowired
	private TransactionsDao transactionsDao;


	@RequestMapping({ "/transfer" })
	public String viewHome(Model model) {
		model.addAttribute("directoryServer", directoryServer + ":" + directortyServerPort);

		return "transfer";
	}



	/**
	 * receiveFile REST endpoint<p>
	 * This is the REST method for a network server to receive a transaction from another network server.
	 * <ul>
	 *     <li>is this network server's id == recipientId</li>
	 *     <li></li>
	 * </ul>
	 * 
	 * @param recipientId     Will use the recipientId to send to end point 
	 * @param file            <code>MultipartFile (required)</code> 
	 * @param networkServerId id of sending network server 
	 * @param senderId        id of sending organization 
	 * @param fileFormat      compliant file format 
	 * @param fileSize        <code>Long</code>  
	 * @param webServiceUrl   <code>String</code>
	 * @return
	 */
	@RequestMapping(value="/sendFile", method= RequestMethod.POST)
	public ModelAndView sendFile(
			@RequestParam(value="recipientId", required=true) Integer recipientId,
			@RequestParam(value="file") MultipartFile file,
			@RequestParam(value="networkServerId", required=true) Integer networkServerId,
			@RequestParam(value="senderId") Integer senderId,
			@RequestParam(value="fileFormat", required=true) String fileFormat,
			@RequestParam(value="fileSize", defaultValue="0") Long fileSize,
			@RequestParam(value="webServiceUrl", required=true) String webServiceUrl,
			RedirectAttributes redir
		) {
		
		ModelAndView mav = new ModelAndView("redirect:/transfer");

		if (!file.isEmpty()) {
	        try {
	        	
	        	// write action to database
	            // [RECEIVED FILE] recipientId:p1, neworkServerId:p3, senderId:p4, fileFormat:p5, fileSize:p6
	            Transaction tx = new Transaction();
	            tx.setRecipientId(recipientId);
	            tx.setNetworkServerId(networkServerId);
	            tx.setSenderId(senderId==null ? networkServerId : senderId);
	            tx.setFileFormat(fileFormat);
	            tx.setFileSize(file.getSize());
	            tx.setDirection("SEND");
	            tx.setSent(new Timestamp(Calendar.getInstance().getTimeInMillis()));
	        	
	        	// update response map
	            Transaction savedTx = transactionsDao.save(tx);
	            
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
            			.addPart("recipientId", new StringBody(DatasourceManagerUtil.getIdentification().getId().toString()))
            			.addPart("networkServerId", new StringBody(recipientId.toString()))
            			.addPart("fileFormat", new StringBody(fileFormat))
            			.addPart("transactionId", new StringBody(tx.getId().toString()))
            			.addPart("webServiceUrl", new StringBody(DatasourceManagerUtil.getIdentification().getWebServiceUrl()))
            			.addPart("file", new FileBody((File)file))
            			.build();
	            	post.setEntity(reqEntity);
	            	
	            	client.execute(post);
	            	
	            } finally {
	            	client.close();
	            }
	            
	            
	        } catch (Exception e) {
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
	 * @param file           The transferred file
	 * @param fileFormat     The expected format of the file
	 * @param transactionId  This is the identifier of the transaction record from the sending network server, we send it back
	 * @param webServiceUrl  This is the url to the network server that we will send the response back to
	 */
	@RequestMapping(value="/receiveFile", method= RequestMethod.POST)
	public void receiveFile(
			@RequestParam(value="recipientId", required=true) Integer recipientId,
			@RequestParam(value="networkServerId", required=true) Integer networkServerId,
			@RequestParam(value="file") MultipartFile file,
			@RequestParam(value="fileFormat", required=true) String fileFormat,
			@RequestParam(value="transactionId", required=true) Integer transactionId,
			@RequestParam(value="webServiceUrl", required=true) String webServiceUrl
		) {
		
		log.debug(String.format("received file from network server " + recipientId));
		
		Transaction tx = new Transaction();
		// we need the directoryId for this organization in the organizations table
		tx.setRecipientId(networkServerId);
        tx.setNetworkServerId(recipientId);
        tx.setFileFormat(fileFormat);
        tx.setFileSize(file.getSize());
        tx.setDirection("RECEIVE");
        tx.setReceived(new Timestamp(Calendar.getInstance().getTimeInMillis()));
        tx.setStatus(true);
		
        Transaction savedTx = transactionsDao.save(tx);
        
        //save file to network server save directory
        Path path = Paths.get( String.format("%s/%s/%s", DatasourceManagerUtil.getSystemProperties().getFilePath(), tx.getId(), file.getOriginalFilename()) );
		try {
	        byte[] bytes = file.getBytes();
	    	File f = new File( path.toString() );
	    	File fp = f.getParentFile();
	    	if(!fp.exists() && !fp.mkdirs()) {
	    		tx.setError("Could not create directory: " + fp);
			} else {
				try {
					if(!f.createNewFile()) {
						tx.setError(String.format("file %s already exists", file.getOriginalFilename()));
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
		
		transactionsDao.save(tx);
		
		// send response back to sending network server
		try {
			Request.Post(webServiceUrl).bodyForm(Form.form().add("transactionId", transactionId.toString()).build()).execute().returnContent();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value="/response", method= RequestMethod.POST)
	public void acceptResponse(@RequestParam(value="transactionId", required=true) Integer transactionId) {
		Transaction tx = transactionsDao.byId(transactionId);
		if(tx!=null) {
			tx.setStatus(true);
			tx.setReceived(new Timestamp(Calendar.getInstance().getTimeInMillis()));
			transactionsDao.save(tx);
		}
	}
}
