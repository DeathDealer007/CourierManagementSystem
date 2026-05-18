package com.capg.smartcourier.dto;

/**
 * Login Request Data Transfer Object
 *
 * This DTO (Data Transfer Object) represents the request payload for user login operations.
 * It encapsulates the credentials required for authentication without exposing sensitive
 * user data or system internals.
 *
 * Purpose:
 * - Transfer login credentials from client to server
 * - Provide clean separation between API contract and domain models
 * - Enable validation and transformation of input data
 * - Support different authentication mechanisms if needed
 *
 * Security Considerations:
 * - Password is transmitted in plain text (should use HTTPS)
 * - No sensitive information beyond credentials
 * - Short-lived object used only for authentication
 * - Not persisted or logged in plain form
 *
 * API Usage:
 * - POST /api/auth/login
 * - Content-Type: application/json
 * - Body: {"username": "user123", "password": "secret123"}
 *
 * Validation:
 * - Username and password are required (handled at controller level)
 * - Input sanitization performed by Spring MVC
 * - Additional validation can be added with Bean Validation annotations
 *
 * Design Pattern:
 * - Follows DTO pattern for clean API contracts
 * - Immutable constructor available for testing
 * - Standard JavaBean getters/setters for framework compatibility
 *
 * @author Smart Courier Development Team
 * @version 1.0.0
 * @since 2026-03-30
 */
public class LoginRequest {

    /**
     * User's login username
     * Must match the username used during registration
     * Case-sensitive for security purposes
     */
    private String username;

    /**
     * User's plain text password
     * Will be compared against encrypted password in database
     * Never stored or logged in this form
     */
    private String password;

    /**
     * Default constructor required by Jackson for JSON deserialization
     * and Spring MVC for request body binding
     */
    public LoginRequest(){

    }

    /**
     * Parameterized constructor for creating LoginRequest instances
     * Useful for testing and programmatic creation
     * Provides immutability during object construction
     *
     * @param username the user's login username
     * @param password the user's plain text password
     */
    LoginRequest(String username, String password){
    	this.username = username;
    	this.password = password;
    }

    /**
     * Gets the username for authentication
     * @return the username string
     */
    public String getUsername() {
    	return username;
    }

    /**
     * Sets the username for authentication
     * @param username the username to set
     */
    public void setUsername(String username) {
    	this.username = username;
    }

    /**
     * Gets the password for authentication
     * Note: Returns plain text password - handle securely
     * @return the password string
     */
    public String getPassword() {
    	return password;
    }

    /**
     * Sets the password for authentication
     * @param password the password to set
     */
    public void setPassword(String password) {
    	this.password = password;
    }
}