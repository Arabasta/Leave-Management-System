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

    // todo : test maanger's having a manager
    private void seedManagement() {
        JobDesignation managementJobDesignation = jobDesignationService.findByName("management");

        // Manager 1
        LeaveBalance leaveBalance1 = new LeaveBalance(managementJobDesignation.getDefaultAnnualLeaves());
        leaveBalanceService.save(leaveBalance1);

        Manager manager1 = new Manager("Madara Uchiha",
                managementJobDesignation,
                null,
                leaveBalance1);
        employeeService.save(manager1);

        User managerUser1 = new User(RoleEnum.ROLE_MANAGER,
                "manager",
                passwordEncoder.encode("manager"),
                "madara_uchiha@gmail.com",
                manager1);
        userService.save(managerUser1);

        // Manager 2
        LeaveBalance leaveBalance2 = new LeaveBalance(managementJobDesignation.getDefaultAnnualLeaves());
        leaveBalanceService.save(leaveBalance2);

        Manager manager2 = new Manager("Mikasa Ackerman",
                managementJobDesignation,
                null,
                leaveBalance2);
        employeeService.save(manager2);

        User managerUser2 = new User(RoleEnum.ROLE_MANAGER,
                "manager2",
                passwordEncoder.encode("manager"),
                "mikasa@gmail.com",
                manager2);
        userService.save(managerUser2);

        // Manager 3
        LeaveBalance leaveBalance3 = new LeaveBalance(managementJobDesignation.getDefaultAnnualLeaves());
        leaveBalanceService.save(leaveBalance3);

        Manager manager3 = new Manager("Muzan Kibutsuji",
                managementJobDesignation,
                null,
                leaveBalance3);
        employeeService.save(manager3);

        User managerUser3 = new User(RoleEnum.ROLE_MANAGER,
                "manager3",
                passwordEncoder.encode("manager"),
                "muzan@gmail.com",
                manager3);
        userService.save(managerUser3);
    }

    private void seedEmployee() {
        JobDesignation administrativeJobDesignation = jobDesignationService.findByName("administrative");

        // administrative employee 1 (with 2 users, admin and employee)
        Manager manager = employeeService.findManagerByName("Madara Uchiha");

        LeaveBalance adminLeaveBalance1 = new LeaveBalance(administrativeJobDesignation.getDefaultAnnualLeaves());
        leaveBalanceService.save(adminLeaveBalance1);

        Employee administrativeEmployee1 = new Employee("Anya Forger",
                administrativeJobDesignation,
                manager,
                adminLeaveBalance1);
        employeeService.save(administrativeEmployee1);

        User employeeUser1 = new User(RoleEnum.ROLE_EMPLOYEE,
                "employee",
                passwordEncoder.encode("employee"),
                "anya@example.com",
                administrativeEmployee1);
        userService.save(employeeUser1);

        User employeeAdminUser1 = new User(RoleEnum.ROLE_ADMIN,
                "admin",
                passwordEncoder.encode("adminadmin"),
                "anya@example.com",
                administrativeEmployee1);
        userService.save(employeeAdminUser1);


    }

    private void seedIntern() {
        JobDesignation internJobDesignation = jobDesignationService.findByName("intern");

        // intern 1
        Manager manager = employeeService.findManagerByName("Muzan Kibutsuji");

        LeaveBalance internLeaveBalance1 = new LeaveBalance(internJobDesignation.getDefaultAnnualLeaves());
        leaveBalanceService.save(internLeaveBalance1);

        Employee intern1 = new Employee("Inosuke",
                internJobDesignation,
                manager,
                internLeaveBalance1);
        employeeService.save(intern1);

        User internUser1 = new User(RoleEnum.ROLE_EMPLOYEE,
                "intern",
                passwordEncoder.encode("intern"),
                "inosuke@example.com",
                intern1);
        userService.save(internUser1);
    }

    private void seedCleaning() {
        JobDesignation cleaningJobDesignation = jobDesignationService.findByName("cleaning");

        // cleaning staff 1
        Manager manager = employeeService.findManagerByName("Madara Uchiha");
        LeaveBalance cleaningLeaveBalance = new LeaveBalance(cleaningJobDesignation.getDefaultAnnualLeaves());
        leaveBalanceService.save(cleaningLeaveBalance);

        Employee cleaningEmployee1 = new Employee("Andrew",
                cleaningJobDesignation,
                manager,
                cleaningLeaveBalance);
        employeeService.save(cleaningEmployee1);

        User cleaningUser1 = new User(RoleEnum.ROLE_EMPLOYEE,
                "cleaning",
                passwordEncoder.encode("cleaning"),
                "andrew@gmail.com",
                cleaningEmployee1);
        userService.save(cleaningUser1);


    }
}