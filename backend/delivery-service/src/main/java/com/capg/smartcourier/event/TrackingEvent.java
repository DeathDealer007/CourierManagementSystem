package com.capg.smartcourier.event;

/**
 * Tracking Event
 *
 * Event object representing delivery status updates in the Smart Courier System.
 * Used for inter-service communication between delivery-service and tracking-service.
 * Published to message queue when delivery status changes occur.
 *
 * Event Flow:
 * 1. Delivery status changes in delivery-service
 * 2. TrackingEvent is created with current status
 * 3. Event is published to RabbitMQ message queue
 * 4. Tracking-service consumes event and updates tracking data
 * 5. Real-time updates sent to clients via WebSocket
 *
 * Message Structure:
 * - trackingNumber: Unique identifier for the delivery
 * - status: Current delivery status (CREATED, PICKED, IN_TRANSIT, etc.)
 * - location: Current or next location in the delivery chain
 * - message: Human-readable description of the status change
 *
 * Integration:
 * - Serialized to JSON for message queue transport
 * - Consumed by tracking-service for real-time updates
 * - Enables event-driven architecture for delivery tracking
 * - Supports loose coupling between microservices
 *
 * @author Smart Courier Development Team
 * @version 1.0.0
 * @since 2024
 */
public class TrackingEvent {


	/**
	 * Message structure that we are going to send to tracking-service, after we have created a delivery
	 */
    /**
     * Unique tracking number of the delivery
     * Used to correlate events with specific deliveries
     * Must match the tracking number in the delivery record
     */
    private String trackingNumber;

    /**
     * Current status of the delivery
     * Values: CREATED, PICKED, IN_TRANSIT, DELIVERED, FAILED, CANCELLED
     * Indicates the current state in the delivery workflow
     */
    private String status;

    /**
     * Current location of the delivery
     * Describes where the package is or will be next
     * Examples: "Warehouse", "Distribution Center", "In Transit to [City]"
     */
    private String location;

    /**
     * Human-readable message describing the status change
     * Provides context for the status update
     * Examples: "Order created", "Picked up by courier", "Out for delivery"
     */
    private String message;

    /**
     * Default constructor
     * Required for JSON deserialization from message queue
     */
    public TrackingEvent() {}

    /**
     * Parameterized constructor
     *
     * Creates a TrackingEvent with all required fields for status updates.
     *
     * @param trackingNumber the unique tracking number of the delivery
     * @param status the current delivery status
     * @param location the current location of the delivery
     * @param message descriptive message about the status change
     */
    public TrackingEvent(String trackingNumber, String status, String location, String message) {
        this.trackingNumber = trackingNumber;
        this.status = status;
        this.location = location;
        this.message = message;
    }

    /**
     * Get the tracking number
     * @return the unique tracking number
     */
    public String getTrackingNumber() {
        return trackingNumber;
    }

    /**
     * Set the tracking number
     * @param trackingNumber the tracking number to set
     */
    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    /**
     * Get the delivery status
     * @return the current status of the delivery
     */
    public String getStatus() {
        return status;
    }

    /**
     * Set the delivery status
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Get the current location
     * @return the location description
     */
    public String getLocation() {
        return location;
    }

    /**
     * Set the current location
     * @param location the location to set
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Get the status message
     * @return the descriptive message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Set the status message
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }
}