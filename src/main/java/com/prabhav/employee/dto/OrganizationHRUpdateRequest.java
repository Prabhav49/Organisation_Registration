package com.prabhav.employee.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)  // Ignore null values while deserializing
public class OrganizationHRUpdateRequest {

    @JsonProperty("first_name")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;

    @JsonProperty("last_name")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;

    @JsonProperty("email")
    @Email(message = "Email should be valid")
    private String email;

    @JsonProperty("contact_number")
    @Pattern(regexp = "^[0-9]{10}$", message = "Contact number must be a 10-digit numeric value")
    private String contactNumber;
}
