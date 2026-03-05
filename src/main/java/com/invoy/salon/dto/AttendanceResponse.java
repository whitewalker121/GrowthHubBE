package com.growthhub.salon.dto;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AttendanceResponse {
    private Long id;
    private Long staffId;
    private String staffName;
    private LocalDate date;
    private LocalTime checkIn;
    private LocalTime checkOut;
    private BigDecimal hoursWorked;
    private BigDecimal overtimeHours;
    private String status;
    private String notes;
}
