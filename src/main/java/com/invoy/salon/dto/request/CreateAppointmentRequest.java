package com.growthhub.salon.dto.request;

import com.growthhub.salon.enums.BookingSource;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
public class CreateAppointmentRequest {
    @NotNull
    private UUID clientId;
    @NotNull
    private UUID staffId;
    @NotNull
    private UUID serviceId;

    @NotNull
    private LocalDate appointmentDate;
    @NotNull
    private LocalTime startTime;

    private BookingSource source = BookingSource.WALK_IN;
    private String notes;
}
