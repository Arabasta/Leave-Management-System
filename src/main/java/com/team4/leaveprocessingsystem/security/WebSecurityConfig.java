package com.team4.leaveprocessingsystem.security;

import com.team4.leaveprocessingsystem.service.RedirectService;
import com.team4.leaveprocessingsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

// source: https://docs.spring.io/spring-security/reference/servlet/getting-started.html
// source: https://spring.io/guides/gs/securing-web
// source: https://spring.io/blog/2022/02/21/spring-security-without-the-websecurityconfigureradapter
// to check : https://stackoverflow.com/questions/74753700/cannot-resolve-method-antmatchers-in-authorizationmanagerrequestmatcherregis
// CORS Ref: https://www.baeldung.com/spring-cors

@Configuration
@EnableAsync
@EnableWebSecurity
public class WebSecurityConfig {
    private final UserService userService;
    private final RedirectService redirectService;

    @Autowired
    public WebSecurityConfig(UserService userService, RedirectService redirectService) {
        this.userService = userService;
        this.redirectService = redirectService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults()) // must come first to allow handling of CORS before Spring Security
                .authorizeHttpRequests((authorize) -> authorize
                        // allow all roles and unauthenticated to visit login
                        .requestMatchers("/images/team_4_logo.png",
                                "/auth/login",
                                "/js/auth/loginValidation.js",
                                "/diagram",
                                "/images/aws_system_architecture_diagram.png",
                                "/icons/tooltip-icon.png",
                                "/error/**").permitAll()

                         // employee
                        .requestMatchers("/", "/employee/**").hasAnyRole("EMPLOYEE", "MANAGER")

                        // manager
                        .requestMatchers("/", "/manager/**").hasRole("MANAGER")

                        // admin
                        .requestMatchers("/", "/admin/**", "/error/**").hasRole("ADMIN")

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
                        .accessDeniedPage("/error/403-forbidden")
                        .authenticationEntryPoint((request, response, authException)
                                -> response.sendRedirect("/error/401-badrequest"))
                )
                .httpBasic(Customizer.withDefaults()) //For API consumption, allow http requests with authorization header
                .csrf((csrf) -> csrf
                        .ignoringRequestMatchers("/api/**") // For PUT, POST requests
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
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
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
