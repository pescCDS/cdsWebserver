package org.pesc.cds.domain;

public class NetworkServerSettings {
	private String filePath;
	
	public NetworkServerSettings() {}
	
	public NetworkServerSettings(String path) {
		setFilePath(path);
	}
	
	public String getFilePath() { return filePath; }
	public void setFilePath(String path) { filePath = path; }
}
