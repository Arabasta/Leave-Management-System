package com.team4.leaveprocessingsystem.security;

import com.team4.leaveprocessingsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

// source: https://docs.spring.io/spring-security/reference/servlet/getting-started.html
// source: https://spring.io/guides/gs/securing-web
// source: https://spring.io/blog/2022/02/21/spring-security-without-the-websecurityconfigureradapter
// to check : https://stackoverflow.com/questions/74753700/cannot-resolve-method-antmatchers-in-authorizationmanagerrequestmatcherregis

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
    // used for loading user data
    @Autowired
    private UserService userService;


    // defines which URL paths should be secured
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // allows users to access requestMatchers without authentication
                .authorizeHttpRequests((authorize) -> authorize
                        // allow unauthenticated to visit login
                        .requestMatchers("/login").permitAll()

                        .requestMatchers("/", "/home").hasAnyRole("EMPLOYEE", "MANAGER", "ADMIN")
                        //.requestMatchers("/admin home").hasRole("ADMIN")

                        .anyRequest().authenticated()

                )
                // successful login redirection path
                .formLogin((form) -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true)
                        .permitAll()
                )
                // allows everyone to access logout
                .logout(LogoutConfigurer::permitAll);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, PasswordEncoder passwordEncoder,
                                                       UserDetailsService userDetailsService) throws Exception {
        AuthenticationManagerBuilder authMB = http.getSharedObject(AuthenticationManagerBuilder.class);
        authMB.userDetailsService(userService).passwordEncoder(passwordEncoder());
        return authMB.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // use BCrypt for hashing, generates random salt internally
        return new BCryptPasswordEncoder();
    }

}
