package com.growthhub.salon.dto.response;

import com.growthhub.salon.enums.AppointmentStatus;
import com.growthhub.salon.enums.BookingSource;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
@Builder
public class AppointmentResponse {
    private UUID id;
    private ClientSummary client;
    private StaffSummary staff;
    private ServiceSummary service;
    private LocalDate appointmentDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer durationMins;
    private BigDecimal amount;
    private AppointmentStatus status;
    private BookingSource source;
    private String notes;
    private Boolean reminderSent;
    private String cancelReason;
    private Instant createdAt;
}
