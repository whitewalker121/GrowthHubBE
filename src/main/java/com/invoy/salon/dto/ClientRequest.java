package com.growthhub.salon.dto;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class ClientRequest {
    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^\\+?[0-9\\s]{10,15}$", message = "Invalid phone number")
    private String phone;

    @Email(message = "Invalid email")
    private String email;

    private LocalDate dateOfBirth;
    private String gender;
    private String address;
    private String notes;
    private List<String> tags;
}
