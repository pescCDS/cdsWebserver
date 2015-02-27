package org.pesc.cds.directoryserver.view;

import java.io.IOException;
import java.sql.Timestamp;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

public class JsonTimestampSerializer extends JsonSerializer<Timestamp> {
	
	public void serialize(Timestamp value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
		jgen.writeNumber(value.getTime());
	}
}
