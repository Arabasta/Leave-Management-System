package com.team4.leaveprocessingsystem.interfacemethods;

import com.team4.leaveprocessingsystem.model.JobDesignation;

public interface IJobDesignation {
    boolean save(JobDesignation jobDesignation);
    JobDesignation findByName(String name);
}
