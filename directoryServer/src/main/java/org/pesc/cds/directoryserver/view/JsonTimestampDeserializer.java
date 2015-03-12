package org.pesc.cds.directoryserver.view;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ListIterator;
import java.util.Map.Entry;

import javassist.bytecode.Descriptor.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.ObjectCodec;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;

public class JsonTimestampDeserializer extends JsonDeserializer<Timestamp> {
	private static final Log log = LogFactory.getLog(JsonTimestampDeserializer.class);

	@Override
	public Timestamp deserialize(JsonParser jparser, DeserializationContext ctx) throws IOException, JsonProcessingException {
		
		ObjectCodec oc = jparser.getCodec();
		JsonNode node = oc.readTree(jparser);
		
		return new Timestamp(node.asLong());
	}

}
