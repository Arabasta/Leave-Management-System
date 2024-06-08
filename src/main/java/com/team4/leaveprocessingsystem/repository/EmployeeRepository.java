package com.team4.leaveprocessingsystem.repository;

import com.team4.leaveprocessingsystem.model.Employee;
import com.team4.leaveprocessingsystem.model.Manager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
    Optional<Employee> findByName(String name);
    List<Employee> findByManager(Manager manager);

    @Query("Select emp from Employee as emp where emp.name like CONCAT('%', :k, '%') ")
    public List<Employee> SearchEmployeeByName(@Param("k") String keyword);

    // TODO: fix query string
    /*
    @Query(nativeQuery = true,
            value = "Select e.*, JD.name from Employee E " +
                    "join JobDesignation JD " +
                    "on e.id = JD.id " +
                    "where JD.name like CONCAT('%', :k, '%') ")
    List<Employee> findEmployeeByJobDesignation(@Param("k") String keyword);

     */

    // TODO: fix query string
    /*
    @Query("Select u " +
            "from User U " +
            "where cast(u.role as STRING) like concat('%', :k, '%') ")
    List<Employee> findUserByRoleType(@Param("k") String keyword);
     */

}
