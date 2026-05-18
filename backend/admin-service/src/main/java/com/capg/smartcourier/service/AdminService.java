package com.capg.smartcourier.service;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.capg.smartcourier.feign.DeliveryClient;
import com.capg.smartcourier.feign.TrackingClient;

@Service
public class AdminService {

    @Autowired
    private DeliveryClient deliveryClient;

    @Autowired
    private TrackingClient trackingClient;
    
    public Object updateDelivery(Long id, Map<String, String> body) {
        return deliveryClient.updateDelivery(id, body);
    }

    public List<Object> getAllDeliveries() {
        return deliveryClient.getAllDeliveries();
    }

    public List<Object> getTracking(String trackingNumber) {
        return trackingClient.getTracking(trackingNumber);
    }

    // Dashboard stats
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        List<Object> deliveries = getAllDeliveries();
        
        long totalShipments = deliveries.size();
        long pending = deliveries.stream()
            .filter(d -> d instanceof Map && "CREATED".equals(((Map<?, ?>) d).get("status")))
            .count();
        long delivered = deliveries.stream()
            .filter(d -> d instanceof Map && "DELIVERED".equals(((Map<?, ?>) d).get("status")))
            .count();
        long outForDelivery = deliveries.stream()
            .filter(d -> d instanceof Map && "OUT_FOR_DELIVERY".equals(((Map<?, ?>) d).get("status")))
            .count();
        long failed = deliveries.stream()
            .filter(d -> d instanceof Map && "FAILED".equals(((Map<?, ?>) d).get("status")))
            .count();
        
        stats.put("totalShipments", totalShipments);
        stats.put("pending", pending);
        stats.put("delivered", delivered);
        stats.put("outForDelivery", outForDelivery);
        stats.put("failed", failed);
        stats.put("inTransit", totalShipments - pending - delivered - outForDelivery - failed);
        
        return stats;
    }

    // Resolve delivery
    public Object resolveDelivery(Long id, String status) {
        Map<String, String> body = new HashMap<>();
        body.put("status", status);
        return deliveryClient.updateDelivery(id, body);
    }

    // Reports
    public Map<String, Object> getReports() {
        Map<String, Object> reports = new HashMap<>();
        List<Object> deliveries = getAllDeliveries();
        
        // Simple aggregation
        reports.put("totalDeliveries", deliveries.size());
        reports.put("deliveriesByStatus", getDeliveriesByStatus(deliveries));
        
        return reports;
    }

    private Map<String, Long> getDeliveriesByStatus(List<Object> deliveries) {
        Map<String, Long> statusCount = new HashMap<>();
        for (Object obj : deliveries) {
            if (obj instanceof Map) {
                Map<?, ?> delivery = (Map<?, ?>) obj;
                String status = (String) delivery.get("status");
                statusCount.put(status, statusCount.getOrDefault(status, 0L) + 1);
            }
        }
        return statusCount;
    }

    // Get all users (placeholder - would need auth service integration)
    public List<Object> getAllUsers() {
        // This would need to call auth service
        return List.of(); // Placeholder
    }
}