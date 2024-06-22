package com.team4.leaveprocessingsystem.User;

import com.team4.leaveprocessingsystem.model.Employee;
import com.team4.leaveprocessingsystem.model.JobDesignation;
import com.team4.leaveprocessingsystem.model.LeaveBalance;
import com.team4.leaveprocessingsystem.model.User;
import com.team4.leaveprocessingsystem.repository.EmployeeRepository;
import com.team4.leaveprocessingsystem.repository.JobDesignationRepository;
import com.team4.leaveprocessingsystem.repository.LeaveBalanceRepository;
import com.team4.leaveprocessingsystem.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static com.team4.leaveprocessingsystem.ObjectMother.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class UserRepositoryTest {
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private LeaveBalanceRepository leaveBalanceRepository;
    @Autowired
    private JobDesignationRepository jobDesignationRepository;
    @Autowired
    private UserRepository userRepository;

    private Employee testEmployee;
    private User testUser;
    private JobDesignation testJobDesignation3;

    @BeforeEach()
    void init() {
        System.out.println("startup");

        testJobDesignation3 = jobDesignationRepository.save(createJobDesignation("Test"));
        LeaveBalance testLeaveBalance3 = leaveBalanceRepository.save(createLeaveBalance());

        // Set up Employee object
        Employee e = createEmployee(testJobDesignation3, testLeaveBalance3);
        e.setName("test");
        testEmployee = employeeRepository.save(e);

        // set up User object
        User u = createUser(testEmployee);
        this.testUser = userRepository.save(u);

        System.out.println("startup executed");
    }

    @Test
    public void testFindByUserName() {

        Optional<User> retrievedUser = userRepository.findByUsername("ObjectMotherUsername");
        assertThat(retrievedUser).isNotNull();
    }

     @Test
    public void testFindByEmail() {
        Optional<User> retrievedUser = userRepository.findByEmail("ObjectMotherEmail@Email.com");
        assertThat(retrievedUser).isNotNull();
    }

    @Test
    public void testFindByEmployeeId() {
        List<User> retrievedUsers = userRepository.findByEmployeeId(testEmployee.getId());
        assertThat(retrievedUsers).size().isEqualTo(1);
        assertThat(retrievedUsers.get(0).getId()).isEqualTo(testUser.getId());
    }

    @Test
    public void testFindUserRolesByEmployeeId() {
        List<User> retrievedUsers = userRepository.findUserRolesByEmployeeId(testEmployee.getId());
        assertThat(retrievedUsers.size()).isEqualTo(1);
        assertThat(retrievedUsers.get(0).getRole()).isEqualTo(testUser.getRole());
    }

    /*
    @Query("Select u from User u join u.employee userEmployee where userEmployee.id = :k")
    List<User> findUserRolesByEmployeeId(@Param("k") Integer employeeId);

    User findById(int id);

    @Query("Select u from User u " +
        "where CAST(u.role as String) like CONCAT('%', :k, '%')")
    List<User> findUsersByRoleType(@Param("k") String keyword);

    @Query("select u from User u where u.username like CONCAT('%', :k, '%')")
    List<User> findUsersByUsername(@Param("k") String keyword);

    @Query("Select u from User u where u.email like CONCAT('%', :k, '%')")
    List<User> findUsersByEmail(@Param("k") String keyword);

    @Query("Select u from User u where u.id = :id")
    List<User> findUsersById(@Param("id") int id );
    */

    @AfterEach()
    void teardown() {
        System.out.println("teardown");
        userRepository.delete(testUser);
        employeeRepository.delete(testEmployee);
        jobDesignationRepository.delete(testJobDesignation3);
        System.out.println("teardown executed");
    }
}
