package org.pesc.cds.domain;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name="transactions", schema="pesc_networkserver")
public class Transaction {
	@Id
	@GeneratedValue
	@Column(name="id")
	private Integer id;
	
	@Column(name="recipient_id")
	private Integer recipientId;

	@Column(name="sender_id")
	private Integer senderId;
	
	@Column(name="file_format")
	private String fileFormat;

	@Column(name="document_type")
	private String documentType;

	@Column(name="department")
	private String department;
	
	@Column(name="file_size")
	private Long fileSize = 0l;
	
	@Column(name="file_path")
	private String filePath;
	
	@Column(name="operation")
	private String operation;
	
	@Column(name="sent")
	private Timestamp sent;
	
	@Column(name="received")
	private Timestamp received;
	
	@Column(name="error", columnDefinition = "text")
	private String error;
	
	@Column(name="status")
	private Boolean status = false;
	
	public Transaction() {}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getRecipientId() {
		return recipientId;
	}

	public void setRecipientId(Integer recipientId) {
		this.recipientId = recipientId;
	}

	public Integer getSenderId() {
		return senderId;
	}

	public void setSenderId(Integer senderId) {
		this.senderId = senderId;
	}

	public String getFileFormat() {
		return fileFormat;
	}

	public void setFileFormat(String fileFormat) {
		this.fileFormat = fileFormat;
	}

	public String getDocumentType() {
		return documentType;
	}

	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public Long getFileSize() {
		return fileSize;
	}

	public void setFileSize(Long fileSize) {
		this.fileSize = fileSize;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public Timestamp getSent() {
		return sent;
	}

	public void setSent(Timestamp sent) {
		this.sent = sent;
	}

	public Timestamp getReceived() {
		return received;
	}

	public void setReceived(Timestamp received) {
		this.received = received;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public Boolean getStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}
}
