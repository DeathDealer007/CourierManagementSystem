package com.capg.smartcourier.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.capg.smartcourier.entity.Hub;
import com.capg.smartcourier.repository.HubRepository;

@ExtendWith(MockitoExtension.class)
class HubServiceTest {

    @Mock
    private HubRepository repo;

    @InjectMocks
    private HubService service;

    @Test
    void testSaveHub() {
        Hub hub = new Hub();
        when(repo.save(any(Hub.class))).thenReturn(hub);
        Hub result = service.saveHub(hub);
        assertNotNull(result);
    }

    @Test
    void testGetAllHubs() {
        when(repo.findAll()).thenReturn(Collections.emptyList());
        List<Hub> result = service.getAllHubs();
        assertNotNull(result);
    }

    @Test
    void testGetActiveHubs() {
        when(repo.findByActive(true)).thenReturn(Collections.emptyList());
        List<Hub> result = service.getActiveHubs();
        assertNotNull(result);
    }

    @Test
    void testGetHubById() {
        Hub hub = new Hub();
        when(repo.findById(1L)).thenReturn(Optional.of(hub));
        Hub result = service.getHubById(1L);
        assertNotNull(result);
    }

    @Test
    void testUpdateHub_Success() {
        Hub existing = new Hub();
        Hub updated = new Hub();
        updated.setName("New Name");
        when(repo.findById(1L)).thenReturn(Optional.of(existing));
        when(repo.save(any(Hub.class))).thenReturn(existing);

        Hub result = service.updateHub(1L, updated);
        assertNotNull(result);
        assertEquals("New Name", result.getName());
    }

    @Test
    void testUpdateHub_NotFound() {
        when(repo.findById(1L)).thenReturn(Optional.empty());
        Hub result = service.updateHub(1L, new Hub());
        assertNull(result);
    }

    @Test
    void testDeleteHub() {
        doNothing().when(repo).deleteById(1L);
        service.deleteHub(1L);
        verify(repo, times(1)).deleteById(1L);
    }
}
