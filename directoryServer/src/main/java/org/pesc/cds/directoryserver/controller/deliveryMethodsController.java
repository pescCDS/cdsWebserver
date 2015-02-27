package org.pesc.cds.directoryserver.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class deliveryMethodsController {
	private static final Log log = LogFactory.getLog(deliveryMethodsController.class);
	private final String templateView = "deliveryMethods";
	
	@RequestMapping("/deliveryMethods")
	public String viewDeliveryMethods(HttpServletRequest request) {
		return templateView;
	}
}
