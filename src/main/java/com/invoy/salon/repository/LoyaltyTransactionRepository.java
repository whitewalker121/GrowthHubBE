package com.growthhub.salon.repository;
import com.growthhub.salon.entity.LoyaltyTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface LoyaltyTransactionRepository extends JpaRepository<LoyaltyTransaction, Long> {
    List<LoyaltyTransaction> findByClientIdOrderByTransactionDateDesc(Long clientId);

    Collection<LoyaltyTransaction> findByClientId(Long id);
}
