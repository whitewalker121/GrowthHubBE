package com.growthhub.salon.dto.request;

import com.growthhub.salon.enums.GenderType;
import com.growthhub.salon.enums.StaffStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
public class UpdateStaffRequest {
    @Size(max = 150)
    private String fullName;
    private String phone;
    @Email
    private String email;
    private GenderType gender;
    private LocalDate dateOfBirth;
    private String address;
    @Size(max = 100)
    private String role;
    private BigDecimal baseSalary;
    private BigDecimal commissionRate;
    private BigDecimal targetRevenue;
    private List<String> specializations;
    private String workingDays;
    private LocalTime shiftStart;
    private LocalTime shiftEnd;
    private StaffStatus status;
}
