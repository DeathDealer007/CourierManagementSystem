package com.capg.smartcourier.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.capg.smartcourier.entity.Address;
import com.capg.smartcourier.entity.Delivery;
import com.capg.smartcourier.entity.Parcel;
import com.capg.smartcourier.exception.ResourceNotFoundException;
import com.capg.smartcourier.messaging.MessageProducer;
import com.capg.smartcourier.repository.DeliveryRepository;

@ExtendWith(MockitoExtension.class)
class DeliveryServiceTest {

    @Mock
    private DeliveryRepository repo;

    @Mock
    private MessageProducer producer;

    @InjectMocks
    private DeliveryService service;

    @Test
    void testCreateDelivery_Express() {
        Delivery delivery = new Delivery();
        delivery.setTrackingNumber("TRK1234567890");
        delivery.setServiceType("EXPRESS");
        Parcel parcel = new Parcel();
        parcel.setWeight(2.0);
        delivery.setParcel(parcel);
        delivery.setAddresses(Collections.singletonList(new Address()));

        when(repo.save(any(Delivery.class))).thenAnswer(i -> i.getArguments()[0]);

        Delivery result = service.createDelivery(delivery);

        assertNotNull(result);
        assertEquals("CREATED", result.getStatus());
        assertEquals(LocalDate.now().plusDays(2), result.getExpectedDeliveryDate());
        assertEquals(200.0, result.getTotalAmount()); // 2.0 * 100.0
        verify(producer, times(1)).sendTrackingEvent(any());
    }

    @Test
    void testCreateDelivery_Overnight_Fragile() {
        Delivery delivery = new Delivery();
        delivery.setServiceType("OVERNIGHT");
        Parcel parcel = new Parcel();
        parcel.setWeight(1.0);
        parcel.setFragile(true);
        delivery.setParcel(parcel);

        when(repo.save(any(Delivery.class))).thenAnswer(i -> i.getArguments()[0]);

        Delivery result = service.createDelivery(delivery);

        assertEquals(LocalDate.now().plusDays(1), result.getExpectedDeliveryDate());
        assertEquals(200.0, result.getTotalAmount()); // 1.0 * 200.0
    }

    @Test
    void testCreateDelivery_SameDay() {
        Delivery delivery = new Delivery();
        delivery.setServiceType("SAME_DAY");
        Parcel parcel = new Parcel();
        parcel.setWeight(1.0);
        delivery.setParcel(parcel);

        when(repo.save(any(Delivery.class))).thenAnswer(i -> i.getArguments()[0]);

        Delivery result = service.createDelivery(delivery);

        assertEquals(LocalDate.now(), result.getExpectedDeliveryDate());
        assertEquals(280.0, result.getTotalAmount());
    }

    @Test
    void testCreateDelivery_Standard_NullParcel() {
        Delivery delivery = new Delivery();
        delivery.setServiceType("STANDARD");
        
        when(repo.save(any(Delivery.class))).thenAnswer(i -> i.getArguments()[0]);

        Delivery result = service.createDelivery(delivery);

        assertEquals(LocalDate.now().plusDays(4), result.getExpectedDeliveryDate());
        assertEquals(100.0, result.getTotalAmount());
    }

    @Test
    void testGetAllDeliveries() {
        when(repo.findAll()).thenReturn(List.of(new Delivery()));
        List<Delivery> result = service.getAllDeliveries();
        assertFalse(result.isEmpty());
    }

    @Test
    void testGetDeliveryById_Success() {
        Delivery delivery = new Delivery();
        when(repo.findById(1L)).thenReturn(Optional.of(delivery));
        Delivery result = service.getDeliveryById(1L);
        assertNotNull(result);
    }

    @Test
    void testGetDeliveryById_NotFound() {
        when(repo.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.getDeliveryById(1L));
    }

    @Test
    void testUpdateDelivery_Delivered() {
        Delivery delivery = new Delivery();
        delivery.setTrackingNumber("TRK123");
        when(repo.findById(1L)).thenReturn(Optional.of(delivery));
        when(repo.save(any(Delivery.class))).thenReturn(delivery);

        Delivery result = service.updateDelivery(1L, "DELIVERED");

        assertEquals("DELIVERED", result.getStatus());
        assertNotNull(result.getDeliveredAt());
        verify(producer, times(1)).sendTrackingEvent(any());
    }

    @Test
    void testUpdateDelivery_NotFound() {
        when(repo.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.updateDelivery(1L, "DELIVERED"));
    }

    @Test
    void testGetDeliveriesByUserId() {
        when(repo.findByUserId(1L)).thenReturn(List.of(new Delivery()));
        List<Delivery> result = service.getDeliveriesByUserId(1L);
        assertFalse(result.isEmpty());
    }

    @Test
    void testGetDeliveryByTrackingNumber_Success() {
        Delivery delivery = new Delivery();
        when(repo.findByTrackingNumber("TRK123")).thenReturn(Optional.of(delivery));
        Delivery result = service.getDeliveryByTrackingNumber("TRK123");
        assertNotNull(result);
    }

    @Test
    void testGetDeliveryByTrackingNumber_NotFound() {
        when(repo.findByTrackingNumber("TRK123")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.getDeliveryByTrackingNumber("TRK123"));
    }
}