package org.pesc.cds.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class HomeController {

    private static final Log log = LogFactory.getLog(HomeController.class);

    @Value("${directory.server}")
    private String directoryServer;
    @Value("${directory.server.port}")
    private String directortyServerPort;

    @RequestMapping({ "/", "/home" })
    public String viewHome(Model model) {
        model.addAttribute("directoryServer", directoryServer + ":" + directortyServerPort);

        return "home";
    }

}
