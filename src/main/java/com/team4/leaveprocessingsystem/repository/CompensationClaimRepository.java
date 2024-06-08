package com.team4.leaveprocessingsystem.repository;

import com.team4.leaveprocessingsystem.model.CompensationClaim;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompensationClaimRepository extends JpaRepository<CompensationClaim, Integer> {
}
