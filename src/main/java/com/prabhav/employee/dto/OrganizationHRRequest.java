package com.prabhav.employee.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class OrganizationHRRequest {

    @NotEmpty(message = "First name is required")
    private String firstName;

    @NotEmpty(message = "Last name is required")
    private String lastName;

    @Email(message = "Email should be valid")
    @NotEmpty(message = "Email is required")
    private String email;

    @NotEmpty(message = "Contact number is required")
    private String contactNumber;
}
