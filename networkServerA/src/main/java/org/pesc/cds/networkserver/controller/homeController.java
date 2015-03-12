package org.pesc.cds.networkserver.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class homeController {
	
	private static final Log log = LogFactory.getLog(homeController.class);
	
	@RequestMapping("/")
	public String viewHome() {
		return "home";
	}
	
	
}
