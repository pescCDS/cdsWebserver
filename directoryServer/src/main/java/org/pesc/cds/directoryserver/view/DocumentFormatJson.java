package org.pesc.cds.directoryserver.view;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DocumentFormatJson {
	private static final Log log = LogFactory.getLog(DocumentFormatJson.class);
	private Integer id;
	private String format_name;
	private String format_description;
	
	public Integer getId() { return id; }
	public void setId(Integer id) { this.id = id; }
	public String getFormat_name() { return format_name; }
	public void setFormat_name(String format_name) { this.format_name = format_name; }
	public String getFormat_description() {return format_description;}
	public void setFormat_description(String format_description) {this.format_description = format_description;}
	
	public String toString() {
		return String.format("DocumentFormatJson {%n  id:%s,%n  format_name:%s,%n  format_description:%s%n}",id,format_name,format_description);
	}
}
