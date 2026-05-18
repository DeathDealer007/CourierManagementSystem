package com.capg.smartcourier.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security Configuration
 */
@Configuration
public class SecurityConfig {

    /**
     * Configure Security Filter Chain
     *
     * Defines the security filter chain for HTTP requests in the delivery service.
     * Configures authentication, authorization, and security headers.
     *
     * Current Setup:
     * - CSRF protection disabled for API compatibility
     * - All requests permitted (development configuration)
     * - No authentication required at service level
     *
     * Security Filters Applied:
     * - CSRF protection filter (disabled)
     * - Authorization filter (permit all)
     * - Security headers filter
     * - Exception handling filter
     *
     * @param http the HttpSecurity object to configure
     * @return configured SecurityFilterChain
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable()) // for preventing the malicious data from the other users
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()
            );

        return http.build();
    }
}