package org.pesc.cds.networkserver.domain;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="transactions", schema="pesc_networkserver")
public class Transaction {
	@Id
	@GeneratedValue
	@Column(name="id")
	private Integer id;
	
	@Column(name="recipientId")
	private Integer recipientId;
	
	@Column(name="networkServerId")
	private Integer networkServerId;
	
	@Column(name="senderId")
	private Integer senderId;
	
	@Column(name="fileFormat")
	private String fileFormat;
	
	@Column(name="fileSize")
	private Long fileSize = 0l;
	
	@Column(name="direction")
	private String direction;
	
	@Column(name="sent")
	private Timestamp sent;
	
	@Column(name="received")
	private Timestamp received;
	
	@Column(name="error")
	private String error;
	
	@Column(name="status")
	private Boolean status = false;
	
	public Transaction() {}
	
	public Integer getId() { return id; }
	public Integer getRecipientId() { return recipientId; }
	public Integer getNetworkServerId() { return networkServerId; }
	public Integer getSenderId() { return senderId; }
	public String getFileFormat() { return fileFormat; }
	public Long getFileSize() { return fileSize; }
	public String getDirection() { return direction; }
	public Timestamp getSent() { return sent; }
	public Timestamp getReceived() { return received; }
	public String getError() { return error; }
	public Boolean getStatus() { return status; }
	
	public void setId(Integer id) { this.id = id; }
	public void setRecipientId(Integer recipientId) { this.recipientId = recipientId; }
	public void setNetworkServerId(Integer networkServerId) { this.networkServerId = networkServerId; }
	public void setSenderId(Integer senderId) { this.senderId = senderId; }
	public void setFileFormat(String fileFormat) { this.fileFormat = fileFormat; }
	public void setFileSize(Long fileSize) { this.fileSize = fileSize; }
	public void setDirection(String direction) { this.direction = direction; }
	public void setSent(Timestamp sent) { this.sent = sent; }
	public void setReceived(Timestamp received) { this.received = received; }
	public void setError(String error) { this.error = error; }
	public void setStatus(Boolean status) { this.status = status; }
}
