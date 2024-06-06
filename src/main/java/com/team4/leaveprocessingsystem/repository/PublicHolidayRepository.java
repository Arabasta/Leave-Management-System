package com.team4.leaveprocessingsystem.repository;

import com.team4.leaveprocessingsystem.model.PublicHoliday;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PublicHolidayRepository extends JpaRepository<PublicHoliday, Integer> {
}
