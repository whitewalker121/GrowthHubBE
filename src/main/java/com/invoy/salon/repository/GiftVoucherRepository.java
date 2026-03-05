package com.growthhub.salon.repository;
import com.growthhub.salon.entity.GiftVoucher;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface GiftVoucherRepository extends JpaRepository<GiftVoucher, Long> {
    Optional<GiftVoucher> findByCode(String code);
    List<GiftVoucher> findByStatus(String status);
}
