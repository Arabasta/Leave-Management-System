package com.team4.leaveprocessingsystem.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CustomErrorController implements ErrorController {

    @GetMapping("/error")
    public String handleError(HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (status != null) {
            switch (status.toString()) {
                case "400":
                    return "error/400-badrequest";
                case "401":
                    return "error/401-unauthorised";
                case "403":
                    return "error/403-forbidden";
                case "404":
                    return "error/404-notfound";
                case "500":
                    return "error/500-internalservererror";
                default:
                    return "error/unknown-error";
            }
        }
        return "error/unknown-error";
    }

}
