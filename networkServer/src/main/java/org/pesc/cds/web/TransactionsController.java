package org.pesc.cds.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
import org.springframework.web.bind.annotation.ResponseBody;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Controller
public class TransactionsController {
	
	private static final Log log = LogFactory.getLog(TransactionsController.class);

	@Autowired
	private TransactionsDao transactionsDao;

	
	/**
	 * Get Transactions REST endpoint<p>
	 * TODO: add direction, received, sent
	 * 
	 * @param senderId <code>Integer</code> The id of the sending organization
	 * @param status <code>Boolean</code> If the transaction was completed or not
	 * @param from <code>Long</code> 
	 * @param to <code>Long</code> 
	 * @param fetchSize <code></code> 
	 * @return <code>List&lt;Transaction%gt;</code> Transactions matching the passed parameters.
	 */
	@RequestMapping(value="/getTransactions", method= RequestMethod.GET)
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
		retList = transactionsDao.bySenderStatusDate(senderId, status, fromCal.getTimeInMillis(), toTimestamp, fetchSize);
		log.debug(String.format("Number of Transactions returned: %s", retList.size()));
		return retList;
	}
	
	
	/**
	 * Get Completed endpoint<p>
	 * This is just a shortcut method for the "getTransactions" endpoint with the status set to 1
	 * @return <code>List&lt;Transaction&gt;</code> Transactions with their status set to 1
	 */
	@RequestMapping(value="/getCompleted", method= RequestMethod.GET)
	@Produces(MediaType.APPLICATION_JSON)
	@ResponseBody
	public List<Transaction> getCompleted() {
		// defaults to status=complete, from/to=all
		List<Transaction> retList = new ArrayList<Transaction>();
		retList = transactionsDao.bySenderStatusDate(1, true, null, null, -1l);
		return retList;
	}
	
	
	/**
	 * List Files endpoint<p>
	 * TODO finish this
	 * 
	 * @return <code>List&lt;String&gt;</code> A list of paths to files uploaded to the server.
	 */
	@RequestMapping(value="/monitor/listFiles", method= RequestMethod.GET)
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
}
