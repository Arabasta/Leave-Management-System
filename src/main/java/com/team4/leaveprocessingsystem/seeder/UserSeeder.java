package com.team4.leaveprocessingsystem.seeder;

import com.team4.leaveprocessingsystem.model.Employee;
import com.team4.leaveprocessingsystem.model.User;
import com.team4.leaveprocessingsystem.model.enums.RoleEnum;
import com.team4.leaveprocessingsystem.service.EmployeeService;
import com.team4.leaveprocessingsystem.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserSeeder {

    private final UserService userService;
    private final EmployeeService employeeService;
    private final PasswordEncoder passwordEncoder;

    public UserSeeder(UserService userService, EmployeeService employeeService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.employeeService = employeeService;
        this.passwordEncoder = passwordEncoder;
    }

    public void seed() {
            seedAdminUsers();
    }

    private void seedAdminUsers() {
        Employee adminEmployee = employeeService.findByName("Employee");

        User adminUser = new User(RoleEnum.ROLE_ADMIN,
                "admin",
                passwordEncoder.encode("admin"),
                "admin@example.com");
        adminUser.setEmployee(adminEmployee);
        userService.save(adminUser);
    }
}
