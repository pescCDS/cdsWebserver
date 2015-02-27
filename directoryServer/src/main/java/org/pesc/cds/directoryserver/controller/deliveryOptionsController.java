package org.pesc.cds.directoryserver.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class deliveryOptionsController {
	private static final Log log = LogFactory.getLog(deliveryOptionsController.class);
	private final String templateView = "deliveryOptions";
	
	@RequestMapping("/deliveryOptions")
	public String viewDeliveryOptions(HttpServletRequest request) {
		return templateView;
	}
}
