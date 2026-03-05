package com.growthhub.salon.repository;
import com.growthhub.salon.entity.ClientLoyaltyAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface ClientLoyaltyAccountRepository extends JpaRepository<ClientLoyaltyAccount, Long> {
    Optional<ClientLoyaltyAccount> findByClientId(Long clientId);
}
