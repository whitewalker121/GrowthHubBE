package com.growthhub.salon.repository;
import com.growthhub.salon.entity.ClientMembership;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
public interface ClientMembershipRepository extends JpaRepository<ClientMembership, Long> {
    List<ClientMembership> findByClientId(Long clientId);
    Optional<ClientMembership> findByClientIdAndStatus(Long clientId, String status);
}
