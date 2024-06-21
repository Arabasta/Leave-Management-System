package com.team4.leaveprocessingsystem.repository;

import com.team4.leaveprocessingsystem.model.JobDesignation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface JobDesignationRepository extends JpaRepository<JobDesignation, Integer> {
    Optional<JobDesignation> findByName(String name);

    @Query("Select jd from JobDesignation as jd where jd.name like CONCAT('%', :k, '%') ")
    List<JobDesignation> queryJobDesignationsByName(@Param("k") String keyword);
}
