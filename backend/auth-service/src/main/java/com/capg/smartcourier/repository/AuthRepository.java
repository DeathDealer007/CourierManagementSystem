package com.capg.smartcourier.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.capg.smartcourier.entity.User;

/**
 * Authentication Repository - Data Access Layer for User Authentication
 *
 * This repository interface provides data access methods for user authentication
 * and user management operations. It extends JpaRepository to inherit standard
 * CRUD operations while adding custom query methods for authentication needs.
 *
 * Repository Features:
 * - User lookup by username (case-sensitive)
 * - Standard CRUD operations (inherited from JpaRepository)
 * - Automatic query generation for custom methods
 * - Transaction management integration
 * - Connection pooling through HikariCP
 *
 * Custom Query Methods:
 * - findByUsername(String username): Finds user by unique username
 *   Returns Optional<User> to handle cases where user doesn't exist
 *
 * Inherited Methods (from JpaRepository):
 * - save(User user): Save or update user
 * - findById(Long id): Find user by ID
 * - findAll(): Get all users
 * - delete(User user): Delete user
 * - existsById(Long id): Check if user exists
 * - count(): Get total user count
 *
 * Database Integration:
 * - Connected to auth_db schema in MySQL
 * - Uses Spring Data JPA for ORM mapping
 * - Supports @Query annotations for custom SQL if needed
 * - Automatic transaction management
 *
 * Security Considerations:
 * - Username queries are case-sensitive for security
 * - Passwords are never queried directly (handled in service layer)
 * - User roles are eagerly loaded for authorization
 *
 * @author Smart Courier Development Team
 * @version 1.0.0
 * @since 2026-03-30
 */
public interface AuthRepository extends JpaRepository<User, Long> {

    /**
     * Find user by username for authentication
     *
     * This method performs a database query to find a user by their unique username.
     * Used during login to retrieve user credentials and roles for authentication.
     *
     * Query Generation:
     * - Spring Data JPA automatically generates: SELECT u FROM User u WHERE u.username = ?1
     * - Returns Optional to handle null results gracefully
     * - Case-sensitive matching for security
     *
     * Usage:
     * - Called by AuthService.login() for user authentication
     * - Returns user with roles eagerly loaded (FetchType.EAGER)
     * - Throws no exceptions - returns empty Optional if not found
     *
     * Performance:
     * - Indexed on username column for fast lookups
     * - Eager loading of roles prevents N+1 query problem
     *
     * @param username the unique username to search for
     * @return Optional<User> containing the user if found, empty otherwise
     */
    Optional<User> findByUsername(String username);
}