package com.capg.smartcourier.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.capg.smartcourier.entity.Document;
import java.util.List;

public interface DocumentRepository extends JpaRepository<Document, Long> {
    List<Document> findByDeliveryId(Long deliveryId);
    List<Document> findByUserId(Long userId);
}