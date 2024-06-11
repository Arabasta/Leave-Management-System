package com.team4.leaveprocessingsystem.seeder;

import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
@Transactional
public class DatabaseSeeder {

    @Bean
    public CommandLineRunner commandLineRunner(EmployeeSeeder employeeSeeder,
                                               PublicHolidaySeeder publicHolidaySeeder,
                                               JobDesignationSeeder jobDesignationSeeder,
                                               LeaveApplicationSeeder leaveApplicationSeeder,
                                               CompensationClaimSeeder compensationClaimSeeder) {
        return args -> {
            jobDesignationSeeder.seed();
            publicHolidaySeeder.seed();
            employeeSeeder.seed();
            leaveApplicationSeeder.seed();
            compensationClaimSeeder.seed();
        };
    }
}
