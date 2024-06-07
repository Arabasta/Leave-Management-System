package com.team4.leaveprocessingsystem.seeder;

import com.team4.leaveprocessingsystem.model.*;
import com.team4.leaveprocessingsystem.model.enums.RoleEnum;
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
    private final JobDesignationService jobDesignationService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public EmployeeSeeder(EmployeeService employeeService,
                          LeaveBalanceService leaveBalanceService,
                          JobDesignationService jobDesignationService,
                          UserService userService,
                          PasswordEncoder passwordEncoder) {
        this.employeeService = employeeService;
        this.leaveBalanceService = leaveBalanceService;
        this.jobDesignationService = jobDesignationService;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    public void seed() {
        if (employeeService.count() == 0) {
            seedManagement();
            seedEmployee();
            seedIntern();
            seedPartTime();
            seedCleaning();
        }
    }

    private void seedManagement() {
        JobDesignation managementJobDesignation = jobDesignationService.findByName("management");
        LeaveBalance managerLeaveBalance = new LeaveBalance(managementJobDesignation.getAnnualLeaves());
        leaveBalanceService.save(managerLeaveBalance);

        User managerUser = new User(RoleEnum.ROLE_MANAGER,
                "manager",
                passwordEncoder.encode("manager"),
                "manager@example.com");

        Manager manager = new Manager("Manager",
                managementJobDesignation,
                null,
                managerLeaveBalance);
        manager.setUser(managerUser);
        employeeService.save(manager);

        managerUser.setEmployee(manager);
        userService.save(managerUser);
    }

    private void seedEmployee() {
        JobDesignation administrativeJobDesignation = jobDesignationService.findByName("administrative");
        Manager manager = employeeService.findManagerByName("Manager");
        LeaveBalance adminLeaveBalance = new LeaveBalance(administrativeJobDesignation.getAnnualLeaves());
        leaveBalanceService.save(adminLeaveBalance);

        User employeeUser = new User(RoleEnum.ROLE_EMPLOYEE,
                "employee",
                passwordEncoder.encode("employee"),
                "employee@example.com");

        User employeeAdminUser = new User(RoleEnum.ROLE_ADMIN,
                "admin",
                passwordEncoder.encode("admin"),
                "employee@example.com");

        Employee administrativeEmployee = new Employee("Employee",
                administrativeJobDesignation,
                manager,
                adminLeaveBalance);
        administrativeEmployee.setUser(employeeUser);
        employeeService.save(administrativeEmployee);

        employeeUser.setEmployee(administrativeEmployee);
        userService.save(employeeUser);

        employeeAdminUser.setEmployee(administrativeEmployee);
        userService.save(employeeAdminUser);
    }

    private void seedIntern() {
        JobDesignation internJobDesignation = jobDesignationService.findByName("intern");
        Manager manager = employeeService.findManagerByName("Manager");
        LeaveBalance internLeaveBalance = new LeaveBalance(internJobDesignation.getAnnualLeaves());
        leaveBalanceService.save(internLeaveBalance);

        User internUser = new User(RoleEnum.ROLE_EMPLOYEE,
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

        internUser.setEmployee(intern);
        userService.save(internUser);
    }

    private void seedPartTime() {
        JobDesignation parttimeJobDesignation = jobDesignationService.findByName("parttime");
        Manager manager = employeeService.findManagerByName("Manager");
        LeaveBalance parttimeLeaveBalance = new LeaveBalance(parttimeJobDesignation.getAnnualLeaves());
        leaveBalanceService.save(parttimeLeaveBalance);

        User parttimeUser = new User(RoleEnum.ROLE_EMPLOYEE,
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

        parttimeUser.setEmployee(parttimeEmployee);
        userService.save(parttimeUser);
    }

    private void seedCleaning() {
        JobDesignation cleaningJobDesignation = jobDesignationService.findByName("cleaning");
        Manager manager = employeeService.findManagerByName("Manager");
        LeaveBalance cleaningLeaveBalance = new LeaveBalance(cleaningJobDesignation.getAnnualLeaves());
        leaveBalanceService.save(cleaningLeaveBalance);

        User cleaningUser = new User(RoleEnum.ROLE_EMPLOYEE,
                "cleaning",
                passwordEncoder.encode("cleaning"),
                "cleaning@example.com");
        userService.save(cleaningUser);

        Employee cleaningEmployee = new Employee("Andrew",
                cleaningJobDesignation,
                manager,
                cleaningLeaveBalance);
        cleaningEmployee.setUser(cleaningUser);
        employeeService.save(cleaningEmployee);

        cleaningUser.setEmployee(cleaningEmployee);
        userService.save(cleaningUser);
    }
}