package com.growthhub.salon.dto.request;

import com.growthhub.salon.enums.GenderType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class CreateClientRequest {
    @NotBlank
    @Size(max = 150)
    private String name;

    @NotBlank
    @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Invalid phone number")
    private String phone;

    @Email
    @Size(max = 150)
    private String email;

    private LocalDate dateOfBirth;

    private GenderType gender = GenderType.PREFER_NOT_TO_SAY;

    @Size(max = 500)
    private String address;

    private String notes;

    private List<String> tags;

    private String referredByPhone;   // optional — referral lookup by phone
}
