package com.team4.leaveprocessingsystem.seeder;

import com.team4.leaveprocessingsystem.model.PublicHoliday;
import com.team4.leaveprocessingsystem.repository.PublicHolidayRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class PublicHolidaySeeder {

    private final PublicHolidayRepository publicHolidayRepository;

    public PublicHolidaySeeder(PublicHolidayRepository publicHolidayRepository) {
        this.publicHolidayRepository = publicHolidayRepository;
    }
    // try get from API

    public void seed() {
        if (publicHolidayRepository.count() == 0) {
            PublicHoliday publicHoliday = new PublicHoliday();
            publicHoliday.setDate(LocalDate.of(2024, 12, 25));
            publicHoliday.setHoliday("Christmas");
            publicHolidayRepository.save(publicHoliday);
        }
    }
}