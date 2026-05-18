package com.capg.smartcourier.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.capg.smartcourier.entity.Tracking;
import com.capg.smartcourier.repository.TrackingRepository;

@ExtendWith(MockitoExtension.class)
class TrackingServiceTest {

    @Mock
    private TrackingRepository repo;

    @InjectMocks
    private TrackingService service;

    @Test
    void testAddTracking() {
        Tracking tracking = new Tracking();
        when(repo.save(any(Tracking.class))).thenReturn(tracking);

        Tracking result = service.addTracking(tracking);

        assertNotNull(result);
        verify(repo, times(1)).save(tracking);
    }

    @Test
    void testGetTracking_Success() {
        String trackingNumber = "TRK123";
        when(repo.findByTrackingNumber(trackingNumber)).thenReturn(Collections.singletonList(new Tracking()));

        List<Tracking> result = service.getTracking(trackingNumber);

        assertFalse(result.isEmpty());
    }

    @Test
    void testGetTracking_Null() {
        List<Tracking> result = service.getTracking(null);
        assertTrue(result.isEmpty());
        verify(repo).findByTrackingNumber("");
    }
}