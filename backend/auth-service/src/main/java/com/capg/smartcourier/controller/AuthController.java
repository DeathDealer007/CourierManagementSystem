package com.capg.smartcourier.controller;


import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.capg.smartcourier.entity.User;
import com.capg.smartcourier.service.AuthService;

import jakarta.validation.Valid;

/**
 * Authentication Controller - REST API endpoints for user authentication
 *
 * This controller provides the main entry points for user authentication operations
 * in the Smart Courier System. It handles user registration and login requests,
 * delegating business logic to the AuthService while providing RESTful API responses.
 *
 * Base URL: /api/auth
 * Content-Type: application/json
 *
 * Security Features:
 * - Input validation using Jakarta Bean Validation (@Valid)
 * - JWT token generation for successful authentication
 * - Comprehensive error handling through GlobalExceptionHandler
 * - Structured logging for audit trails
 *
 * API Endpoints:
 * - POST /api/auth/register - User registration
 * - POST /api/auth/login - User authentication and token generation
 *
 * Error Responses:
 * - 400 Bad Request: Validation errors (invalid input data)
 * - 401 Unauthorized: Invalid credentials
 * - 409 Conflict: User already exists (registration)
 * - 500 Internal Server Error: System errors
 *
 * @author Smart Courier Development Team
 * @version 1.0.0
 * @since 2026-03-30
 */
@RestController
@RequestMapping("/api/auth")
//@CrossOrigin("http://localhost:4200") // Uncomment for frontend integration
public class AuthController {

    /**
     * Authentication service dependency
     * Injected by Spring's dependency injection container
     * Handles all business logic for authentication operations
     */
    @Autowired
    private AuthService service;

    /**
     * User Registration Endpoint
     *
     * Registers a new user in the system with the provided credentials.
     * The user data is validated before processing, and a default USER role
     * is automatically assigned to new registrations.
     *
     * Process Flow:
     * 1. Validate input data (@Valid annotation)
     * 2. Check if username/email already exists
     * 3. Encrypt password using BCrypt
     * 4. Assign default USER role
     * 5. Save user to database
     * 6. Return success message
     *
     * @param user the user registration data from request body
     * @return success message if registration is successful
     * @throws MethodArgumentNotValidException if validation fails
     * @throws DataIntegrityViolationException if user already exists
     */
    @PostMapping("/register")
    public String register(@Valid @RequestBody User user) {
        return service.register(user);
    }

    /**
     * User Login Endpoint
     *
     * Authenticates a user with provided credentials and generates a JWT token
     * for authorized access to protected resources throughout the system.
     *
     * Process Flow:
     * 1. Validate input data (@Valid annotation)
     * 2. Find user by username in database
     * 3. Verify password using BCrypt comparison
     * 4. Load user roles for authorization
     * 5. Generate JWT token with user details and roles
     * 6. Return JWT token for subsequent API calls
     *
     * Security Notes:
     * - Password is never returned in responses
     * - JWT token contains user ID, username, and roles
     * - Token expiration is configurable (default: 1 hour)
     * - Failed login attempts are logged for security monitoring
     *
     * @param user the login credentials (username and password)
     * @return JWT token string if authentication is successful
     * @throws BadCredentialsException if credentials are invalid
     * @throws MethodArgumentNotValidException if input validation fails
     */
    @PostMapping("/login")
    public String login(@Valid @RequestBody User user) {
        return service.login(user);
    }
}