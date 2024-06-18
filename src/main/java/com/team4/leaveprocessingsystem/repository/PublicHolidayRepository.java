package com.team4.leaveprocessingsystem.repository;

import com.team4.leaveprocessingsystem.model.PublicHoliday;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PublicHolidayRepository extends JpaRepository<PublicHoliday, Integer> {
    Optional<PublicHoliday> findByDateAndHoliday(LocalDate date, String holiday);
    List<PublicHoliday> findByDateBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT DISTINCT YEAR(ph.date) FROM PublicHoliday ph")
    List<Integer> findDistinctYears();
}
