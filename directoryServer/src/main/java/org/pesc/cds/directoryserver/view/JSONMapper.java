package org.pesc.cds.directoryserver.view;

import java.text.DateFormat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;

public class JSONMapper extends ObjectMapper {
	private static final Log log = LogFactory.getLog(JSONMapper.class);
	
	public JSONMapper() {
		super();
		this.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		this.configure(SerializationConfig.Feature.INDENT_OUTPUT, true);
		this.configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS, true);
		
		this.setDateFormat(DateFormat.getDateTimeInstance(DateFormat.SHORT,DateFormat.MEDIUM));
	}
}
