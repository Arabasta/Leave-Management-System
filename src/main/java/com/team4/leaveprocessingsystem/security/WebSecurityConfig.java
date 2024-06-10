package com.team4.leaveprocessingsystem.security;

import com.team4.leaveprocessingsystem.service.RedirectService;
import com.team4.leaveprocessingsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

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

    @Autowired
    private RedirectService redirectService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((authorize) -> authorize
                        // allow all roles and unauthenticated to visit login
                        .requestMatchers("/images/team_4_logo.png", "/auth/login").permitAll()

                         // employee
                        .requestMatchers("/", "/home/employee").hasAnyRole("EMPLOYEE", "MANAGER")

                        // manager
                        .requestMatchers("/", "/home/manager").hasRole("MANAGER")

                        // admin
                        .requestMatchers("/", "/home/admin").hasRole("ADMIN")

                        .anyRequest().authenticated()

                )
                // successful login redirection path
                .formLogin((form) -> form
                        .loginPage("/auth/login")
                        .successHandler(authenticationSuccessHandler())
                        .failureUrl("/auth/login?error=true")
                        .permitAll()
                )
                // logout
                .logout((logout) -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/auth/login?logout")
                        .permitAll()
                )
                .exceptionHandling((exceptions) -> exceptions
                        .accessDeniedHandler(accessDeniedHandler())
                );

        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        // redirects to respective home pages after logging in
        return (request, response, authentication) -> {
            String redirectUrl = redirectService.getAuthSuccessRedirectUrl();
            response.sendRedirect(redirectUrl);
        };
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        // redirects to 403-forbidden when accessDeniedException thrown
        return (request, response, accessDeniedException) -> response.sendRedirect("/error/403-forbidden");
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
