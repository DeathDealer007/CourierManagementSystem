package com.capg.smartcourier.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.capg.smartcourier.entity.Delivery;
import com.capg.smartcourier.service.DeliveryService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * Delivery Controller
 *
 * REST API controller for delivery management operations in the Smart Courier System.
 * Provides endpoints for creating, retrieving, and updating delivery information.
 * Acts as the API gateway for delivery-related operations.
 *
 * Key Responsibilities:
 * - Handle HTTP requests for delivery CRUD operations
 * - Validate input data using Jakarta Bean Validation
 * - Delegate business logic to the DeliveryService layer
 * - Return appropriate HTTP status codes and responses
 * - Support both direct API calls and gateway routing
 *
 * API Endpoints:
 * - POST /api/deliveries: Create new delivery
 * - GET /api/deliveries: Get all deliveries (admin use)
 * - GET /api/deliveries/{id}: Get delivery by ID
 * - GET /api/deliveries/my: Get current user's deliveries
 * - PUT /api/deliveries/{id}: Update delivery status
 *
 * Security:
 * - JWT authentication required for all endpoints
 * - User-specific data access control
 * - Admin privileges for system-wide operations
 * - Input validation and sanitization
 *
 * Integration:
 * - Communicates with DeliveryService for business logic
 * - Uses X-User-Id header for user identification
 * - Supports both /deliveries and /api/deliveries paths
 * - Compatible with API Gateway routing
 *
 * @author Smart Courier Development Team
 * @version 1.0.0
 * @since 2024
 */
@RestController
@RequestMapping({"/api/deliveries", "/deliveries"})
//@CrossOrigin("http://localhost:4200")
public class DeliveryController {

    /**
     * Service layer dependency for delivery business logic
     * Injected by Spring's dependency injection container
     */
    @Autowired
    private DeliveryService service;

    /**
     * Create a new delivery order
     *
     * Creates a new delivery with all associated information including
     * parcel details and addresses. Performs validation on all input data.
     *
     * @param delivery the delivery object containing all required information
     * @return the created delivery with generated ID and tracking number
     */
    @PostMapping
    public Delivery create(@Valid @RequestBody Delivery delivery, @RequestHeader("X-User-Id") Long userId) {
        delivery.setUserId(userId);
        return service.createDelivery(delivery);
    }

    /**
     * Get all deliveries in the system
     *
     * Administrative endpoint to retrieve all deliveries.
     * Should be restricted to admin users only.
     *
     * @return list of all deliveries in the system
     */
    @GetMapping
    public List<Delivery> getAllDeliveries() {
        return service.getAllDeliveries();
    }

    /**
     * Get a specific delivery by ID
     *
     * Retrieves detailed information about a specific delivery.
     * Access control ensures users can only view their own deliveries
     * unless they have admin privileges.
     *
     * @param id the unique identifier of the delivery
     * @return the delivery details
     */
    @GetMapping("/{id}")
    public Delivery getById(@PathVariable Long id) {
        return service.getDeliveryById(id);
    }

    /**
     * Get deliveries for the current user
     *
     * Retrieves all deliveries associated with the authenticated user.
     * Uses the X-User-Id header passed from the API Gateway.
     *
     * @param userId the ID of the authenticated user from request header
     * @return list of deliveries belonging to the current user
     */
    @GetMapping("/my")
    public List<Delivery> getMyDeliveries(@RequestHeader("X-User-Id") Long userId) {
        return service.getDeliveriesByUserId(userId);
    }

    /**
     * Update delivery status
     *
     * Administrative endpoint to update the status of a delivery.
     * Used by admin service and delivery personnel to update
     * delivery progress (PICKED, IN_TRANSIT, DELIVERED, etc.).
     *
     * @param id the unique identifier of the delivery to update
     * @param body request body containing the new status
     * @return the updated delivery object
     */
    @PutMapping("/{id}")
    public Delivery update(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return service.updateDelivery(id, body.get("status"));
    }

    /**
     * Get a specific delivery by tracking number
     *
     * @param trackingNumber the unique tracking number
     * @return the delivery details
     */
    @GetMapping("/tracking/{trackingNumber}")
    public Delivery getByTrackingNumber(@PathVariable String trackingNumber) {
        return service.getDeliveryByTrackingNumber(trackingNumber);
    }
}