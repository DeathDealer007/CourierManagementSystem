package com.capg.smartcourier.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import com.capg.smartcourier.entity.Tracking;
import com.capg.smartcourier.entity.Document;
import com.capg.smartcourier.entity.DeliveryProof;
import com.capg.smartcourier.service.TrackingService;
import com.capg.smartcourier.service.DocumentService;
import com.capg.smartcourier.service.DeliveryProofService;
import jakarta.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Tracking Controller
 *
 * REST API controller for tracking and document management operations
 */
@RestController
@RequestMapping({"/getdelivery", "/api/getdelivery"})
public class TrackingController {

    /**
     * Service for tracking business logic operations
     * Injected by Spring's dependency injection container
     */
    @Autowired
    private TrackingService trackingService;

    /**
     * Service for document management operations
     * Injected by Spring's dependency injection container
     */
    @Autowired
    private DocumentService documentService;

    /**
     * Service for delivery proof operations
     * Injected by Spring's dependency injection container
     */
    @Autowired
    private DeliveryProofService deliveryProofService;

    /**
     * Add a new tracking record
     *
     * Creates a new tracking entry for delivery status updates.
     * Used internally by the system and delivery personnel.
     *
     * @param tracking the tracking information to add
     * @return the saved tracking record with generated ID
     */
    @PostMapping
    public Tracking addTracking(@Valid @RequestBody Tracking tracking) {
        return trackingService.addTracking(tracking);
    }

    /**
     * Get tracking history for a delivery
     *
     * Retrieves all tracking records for a specific delivery
     * identified by its tracking number. Used by customers
     * to monitor delivery progress.
     *
     * @param trackingNumber the unique tracking number of the delivery
     * @return list of tracking records in chronological order
     */
    @GetMapping("/{trackingNumber}")
    public List<Tracking> getTracking(@PathVariable String trackingNumber) {
        return trackingService.getTracking(trackingNumber);
    }

    /**
     * Upload a document related to a delivery
     *
     * Handles file uploads for delivery-related documents such as
     * receipts, invoices, or special instructions. Files are stored
     * in the local file system with unique names.
     *
     * Upload Process:
     * 1. Validate file and parameters
     * 2. Create uploads directory if needed
     * 3. Generate unique filename with timestamp
     * 4. Save file to disk
     * 5. Create document record in database
     *
     * @param file the uploaded file (multipart)
     * @param deliveryId ID of the associated delivery
     * @param userId ID of the user uploading the file
     * @return the created document record
     * @throws IOException if file upload fails
     */
    @PostMapping("/documents/upload")
    public Document uploadDocument(@RequestParam("file") MultipartFile file,
                                   @RequestParam("deliveryId") Long deliveryId,
                                   @RequestParam("userId") Long userId) throws IOException {
        String uploadDir = "uploads/";
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(uploadDir + fileName);
        Files.write(filePath, file.getBytes());

        Document document = new Document(
            file.getOriginalFilename(),
            file.getContentType(),
            file.getSize(),
            filePath.toString(),
            deliveryId,
            userId
        );

        return documentService.saveDocument(document);
    }

    /**
     * Get delivery proofs for a specific delivery
     *
     * Retrieves all proof of delivery records for a delivery.
     * Used by customers and administrators to verify delivery completion.
     *
     * @param id the unique identifier of the delivery
     * @return list of delivery proofs associated with the delivery
     */
    @GetMapping("/{id}/proof")
    public List<DeliveryProof> getDeliveryProof(@PathVariable Long id) {
        return deliveryProofService.getDeliveryProofsByDeliveryId(id);
    }

    /**
     * Add delivery proof for a specific delivery
     *
     * Creates a new proof of delivery record. Used by delivery personnel
     * to upload evidence of successful delivery completion.
     *
     * @param id the delivery ID from URL path
     * @param proof the delivery proof information
     * @return the saved delivery proof record
     */
    @PostMapping("/{id}/proof")
    public DeliveryProof addDeliveryProof(@PathVariable Long id, @Valid @RequestBody DeliveryProof proof) {
        if (proof == null || proof.getProofData() == null || proof.getProofData().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "proofData is required");
        }
        proof.setDeliveryId(id);
        return deliveryProofService.saveDeliveryProof(proof);
    }

    /**
     * Add delivery proof with delivery ID in request body
     *
     * Alternative endpoint for adding delivery proof where the delivery
     * ID is provided in the request body instead of URL path.
     *
     * @param proof the delivery proof information including delivery ID
     * @return the saved delivery proof record
     */
    @PostMapping("/proof")
    public DeliveryProof addDeliveryProof(@Valid @RequestBody DeliveryProof proof) {
        if (proof == null || proof.getDeliveryId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "deliveryId is required in payload");
        }
        if (proof.getProofData() == null || proof.getProofData().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "proofData is required");
        }
        return deliveryProofService.saveDeliveryProof(proof);
    }
}