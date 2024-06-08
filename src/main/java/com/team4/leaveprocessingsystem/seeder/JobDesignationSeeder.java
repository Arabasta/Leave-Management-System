package com.team4.leaveprocessingsystem.seeder;

import com.team4.leaveprocessingsystem.model.JobDesignation;
import com.team4.leaveprocessingsystem.repository.JobDesignationRepository;
import org.springframework.stereotype.Service;

@Service
public class JobDesignationSeeder {

    private final JobDesignationRepository jobDesignationRepository;

    public JobDesignationSeeder(JobDesignationRepository jobDesignationRepository) {
        this.jobDesignationRepository = jobDesignationRepository;
    }

    public void seed() {
        if (jobDesignationRepository.count() == 0) {
            JobDesignation administrative = new JobDesignation("administrative", 14);
            jobDesignationRepository.save(administrative);

            JobDesignation management = new JobDesignation("management", 30);
            jobDesignationRepository.save(management);

            JobDesignation cleaning = new JobDesignation("cleaning", 3);
            jobDesignationRepository.save(cleaning);

            JobDesignation intern = new JobDesignation("intern", 10);
            jobDesignationRepository.save(intern);
        }
    }
}
