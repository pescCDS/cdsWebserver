package org.pesc.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pesc.web.model.Organization;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by james on 2/23/16.
 */
@Controller
public class OrganizationController {

    private static final Log log = LogFactory.getLog(OrganizationController.class);

    @RequestMapping(value="/organization/{orgID}", method = RequestMethod.GET)
    public @ResponseBody
    Organization getOrganization(@PathVariable String orgID) {

        Organization org = new Organization();
        org.setId(orgID);
        org.setName("Butte College");

        return org;

    }
}
