package com.capg.smartcourier.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.capg.smartcourier.entity.Tracking;
import com.capg.smartcourier.repository.TrackingRepository;
import com.capg.smartcourier.exception.ResourceNotFoundException;
import java.util.List;

/**
 * Tracking Service
 *
 * Core business logic layer for tracking operations in the Smart Courier System.
 */
@Service
public class TrackingService {

    /**
     * Repository for tracking data access operations
     * Injected by Spring's dependency injection container
     */
    @Autowired
    private TrackingRepository repo;

    /**
     * Add a new tracking record
     *
     * Creates and saves a new tracking record for delivery status updates.
     * Used when delivery status changes occur or location updates are received.
     *
     * @param tracking the tracking record to add
     * @return the saved tracking record with generated ID
     */
    public Tracking addTracking(Tracking tracking) {
        return repo.save(tracking);
    }

    /**
     * Get tracking history for a delivery
     *
     * Retrieves all tracking records for a specific delivery identified
     * by its tracking number. Returns chronological history of status
     * updates and location changes.
     *
     * @param trackingNumber the unique tracking number of the delivery
     * @return list of tracking records in chronological order
     * @throws ResourceNotFoundException if no tracking records found for the tracking number
     */
    public List<Tracking> getTracking(String trackingNumber) {
        String cleanTrackingNumber = (trackingNumber != null) ? trackingNumber.trim() : "";
        return repo.findByTrackingNumber(cleanTrackingNumber);
    }
}