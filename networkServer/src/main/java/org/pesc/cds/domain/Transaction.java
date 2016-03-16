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
	
	@Column(name="filePath")
	private String filePath;
	
	@Column(name="direction")
	private String direction;
	
	@Column(name="sent")
	private Timestamp sent;
	
	@Column(name="received")
	private Timestamp received;
	
	@Column(name="error", columnDefinition = "text")
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
	public String getFilePath() { return filePath; }
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
	public void setFilePath(String path) { filePath = path; }
	public void setDirection(String direction) { this.direction = direction; }
	public void setSent(Timestamp sent) { this.sent = sent; }
	public void setReceived(Timestamp received) { this.received = received; }
	public void setError(String error) { this.error = error; }
	public void setStatus(Boolean status) { this.status = status; }
}
