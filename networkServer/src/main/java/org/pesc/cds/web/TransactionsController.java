package org.pesc.cds.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.pesc.cds.domain.Transaction;
import org.pesc.cds.repository.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@RestController
@RequestMapping(value="api/v1/transactions")
public class TransactionsController {
	
	private static final Log log = LogFactory.getLog(TransactionsController.class);

	@Autowired
	private TransactionService transactionService;

	
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
	@RequestMapping(method= RequestMethod.GET)
	@Produces(MediaType.APPLICATION_JSON)
	@ResponseBody
	public List<Transaction> getTransactions(
			@RequestParam(value="senderId", required=false) Integer senderId,
			@RequestParam(value="status", required=false) Boolean status,
			@RequestParam(value="from", required=false) Long from,
			@RequestParam(value="to", required=false) Long to,
			@RequestParam(value="fetchSize", defaultValue="1000", required=false) Long fetchSize
		) {
		List<Transaction> retList = new ArrayList<Transaction>();
		
		log.debug(String.format("incoming parameters: {%n  senderId: %s,%n  status: %s,%n  from: %s,%n  to: %s,%n  fetchSize: %s%n}", senderId, status, from, to, fetchSize));
		

		retList = transactionService.search(senderId, status, from, to, fetchSize);
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
		retList = transactionService.search(1, true, null, null, -1l);
		return retList;
	}



	@RequestMapping(method= RequestMethod.POST)
	public void markAsReceived(@RequestParam(value="transactionId", required=true) Integer transactionId) {
		Transaction tx = transactionService.findById(transactionId);
		if(tx!=null) {
			tx.setStatus(true);
			tx.setReceived(new Timestamp(Calendar.getInstance().getTimeInMillis()));
			transactionService.update(tx);
		}
	}



}
