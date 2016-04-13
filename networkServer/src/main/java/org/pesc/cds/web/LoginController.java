package org.pesc.cds.web;

/**
 * Created by james on 3/2/16.
 */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by james on 2/18/16.
 */


@Controller
public class LoginController {

    private static final Log log = LogFactory.getLog(LoginController.class);
    private final String templateView = "login";

    @RequestMapping("/login")
    public String login() {
        return templateView;
    }

    @RequestMapping("/login-error")
    public String loginError(Model model) {
        model.addAttribute("loginError",true);
        return templateView;
    }
}
