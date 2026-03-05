package com.growthhub.salon.repository;
import com.growthhub.salon.entity.Appointment;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.*;
public interface AppointmentRepository extends JpaRepository<Appointment, Long>, JpaSpecificationExecutor<Appointment> {
    List<Appointment> findByDateAndStatus(LocalDate date, String status);
    List<Appointment> findByClientIdOrderByDateDesc(Long clientId);
    List<Appointment> findByStaffIdAndDate(Long staffId, LocalDate date);
    List<Appointment> findByDateBetweenOrderByDateAscTimeAsc(LocalDate from, LocalDate to);
    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.date = :date")
    long countByDate(@Param("date") LocalDate date);
}
