package com.capg.smartcourier.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.capg.smartcourier.feign.DeliveryClient;
import com.capg.smartcourier.feign.TrackingClient;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private DeliveryClient deliveryClient;

    @Mock
    private TrackingClient trackingClient;

    @InjectMocks
    private AdminService service;

    @Test
    void testUpdateDelivery() {
        Map<String, String> body = new HashMap<>();
        when(deliveryClient.updateDelivery(anyLong(), any())).thenReturn(new Object());
        Object result = service.updateDelivery(1L, body);
        assertNotNull(result);
    }

    @Test
    void testGetAllDeliveries() {
        when(deliveryClient.getAllDeliveries()).thenReturn(new ArrayList<>());
        List<Object> result = service.getAllDeliveries();
        assertNotNull(result);
    }

    @Test
    void testGetTracking() {
        when(trackingClient.getTracking("TRK123")).thenReturn(new ArrayList<>());
        List<Object> result = service.getTracking("TRK123");
        assertNotNull(result);
    }

    @Test
    void testGetDashboardStats() {
        List<Object> deliveries = new ArrayList<>();
        deliveries.add(Map.of("status", "CREATED"));
        deliveries.add(Map.of("status", "DELIVERED"));
        deliveries.add(Map.of("status", "OUT_FOR_DELIVERY"));
        deliveries.add(Map.of("status", "FAILED"));
        deliveries.add(Map.of("status", "IN_TRANSIT"));

        when(deliveryClient.getAllDeliveries()).thenReturn(deliveries);

        Map<String, Object> stats = service.getDashboardStats();

        assertEquals(5L, stats.get("totalShipments"));
        assertEquals(1L, stats.get("pending"));
        assertEquals(1L, stats.get("delivered"));
        assertEquals(1L, stats.get("outForDelivery"));
        assertEquals(1L, stats.get("failed"));
        assertEquals(1L, stats.get("inTransit"));
    }

    @Test
    void testResolveDelivery() {
        when(deliveryClient.updateDelivery(anyLong(), any())).thenReturn(new Object());
        Object result = service.resolveDelivery(1L, "DELIVERED");
        assertNotNull(result);
    }

    @Test
    void testGetReports() {
        List<Object> deliveries = new ArrayList<>();
        deliveries.add(Map.of("status", "DELIVERED"));
        when(deliveryClient.getAllDeliveries()).thenReturn(deliveries);

        Map<String, Object> reports = service.getReports();

        assertEquals(1, reports.get("totalDeliveries"));
        assertNotNull(reports.get("deliveriesByStatus"));
    }

    @Test
    void testGetAllUsers() {
        List<Object> result = service.getAllUsers();
        assertTrue(result.isEmpty());
    }
}