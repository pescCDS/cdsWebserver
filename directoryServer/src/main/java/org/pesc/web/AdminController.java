package org.pesc.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created by james on 2/18/16.
 */

@Controller
public class AdminController {

    @RequestMapping("/admin")
    public String adminConsole(Model model) {

        return "admin";
    }

}