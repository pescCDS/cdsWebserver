package org.pesc.cds.directoryserver.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class OrganizationsPageController {
	private static final Log log = LogFactory.getLog(OrganizationsPageController.class);
	private final String templateView = "organizations";
	
	@RequestMapping("/organizations")
	public String viewOrganizations(HttpServletRequest request) {
		return templateView;
	}
}
