package com.growthhub.salon.dto.request;

import com.growthhub.salon.enums.AppointmentStatus;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
public class UpdateAppointmentRequest {
    private UUID staffId;
    private UUID serviceId;
    private LocalDate appointmentDate;
    private LocalTime startTime;
    private AppointmentStatus status;
    private String notes;
    private String cancelReason;
}
