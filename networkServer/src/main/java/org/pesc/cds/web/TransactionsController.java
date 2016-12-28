package org.pesc.cds.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pesc.cds.domain.PagedData;
import org.pesc.cds.domain.Transaction;
import org.pesc.cds.model.TransactionStatus;
import org.pesc.cds.repository.TransactionService;
import org.pesc.sdk.core.coremain.v1_14.AcknowledgmentCodeType;
import org.pesc.sdk.message.functionalacknowledgement.v1_2.impl.AcknowledgmentImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(value="api/v1/transactions")
public class TransactionsController {
	
	private static final Log log = LogFactory.getLog(TransactionsController.class);


	@Value("${networkServer.ack.path}")
	private String acknowledgementsDirectory;

	@Autowired
	private TransactionService transactionService;

    @Autowired
    private HttpServletResponse servletResponse;


    @RequestMapping(method= RequestMethod.GET)
	@Produces(MediaType.APPLICATION_JSON)
	@ResponseBody
	public List<Transaction> getTransactions(
			@RequestParam(value="senderId", required=false) Integer senderId,
			@RequestParam(value="status", required=false) String status,
            @RequestParam(value="operation", required = false) String operation,
			@RequestParam(value = "delivery-status", required = false) TransactionStatus deliveryStatus,
			@RequestParam(value="from", required=false) String from,
			@RequestParam(value="to", required=false) String to,
			@RequestParam(value = "limit", required = false, defaultValue = "5") Integer limit,
			@RequestParam(value = "offset", required = false, defaultValue = "0") Integer offset
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

		PagedData<Transaction> pagedData = new PagedData<Transaction>(limit,offset);

		pagedData = transactionService.search(senderId, status, operation, deliveryStatus, start, end, pagedData);

        servletResponse.addHeader("X-Total-Count", String.valueOf(pagedData.getTotal()) );

        return pagedData.getData();

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
		PagedData<Transaction> pagedData = transactionService.search(1, "Complete", "Send", null, null, null, new PagedData<Transaction>(20, 0));
		return pagedData.getData();
	}


	/**
	 * An acknowledgement URL that uses the PESC Functional Acknowledgement Standard
	 * @param acknowledgment
	 */
	@RequestMapping(value="/acknowledgement",method= RequestMethod.POST, consumes = { "text/xml"})
	public void markAsReceived(@RequestBody AcknowledgmentImpl acknowledgment) {

		Transaction tx = transactionService.findById(Integer.valueOf(acknowledgment.getTransmissionData().getRequestTrackingID()));
		if(tx!=null) {
			tx.setAcknowledged(true);
			tx.setAcknowledgedAt(new Timestamp(Calendar.getInstance().getTimeInMillis()));

			acknowledgment.getAcknowledgmentData().getAcknowledgmentCode();

			AcknowledgmentCodeType codeType = acknowledgment.getAcknowledgmentData().getAcknowledgmentCode();

			switch (codeType){
				case ACCEPTED:
					tx.setStatus(TransactionStatus.SUCCESS);
					break;
				case REJECTED:
					tx.setStatus(TransactionStatus.FAILURE);
					break;
				default:
					tx.setStatus(TransactionStatus.FAILURE);
			}

			//TODO: persist the acknowledgement
			transactionService.update(tx);

		}


		log.info(acknowledgment.toString());
	}



}
