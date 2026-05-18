package com.capg.smartcourier.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.capg.smartcourier.entity.DeliveryProof;
import com.capg.smartcourier.repository.DeliveryProofRepository;
import java.util.List;

@Service
public class DeliveryProofService {

    @Autowired
    private DeliveryProofRepository repo;

    public DeliveryProof saveDeliveryProof(DeliveryProof proof) {
        return repo.save(proof);
    }

    public List<DeliveryProof> getDeliveryProofsByDeliveryId(Long deliveryId) {
        return repo.findByDeliveryId(deliveryId);
    }

    public DeliveryProof getDeliveryProofById(Long id) {
        return repo.findById(id).orElse(null);
    }
}