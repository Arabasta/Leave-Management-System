package com.team4.leaveprocessingsystem.service;

import com.team4.leaveprocessingsystem.exception.LeaveApplicationNotFoundException;
import com.team4.leaveprocessingsystem.exception.ServiceSaveException;
import com.team4.leaveprocessingsystem.interfacemethods.ILeaveApplication;
import com.team4.leaveprocessingsystem.model.CompensationClaim;
import com.team4.leaveprocessingsystem.model.Employee;
import com.team4.leaveprocessingsystem.model.LeaveApplication;
import com.team4.leaveprocessingsystem.model.Manager;
import com.team4.leaveprocessingsystem.model.enums.LeaveStatusEnum;
import com.team4.leaveprocessingsystem.repository.EmployeeRepository;
import com.team4.leaveprocessingsystem.repository.LeaveApplicationRepository;
import com.team4.leaveprocessingsystem.util.StringCleaningUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;

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
}
