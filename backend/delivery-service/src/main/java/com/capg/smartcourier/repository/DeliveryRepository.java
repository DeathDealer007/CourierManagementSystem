package com.capg.smartcourier.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.capg.smartcourier.entity.Delivery;
import java.util.List;

/**
 * Delivery Repository
 *
 * Data access layer for Delivery entities in the Smart Courier System.
 */
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {

    /**
     * Find deliveries by user ID
     *
     * Retrieves all delivery records associated with a specific user.
     * Used for customer dashboards, order history, and user-specific operations.
     *
     * Query: SELECT d FROM Delivery d WHERE d.userId = ?1
     *
     * @param userId the unique identifier of the user
     * @return list of deliveries belonging to the specified user
     */
    List<Delivery> findByUserId(Long userId);

    /**
     * Find a delivery by its tracking number
     *
     * @param trackingNumber the unique tracking number
     * @return an Optional containing the delivery if found
     */
    java.util.Optional<Delivery> findByTrackingNumber(String trackingNumber);
}