package org.pesc.cds.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pesc.cds.domain.PagedData;
import org.pesc.cds.domain.Transaction;
import org.pesc.cds.model.DocumentFormat;
import org.pesc.cds.model.DocumentType;
import org.pesc.cds.model.TransactionStatus;
import org.pesc.cds.repository.TransactionService;
import org.pesc.sdk.core.coremain.v1_14.AcknowledgmentCodeType;
import org.pesc.sdk.message.functionalacknowledgement.v1_2.SyntaxErrorType;
import org.pesc.sdk.message.functionalacknowledgement.v1_2.impl.AcknowledgmentImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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

	private File ackDir;

	@Autowired
	private TransactionService transactionService;

    @Autowired
    private HttpServletResponse servletResponse;

    public TransactionsController(Environment env){
        ackDir = new File(env.getProperty("networkServer.ack.path"));
        ackDir.mkdirs();
    }

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

        servletResponse.addHeader("X-Total-Count", String.valueOf(pagedData.getTotal()));

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

	@ExceptionHandler
	void handleIllegalArgumentException(IllegalArgumentException e, HttpServletResponse response) throws IOException {
		response.sendError(HttpStatus.BAD_REQUEST.value());
	}


	/**
	 * An acknowledgement URL that uses the PESC Functional Acknowledgement Standard
	 * @param acknowledgment
	 */
	@RequestMapping(value="/acknowledgement",method= RequestMethod.POST)
	public ResponseEntity<String> acknowledgement(@RequestBody AcknowledgmentImpl acknowledgment) throws IOException {

        if (acknowledgment.getTransmissionData().getRequestTrackingID()  == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A request tracking ID (transaction ID) is required in the acknowledgement.");
        }

        //TODO: if better performance is required, change the request body to string and marshal it manually.  This
        //should eliminate an extra unmarshal that occurs before this method is invoked.
        File ackFile = File.createTempFile("ack-", ".xml", ackDir);
        BufferedOutputStream stream2 = new BufferedOutputStream(new FileOutputStream(ackFile));
        String xml = transactionService.toXml(acknowledgment);
        stream2.write(xml.getBytes("UTF-8"));
        stream2.close();

		Transaction tx = null;

		try {
			transactionService.findById(Integer.valueOf(acknowledgment.getTransmissionData().getRequestTrackingID()));
		}
		catch (Exception e){
			log.error( String.format("Failed to retrieve transaction with id (%s)", acknowledgment.getTransmissionData().getRequestTrackingID()), e);
		}
		if(tx!=null) {
			tx.setAcknowledged(true);
			tx.setAcknowledgedAt(new Timestamp(Calendar.getInstance().getTimeInMillis()));
            tx.setAckFilePath(ackFile.getAbsolutePath());

			acknowledgment.getAcknowledgmentData().getAcknowledgmentCode();

			AcknowledgmentCodeType codeType = acknowledgment.getAcknowledgmentData().getAcknowledgmentCode();

			switch (codeType){
				case ACCEPTED:
					tx.setStatus(TransactionStatus.SUCCESS);
					break;
				case REJECTED:
				default:
					tx.setStatus(TransactionStatus.FAILURE);
					StringBuilder buf = new StringBuilder();
					for(SyntaxErrorType error: acknowledgment.getAcknowledgmentData().getSyntaxErrors()){
						buf.append(String.format("%s %s Column Number: %d Line Number: %d%n", error.getErrorMessage(), error.getSeverityCode().value(), error.getLocator().getColumnNumber(), error.getLocator().getLineNumber()));
					}
					tx.setError(buf.toString());
			}

			transactionService.update(tx);
		}
		else {
			Transaction tran = new Transaction();

            if (acknowledgment.getTransmissionData().getDestination().getOrganization().getMutuallyDefined() != null) {
                tran.setRecipientId(Integer.valueOf(acknowledgment.getTransmissionData().getDestination().getOrganization().getMutuallyDefined()));
            }
            else {
                tran.setRecipientId(0);
            }

            if (acknowledgment.getTransmissionData().getSource().getOrganization().getMutuallyDefined() != null) {
                tran.setSenderId(Integer.valueOf(acknowledgment.getTransmissionData().getSource().getOrganization().getMutuallyDefined()));
            }
            else {
                tran.setSenderId(0);
            }

            tran.setSignerId(0);


            tran.setFilePath(ackFile.getAbsolutePath());
            tran.setFileFormat(DocumentFormat.PESCXML.getFormatName());
            tran.setFileSize(Long.valueOf(xml.length()));
            tran.setDepartment("");
            tran.setDocumentType(DocumentType.FUNCTIONAL_ACKNOWLEDGEMENT.getDocumentName());
            tran.setOperation("RECEIVE");
            Timestamp occurredAt = new Timestamp(Calendar.getInstance().getTimeInMillis());
            tran.setOccurredAt(occurredAt);
            tran.setAcknowledged(false);
            tran.setStatus(TransactionStatus.FAILURE);
            tran.setError(String.format("A acknowledgement was received, but the request tracking id (%s) was not found.", acknowledgment.getTransmissionData().getRequestTrackingID()));

            transactionService.create(tran);

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(String.format("Invalid request tracking ID found %s.", acknowledgment.getTransmissionData().getRequestTrackingID()));
		}


		log.info(acknowledgment.toString());

        return ResponseEntity.ok().body(null);
	}

}
