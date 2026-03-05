package com.growthhub.salon.dto.request;

import com.growthhub.salon.enums.AttendanceStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
public class MarkAttendanceRequest {
    @NotNull
    private UUID staffId;
    @NotNull
    private LocalDate workDate;
    private LocalTime checkIn;
    private LocalTime checkOut;
    @NotNull
    private AttendanceStatus status;
    private String notes;
}
