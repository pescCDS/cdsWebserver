package org.pesc.web;

/**
 * Created by james on 2/24/16.
 */

import org.pesc.web.model.OrganizationContact;
import org.pesc.web.model.OrganizationContactDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Class OrganizationContactController
 */
@Controller
public class OrganizationContactController {

    // ------------------------
    // PUBLIC METHODS
    // ------------------------

    /**
     * Create a new user with an auto-generated id and email and name as passed 
     * values.
     */
    @RequestMapping(value="/create")
    @ResponseBody
    public String create() {
        try {
            OrganizationContact user = new OrganizationContact();
            user.setContactName("James");
            user.setContactTitle("Dude");

            userDao.save(user);
        }
        catch (Exception ex) {
            return "Error creating the user: " + ex.toString();
        }
        return "OrganizationContact succesfully created!";
    }

    // ------------------------
    // PRIVATE FIELDS
    // ------------------------

    // Wire the OrganizationContactDao used inside this controller.
    @Autowired
    private OrganizationContactDao userDao;

} // class OrganizationContactController
