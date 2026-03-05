package com.growthhub.salon.dto;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class AppointmentRequest {
    @NotNull Long clientId;
    @NotNull Long serviceId;
    @NotNull Long staffId;
    @NotNull LocalDate date;
    @NotNull LocalTime time;
    Integer duration;
    String status;
    String notes;
    String source;
}
