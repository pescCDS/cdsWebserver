/*
 * Copyright (c) 2017. California Community Colleges Technology Center
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.pesc.cds.domain;

import org.pesc.cds.model.TransactionStatus;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name="transactions")
public class Transaction {
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(name="recipient_id")
	private Integer recipientId;

	@Column(name="sender_id")
	private Integer senderId;

	@Column(name="signer_id")
	private Integer signerId;

	@Column(name="sender_transaction_id")
	private Integer senderTransactionId;
	
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

	@Column(name="request_file_path")
	private String requestFilePath;

	//The path to the PESC functional acknowledgement file, if there is one.
	@Column(name="ack_file_path")
	private String ackFilePath;

	@Column(name="ack_url")
	private String ackURL;
	
	@Column(name="operation")
	private String operation;
	
	@Column(name="occurred_at")
	private Timestamp occurredAt;
	
	@Column(name="acknowledged_at")
	private Timestamp acknowledgedAt;

	@Column(name="error", columnDefinition = "text")
	private String error;

	@Column(name="delivery_message")
	private String message;

	@Column(name="delivery_status")
	@Enumerated(EnumType.STRING)
	private TransactionStatus status;
	
	@Column(name="acknowledged")
	private Boolean acknowledged = false;

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

	public String getRequestFilePath() {
		return requestFilePath;
	}

	public void setRequestFilePath(String requestFilePath) {
		this.requestFilePath = requestFilePath;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public Timestamp getOccurredAt() {
		return occurredAt;
	}

	public void setOccurredAt(Timestamp occurredAt) {
		this.occurredAt = occurredAt;
	}

	public Timestamp getAcknowledgedAt() {
		return acknowledgedAt;
	}

	public void setAcknowledgedAt(Timestamp acknowledgedAt) {
		this.acknowledgedAt = acknowledgedAt;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public Boolean getAcknowledged() {
		return acknowledged;
	}

	public void setAcknowledged(Boolean acknowledged) {
		this.acknowledged = acknowledged;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public TransactionStatus getStatus() {
		return status;
	}

	public void setStatus(TransactionStatus status) {
		this.status = status;
	}

	public String getAckURL() {
		return ackURL;
	}

	public void setAckURL(String ackURL) {
		this.ackURL = ackURL;
	}

	public Integer getSenderTransactionId() {
		return senderTransactionId;
	}

	public void setSenderTransactionId(Integer senderTransactionId) {
		this.senderTransactionId = senderTransactionId;
	}

	public Integer getSignerId() {
		return signerId;
	}

	public void setSignerId(Integer signerId) {
		this.signerId = signerId;
	}

	public String getAckFilePath() {
		return ackFilePath;
	}

	public void setAckFilePath(String ackFilePath) {
		this.ackFilePath = ackFilePath;
	}

}
