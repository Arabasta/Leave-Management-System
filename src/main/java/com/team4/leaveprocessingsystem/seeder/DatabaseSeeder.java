package com.team4.leaveprocessingsystem.seeder;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class DatabaseSeeder {

    @Bean
    public CommandLineRunner commandLineRunner(EmployeeSeeder employeeSeeder,
                                               PublicHolidaySeeder publicHolidaySeeder,
                                               JobDesignationSeeder jobDesignationSeeder,
                                               LeaveApplicationSeeder leaveApplicationSeeder,
                                               CompensationClaimSeeder compensationClaimSeeder,
                                               LeaveTypeSeeder leaveTypeSeeder) {
        return args -> {
            jobDesignationSeeder.seed();
            publicHolidaySeeder.seed();
            employeeSeeder.seed();
//            leaveApplicationSeeder.seed();
            compensationClaimSeeder.seed();
            leaveTypeSeeder.seed();
        };
    }
}
