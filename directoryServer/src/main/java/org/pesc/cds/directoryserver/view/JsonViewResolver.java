package org.pesc.cds.directoryserver.view;

import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.SerializationConfig;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.json.MappingJacksonJsonView;

public class JsonViewResolver implements ViewResolver {
	private static final Log log = LogFactory.getLog(JsonViewResolver.class);
	
	private static View buildJsonView() {
		MappingJacksonJsonView view = new MappingJacksonJsonView();
		view.setPrettyPrint(true);
		
		JSONMapper jmap = new JSONMapper();
		jmap.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		jmap.configure(SerializationConfig.Feature.INDENT_OUTPUT, true);
		jmap.configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS, true);
		
		view.setObjectMapper(jmap);
		return view;
	}
	
	public View resolveViewName(String viewName, Locale locale) throws Exception {
		return JsonViewResolver.createStandardJsonView();
	}
	
	public static View createStandardJsonView() {
		return buildJsonView();
	}
}
