package com.growthhub.salon.repository;
import com.growthhub.salon.entity.SalonService;
import org.springframework.data.jpa.repository.*;
import java.util.*;
public interface SalonServiceRepository extends JpaRepository<SalonService, Long> {
    List<SalonService> findByCategoryIdAndStatus(Long categoryId, String status);
    List<SalonService> findByStatus(String status);
    List<SalonService> findByPopularTrue();
}
