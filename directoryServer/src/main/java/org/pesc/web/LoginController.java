package org.pesc.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created by James Whetstone (jwhetstone@ccctechcenter.org) on 2/18/16.
 */


@Controller
public class LoginController {

    private static final Log log = LogFactory.getLog(LoginController.class);
    private final String templateView = "login";

    @RequestMapping({"/login"} )
    public String login(@RequestParam(value = "error", required = false) String error,
                        @RequestParam(value = "logout", required = false) String logout) {
        return templateView;
    }

    @RequestMapping("/login-error")
    public String loginError(Model model) {
        model.addAttribute("loginError", true);
        return templateView;
    }

}
