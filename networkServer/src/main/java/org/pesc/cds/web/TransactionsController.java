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
import javax.ws.rs.DefaultValue;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(value="api/v1/transactions")
public class TransactionsController {
	
	private static final Log log = LogFactory.getLog(TransactionsController.class);

	@Autowired
	private TransactionService transactionService;

	

	@RequestMapping(method= RequestMethod.GET)
	@Produces(MediaType.APPLICATION_JSON)
	@ResponseBody
	public List<Transaction> getTransactions(
			@RequestParam(value="senderId", required=false) Integer senderId,
			@RequestParam(value="status", required=false) String status,
			@RequestParam(value="from", required=false) String from,
			@RequestParam(value="to", required=false) String to,
			@RequestParam(value = "limit", required = false, defaultValue = "5") Long limit,
			@RequestParam(value = "offset", required = false, defaultValue = "0") Long offset
		) {

		log.debug(String.format("incoming parameters: {%n  senderId: %s,%n  status: %s,%n  from: %s,%n  to: %s%n}", senderId, status, from, to));

		Date start = null;
		Date end = null;
		if (to != null && from != null) {

			try {
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				start = format.parse(from);
				end = format.parse(to);
			}
			catch (ParseException e) {
				log.error("Failed to convert to and from dates.", e);
			}


		}

		return transactionService.search(senderId, status, start, end, limit, offset);
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
		retList = transactionService.search(1, "Complete", null, null, 20L, 0L);
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
