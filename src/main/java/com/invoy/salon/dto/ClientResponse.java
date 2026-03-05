package com.growthhub.salon.dto;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ClientResponse {
    private Long id;
    private String name;
    private String phone;
    private String email;
    private LocalDate dateOfBirth;
    private String gender;
    private String address;
    private String avatar;
    private Integer totalVisits;
    private BigDecimal totalSpend;
    private LocalDate lastVisit;
    private String membershipType;
    private LocalDate joinDate;
    private String notes;
    private Boolean isActive;
    private List<String> tags;
    private LocalDateTime createdAt;
}
