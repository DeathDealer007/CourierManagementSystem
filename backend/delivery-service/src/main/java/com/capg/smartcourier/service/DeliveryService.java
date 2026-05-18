package com.capg.smartcourier.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.capg.smartcourier.entity.Delivery;
import com.capg.smartcourier.exception.ResourceNotFoundException;
import com.capg.smartcourier.repository.DeliveryRepository;
import com.capg.smartcourier.messaging.MessageProducer;
import com.capg.smartcourier.event.TrackingEvent;
import java.util.List;

/**
 * Delivery Service
 *
 * Core business logic layer for delivery management in the Smart Courier System.
 */
@Service
public class DeliveryService {

    /**
     * Repository for delivery data access operations
     * Injected by Spring's dependency injection container
     */
    @Autowired
    private DeliveryRepository repo;

    /**
     * Message producer for publishing tracking events
     * Injected by Spring's dependency injection container
     */
    @Autowired
    private MessageProducer producer;

    /**
     * Create a new delivery order
     *
     * Creates a new delivery with proper relationship management and
     * publishes a tracking event for real-time updates.
     *
     * Process:
     * 1. Establish bidirectional relationships with Parcel and Addresses
     * 2. Set default status to "CREATED"
     * 3. Save delivery to database
     * 4. Publish tracking event to message queue
     *
     * @param delivery the delivery object to create
     * @return the saved delivery with generated ID and relationships
     */
    public Delivery createDelivery(Delivery delivery) {

        // 🔗 Set relationships properly
        if (delivery.getParcel() != null) {
            delivery.getParcel().setDelivery(delivery);
        }

        if (delivery.getAddresses() != null) {
            delivery.getAddresses().forEach(addr -> addr.setDelivery(delivery));
        }

        // Set default status at creation time; for now create is always 'CREATED'.
// Note: existing code path checks delivery.getStatus() != null, but we always override.
// Keep in mind if fields come in as null additional model validation may be needed.
        if (delivery.getStatus() == null) {
            delivery.setStatus("CREATED");
        }
        
        // 🕒 Set creation timestamp
        delivery.setCreatedAt(java.time.LocalDateTime.now());
        
        // 📅 Set expected delivery date based on service type
        if (delivery.getExpectedDeliveryDate() == null) {
            int daysToAdd = switch (delivery.getServiceType()) {
                case "EXPRESS" -> 2;
                case "OVERNIGHT" -> 1;
                case "SAME_DAY" -> 0;
                default -> 4; // STANDARD
            };
            delivery.setExpectedDeliveryDate(java.time.LocalDate.now().plusDays(daysToAdd));
        }

        // 💰 Autogenerate Amount based on weight and service type
        if (delivery.getParcel() != null) {
            double weight = delivery.getParcel().getWeight() != null ? delivery.getParcel().getWeight() : 0.0;
            boolean isFragile = Boolean.TRUE.equals(delivery.getParcel().getFragile());
            
            double baseRate = switch (delivery.getServiceType()) {
                case "EXPRESS" -> isFragile ? 120.0 : 100.0;
                case "OVERNIGHT" -> isFragile ? 200.0 : 180.0;
                case "SAME_DAY" -> isFragile ? 300.0 : 280.0;
                default -> isFragile ? 70.0 : 50.0; // STANDARD
            };
            
            delivery.setTotalAmount(weight * baseRate);
        } else if (delivery.getTotalAmount() == null) {
            delivery.setTotalAmount(100.0);
        }

        // 💾 Save delivery
        Delivery saved = repo.save(delivery);

        // 📤 Send message to RabbitMQ
        TrackingEvent event = new TrackingEvent(
                saved.getTrackingNumber(),
                "CREATED",
                "Warehouse",
                "Order created"
        );

        producer.sendTrackingEvent(event);

        return saved;
    }

    /**
     * Get all deliveries in the system
     *
     * Administrative operation to retrieve all deliveries.
     * Throws exception if no deliveries exist.
     *
     * @return list of all deliveries
     * @throws ResourceNotFoundException if no deliveries are found
     */
    public List<Delivery> getAllDeliveries() {
        return repo.findAll();
    }

    /**
     * Get a delivery by its unique identifier
     *
     * Retrieves a specific delivery with all related data.
     * Throws exception if delivery doesn't exist.
     *
     * @param id the unique identifier of the delivery
     * @return the delivery object with all relationships loaded
     * @throws ResourceNotFoundException if delivery with given ID is not found
     */
    public Delivery getDeliveryById(Long id) {

        return repo.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Delivery not found with id: " + id));
    }

    /**
     * Update delivery status
     *
     * Administrative operation to change the status of a delivery.
     * Used by delivery personnel and admin systems to update progress.
     *
     * @param id the unique identifier of the delivery to update
     * @param status the new status to set
     * @return the updated delivery object
     * @throws ResourceNotFoundException if delivery with given ID is not found
     */
    public Delivery updateDelivery(Long id, String status) {

        Delivery delivery = repo.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Delivery not found with id: " + id));

        delivery.setStatus(status);
        
        // ✅ Set deliveredAt if status is DELIVERED
        if ("DELIVERED".equals(status)) {
            delivery.setDeliveredAt(java.time.LocalDateTime.now());
        }
        
        Delivery saved = repo.save(delivery);

        // 📤 Send message to RabbitMQ for tracking update
        TrackingEvent event = new TrackingEvent(
                saved.getTrackingNumber(),
                status,
                "In Transit", // Default location, would ideally be dynamic
                "Delivery status updated to " + status
        );
        producer.sendTrackingEvent(event);

        return saved;
    }

    /**
     * Get deliveries for a specific user
     *
     * Retrieves all deliveries associated with a particular user.
     * Used for customer dashboards and order history.
     *
     * @param userId the unique identifier of the user
     * @return list of deliveries belonging to the user
     * @throws ResourceNotFoundException if no deliveries are found for the user
     */
    public List<Delivery> getDeliveriesByUserId(Long userId) {
        return repo.findByUserId(userId);
    }

    /**
     * Get a delivery by tracking number
     *
     * @param trackingNumber the unique tracking number
     * @return the delivery object
     */
    public Delivery getDeliveryByTrackingNumber(String trackingNumber) {
        return repo.findByTrackingNumber(trackingNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery not found with tracking number: " + trackingNumber));
    }
}