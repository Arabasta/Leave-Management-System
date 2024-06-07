package com.team4.leaveprocessingsystem.seeder;

import com.team4.leaveprocessingsystem.model.*;
import com.team4.leaveprocessingsystem.model.enums.AccessLevelEnum;
import com.team4.leaveprocessingsystem.service.EmployeeService;
import com.team4.leaveprocessingsystem.service.LeaveBalanceService;
import com.team4.leaveprocessingsystem.service.JobDesignationService;
import com.team4.leaveprocessingsystem.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class EmployeeSeeder {

    private final EmployeeService employeeService;
    private final LeaveBalanceService leaveBalanceService;
    private final JobDesignationService roleService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public EmployeeSeeder(EmployeeService employeeService,
                          LeaveBalanceService leaveBalanceService,
                          JobDesignationService roleService,
                          UserService userService,
                          PasswordEncoder passwordEncoder) {
        this.employeeService = employeeService;
        this.leaveBalanceService = leaveBalanceService;
        this.roleService = roleService;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    public void seed() {
        if (employeeService.count() == 0) {
            seedManagement();
            seedAdministrative();
            seedIntern();
            seedPartTime();
            seedCleaning();
        }
    }

    private void seedManagement() {
        JobDesignation managementJobDesignation = roleService.findByName("management");
        LeaveBalance managerLeaveBalance = new LeaveBalance(managementJobDesignation.getAnnualLeaves());
        leaveBalanceService.save(managerLeaveBalance);

        User managerUser = new User(AccessLevelEnum.ROLE_MANAGER,
                "manager",
                passwordEncoder.encode("manager"),
                "manager@example.com");
        userService.save(managerUser);

        Manager manager = new Manager("Manager", managementJobDesignation, null, managerLeaveBalance);
        manager.setUser(managerUser);
        employeeService.save(manager);
    }

    private void seedAdministrative() {
        JobDesignation administrativeJobDesignation = roleService.findByName("administrative");
        Manager manager = employeeService.findManagerByName("Manager");
        LeaveBalance adminLeaveBalance = new LeaveBalance(administrativeJobDesignation.getAnnualLeaves());
        leaveBalanceService.save(adminLeaveBalance);

        User adminUser = new User(AccessLevelEnum.ROLE_EMPLOYEE,
                "employee",
                passwordEncoder.encode("employee"),
                "employee@example.com");
        userService.save(adminUser);

        Employee administrativeEmployee = new Employee("Administrative Employee",
                administrativeJobDesignation,
                manager,
                adminLeaveBalance);
        administrativeEmployee.setUser(adminUser);
        employeeService.save(administrativeEmployee);
    }

    private void seedIntern() {
        JobDesignation internJobDesignation = roleService.findByName("intern");
        Manager manager = employeeService.findManagerByName("Manager");
        LeaveBalance internLeaveBalance = new LeaveBalance(internJobDesignation.getAnnualLeaves());
        leaveBalanceService.save(internLeaveBalance);

        User internUser = new User(AccessLevelEnum.ROLE_EMPLOYEE,
                "intern",
                passwordEncoder.encode("intern"),
                "intern@example.com");
        userService.save(internUser);

        Employee intern = new Employee("Intern",
                internJobDesignation,
                manager,
                internLeaveBalance);
        intern.setUser(internUser);
        employeeService.save(intern);
    }

    private void seedPartTime() {
        JobDesignation parttimeJobDesignation = roleService.findByName("parttime");
        Manager manager = employeeService.findManagerByName("Manager");
        LeaveBalance parttimeLeaveBalance = new LeaveBalance(parttimeJobDesignation.getAnnualLeaves());
        leaveBalanceService.save(parttimeLeaveBalance);

        User parttimeUser = new User(AccessLevelEnum.ROLE_EMPLOYEE,
                "parttime",
                passwordEncoder.encode("parttime"),
                "parttime@example.com");
        userService.save(parttimeUser);

        Employee parttimeEmployee = new Employee("Part-time Employee",
                parttimeJobDesignation,
                manager,
                parttimeLeaveBalance);
        parttimeEmployee.setUser(parttimeUser);
        employeeService.save(parttimeEmployee);
    }

    private void seedCleaning() {
        JobDesignation cleaningJobDesignation = roleService.findByName("cleaning");
        Manager manager = employeeService.findManagerByName("Manager");
        LeaveBalance cleaningLeaveBalance = new LeaveBalance(cleaningJobDesignation.getAnnualLeaves());
        leaveBalanceService.save(cleaningLeaveBalance);

        User cleaningUser = new User(AccessLevelEnum.ROLE_EMPLOYEE,
                "cleaning",
                passwordEncoder.encode("cleaning"),
                "cleaning@example.com");
        userService.save(cleaningUser);

        Employee cleaningEmployee = new Employee("Cleaning Staff",
                cleaningJobDesignation,
                manager,
                cleaningLeaveBalance);
        cleaningEmployee.setUser(cleaningUser);
        employeeService.save(cleaningEmployee);
    }
}