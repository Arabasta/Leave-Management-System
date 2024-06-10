package com.team4.leaveprocessingsystem.service;

import com.team4.leaveprocessingsystem.exception.ServiceSaveException;
import com.team4.leaveprocessingsystem.interfacemethods.IJobDesignation;
import com.team4.leaveprocessingsystem.model.JobDesignation;
import com.team4.leaveprocessingsystem.repository.JobDesignationRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobDesignationService implements IJobDesignation {
    @Autowired
    JobDesignationRepository jobDesignationRepository;

    @Override
    @Transactional
    public boolean save(JobDesignation jobDesignation) {
        try {
            jobDesignationRepository.save(jobDesignation);
            return true;
        } catch (Exception e) {
            throw new ServiceSaveException(jobDesignation.getName(), e);
        }
    }

    @Override
    @Transactional
    public JobDesignation findByName(String name) {
        return jobDesignationRepository.findByName(name)
                .orElseThrow(() -> new EntityNotFoundException("Role " + name + " not found"));
    }

    @Override
    @Transactional
    public List<JobDesignation> listAllJobDesignations() {
        return jobDesignationRepository.findAll();
    }
}
