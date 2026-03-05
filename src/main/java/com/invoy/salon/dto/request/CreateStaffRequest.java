package com.growthhub.salon.dto.request;

import com.growthhub.salon.enums.GenderType;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
public class CreateStaffRequest {
    @NotBlank
    @Size(max = 150)
    private String fullName;

    @NotBlank
    @Pattern(regexp = "^[+]?[0-9]{10,15}$")
    private String phone;

    @Email
    private String email;

    private GenderType gender = GenderType.PREFER_NOT_TO_SAY;

    private LocalDate dateOfBirth;

    @Size(max = 500)
    private String address;

    @NotBlank
    @Size(max = 100)
    private String role;

    private LocalDate joinDate;

    @NotNull
    @DecimalMin("0")
    private BigDecimal baseSalary;

    @DecimalMin("0")
    @DecimalMax("100")
    private BigDecimal commissionRate = BigDecimal.ZERO;

    @DecimalMin("0")
    private BigDecimal targetRevenue = BigDecimal.ZERO;

    private List<String> specializations;

    private String workingDays;   // "MON,TUE,WED,THU,FRI,SAT"

    private LocalTime shiftStart;
    private LocalTime shiftEnd;

    private String portalEmail;    // creates a User account if provided
    private String portalPassword;
}
