package com.capg.smartcourier.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.capg.smartcourier.entity.Tracking;

public interface TrackingRepository extends JpaRepository<Tracking, Long> {

    List<Tracking> findByTrackingNumber(String trackingNumber);
}