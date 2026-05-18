package com.capg.smartcourier.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.capg.smartcourier.entity.DeliveryProof;
import java.util.List;

public interface DeliveryProofRepository extends JpaRepository<DeliveryProof, Long> {
    List<DeliveryProof> findByDeliveryId(Long deliveryId);
}