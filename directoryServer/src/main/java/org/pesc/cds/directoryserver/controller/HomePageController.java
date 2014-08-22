package org.pesc.cds.directoryserver.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class HomePageController {
	
	private static final Log log = LogFactory.getLog(HomePageController.class);
	private final String templateView = "home";
	
	@RequestMapping({"/","/home"})
	public String viewHome(HttpServletRequest request) {
		return templateView;
	}
}
