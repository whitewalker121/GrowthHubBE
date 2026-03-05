package com.growthhub.salon.repository;
import com.growthhub.salon.entity.Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.*;
public interface ClientRepository extends JpaRepository<Client, Long>, JpaSpecificationExecutor<Client> {
    Optional<Client> findByPhone(String phone);
    boolean existsByPhone(String phone);
    boolean existsByEmail(String email);
    @Query("SELECT c FROM Client c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%',:q,'%')) OR c.phone LIKE CONCAT('%',:q,'%')")
    Page<Client> search(@Param("q") String query, Pageable pageable);
}
