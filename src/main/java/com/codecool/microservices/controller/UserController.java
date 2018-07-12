package com.codecool.microservices.controller;


import com.codecool.microservices.model.User;
import com.codecool.microservices.service.CommunicationService;
import com.codecool.microservices.service.UserService;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.naming.AuthenticationException;
import java.io.UnsupportedEncodingException;

@Controller
@SessionAttributes({"user"})
public class UserController {

    private static final String loginHTML = "login";

    private UserService userService;
    private CommunicationService communicationService;

    public UserController(UserService userService, CommunicationService communicationService){
        this.userService = userService;
        this.communicationService = communicationService;
    }

    @ModelAttribute("user")
    public User setUpUser(){
        return null;
    }

    @GetMapping(value = "/login")
    public String displayLogin(@ModelAttribute("user") User user, Model model){
        if (user != null && user.loggedIn()){
        }
        model.addAttribute("user", user);
        return loginHTML;
    }

    @PostMapping(value = "/login")
    public String login(@RequestParam("login_email") String email,
                        @RequestParam("login_password") String password,
                        Model model) {
        try {
            User user = userService.login(email);
            if (BCrypt.checkpw(password, user.getPassword())) {
                user.removePassword();
                user.login();
                model.addAttribute("user", user);
                return "redirect:/";
            } else {
                throw new AuthenticationException();
            }
        } catch (NullPointerException | AuthenticationException e) {
            System.out.println("Couldn't log in");
            return loginHTML;
        }
    }

    @PostMapping(value = "/registration")
    public String registration(@RequestParam("reg_email") String email,
                               @RequestParam("reg_password") String password,
                               @RequestParam("first_name") String firstName,
                               @RequestParam("last_name") String lastName,
                               @RequestParam("address") String address,
                               @RequestParam("phone_number") String phoneNumber, Model model){
        password = BCrypt.hashpw(password, BCrypt.gensalt());
        try {
            userService.registration(email, password, firstName, lastName, address, phoneNumber);
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
            model.addAttribute("error", "Couldn't register user!");
        }
        return loginHTML;
    }

    @GetMapping(value = "/logout")
    public String logout(@ModelAttribute User user, Model model){
        if (user != null && user.loggedIn()){
            System.out.println("LOGOUT!");
            user.logout();
            model.addAttribute("user", user);
        }
        return loginHTML;
    }
}
