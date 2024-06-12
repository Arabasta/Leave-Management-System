package com.team4.leaveprocessingsystem.seeder;

import com.team4.leaveprocessingsystem.model.*;
import com.team4.leaveprocessingsystem.model.enums.RoleEnum;
import com.team4.leaveprocessingsystem.service.EmployeeService;
import com.team4.leaveprocessingsystem.service.JobDesignationService;
import com.team4.leaveprocessingsystem.service.LeaveBalanceService;
import com.team4.leaveprocessingsystem.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class EmployeeSeeder {

    private final EmployeeService employeeService;
    private final LeaveBalanceService leaveBalanceService;
    private final JobDesignationService jobDesignationService;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    public EmployeeSeeder(EmployeeService employeeService,
                          LeaveBalanceService leaveBalanceService,
                          JobDesignationService jobDesignationService,
                          PasswordEncoder passwordEncoder, UserService userService) {
        this.employeeService = employeeService;
        this.leaveBalanceService = leaveBalanceService;
        this.jobDesignationService = jobDesignationService;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
    }

    public void seed() {
        if (employeeService.count() == 0) {
            seedManagement();
            seedEmployee();
            seedIntern();
            seedCleaning();
        }
    }

    private void seedManagement() {
        JobDesignation managementJobDesignation = jobDesignationService.findByName("management");

        LeaveBalance managerLeaveBalance = new LeaveBalance(managementJobDesignation.getDefaultAnnualLeaves());
        leaveBalanceService.save(managerLeaveBalance);

        Manager manager = new Manager("Manager",
                managementJobDesignation,
                null,
                managerLeaveBalance);
        employeeService.save(manager);

        User managerUser = new User(RoleEnum.ROLE_MANAGER,
                "manager",
                passwordEncoder.encode("manager"),
                "manager@example.com", manager);
        userService.save(managerUser);

    }

    private void seedEmployee() {
        JobDesignation administrativeJobDesignation = jobDesignationService.findByName("administrative");
        Manager manager = employeeService.findManagerByName("Manager");

        LeaveBalance adminLeaveBalance = new LeaveBalance(administrativeJobDesignation.getDefaultAnnualLeaves());
        leaveBalanceService.save(adminLeaveBalance);

        Employee administrativeEmployee = new Employee("Employee",
                administrativeJobDesignation,
                manager,
                adminLeaveBalance);
        employeeService.save(administrativeEmployee);

        User employeeUser = new User(RoleEnum.ROLE_EMPLOYEE,
                "employee",
                passwordEncoder.encode("employee"),
                "employee@example.com", administrativeEmployee);
        userService.save(employeeUser);

        User employeeAdminUser = new User(RoleEnum.ROLE_ADMIN,
                "admin",
                passwordEncoder.encode("adminadmin"),
                "employee@example.com", administrativeEmployee);
        userService.save(employeeAdminUser);
    }

    private void seedIntern() {
//        JobDesignation internJobDesignation = jobDesignationService.findByName("intern");
//        Manager manager = employeeService.findManagerByName("Manager");
//        LeaveBalance internLeaveBalance = new LeaveBalance(internJobDesignation.getDefaultAnnualLeaves());
//        leaveBalanceService.save(internLeaveBalance);
//
//        User internUser = new User(RoleEnum.ROLE_EMPLOYEE,
//                "intern",
//                passwordEncoder.encode("intern"),
//                "intern@example.com");
//
//        Employee intern = new Employee("Intern",
//                internJobDesignation,
//                manager,
//                internLeaveBalance);
//        intern.addUser(internUser);
//
//        internUser.setEmployee(intern);
//
//        employeeService.save(intern);
    }

    private void seedCleaning() {
//        JobDesignation cleaningJobDesignation = jobDesignationService.findByName("cleaning");
//        Manager manager = employeeService.findManagerByName("Manager");
//        LeaveBalance cleaningLeaveBalance = new LeaveBalance(cleaningJobDesignation.getDefaultAnnualLeaves());
//        leaveBalanceService.save(cleaningLeaveBalance);
//
//        User cleaningUser = new User(RoleEnum.ROLE_EMPLOYEE,
//                "cleaning",
//                passwordEncoder.encode("cleaning"),
//                "cleaning@example.com");
//
//        Employee cleaningEmployee = new Employee("Andrew",
//                cleaningJobDesignation,
//                manager,
//                cleaningLeaveBalance);
//        cleaningEmployee.addUser(cleaningUser);
//
//        cleaningUser.setEmployee(cleaningEmployee);
//
//        employeeService.save(cleaningEmployee);
    }
}