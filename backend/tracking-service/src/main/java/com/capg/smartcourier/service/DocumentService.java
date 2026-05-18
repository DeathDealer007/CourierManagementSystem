package com.capg.smartcourier.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.capg.smartcourier.entity.Document;
import com.capg.smartcourier.repository.DocumentRepository;
import java.util.List;

@Service
public class DocumentService {

    @Autowired
    private DocumentRepository repo;

    public Document saveDocument(Document document) {
        return repo.save(document);
    }

    public List<Document> getDocumentsByDeliveryId(Long deliveryId) {
        return repo.findByDeliveryId(deliveryId);
    }

    public List<Document> getDocumentsByUserId(Long userId) {
        return repo.findByUserId(userId);
    }

    public Document getDocumentById(Long id) {
        return repo.findById(id).orElse(null);
    }
}