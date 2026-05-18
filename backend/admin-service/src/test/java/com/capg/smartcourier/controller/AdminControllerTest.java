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

import com.capg.smartcourier.service.AdminService;
import com.capg.smartcourier.service.HubService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(AdminController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminService adminService;

    @MockBean
    private HubService hubService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testUpdateDelivery() throws Exception {
        Map<String, String> body = new HashMap<>();
        body.put("status", "DELIVERED");
        when(adminService.updateDelivery(anyLong(), any())).thenReturn(new HashMap<>());

        mockMvc.perform(put("/api/admin/delivery/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAllDeliveries() throws Exception {
        when(adminService.getAllDeliveries()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/admin/deliveries"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetTracking() throws Exception {
        when(adminService.getTracking("TRK123")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/admin/tracking/TRK123"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetDashboard() throws Exception {
        when(adminService.getDashboardStats()).thenReturn(new HashMap<>());

        mockMvc.perform(get("/api/admin/dashboard"))
                .andExpect(status().isOk());
    }

    @Test
    void testResolveDelivery() throws Exception {
        Map<String, String> body = new HashMap<>();
        body.put("status", "DELIVERED");
        when(adminService.resolveDelivery(anyLong(), anyString())).thenReturn(new HashMap<>());

        mockMvc.perform(put("/api/admin/deliveries/1/resolve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk());
    }

    @Test
    void testGetReports() throws Exception {
        when(adminService.getReports()).thenReturn(new HashMap<>());

        mockMvc.perform(get("/api/admin/reports"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAllUsers() throws Exception {
        when(adminService.getAllUsers()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAllHubs() throws Exception {
        when(hubService.getAllHubs()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/admin/hubs"))
                .andExpect(status().isOk());
    }
}
