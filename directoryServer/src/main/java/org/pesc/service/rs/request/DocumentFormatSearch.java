package org.pesc.service.rs.request;

public class DocumentFormatSearch {
	private Integer id;
	private String formatName;
	private String formatDescription;
	private Integer formatInuseCount;
	private Long createdTime;
	private Long modifiedTime;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getFormatName() {
		return formatName;
	}
	public void setFormatName(String formatName) {
		this.formatName = formatName;
	}
	public String getFormatDescription() {
		return formatDescription;
	}
	public void setFormatDescription(String formatDescription) {
		this.formatDescription = formatDescription;
	}
	public Integer getFormatInuseCount() {
		return formatInuseCount;
	}
	public void setFormatInuseCount(Integer formatInuseCount) {
		this.formatInuseCount = formatInuseCount;
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
