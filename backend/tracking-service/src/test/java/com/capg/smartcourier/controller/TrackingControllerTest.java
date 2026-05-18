package com.capg.smartcourier.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import com.capg.smartcourier.entity.DeliveryProof;
import com.capg.smartcourier.entity.Document;
import com.capg.smartcourier.entity.Tracking;
import com.capg.smartcourier.service.DeliveryProofService;
import com.capg.smartcourier.service.DocumentService;
import com.capg.smartcourier.service.TrackingService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(TrackingController.class)
@AutoConfigureMockMvc(addFilters = false)
class TrackingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TrackingService trackingService;

    @MockBean
    private DocumentService documentService;

    @MockBean
    private DeliveryProofService deliveryProofService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testAddTracking() throws Exception {
        Tracking tracking = new Tracking();
        tracking.setTrackingNumber("TRK123");
        when(trackingService.addTracking(any(Tracking.class))).thenReturn(tracking);

        mockMvc.perform(post("/api/tracking")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tracking)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trackingNumber").value("TRK123"));
    }

    @Test
    void testGetTracking() throws Exception {
        when(trackingService.getTracking("TRK123")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/tracking/TRK123"))
                .andExpect(status().isOk());
    }

    @Test
    void testUploadDocument() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "hello".getBytes());
        Document doc = new Document();
        when(documentService.saveDocument(any(Document.class))).thenReturn(doc);

        mockMvc.perform(multipart("/api/tracking/documents/upload")
                .file(file)
                .param("deliveryId", "1")
                .param("userId", "1"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetDeliveryProof() throws Exception {
        when(deliveryProofService.getDeliveryProofsByDeliveryId(1L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/tracking/1/proof"))
                .andExpect(status().isOk());
    }

    @Test
    void testAddDeliveryProof_Path() throws Exception {
        DeliveryProof proof = new DeliveryProof();
        proof.setProofData("some-proof");
        when(deliveryProofService.saveDeliveryProof(any(DeliveryProof.class))).thenReturn(proof);

        mockMvc.perform(post("/api/tracking/1/proof")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(proof)))
                .andExpect(status().isOk());
    }

    @Test
    void testAddDeliveryProof_Body() throws Exception {
        DeliveryProof proof = new DeliveryProof();
        proof.setDeliveryId(1L);
        proof.setProofData("some-proof");
        when(deliveryProofService.saveDeliveryProof(any(DeliveryProof.class))).thenReturn(proof);

        mockMvc.perform(post("/api/tracking/proof")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(proof)))
                .andExpect(status().isOk());
    }

    @Test
    void testAddDeliveryProof_Invalid() throws Exception {
        DeliveryProof proof = new DeliveryProof();
        // missing proofData

        mockMvc.perform(post("/api/tracking/1/proof")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(proof)))
                .andExpect(status().isBadRequest());
    }
}
