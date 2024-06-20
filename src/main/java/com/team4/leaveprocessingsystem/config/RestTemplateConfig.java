package com.team4.leaveprocessingsystem.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

// source: https://www.geeksforgeeks.org/spring-boot-configure-a-resttemplate-with-resttemplatebuilder/
@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
