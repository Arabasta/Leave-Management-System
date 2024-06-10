package com.team4.leaveprocessingsystem.controller.auth;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    // IMPORTANT: do not change route
    @GetMapping("/auth/login")
    public String login() {
        return "auth/login";
    }

}
