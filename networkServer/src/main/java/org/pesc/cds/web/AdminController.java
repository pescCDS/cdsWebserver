package org.pesc.cds.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pesc.cds.domain.TransactionsDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by james on 3/3/16.
 */
@Controller
public class AdminController {
    private static final Log log = LogFactory.getLog(AdminController.class);

    @Autowired
    private TransactionsDao transactionsDao;

    @Value("${directory.server}")
    private String directoryServer;
    @Value("${directory.server.port}")
    private String directortyServerPort;


    @RequestMapping("/admin")
    public String viewHome(Model model) {
        model.addAttribute("transactions", transactionsDao.all());
        model.addAttribute("directoryServer", directoryServer + ":" + directortyServerPort);

        return "admin";
    }
}
