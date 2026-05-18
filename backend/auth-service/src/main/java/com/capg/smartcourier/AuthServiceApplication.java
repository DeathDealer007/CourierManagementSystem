package com.capg.smartcourier;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.capg.smartcourier.entity.User;
import com.capg.smartcourier.entity.Role;

/**
 * Smart Courier System - Authentication Service Application
 *
 * This is the main entry point for the Authentication Service microservice
 * in the Smart Courier System. This service handles user authentication,
 * registration, JWT token generation, and user management operations.
 *
 * Key Features:
 * - User registration and login with JWT authentication
 * - Password encryption using BCrypt
 * - Role-based access control (USER, ADMIN roles)
 * - Integration with Eureka Service Discovery
 * - RESTful API endpoints for authentication operations
 * - Comprehensive input validation and error handling
 * - Structured logging with SLF4J and Logback
 *
 * Technology Stack:
 * - Spring Boot 3.2.5
 * - Spring Security with JWT
 * - Spring Data JPA with Hibernate
 * - MySQL Database
 * - Eureka Client for service discovery
 * - Maven for dependency management
 *
 * Configuration:
 * - Server Port: 8081
 * - Database: MySQL (auth_db schema)
 * - JWT Expiration: 1 hour (configurable via environment)
 * - Logging: Configurable via logback.xml
 *
 * @author Smart Courier Development Team
 * @version 1.0.0
 * @since 2026-03-30
 */
@SpringBootApplication
public class AuthServiceApplication {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(AuthServiceApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
        System.out.println("auth service is running");
    }

    @org.springframework.beans.factory.annotation.Autowired
    private com.capg.smartcourier.repository.AuthRepository userRepo;

    @org.springframework.beans.factory.annotation.Autowired
    private com.capg.smartcourier.repository.RoleRepository roleRepo;

    @org.springframework.beans.factory.annotation.Autowired
    private org.springframework.security.crypto.password.PasswordEncoder encoder;

    @org.springframework.context.annotation.Bean
    public org.springframework.boot.CommandLineRunner dataInitializer() {
        return args -> {
            try {
                // Seed Roles
                Role adminRole = roleRepo.findByName("ROLE_ADMIN")
                        .orElseGet(() -> roleRepo.save(new Role(null, "ROLE_ADMIN")));
                Role userRole = roleRepo.findByName("ROLE_USER")
                        .orElseGet(() -> roleRepo.save(new Role(null, "ROLE_USER")));

                // Seed Admin User if not exists
                if (userRepo.findByUsername("admin").isEmpty()) {
                    java.util.Set<Role> adminRoles = new java.util.HashSet<>();
                    adminRoles.add(adminRole);
                    User admin = new User(null, "admin", "admin@smartcourier.com", encoder.encode("Admin@123"), adminRoles);
                    userRepo.saveAndFlush(admin);
                    logger.info("✅ Default Admin User created: admin/Admin@123");
                }

                // Seed normal user if not exists
                if (userRepo.findByUsername("user").isEmpty()) {
                    java.util.Set<Role> userRoles = new java.util.HashSet<>();
                    userRoles.add(userRole);
                    User user = new User(null, "user", "user@smartcourier.com", encoder.encode("User@123"), userRoles);
                    userRepo.saveAndFlush(user);
                    logger.info("✅ Default Normal User created: user/User@123");
                }
            } catch (jakarta.validation.ConstraintViolationException e) {
                e.getConstraintViolations().forEach(v -> logger.error("❌ Validation error: {} - {}", v.getPropertyPath(), v.getMessage()));
                logger.error("Error during data initialization", e);
            } catch (Exception e) {
                logger.error("Error during data initialization", e);
            }
        };
    }
}
