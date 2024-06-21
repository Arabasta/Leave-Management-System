package com.team4.leaveprocessingsystem.interfacemethods;

import com.team4.leaveprocessingsystem.model.JobDesignation;
import jakarta.transaction.Transactional;

import java.util.List;

public interface IJobDesignation {
    boolean save(JobDesignation jobDesignation);
    JobDesignation findByName(String name);

    List<JobDesignation> listAllJobDesignations();

    JobDesignation findJobDesignationById(Integer id);

    @Transactional
    List<JobDesignation> queryJobDesignationsByName(String name);
}