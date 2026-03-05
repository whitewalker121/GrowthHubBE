package com.growthhub.salon.dto;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class StaffRequest {
    @NotBlank String name;
    @NotBlank String role;
    @NotBlank @Pattern(regexp = "^\\+?[0-9\\s]{10,15}$") String phone;
    @Email String email;
    LocalDate joinDate;
    String gender;
    BigDecimal salary;
    BigDecimal commissionRate;
    BigDecimal targetRevenue;
    List<String> specializations;
    List<String> workingDays;
    String workStartTime;
    String workEndTime;
    String status;
}
