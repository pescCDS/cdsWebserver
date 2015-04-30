package org.pesc.cds.webservice.service.request;

public class OrganizationSearch {
	private Integer directoryId;
	private String organizationId;
	private String organizationIdType;
	private String organizationName;
	private String organizationSubcode;
	private Integer entityId;
	private String organizationEin;
	private Long createdTime;
	private Long modifiedTime;
	
	
	public Integer getDirectoryId() {
		return directoryId;
	}
	public void setDirectoryId(Integer directoryId) {
		this.directoryId = directoryId;
	}
	public String getOrganizationId() {
		return organizationId;
	}
	public void setOrganizationId(String organizationId) {
		this.organizationId = organizationId;
	}
	public String getOrganizationIdType() {
		return organizationIdType;
	}
	public void setOrganizationIdType(String organizationIdType) {
		this.organizationIdType = organizationIdType;
	}
	public String getOrganizationName() {
		return organizationName;
	}
	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}
	public String getOrganizationSubcode() {
		return organizationSubcode;
	}
	public void setOrganizationSubcode(String organizationSubcode) {
		this.organizationSubcode = organizationSubcode;
	}
	public Integer getEntityId() {
		return entityId;
	}
	public void setEntityId(Integer entityId) {
		this.entityId = entityId;
	}
	public String getOrganizationEin() {
		return organizationEin;
	}
	public void setOrganizationEin(String organizationEin) {
		this.organizationEin = organizationEin;
	}
	public Long getCreatedTime() {
		return createdTime;
	}
	public void setCreatedTime(Long createdTime) {
		this.createdTime = createdTime;
	}
	public Long getModifiedTime() {
		return modifiedTime;
	}
	public void setModifiedTime(Long modifiedTime) {
		this.modifiedTime = modifiedTime;
	}
}
