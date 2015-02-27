package org.pesc.cds.directoryserver.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ContactsController {
	private static final Log log = LogFactory.getLog(ContactsController.class);
	private final String templateView = "contacts";
	
	@RequestMapping("/contacts")
	public String viewContacts(HttpServletRequest request) {
		return templateView;
	}
}
