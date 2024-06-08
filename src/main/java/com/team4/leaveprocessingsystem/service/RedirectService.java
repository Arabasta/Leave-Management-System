package com.team4.leaveprocessingsystem.service;


import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class RedirectService {

    public String getHomeRedirectUrl() {
        // source: https://www.baeldung.com/spring-redirect-after-login
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            // note for future: redirects to whichever the first authority is
            // works cause each user can only have 1 role
            for (GrantedAuthority authority : authentication.getAuthorities()) {
                switch (authority.getAuthority()) {
                    case "ROLE_ADMIN" -> {
                        return "redirect:/home/admin";
                    }
                    case "ROLE_MANAGER" -> {
                        return "redirect:/home/manager";
                    }
                    case "ROLE_EMPLOYEE" -> {
                        return "redirect:/home/employee";
                    }
                }
            }
        }
        throw new IllegalStateException("No role found");
    }

    public String getAuthSuccessRedirectUrl() {
        // source: https://www.baeldung.com/spring-redirect-after-login
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            // note for future: redirects to whichever the first authority is
            // works cause each user can only have 1 role
            for (GrantedAuthority authority : authentication.getAuthorities()) {
                switch (authority.getAuthority()) {
                    case "ROLE_ADMIN" -> {
                        return "/home/admin";
                    }
                    case "ROLE_MANAGER" -> {
                        return "/home/manager";
                    }
                    case "ROLE_EMPLOYEE" -> {
                        return "/home/employee";
                    }
                }
            }
        }
        throw new IllegalStateException("No role found");
    }

}
