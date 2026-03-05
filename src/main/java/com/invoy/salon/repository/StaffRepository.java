package com.growthhub.salon.repository;
import com.growthhub.salon.entity.Staff;
import org.springframework.data.jpa.repository.*;
import java.util.*;
public interface StaffRepository extends JpaRepository<Staff, Long> {
    Optional<Staff> findByPhone(String phone);
    List<Staff> findByStatus(String status);
    boolean existsByPhone(String phone);
    boolean existsByEmail(String email);
}
