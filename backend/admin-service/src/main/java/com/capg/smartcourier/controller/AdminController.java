package com.capg.smartcourier.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

import com.capg.smartcourier.service.AdminService;
import com.capg.smartcourier.service.HubService;
import com.capg.smartcourier.entity.Hub;

@RestController
@RequestMapping({"/admin", "/api/admin"})
//@CrossOrigin("http://localhost:4200")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private HubService hubService;
    
    @PutMapping("/delivery/{id}")
    public Object updateDelivery(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return adminService.updateDelivery(id, body);
    }

    @GetMapping("/deliveries")
    public List<Object> getAllDeliveries() {
        return adminService.getAllDeliveries();
    }

    @GetMapping("/tracking/{trackingNumber}")
    public List<Object> getTracking(@PathVariable String trackingNumber) {
        return adminService.getTracking(trackingNumber);
    }

    // Admin Dashboard
    @GetMapping("/dashboard")
    public Map<String, Object> getDashboard() {
        return adminService.getDashboardStats();
    }

    // Resolve delivery exception
    @PutMapping("/deliveries/{id}/resolve")
    public Object resolveDelivery(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return adminService.resolveDelivery(id, body.get("status"));
    }

    // Reports
    @GetMapping("/reports")
    public Map<String, Object> getReports() {
        return adminService.getReports();
    }

    // User management
    @GetMapping("/users")
    public List<Object> getAllUsers() {
        return adminService.getAllUsers();
    }

    // Hub management
    @GetMapping("/hubs")
    public List<Hub> getAllHubs() {
        return hubService.getAllHubs();
    }
}
