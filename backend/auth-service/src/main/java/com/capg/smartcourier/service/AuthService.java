package com.capg.smartcourier.service;

import org.springframework.beans.factory.annotation.Autowired;//used for automatically injecting required dependencies into a class
import org.springframework.security.crypto.password.PasswordEncoder;
//BCryptPasswordEncoder is a class from spring security used for hashing passwords, verifying passwords securely
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.capg.smartcourier.entity.User;
import com.capg.smartcourier.exception.ResourceNotFoundException;
import com.capg.smartcourier.repository.AuthRepository;
import com.capg.smartcourier.security.JwtUtil;
import com.capg.smartcourier.repository.RoleRepository;
import com.capg.smartcourier.entity.Role;
import java.util.Set;
import java.util.HashSet;
import java.util.Collections;

/**
 * Authentication Service - Core business logic for user authentication
 *
 * This service handles all authentication-related operations including user registration,
 * login verification, password encryption, and JWT token generation. It serves as the
 * business logic layer between the controller and data access layers.
 *
 * Key Responsibilities:
 * - User registration with password encryption
 * - User authentication and credential verification
 * - JWT token generation for authenticated users
 * - Role assignment for new users
 * - Comprehensive error handling and logging
 * - Security audit trail maintenance
 *
 * Security Features:
 * - BCrypt password hashing (one-way encryption)
 * - JWT token-based authentication
 * - Structured logging for security events
 * - Input validation through entity constraints
 * - Exception handling for security violations
 *
 * Dependencies:
 * - AuthRepository: Data access for user operations
 * - PasswordEncoder: BCrypt password encryption
 * - JwtUtil: JWT token generation and validation
 *
 * Logging Levels:
 * - INFO: Successful operations (registration, login)
 * - WARN: Security-related warnings (invalid credentials, user not found)
 * - ERROR: System errors and exceptions
 * - DEBUG: Detailed operation flow (login attempts)
 *
 * @author Smart Courier Development Team
 * @version 1.0.0
 * @since 2026-03-30
 */
@Service
public class AuthService {

    /**
     * Logger instance for structured logging
     * Uses SLF4J facade with Logback implementation
     * Provides audit trail for all authentication operations
     */
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    /**
     * Repository for user data access operations
     * Injected by Spring's dependency injection
     * Provides CRUD operations for User entities
     */
    @Autowired
    private AuthRepository repo;

    /**
     * Password encoder for secure password hashing
     * Uses BCrypt algorithm for one-way password encryption
     * Configured in SecurityConfig with strength settings
     */
    @Autowired
    private PasswordEncoder encoder;

    /**
     * JWT utility for token generation and validation
     * Handles creation of signed JWT tokens for authenticated users
     * Includes user details and role information in tokens
     */
    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Repository for role data access operations
     */
    @Autowired
    private RoleRepository roleRepo;

    /**
     * User Registration Method
     *
     * Registers a new user in the system with encrypted password and default role.
     * This method handles the complete user registration workflow including
     * password encryption, role assignment, and database persistence.
     *
     * Process Flow:
     * 1. Log registration attempt
     * 2. Encrypt password using BCrypt
     * 3. Assign default USER role (handled by repository or entity)
     * 4. Save user to database
     * 5. Log successful registration
     * 6. Return success confirmation
     *
     * Security Considerations:
     * - Password is encrypted before database storage
     * - User input is validated at controller level
     * - Database constraints prevent duplicate users
     * - All operations are logged for audit purposes
     *
     * @param user the user object containing registration data
     * @return success message string
     * @throws DataIntegrityViolationException if user already exists
     * @throws Exception for any other registration errors
     */
    public String register(User user) {
        logger.info("Registering new user: {}", user.getUsername());
        try {
            // Handle roles - fetch from DB or create if new
            if (user.getRoles() != null && !user.getRoles().isEmpty()) {
                Set<Role> managedRoles = new HashSet<>();
                for (Role role : user.getRoles()) {
                    Role dbRole = roleRepo.findByName(role.getName())
                            .orElseGet(() -> roleRepo.save(role));
                    managedRoles.add(dbRole);
                }
                user.setRoles(managedRoles);
            } else {
                // Assign default USER role if none provided
                Role userRole = roleRepo.findByName("USER")
                        .orElseGet(() -> roleRepo.save(new Role(null, "USER")));
                user.setRoles(Collections.singleton(userRole));
            }

            user.setPassword(encoder.encode(user.getPassword()));
            repo.save(user);
            logger.info("User {} registered successfully", user.getUsername());
            return "User registered";
        } catch (Exception e) {
            logger.error("Error registering user {}: {}", user.getUsername(), e.getMessage(), e);
            throw e;
        }
    }

    /**
     * User Login Method
     *
     * Authenticates a user with provided credentials and generates a JWT token
     * for authorized access to protected system resources.
     *
     * Process Flow:
     * 1. Log login attempt (debug level)
     * 2. Retrieve user from database by username
     * 3. Verify password using BCrypt comparison
     * 4. Generate JWT token with user details
     * 5. Log successful authentication
     * 6. Return JWT token
     *
     * Security Features:
     * - Password verification uses secure comparison
     * - Failed attempts are logged as warnings
     * - JWT tokens include user ID and roles
     * - Token expiration prevents indefinite access
     * - User not found exceptions are handled gracefully
     *
     * Error Handling:
     * - ResourceNotFoundException: User doesn't exist
     * - IllegalArgumentException: Invalid credentials
     * - Generic Exception: System errors
     *
     * @param user the user object with login credentials
     * @return JWT token string for authenticated access
     * @throws ResourceNotFoundException if user doesn't exist
     * @throws IllegalArgumentException if credentials are invalid
     * @throws Exception for system-level errors
     */
    public String login(User user) {
        logger.debug("Login attempt for user: {}", user.getUsername());
        try {
            User dbUser = repo.findByUsername(user.getUsername())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));

            if (encoder.matches(user.getPassword(), dbUser.getPassword())) {
                java.util.List<String> roles = dbUser.getRoles().stream()
                        .map(com.capg.smartcourier.entity.Role::getName)
                        .toList();
                String token = jwtUtil.generateToken(user.getUsername(), dbUser.getId(), roles);
                logger.info("User {} logged in successfully", user.getUsername());
                return token;
            } else {
                // Log security warning for invalid credentials
                // This helps track potential brute force attacks
                logger.warn("Invalid credentials for user: {}", user.getUsername());
                throw new IllegalArgumentException("Invalid credentials");
            }
        } catch (ResourceNotFoundException e) {
            // User not found - log as warning for security monitoring
            // Differentiates between "user doesn't exist" vs "wrong password"
            logger.warn("Login failed: User not found - {}", user.getUsername());
            throw e;
        } catch (Exception e) {
            // Catch-all for unexpected system errors during login
            // Logs full stack trace for debugging while maintaining security
            logger.error("Login error for user {}: {}", user.getUsername(), e.getMessage(), e);
            throw e;
        }
    }
}