package com.team4.leaveprocessingsystem.repository;

import com.team4.leaveprocessingsystem.model.JobDesignation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JobDesignationRepository extends JpaRepository<JobDesignation, Integer> {
    Optional<JobDesignation> findByName(String name);
}
