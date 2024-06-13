package com.team4.leaveprocessingsystem.service;

import com.team4.leaveprocessingsystem.model.Employee;
import com.team4.leaveprocessingsystem.model.User;
import com.team4.leaveprocessingsystem.model.enums.RoleEnum;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    public int getLoggedInEmployeeId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            User user = (User) authentication.getPrincipal();
            Employee employee = user.getEmployee();
            if (employee != null) {
                return employee.getId();
            }
            throw new IllegalStateException("Employee not found for the logged-in user");
        }
        return -1;
    }

    public RoleEnum getLoggedInRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            User user = (User) authentication.getPrincipal();
            RoleEnum role = user.getRole();
            if (role != null) {
                return role;
            }
            throw new IllegalStateException("Role not found for the logged-in user");
        }
        return null;
    }
}
