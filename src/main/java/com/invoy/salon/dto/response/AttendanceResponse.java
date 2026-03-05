package com.growthhub.salon.dto.response;

import com.growthhub.salon.enums.AttendanceStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
@Builder
public class AttendanceResponse {
    private UUID id;
    private StaffSummary staff;
    private LocalDate workDate;
    private LocalTime checkIn;
    private LocalTime checkOut;
    private BigDecimal hoursWorked;
    private BigDecimal overtimeHours;
    private AttendanceStatus status;
    private String notes;
    private Instant createdAt;
}
