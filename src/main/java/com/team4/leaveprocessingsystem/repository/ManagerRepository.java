package com.team4.leaveprocessingsystem.repository;

import com.team4.leaveprocessingsystem.model.Manager;
import org.springframework.data.jpa.repository.JpaRepository;

;

public interface ManagerRepository extends JpaRepository<Manager, Integer> {
}
