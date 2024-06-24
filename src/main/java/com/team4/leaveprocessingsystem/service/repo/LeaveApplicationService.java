package com.team4.leaveprocessingsystem.service.repo;

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
import org.springframework.cglib.core.Local;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
    public Page<LeaveApplication> filterManagerViewSearch(int managerId,
                                                          String keyword,
                                                          String searchType,
                                                          String startDate,
                                                          String endDate,
                                                          String leaveStatus,
                                                          Pageable pageable) {

        int employeeId = 0;
        String employeeName = null;
        // include wrapper %'s to employeeName String for Query
        if (Objects.equals(searchType, "name")) {
            employeeName = "%" + StringCleaningUtil.forDatabase(keyword) + "%";
        }
        if (Objects.equals(searchType, "id")) {
            try {
                employeeId = Integer.parseInt(keyword);
            } catch (NumberFormatException e) {
                System.out.println(e.getMessage());
            }
        }
        LocalDate startDateFormatted = null;
        LocalDate endDateFormatted = null;
        if (startDate != null && endDate != null && !startDate.isBlank() && !endDate.isBlank()) {
            DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            startDateFormatted = LocalDate.parse(startDate, df);
            endDateFormatted = LocalDate.parse(endDate, df);
        }
        LeaveStatusEnum leaveStatusEnum;
        if (Objects.equals(leaveStatus, "ALL")) {
            leaveStatusEnum = null;
        } else {
            leaveStatusEnum = LeaveStatusEnum.valueOf(leaveStatus);
        }
        return leaveApplicationRepository.findByNameOrIdAndDateRangeAndStatus(managerId,
                                                                            employeeId,
                                                                            employeeName,
                                                                            startDateFormatted,
                                                                            endDateFormatted,
                                                                            leaveStatusEnum,
                                                                            pageable);
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