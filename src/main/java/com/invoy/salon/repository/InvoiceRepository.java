package com.growthhub.salon.repository;
import com.growthhub.salon.entity.Invoice;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.*;
public interface InvoiceRepository extends JpaRepository<Invoice, Long>, JpaSpecificationExecutor<Invoice> {
    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);
    List<Invoice> findByClientIdOrderByDateDesc(Long clientId);
    List<Invoice> findByDateBetween(LocalDate from, LocalDate to);
    @Query("SELECT COALESCE(SUM(i.total),0) FROM Invoice i WHERE i.date = :date AND i.status='paid'")
    BigDecimal sumRevenueByDate(@Param("date") LocalDate date);
    @Query("SELECT COALESCE(SUM(i.total),0) FROM Invoice i WHERE i.date BETWEEN :from AND :to AND i.status='paid'")
    BigDecimal sumRevenueBetween(@Param("from") LocalDate from, @Param("to") LocalDate to);
    @Query("SELECT COALESCE(MAX(CAST(SUBSTRING(i.invoiceNumber, 9) AS integer)),0) FROM Invoice i WHERE i.invoiceNumber LIKE :prefix%")
    Optional<Integer> maxSequenceForPrefix(@Param("prefix") String prefix);
}
