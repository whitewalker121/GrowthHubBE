package com.growthhub.salon.dto.request;

import com.growthhub.salon.enums.GenderType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class UpdateClientRequest {
    @Size(max = 150)
    private String name;
    @Email
    @Size(max = 150)
    private String email;
    private LocalDate dateOfBirth;
    private GenderType gender;
    @Size(max = 500)
    private String address;
    private String notes;
    private List<String> tags;
    private Boolean isActive;
}
