package org.pesc.cds.domain;

public class NetworkServerId {
	private String id;
	private String name;
	private String subcode;
	private String ein;
	private String webServiceUrl;
	
	public NetworkServerId() {}
	
	public NetworkServerId(String id, String name, String subcode, String ein, String webService) {
		setId(id);
		setName(name);
		setSubcode(subcode);
		setEin(ein);
		setWebServiceUrl(webService);
	}
	
	public String getId() { return id; }
	public void setId(String id) { this.id = id; }
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	public String getSubcode() { return subcode; }
	public void setSubcode(String subcode) { this.subcode = subcode; }
	public String getEin() { return ein; }
	public void setEin(String ein) { this.ein = ein; }
	public String getWebServiceUrl() { return webServiceUrl; }
	public void setWebServiceUrl(String webservice) { webServiceUrl = webservice; }
}
