package org.pesc.cds.directoryserver.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class DocumentFormatsController {
	private static final Log log = LogFactory.getLog(DocumentFormatsController.class);
	private final String templateView = "documentFormats";
	
	@RequestMapping("/documentFormats")
	public String viewDocumentFormats(HttpServletRequest request) {
		return this.templateView;
	}
}
