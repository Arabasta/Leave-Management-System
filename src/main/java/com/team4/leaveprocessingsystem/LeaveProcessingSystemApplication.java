package com.team4.leaveprocessingsystem;

import com.team4.leaveprocessingsystem.model.User;
import com.team4.leaveprocessingsystem.model.enums.AccessLevelEnum;
import com.team4.leaveprocessingsystem.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class LeaveProcessingSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(LeaveProcessingSystemApplication.class, args);
    }

//    @Bean
//    public CommandLineRunner commandLineRunner(UserRepository ur, PasswordEncoder passwordEncoder) {
//        return args -> {
//            if (ur.count() == 0) {
//                ur.save(
//                        new User(AccessLevelEnum.ROLE_ADMIN,
//                                "root",
//                                passwordEncoder.encode("root"),
//                                "root@gmail.com"));
//            }
//        };
//    }
}

