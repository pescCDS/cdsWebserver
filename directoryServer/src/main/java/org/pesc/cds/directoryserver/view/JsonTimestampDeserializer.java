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
		log.debug("deserializing Timestamp");
		
		log.debug(jparser.getCurrentName());
		
		ObjectCodec oc = jparser.getCodec();
		
		JsonNode node = oc.readTree(jparser);
		
		for(ListIterator<Entry<String, JsonNode>> rootIter = (ListIterator<Entry<String, JsonNode>>)node.getFields(); rootIter.hasNext();) {
			Entry<String, JsonNode> cit = rootIter.next();
			log.debug( String.format("%s, %s", cit.getKey(), cit.toString()) );
			//cit.getValue().
		}
		
		log.debug(node.get("time"));
		return new Timestamp(node.get("time").asLong());
	}

}
