package com.growthhub.salon.repository;
import com.growthhub.salon.entity.Expense;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
public interface ExpenseRepository extends JpaRepository<Expense, Long>, JpaSpecificationExecutor<Expense> {
    List<Expense> findByDateBetweenOrderByDateDesc(LocalDate from, LocalDate to);
    List<Expense> findByCategoryAndDateBetween(String category, LocalDate from, LocalDate to);
    @Query("SELECT COALESCE(SUM(e.amount),0) FROM Expense e WHERE e.date BETWEEN :from AND :to AND e.status='approved'")
    BigDecimal sumApprovedBetween(@Param("from") LocalDate from, @Param("to") LocalDate to);
}
