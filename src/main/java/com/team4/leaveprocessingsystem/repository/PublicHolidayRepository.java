package com.team4.leaveprocessingsystem.repository;

import com.team4.leaveprocessingsystem.model.PublicHoliday;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface PublicHolidayRepository extends JpaRepository<PublicHoliday, Integer> {
    Optional<PublicHoliday> findByDateAndHoliday(LocalDate date, String holiday);
}
