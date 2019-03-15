package com.cloudia.controller;

import javax.validation.Valid;

import com.cloudia.model.User;
import com.cloudia.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserDetailsService userDetailsService;

    /**
     * https://www.baeldung.com/spring-mvc-model-model-map-model-view
     * An interface to pass values to a view is the ModelAndView.
     *
     * "This interface allows us to pass all the information required by
     * Spring MVC in one return."

     */

// Lade die Anmeldeseite
    @RequestMapping(value={"/", "/login"}, method = RequestMethod.GET)
    public ModelAndView login(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("login");
        return modelAndView;
    }

    // Lade die Registrierungsseite
    @RequestMapping(value="/registration", method = RequestMethod.GET)
    public ModelAndView registration(){
        ModelAndView modelAndView = new ModelAndView();
        // User user = new User("email", "password");
        User user = new User();
        modelAndView.addObject("user", user);
        modelAndView.setViewName("registration");
        return modelAndView;
    }

    // Prüfe, ob der Nutzer schon angelegt ist, sonst erstelle einen neuen.
    @RequestMapping(value = "/registration", method = RequestMethod.POST)
    public ModelAndView createNewUser(@Valid User user, BindingResult bindingResult) {
        ModelAndView modelAndView = new ModelAndView();
        User userExists = userService.findUserByEmail(user.getEmail());
        if (userExists != null) {
            bindingResult
                    .rejectValue("email", "error.user",
                            "Ein Nutzer ist schon mit dieser E-Mail Adresse registriert.");
        }
        if (bindingResult.hasErrors()) {
            modelAndView.setViewName("registration");
        } else {
            userService.saveUser(user);
            modelAndView.addObject("successMessage", "Sie wurden registriert!");
            modelAndView.addObject("user", new User());
            modelAndView.setViewName("registration");

        }
        return modelAndView;
    }

    /** Der Zugang zu /home ist nur registrierten Nutzern vorbehalten.*/
    @RequestMapping(value="/home", method = RequestMethod.GET)
    public ModelAndView home(){
        ModelAndView modelAndView = new ModelAndView();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(auth.getName());

        //UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        // modelAndView.addObject("userName", "Willkommen bei Projektron BCS-FIW-Cloud, " + user.getFirstName() + " " + user.getLastName());
        if(user.organization()==null) {
            modelAndView.addObject("welcomeMessage", "Hallo "+user.getFirstName() + " " +user.getLastName() +", Sie sind noch keiner Organisation zugeordnet. Um ihre Tickets sehen zu können, melden Sie sich bitte bei Ihrem Admin! HOTLINE: 0800-123456");
        }else{
            modelAndView.addObject("welcomeMessage", "Hallo " +user.getFirstName() +" " +user.getLastName()+"!  Herzlich willkommen bei Cloudia - Cloud interface application - für " + user.organization().getOrganizationName());
        }

        modelAndView.setViewName("home");
        return modelAndView;
    }
}
