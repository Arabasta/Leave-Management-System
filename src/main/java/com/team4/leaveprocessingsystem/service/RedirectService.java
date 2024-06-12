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
                        return "redirect:/admin/home";
                    }
                    case "ROLE_MANAGER" -> {
                        return "redirect:/manager/home";
                    }
                    case "ROLE_EMPLOYEE" -> {
                        return "redirect:/employee/home";
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
                        return "/admin/home";
                    }
                    case "ROLE_MANAGER" -> {
                        return "/manager/home";
                    }
                    case "ROLE_EMPLOYEE" -> {
                        return "/employee/home";
                    }
                }
            }
        }
        throw new IllegalStateException("No role found");
    }

}
