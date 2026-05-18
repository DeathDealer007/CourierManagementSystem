package com.capg.smartcourier.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.capg.smartcourier.entity.Hub;
import com.capg.smartcourier.repository.HubRepository;
import java.util.List;

@Service
public class HubService {

    @Autowired
    private HubRepository repo;

    public Hub saveHub(Hub hub) {
        return repo.save(hub);
    }

    public List<Hub> getAllHubs() {
        return repo.findAll();
    }

    public List<Hub> getActiveHubs() {
        return repo.findByActive(true);
    }

    public Hub getHubById(Long id) {
        return repo.findById(id).orElse(null);
    }

    public Hub updateHub(Long id, Hub hub) {
        Hub existing = repo.findById(id).orElse(null);
        if (existing != null) {
            existing.setName(hub.getName());
            existing.setLocation(hub.getLocation());
            existing.setAddress(hub.getAddress());
            existing.setContactNumber(hub.getContactNumber());
            existing.setManager(hub.getManager());
            existing.setActive(hub.isActive());
            return repo.save(existing);
        }
        return null;
    }

    public void deleteHub(Long id) {
        repo.deleteById(id);
    }
}