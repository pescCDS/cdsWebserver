package org.pesc.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created by james on 2/17/16.
 */

@Controller
public class HomeController {

    @RequestMapping("/home")
    public String greeting(@RequestParam(value="name", required=false, defaultValue="World") String name, Model model) {

        //TODO: after authentication, replace with user's name

        model.addAttribute("name", name);
        return "home";
    }

}