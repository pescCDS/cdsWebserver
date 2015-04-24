package org.pesc.cds.networkserver.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pesc.cds.networkserver.domain.Transaction;
import org.pesc.cds.networkserver.domain.TransactionsDao;
import org.pesc.cds.networkserver.service.DatasourceManagerUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class homeController {
	
	private static final Log log = LogFactory.getLog(homeController.class);
	
	
	@RequestMapping("/")
	public String viewHome(Model model) {
		model.addAttribute("transactions", DatasourceManagerUtil.getTransactions().all());
		return "home";
	}
	
	
	/**
	 * Get Transactions REST endpoint<p>
	 * 
	 * 
	 * @param senderId <code>Integer</code> The id of the sending organization
	 * @param status <code>Boolean</code> If the transaction was completed or not
	 * @param from <code>Long</code> 
	 * @param to <code>Long</code> 
	 * @param fetchSize <code></code> 
	 * @return <code>List&lt;Transaction%gt;</code> Transactions matching the passed parameters.
	 */
	@RequestMapping(value="/getTransactions", method=RequestMethod.GET)
	@Produces(MediaType.APPLICATION_JSON)
	@ResponseBody
	public List<Transaction> getTransactions(
			@RequestParam(value="senderId", defaultValue="1", required=false) Integer senderId,
			@RequestParam(value="status", required=false) Boolean status,
			@RequestParam(value="from", required=false) Long from,
			@RequestParam(value="to", required=false) Long to,
			@RequestParam(value="fetchSize", defaultValue="1000", required=false) Long fetchSize
		) {
		List<Transaction> retList = new ArrayList<Transaction>();
		
		log.debug(String.format("incoming parameters: {%n  senderId: %s,%n  status: %s,%n  from: %s,%n  to: %s,%n  fetchSize: %s%n}", senderId, status, from, to, fetchSize));
		
		Long toTimestamp = Calendar.getInstance().getTimeInMillis();// this should be present time
		Calendar fromCal = (Calendar)Calendar.getInstance().clone();
		if(from==null) {
			fromCal.add(Calendar.MONTH, -1);
			log.debug(String.format("From Date: %s", fromCal.getTimeInMillis()));
		} else {
			fromCal.setTimeInMillis(from);
		}
		if(to!=null && to>fromCal.getTimeInMillis()) {
			toTimestamp = to;
		}
		log.debug(String.format("To Date: %s", toTimestamp));
		retList = DatasourceManagerUtil.getTransactions().bySenderStatusDate(senderId, status, fromCal.getTimeInMillis(), toTimestamp, fetchSize);
		log.debug(String.format("Number of Transactions returned: %s", retList.size()));
		return retList;
	}
	
	
	/**
	 * Get Completed endpoint<p>
	 * This is just a shortcut method for the "getTransactions" endpoint with the status set to 1
	 * @return <code>List&lt;Transaction&gt;</code> Transactions with their status set to 1
	 */
	@RequestMapping(value="/getCompleted", method=RequestMethod.GET)
	@Produces(MediaType.APPLICATION_JSON)
	@ResponseBody
	public List<Transaction> getCompleted() {
		// defaults to status=complete, from/to=all
		List<Transaction> retList = new ArrayList<Transaction>();
		retList = DatasourceManagerUtil.getTransactions().bySenderStatusDate(1, true, null, null, -1l);
		return retList;
	}
	
	
	/**
	 * List Files endpoint<p>
	 * TODO finish this
	 * 
	 * @return <code>List&lt;String&gt;</code> A list of paths to files uploaded to the server.
	 */
	@RequestMapping(value="/monitor/listFiles", method=RequestMethod.GET)
	@Produces(MediaType.APPLICATION_JSON)
	@ResponseBody
	public List<String> listFiles() {
		List<String> retList = new ArrayList<String>();
		File directory = new File("/usr/share/tomcat6/temp");
		File[] fList = directory.listFiles();
		for (File file : fList) {
			if (file.isFile()){
				retList.add(file.getName());
			}
		}
		return retList;
	}
	
	
	/*
		recipientId=<destination identifier> Will use the recipientId to send to end point
		file=<multipart file>
		fileFormat=<compliant file format>
		networkServerId=<id of sending network server>
		senderId=<id of sending organization> If not provided then use networkServerId
		fileSize=<float> If not provided then calculate from file parameter
	 */
	/**
	 * sendFile REST endpoint<p>
	 * This is the REST method for sending a file to a network server.
	 * 
	 * @param recipientId     <code>Integer (required)</code> 
	 * @param file            <code>MultipartFile (required)</code> 
	 * @param networkServerId <code>Integer (required)</code> 
	 * @param senderId        <code>Integer</code> 
	 * @param fileFormat      <code>String (required)</code> 
	 * @param fileSize        <code>Long</code>  
	 * @return
	 */
	@RequestMapping(value="/sendFile", method=RequestMethod.POST)
	public ModelAndView handleFileUpload(
    		@RequestParam(value="recipientId", required=true) Integer recipientId, 
    		@RequestParam(value="file") MultipartFile file,
    		@RequestParam(value="networkServerId", required=true) Integer networkServerId,
    		@RequestParam(value="senderId") Integer senderId,
    		@RequestParam(value="fileFormat", required=true) String fileFormat,
    		@RequestParam(value="fileSize", defaultValue="0") Long fileSize,
    		RedirectAttributes redir
    	) {
		
		ModelAndView mav = new ModelAndView("redirect:/");
        
		if (!file.isEmpty()) {
            try {
            	
            	/*
            	 * TODO we need to know how to:
            	 *     save a file to a directory (can be within the webapp for demo)
            	 *     write action to database and put the created Transaction into the model
            	 */
            	
            	//file.getOriginalFilename()
            	
                // save file to local directory
            	byte[] bytes = file.getBytes();
                File f = File.createTempFile("edex_", null);
                BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(f));
                stream.write(bytes);
                stream.close();
                
                
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
                Transaction savedTx = DatasourceManagerUtil.getTransactions().save(tx);
                
                log.debug(String.format(
                	"saved Transaction: {%n  recipientId: %s,%n  networkServerId: %s,%n  senderId: %s,%n  fileFormat: %s%n}",
                	savedTx.getRecipientId(),
                	savedTx.getNetworkServerId(),
                	savedTx.getSenderId(),
                	savedTx.getFileFormat()
                ));
                
                
                redir.addAttribute("error", false);
                redir.addAttribute("status","upload successfull");
                
                // TODO an error will be thrown because this isn't a simple class, need to figure out a workaround
                // redir.addAttribute("transaction", savedTx);
                
                
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
	
	
	// TODO list file:
	// file contents of xaction history DB (will be a file for demo)
}
