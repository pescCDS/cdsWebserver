package org.pesc.cds.directoryserver.view;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class EntityCodeJson {
	private static final Log log = LogFactory.getLog(EntityCodeJson.class);
	private Integer id;
	private Integer entity_code;
	private String description;
	
	public Integer getId() { return id; }
	public void setId(Integer id) { this.id = id; }
	public Integer getEntity_code() { return entity_code; }
	public void setEntity_code(Integer entity_code) { this.entity_code = entity_code; }
	public String getDescription() {return description;}
	public void setDescription(String description) {this.description = description;}
	
	public String toString() {
		return String.format("EntityCodeJson {%n  id: %s,%n  entity_code: %s,%n  description: %s%n}", id, entity_code, description);
	}
}
