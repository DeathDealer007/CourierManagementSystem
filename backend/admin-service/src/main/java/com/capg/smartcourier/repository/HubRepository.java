package com.capg.smartcourier.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.capg.smartcourier.entity.Hub;
import java.util.List;

public interface HubRepository extends JpaRepository<Hub, Long> {
    List<Hub> findByActive(boolean active);
}