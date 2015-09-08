package org.pesc.cds.directoryserver.view;

import java.sql.Timestamp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.module.SimpleModule;
import org.codehaus.jackson.map.ser.CustomSerializerFactory;

/**
 * This is the Object Mapper attached to the JSONProvider bean in the CXF context file.
 * The JAX-RS server bean has a list of providers it uses to delegate output to a 
 * particular type.
 * @author Wes Owen
 *
 */
public class JSONMapper extends ObjectMapper {
	private static final Log log = LogFactory.getLog(JSONMapper.class);
	
	public JSONMapper() {
		super();
		
		//CustomSerializerFactory factory = new CustomSerializerFactory();
		//factory.addSpecificMapping(Timestamp.class, new JsonTimestampSerializer());
		//this.setSerializerFactory(factory);
		
		//SimpleModule mod = new SimpleModule("JsonTimestampDeserializer", new Version(1, 0, 0, null));
		//mod.addDeserializer(Timestamp.class, new JsonTimestampDeserializer());
		//this.registerModule(mod);
		
		this.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		this.configure(SerializationConfig.Feature.INDENT_OUTPUT, true);
		this.configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS, true);
		
		this.enable(SerializationConfig.Feature.INDENT_OUTPUT);
		this.enable(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS);
		
		this.setDateFormat(null);
	}
}
