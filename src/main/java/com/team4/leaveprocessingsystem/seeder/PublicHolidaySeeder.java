package com.team4.leaveprocessingsystem.seeder;

import com.team4.leaveprocessingsystem.service.api.PublicHolidayApiService;
import org.springframework.stereotype.Service;

@Service
public class PublicHolidaySeeder {

    private final PublicHolidayApiService publicHolidayApiService;

    public PublicHolidaySeeder(PublicHolidayApiService publicHolidayApiService) {
        this.publicHolidayApiService = publicHolidayApiService;
    }

    public void seed() {
        publicHolidayApiService.getPublicHolidayDatasets();
    }

}