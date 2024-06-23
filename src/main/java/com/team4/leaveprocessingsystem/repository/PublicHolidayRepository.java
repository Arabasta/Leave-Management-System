package com.team4.leaveprocessingsystem.repository;

import com.team4.leaveprocessingsystem.model.PublicHoliday;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PublicHolidayRepository extends JpaRepository<PublicHoliday, Integer> {
    Optional<PublicHoliday> findByDateAndHoliday(LocalDate date, String holiday);
    @Query("SELECT ph FROM PublicHoliday ph WHERE ph.date BETWEEN :startDate AND :endDate ORDER BY ph.date DESC")
    List<PublicHoliday> findByDateBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    @Query("SELECT ph FROM PublicHoliday ph ORDER BY ph.date DESC")
    List<PublicHoliday> findAll();
    @Query("SELECT DISTINCT YEAR(ph.date) FROM PublicHoliday ph")
    List<Integer> findDistinctYears();
}
