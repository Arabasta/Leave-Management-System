package com.team4.leaveprocessingsystem.util;

import com.team4.leaveprocessingsystem.model.enums.LeaveTypeEnum;
import com.team4.leaveprocessingsystem.service.PublicHolidayService;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class DateTimeCounterUtils {

    public static Long countWorkingDays(LocalDate startDate, LocalDate endDate, PublicHolidayService publicHolidayService){
        // If start and end date is on the same day, leave day is 1
        long numOfDays = endDate.toEpochDay() - startDate.toEpochDay() + 1;
        Long countWorkingDays = 0L;
        LocalDate currentDate = startDate;
        List<LocalDate> publicHolidays = publicHolidayService.publicHolidayDateList();

        for (int i = 0; i < numOfDays; i++) {
            if (!isWeekend(currentDate) && !publicHolidays.contains(currentDate)) {
                countWorkingDays++;
            }
            currentDate = currentDate.plusDays(1);
        }
        return countWorkingDays;
    }

    public static Long countCalendarDays(LocalDate startDate, LocalDate endDate){
        return endDate.toEpochDay() - startDate.toEpochDay() + 1;
    }

    public static Long numOfLeaveToBeCounted(LocalDate startDate, LocalDate endDate, LeaveTypeEnum leaveType, PublicHolidayService publicHolidayService){
        Long numOfCalendarDays = DateTimeCounterUtils.countCalendarDays(startDate, endDate);
        Long numOfWorkingDays = DateTimeCounterUtils.countWorkingDays(startDate, endDate, publicHolidayService);
        switch(leaveType) {
            case MEDICAL:
            case COMPASSIONATE:
            case COMPENSATION:
            case UNPAID:
                return numOfWorkingDays;
            case ANNUAL:
                if (numOfCalendarDays <= 14) {
                    return numOfWorkingDays;
                } else {
                    return numOfCalendarDays;
                }
        }
        return 0L;
    }

    public static boolean isWeekend(LocalDate date){
        if (date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY){
            return true;
        }
        return false;
    }
}
