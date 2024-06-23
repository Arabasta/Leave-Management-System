package com.team4.leaveprocessingsystem.repository;

import com.team4.leaveprocessingsystem.model.Employee;
import com.team4.leaveprocessingsystem.model.LeaveApplication;
import com.team4.leaveprocessingsystem.model.enums.LeaveStatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface LeaveApplicationRepository extends JpaRepository<LeaveApplication, Integer> {
    List<LeaveApplication> findBySubmittingEmployee(Employee submittingEmployee);

    @Query("SELECT L FROM LeaveApplication L WHERE L.submittingEmployee = :employee ORDER BY L.id DESC")
    Page<LeaveApplication> findBySubmittingEmployeeWithPaging(@Param("employee") Employee submittingEmployee, Pageable page);

    @Query("SELECT L FROM LeaveApplication L WHERE L.id = :id AND L.submittingEmployee.id = :employeeId")
    Optional<LeaveApplication> findIfBelongsToEmployee(@Param("id") int id, @Param("employeeId") int employeeId);

    @Query("SELECT L FROM LeaveApplication L WHERE YEAR(L.endDate) = :year AND MONTH(L.endDate) = :month AND L.leaveStatus = 'APPROVED'")
    List<LeaveApplication> findApprovedForYearMonth(@Param("year") String year, @Param("month") String month);

    @Query("SELECT L FROM LeaveApplication L " +
            "WHERE L.reviewingManager.id = :managerId " +
            "AND (:employeeId = 0 OR L.submittingEmployee.id = :employeeId) " +
            "AND (:employeeName is NULL OR L.submittingEmployee.name LIKE :employeeName) " +
            "AND (:startDate is NULL OR L.startDate >= :startDate) " +
            "AND (:endDate is NULL OR L.endDate <= :endDate) " +
            "AND (:leaveStatus is NULL OR L.leaveStatus = :leaveStatus) ")
    Page<LeaveApplication> findByNameOrIdAndDateRangeAndStatus(@Param("managerId") int managerId,
                                                               @Param("employeeId") int employeeId,
                                                               @Param("employeeName") String employeeName,
                                                               @Param("startDate") LocalDate startDate,
                                                               @Param("endDate") LocalDate endDate,
                                                               @Param("leaveStatus") LeaveStatusEnum leaveStatus,
                                                               Pageable pageable);
}
