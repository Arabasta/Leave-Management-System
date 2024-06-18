package com.team4.leaveprocessingsystem.interfacemethods;

import com.team4.leaveprocessingsystem.model.PublicHoliday;

import java.time.LocalDate;
import java.util.List;

public interface IPublicHoliday {
    boolean save(PublicHoliday publicHoliday);
    List<LocalDate> publicHolidayDateList();
    List<PublicHoliday> findAll();
    List<PublicHoliday> findAll(String searchType, String query, String year);
    List<Integer> findAllYears();
}
