package com.growthhub.salon.dto;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class StaffResponse {
    private Long id;
    private String name;
    private String role;
    private String phone;
    private String email;
    private LocalDate joinDate;
    private String avatar;
    private String gender;
    private BigDecimal salary;
    private BigDecimal commissionRate;
    private BigDecimal targetRevenue;
    private List<String> specializations;
    private List<String> workingDays;
    private String workStartTime;
    private String workEndTime;
    private String status;
    private BigDecimal rating;
    private Integer totalClients;
    private Integer leavesCount;
    private LocalDateTime createdAt;
}
