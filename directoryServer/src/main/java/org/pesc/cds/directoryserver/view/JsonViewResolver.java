package org.pesc.cds.directoryserver.view;

import java.util.Locale;

import org.codehaus.jackson.map.SerializationConfig;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.json.MappingJacksonJsonView;

public class JsonViewResolver implements ViewResolver {
	public View resolveViewName(String viewName, Locale locale) throws Exception {
		MappingJacksonJsonView view = new MappingJacksonJsonView();
		view.setPrettyPrint(true);
		
		view.setObjectMapper(new JSONMapper());
		view.getObjectMapper().enable(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS);
		return view;
	}
}
