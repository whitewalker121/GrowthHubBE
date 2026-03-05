package com.growthhub.salon.dto;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class AttendanceRequest {
    @NotNull Long staffId;
    @NotNull LocalDate date;
    LocalTime checkIn;
    LocalTime checkOut;
    String status;
    String notes;
}
