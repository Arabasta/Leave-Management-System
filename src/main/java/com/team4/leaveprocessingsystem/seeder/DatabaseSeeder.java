package com.team4.leaveprocessingsystem.seeder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class DatabaseSeeder {
    @Autowired
    private JobDesignationSeeder jobDesignationSeeder;
    @Autowired
    private EmployeeSeeder employeeSeeder;
    @Autowired
    private PublicHolidaySeeder publicHolidaySeeder;
    @Autowired
    private LeaveApplicationSeeder leaveApplicationSeeder;

    @Bean
    public CommandLineRunner commandLineRunner(EmployeeSeeder employeeSeeder,
                                               PublicHolidaySeeder publicHolidaySeeder,
                                               JobDesignationSeeder jobDesignationSeeder,
                                               LeaveApplicationSeeder leaveApplicationSeeder) {
        return args -> {
            jobDesignationSeeder.seed();
            publicHolidaySeeder.seed();

            employeeSeeder.seed();
           // leaveApplicationSeeder.seed(); // broken
        };
    }
}
