package com.team4.leaveprocessingsystem.service;

import com.team4.leaveprocessingsystem.Exceptions.ServiceSaveException;
import com.team4.leaveprocessingsystem.interfacemethods.IPublicHoliday;
import com.team4.leaveprocessingsystem.model.PublicHoliday;
import com.team4.leaveprocessingsystem.repository.PublicHolidayRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PublicHolidayService implements IPublicHoliday {
    @Autowired
    private PublicHolidayRepository publicHolidayRepository;

    @Override
    @Transactional
    public boolean save(PublicHoliday publicHoliday) {
        try {
            publicHolidayRepository.save(publicHoliday);
            return true;
        } catch (Exception e) {
            throw new ServiceSaveException(publicHoliday.getHoliday(), e);
        }
    }
}
