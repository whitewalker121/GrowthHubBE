package com.growthhub.salon.repository;
import com.growthhub.salon.entity.Attendance;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.*;
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findByDate(LocalDate date);
    List<Attendance> findByStaffIdAndDateBetween(Long staffId, LocalDate from, LocalDate to);
    List<Attendance> findByDateBetween(LocalDate from, LocalDate to);
    Optional<Attendance> findByStaffIdAndDate(Long staffId, LocalDate date);
    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.date = :date AND a.status != 'absent'")
    long countPresentByDate(@Param("date") LocalDate date);
}
