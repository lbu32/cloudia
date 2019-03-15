package com.cloudia.controller;

import com.cloudia.model.Organization;
import com.cloudia.model.User;
import com.cloudia.service.OrganizationService;
import com.cloudia.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;

@Controller
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private OrganizationService organizationService;

    @RolesAllowed("ADMIN")
    @RequestMapping(value = "/admin/admindashboard")
    public ModelAndView updateUser(@Valid User user, String email, Integer roleId, Integer organization_id, Organization organization, String organizationName, BindingResult bindingResult) {
        ModelAndView modelAndView = new ModelAndView();

        if (email != null && organization_id != null ) {
            Organization organizationExists = organizationService.findById(organization_id);
            User userExists = userService.findUserByEmail(user.getEmail());
            if (organizationExists == null || userExists == null) {
                bindingResult
                        .rejectValue("email", "error.user",
                                "Ein Nutzer oder Organisation existiert nicht.");

            }
            if (bindingResult.hasErrors()) {
                modelAndView.setViewName("admin/admindashboard");
            }
            else {
                userService.setOrganization(email, organization_id);
                modelAndView.addObject("successMessage", "Der User wurde upgedated!");
                modelAndView.setViewName("admin/admindashboard");
            }
        }
        if (organizationName != null ) {
            Organization organizationNameExists = organizationService.findByOrganizationName(organizationName);

            if (organizationNameExists == null) {
                organizationService.saveOrganization(organization);
                modelAndView.addObject("organization", new Organization());
                modelAndView.addObject("successMessage", "Die Organization wurde angelegt!");
                modelAndView.setViewName("admin/admindashboard");
            }
            else {
                System.out.println("Hello");

            }

        }
        if (email != null && roleId != null ) {
            User userExists = userService.findUserByEmail(user.getEmail());
            if (userExists == null) {
                bindingResult
                        .rejectValue("email", "error.user",
                                "Ein Nutzer oder Rolle existiert nicht.");

            }
            if (bindingResult.hasErrors()) {
                modelAndView.setViewName("admin/admindashboard");
            }
            else {
                userService.setUserRole(email, roleId);
                modelAndView.addObject("successMessage", "Dem User wurde eine Rolle zugeordnet!");
                modelAndView.setViewName("admin/admindashboard");
            }
        }
        return modelAndView;
    }

}
