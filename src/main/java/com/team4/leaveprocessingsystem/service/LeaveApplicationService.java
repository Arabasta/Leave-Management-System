package com.team4.leaveprocessingsystem.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.team4.leaveprocessingsystem.exception.LeaveApplicationNotFoundException;
import com.team4.leaveprocessingsystem.exception.ServiceSaveException;
import com.team4.leaveprocessingsystem.interfacemethods.ILeaveApplication;
import com.team4.leaveprocessingsystem.model.Employee;
import com.team4.leaveprocessingsystem.model.LeaveApplication;
import com.team4.leaveprocessingsystem.model.Manager;
import com.team4.leaveprocessingsystem.model.enums.LeaveStatusEnum;
import com.team4.leaveprocessingsystem.repository.EmployeeRepository;
import com.team4.leaveprocessingsystem.repository.LeaveApplicationRepository;
import com.team4.leaveprocessingsystem.util.StringCleaningUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class LeaveApplicationService implements ILeaveApplication {
    private final LeaveApplicationRepository leaveApplicationRepository;
    private final EmployeeRepository employeeRepository;

    @Autowired
    public LeaveApplicationService(LeaveApplicationRepository leaveApplicationRepository, EmployeeRepository employeeRepository) {
        this.leaveApplicationRepository = leaveApplicationRepository;
        this.employeeRepository = employeeRepository;
    }

    @Override
    @Transactional
    public boolean save(LeaveApplication leaveApplication) {
        try {
            leaveApplicationRepository.save(leaveApplication);
            return true;
        } catch (Exception e) {
            throw new ServiceSaveException("leave application from: " +
                    leaveApplication.getSubmittingEmployee().getName(), e);
        }
    }

    @Override
    @Transactional
    public LeaveApplication findLeaveApplicationById(Integer id){
        return leaveApplicationRepository.findById(id)
                .orElseThrow(() -> new LeaveApplicationNotFoundException("Leave Application Not Found"));
    }

    @Override
    @Transactional
    public LeaveApplication getLeaveApplicationIfBelongsToEmployee(int id, int employeeId) {
        return leaveApplicationRepository.findIfBelongsToEmployee(id, employeeId)
                .orElseThrow(() -> new LeaveApplicationNotFoundException("Leave Application Not Found"));
    }

    @Override
    @Transactional
    public long count() {
        return leaveApplicationRepository.count();
    }

    @Override
    @Transactional
    public List<LeaveApplication> findSubordinatesLeaveApplicationsByReviewingManager_Id(int managerId){
        return leaveApplicationRepository.findSubordinatesLeaveApplicationsByReviewingManager_Id(managerId);
    }

    @Override
    @Transactional
    public List<LeaveApplication> findBySubmittingEmployee(Employee submittingEmployee) {
        return leaveApplicationRepository.findBySubmittingEmployee(submittingEmployee);
    }

    @Override
    @Transactional
    public Page<LeaveApplication> findBySubmittingEmployeeWithPaging(Employee submittingEmployee, Pageable page) {
        return leaveApplicationRepository.findBySubmittingEmployeeWithPaging(submittingEmployee, page);
    }

    @Override
    @Transactional
    public Map<String, List<LeaveApplication>> findLeaveApplicationsPendingApprovalByManager(Manager manager) {
        Map<String, List<LeaveApplication>> pendingLeaveApplications = new HashMap<>();
        List<Employee> employeeList = employeeRepository.findByManager(manager);
        for (Employee employee : employeeList) {
            List<LeaveApplication> employeePendingLeaveApplications = findBySubmittingEmployee(employee)
                    .stream()
                    .filter(application -> application.getLeaveStatus() == LeaveStatusEnum.APPLIED
                            || application.getLeaveStatus() == LeaveStatusEnum.UPDATED)
                    .toList();
            if (!employeePendingLeaveApplications.isEmpty()) {
                pendingLeaveApplications.put(employee.getName(), employeePendingLeaveApplications);
            }
        }
        return pendingLeaveApplications;
    }

    @Override
    @Transactional
    public List<LeaveApplication> findByEmployeeName(String name) {
        return leaveApplicationRepository.findByName(name);
    }

    @Override
    @Transactional
    public List<LeaveApplication> findByEmployeeId(int id) {
        return leaveApplicationRepository.findBySubmittingEmployeeId(id);
    }

    @Override
    @Transactional
    public List<LeaveApplication> getLeaveApplicationIfBelongsToManagerSubordinates(List<LeaveApplication> applications, int managerId) {
        List<LeaveApplication> list = new ArrayList<>();
        for (LeaveApplication application : applications) {
            if (application.getReviewingManager() != null && application.getReviewingManager().getId() == managerId) {
                list.add(application);
            }
        }
        return list;
    }

    public List<LeaveApplication> filterByStringDateRange(List<LeaveApplication> applications, String start, String end) {
        try {
            return applications.stream()
                    .filter(x -> !x.getStartDate().isAfter(LocalDate.parse(end))
                            && !x.getEndDate().isBefore(LocalDate.parse(start))
                    ).toList();
        } catch (DateTimeParseException e) {
            System.out.println(e + ": " + e.getMessage());
            return applications;
        }
    }

    public List<LeaveApplication> filterByStringLeaveStatus(List<LeaveApplication> applications, String leaveStatus) {
        return applications.stream()
                .filter(x -> x.getLeaveStatus().name().equals(leaveStatus))
                .toList();
    }

    public ArrayList<LeaveApplication> setArrayList(List<LeaveApplication> list) {
        ArrayList<LeaveApplication> output = new ArrayList<>();
        try {
            output.addAll(list);
        } catch (NullPointerException e) {
            System.out.println(e + ": " + e.getMessage());
        }
        return output;
    }

    @Override
    @Transactional
    public List<LeaveApplication> filterManagerViewSearch(int managerId,
                                                          String keyword,
                                                          String searchType,
                                                          String startDate,
                                                          String endDate,
                                                          String leaveStatus) {

        List<LeaveApplication> applications = findSubordinatesLeaveApplicationsByReviewingManager_Id(managerId);

        if (Objects.equals(searchType, "name")) {
            applications = getLeaveApplicationIfBelongsToManagerSubordinates(
                    findByEmployeeName(StringCleaningUtil.forDatabase(keyword)), managerId);
        }
        if (Objects.equals(searchType, "id")) {
            try {
                int id = Integer.parseInt(keyword);
                applications = findByEmployeeId(id);
            } catch (NumberFormatException e) {
                System.out.println(e.getMessage());
                applications.clear();
            }
        }
        if (startDate != null && endDate != null && !startDate.isBlank() && !endDate.isBlank()) {
            applications = filterByStringDateRange(applications, startDate, endDate);
        }
        if (!Objects.equals(leaveStatus, "ALL")) {
            applications = filterByStringLeaveStatus(applications, leaveStatus);
        }
        return applications;
    }

    public Map<String, List<LeaveApplication>> mapEmployeeOnLeave(String targetYearMonth) {
        String year;
        String month;
        if (targetYearMonth == null || targetYearMonth.isBlank()) {
            year = String.valueOf(LocalDate.now().getYear());
            month = String.valueOf(LocalDate.now().getMonth().getValue());
        } else {
            year = targetYearMonth.substring(0,4);
            month = targetYearMonth.substring(5,7);
        }
        // get list of applications between during year month.
        List<LeaveApplication> applicationsList = leaveApplicationRepository.findApprovedForYearMonth(year, month);

        // setup empty map
        Map<String, List<LeaveApplication>> map = new HashMap<>();

        // loop through LeaveApplications and add to map
        for(LeaveApplication application : applicationsList) {
            String employeeName = application.getSubmittingEmployee().getName();
            if(!map.containsKey(employeeName)) {
                List<LeaveApplication> list = new ArrayList<>();
                list.add(application);
                map.put(employeeName, list);
            } else {
                map.get(employeeName).add(application);
            }
        }
        return map;
    }
}