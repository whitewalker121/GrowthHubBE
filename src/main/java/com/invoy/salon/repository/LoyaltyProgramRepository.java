package com.growthhub.salon.repository;
import com.growthhub.salon.entity.LoyaltyProgram;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface LoyaltyProgramRepository extends JpaRepository<LoyaltyProgram, Long> {
    List<LoyaltyProgram> findByStatus(String status);
}
