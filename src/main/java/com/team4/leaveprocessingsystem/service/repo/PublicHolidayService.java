package com.team4.leaveprocessingsystem.service.repo;

import com.team4.leaveprocessingsystem.exception.ServiceSaveException;
import com.team4.leaveprocessingsystem.interfacemethods.IPublicHoliday;
import com.team4.leaveprocessingsystem.model.PublicHoliday;
import com.team4.leaveprocessingsystem.repository.PublicHolidayRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PublicHolidayService implements IPublicHoliday {
    private final PublicHolidayRepository publicHolidayRepository;

    @Autowired
    public PublicHolidayService(PublicHolidayRepository publicHolidayRepository) {
        this.publicHolidayRepository = publicHolidayRepository;
    }
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

    public Optional<PublicHoliday> findByDateAndHoliday(LocalDate date, String holiday) {
        return publicHolidayRepository.findByDateAndHoliday(date, holiday);
    }

    @Override
    public List<LocalDate> publicHolidayDateList() {
        List<PublicHoliday> publicHolidayList =  publicHolidayRepository.findAll();
        return publicHolidayList.stream().map(PublicHoliday::getDate).collect(Collectors.toList());
    }


    public Optional<PublicHoliday> findById(Integer id) {
        // Log the ID to ensure it's correctly passed
        System.out.println("Finding public holiday with ID: " + id);
        return publicHolidayRepository.findById(id);
    }

    public boolean update(PublicHoliday publicHoliday) {
        if (publicHolidayRepository.existsById(publicHoliday.getId())) {
            publicHolidayRepository.save(publicHoliday);
            return true;
        }
        return false;
    }

    public boolean deleteById(Integer id) {
        try {
            publicHolidayRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public List<PublicHoliday> findAll(String searchType, String query, String year) {
        if (year != null && !year.isEmpty()) {
            LocalDate startDate = LocalDate.of(Integer.parseInt(year), 1, 1);
            LocalDate endDate = LocalDate.of(Integer.parseInt(year), 12, 31);
            return publicHolidayRepository.findByDateBetween(startDate, endDate);
        }
        return publicHolidayRepository.findAll();
    }

    @Override
    public List<PublicHoliday> findAll() {
        return publicHolidayRepository.findAll();
    }

    @Override
    public List<Integer> findAllYears() {
        return publicHolidayRepository.findDistinctYears();
    }

}
