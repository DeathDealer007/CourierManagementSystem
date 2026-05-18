package com.capg.smartcourier.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.capg.smartcourier.entity.Delivery;
import com.capg.smartcourier.service.DeliveryService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(DeliveryController.class)
@AutoConfigureMockMvc(addFilters = false)
class DeliveryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DeliveryService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreate() throws Exception {
        Delivery delivery = new Delivery();
        delivery.setTrackingNumber("TRK1234567890");
        delivery.setServiceType("EXPRESS");
        delivery.setStatus("CREATED");
        delivery.setUserId(1L);

        when(service.createDelivery(any(Delivery.class))).thenReturn(delivery);

        mockMvc.perform(post("/api/deliveries")
                .header("X-User-Id", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(delivery)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trackingNumber").value("TRK1234567890"));
    }

    @Test
    void testGetAllDeliveries() throws Exception {
        when(service.getAllDeliveries()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/deliveries"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetById() throws Exception {
        Delivery delivery = new Delivery();
        delivery.setId(1L);
        when(service.getDeliveryById(1L)).thenReturn(delivery);

        mockMvc.perform(get("/api/deliveries/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void testGetMyDeliveries() throws Exception {
        when(service.getDeliveriesByUserId(1L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/deliveries/my").header("X-User-Id", "1"))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdate() throws Exception {
        Delivery delivery = new Delivery();
        delivery.setStatus("DELIVERED");
        when(service.updateDelivery(anyLong(), anyString())).thenReturn(delivery);

        Map<String, String> body = new HashMap<>();
        body.put("status", "DELIVERED");

        mockMvc.perform(put("/api/deliveries/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DELIVERED"));
    }

    @Test
    void testGetByTrackingNumber() throws Exception {
        Delivery delivery = new Delivery();
        delivery.setTrackingNumber("TRK123");
        when(service.getDeliveryByTrackingNumber("TRK123")).thenReturn(delivery);

        mockMvc.perform(get("/api/deliveries/tracking/TRK123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trackingNumber").value("TRK123"));
    }
}
