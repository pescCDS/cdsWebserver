/*
 * Copyright (c) 2017. California Community Colleges Technology Center
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.pesc.cds.web;

/**
 * Created by James Whetstone (jwhetstone@ccctechcenter.org) on 3/2/16.
 */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by James Whetstone (jwhetstone@ccctechcenter.org) on 2/18/16.
 */


@Controller
public class LoginController {

    private static final Log log = LogFactory.getLog(LoginController.class);
    private final String templateView = "login";

    @Value("${edex.sso.enabled}")
    private boolean ssoEnabled;

    @RequestMapping("/login")
    public String login(Model model) {
        model.addAttribute("ssoEnabled", ssoEnabled);

        return templateView;
    }

    @RequestMapping("/login-error")
    public String loginError(Model model) {
        model.addAttribute("loginError",true);
        return templateView;
    }
}
