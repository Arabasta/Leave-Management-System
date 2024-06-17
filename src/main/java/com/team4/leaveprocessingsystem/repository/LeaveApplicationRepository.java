package com.team4.leaveprocessingsystem.repository;

import com.team4.leaveprocessingsystem.model.Employee;
import com.team4.leaveprocessingsystem.model.LeaveApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface LeaveApplicationRepository extends JpaRepository<LeaveApplication, Integer> {
    @Query("SELECT L FROM LeaveApplication L WHERE L.reviewingManager.id = :managerId")
    List<LeaveApplication> findSubordinatesLeaveApplicationsByReviewingManager_Id(@Param("managerId") int managerId);

    List<LeaveApplication> findBySubmittingEmployee(Employee submittingEmployee);

    @Query("SELECT L FROM LeaveApplication L WHERE L.submittingEmployee.name LIKE %:name%")
    List<LeaveApplication> findByName(@Param("name") String name);

    @Query("SELECT L FROM LeaveApplication L WHERE L.submittingEmployee.id = :id")
    List<LeaveApplication> findBySubmittingEmployeeId(@Param("id") int id);

    @Query("SELECT L FROM LeaveApplication L WHERE L.id = :id AND L.submittingEmployee.id = :employeeId")
    Optional<LeaveApplication> findIfBelongsToEmployee(@Param("id") int id, @Param("employeeId") int employeeId);
}
