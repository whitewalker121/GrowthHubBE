package com.growthhub.salon.dto;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AppointmentResponse {
    private Long id;
    private Long clientId;
    private String clientName;
    private String clientPhone;
    private Long serviceId;
    private String serviceName;
    private Long staffId;
    private String staffName;
    private LocalDate date;
    private LocalTime time;
    private Integer duration;
    private BigDecimal amount;
    private String status;
    private String notes;
    private String source;
}
