package org.pesc.service.rs.request;

public class DeliveryOptionSearch {
	private Integer id;
	private Integer memberId;
	private Integer formatId;
	private String webserviceUrl;
	private Integer deliveryMethodId;
	private Boolean deliveryConfirm;
	private Boolean error;
	private String operationalStatus;
	
	public DeliveryOptionSearch() {}
	
	public Integer getId() { return id; }
	public void setId(Integer id) { this.id = id; }
	public Integer getMemberId() { return memberId; }
	public void setMemberId(Integer memberId) { this.memberId = memberId; }
	public Integer getFormatId() { return formatId; }
	public void setFormatId(Integer formatId) { this.formatId = formatId; }
	public String getWebserviceUrl() { return webserviceUrl; }
	public void setWebserviceUrl(String wsurl) { this.webserviceUrl = wsurl; }
	public Integer getDeliveryMethodId() { return deliveryMethodId; }
	public void setDeliveryMethodId(Integer dmid) { this.deliveryMethodId = dmid; }
	public Boolean getDeliveryConfirm() { return deliveryConfirm; }
	public void setDeliveryConfirm(Boolean cnfrm) { this.deliveryConfirm = cnfrm; }
	public Boolean getError() { return error; }
	public void setError(Boolean err) { this.error = err; }
	public String getOperationalStatus() { return this.operationalStatus; }
	public void setOperationalStatus(String opStatus) { this.operationalStatus = opStatus; }
}
