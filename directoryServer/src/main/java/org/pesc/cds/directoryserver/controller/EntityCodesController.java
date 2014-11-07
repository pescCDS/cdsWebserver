package org.pesc.cds.directoryserver.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class EntityCodesController {
	private static final Log log = LogFactory.getLog(EntityCodesController.class);
	private final String templateView = "entityCodes";
	
	@RequestMapping("/entityCodes")
	public String viewEntityCodes() {
		return this.templateView;
	}
}
