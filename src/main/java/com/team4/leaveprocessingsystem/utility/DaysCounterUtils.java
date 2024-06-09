package com.team4.leaveprocessingsystem.utility;

import com.team4.leaveprocessingsystem.service.PublicHolidayService;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

public class DaysCounterUtils {

    public static Long countWorkingDays(LocalDate startDate, LocalDate endDate, PublicHolidayService publicHolidayService){
        // If start and end date is on the same day, leave day is 1
        long numOfDays = endDate.toEpochDay() - startDate.toEpochDay() + 1;
        Long countWorkingDays = 0L;
        LocalDate currentDate = startDate;
        List<LocalDate> publicHolidays = publicHolidayService.publicHolidayDateList();

        for (int i = 0; i < numOfDays; i++) {
            if (currentDate.getDayOfWeek() != DayOfWeek.SATURDAY &&
                    currentDate.getDayOfWeek() != DayOfWeek.SUNDAY &&
                    !publicHolidays.contains(currentDate)) {
                countWorkingDays++;
            }
            currentDate = currentDate.plusDays(1);
        }
        return countWorkingDays;
    }

    public static Long countCalendarDays(LocalDate startDate, LocalDate endDate, PublicHolidayService publicHolidayService){
        return endDate.toEpochDay() - startDate.toEpochDay() + 1;
    }
}
